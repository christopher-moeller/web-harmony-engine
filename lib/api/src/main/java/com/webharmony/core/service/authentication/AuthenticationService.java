package com.webharmony.core.service.authentication;

import com.querydsl.jpa.impl.JPAQuery;
import com.webharmony.core.api.rest.model.AuthenticatedUserDto;
import com.webharmony.core.api.rest.model.LoginResult;
import com.webharmony.core.api.rest.model.view.user.UserLoginVM;
import com.webharmony.core.api.rest.model.view.user.UserLogoutRequest;
import com.webharmony.core.configuration.utils.EnvironmentConstants;
import com.webharmony.core.configuration.utils.HttpHeaderConstants;
import com.webharmony.core.context.AppStatusHolder;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.data.enums.ECoreActorRight;
import com.webharmony.core.data.enums.ECoreUserRoles;
import com.webharmony.core.data.jpa.model.actor.AbstractActor;
import com.webharmony.core.data.jpa.model.actor.AppUserActor;
import com.webharmony.core.data.jpa.model.token.AppToken;
import com.webharmony.core.data.jpa.model.token.ETokenType;
import com.webharmony.core.data.jpa.model.token.TokenEntityReferencePayload;
import com.webharmony.core.data.jpa.model.user.AppUser;
import com.webharmony.core.data.jpa.model.user.AppUserAccessConfig;
import com.webharmony.core.data.jpa.model.user.QAppUser;
import com.webharmony.core.configuration.security.ApplicationRight;
import com.webharmony.core.i18n.I18N;
import com.webharmony.core.i18n.I18nTranslation;
import com.webharmony.core.service.ActorService;
import com.webharmony.core.service.TokenService;
import com.webharmony.core.service.authentication.types.JwtAuthentication;
import com.webharmony.core.service.authentication.types.UnknownSystemAuthentication;
import com.webharmony.core.service.data.UserCrudService;
import com.webharmony.core.service.data.validation.ValidatorService;
import com.webharmony.core.utils.dev.DevUtils;
import com.webharmony.core.utils.dev.LocalDevProperties;
import com.webharmony.core.utils.exceptions.BadRequestException;
import com.webharmony.core.utils.exceptions.ForbiddenException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class AuthenticationService implements I18nTranslation {

    final I18N i18n = createI18nInstance(AuthenticationService.class);

    private static final QAppUser qAppUser = QAppUser.appUser;

    private final ValidatorService validatorService;

    private final PasswordEncoder passwordEncoder;

    private final UserCrudService userCrudService;

    private final ActorService actorService;

    private final JwtService jwtService;

    private final TokenService tokenService;

    private final UserLoginBlockService userLoginBlockService;

    @Value( "${" + EnvironmentConstants.ENV_AUTHENTICATION_DEFAULT_ADMIN_EMAIL + "}" )
    private String defaultAdminEmail;

    @PersistenceContext
    private EntityManager em;

    public AuthenticationService(ValidatorService validatorService, PasswordEncoder passwordEncoder, UserCrudService userCrudService, ActorService actorService, JwtService jwtService, TokenService tokenService, UserLoginBlockService userLoginBlockService) {
        this.validatorService = validatorService;
        this.passwordEncoder = passwordEncoder;
        this.userCrudService = userCrudService;
        this.actorService = actorService;
        this.jwtService = jwtService;
        this.tokenService = tokenService;
        this.userLoginBlockService = userLoginBlockService;
    }

    @Transactional
    public LoginResult loginUser(UserLoginVM userLoginVM) {

        if(userLoginVM.getUsername() != null) {
            userLoginVM.setUsername(userLoginVM.getUsername().toLowerCase());
        }

        validatorService.validate(userLoginVM);

        userLoginBlockService.assertUserIsNotBlocked(userLoginVM.getUsername());

        AppUser userEntity = userCrudService.getUserEntityByEmail(userLoginVM.getUsername());
        AppUserActor userActor = actorService.getUserActorByAppUser(userEntity);

        assertApplicationIsNotBlockedOrUserHasSpecialRights(userActor);

        final LocalDateTime expiresAt = LocalDateTime.now().plusHours(jwtService.getExpirationHours());
        final String jwt = jwtService.generateJwtToken(userActor, expiresAt);

        tokenService.persistNewToken(jwt, ETokenType.USER_ACCESS_TOKEN, new TokenEntityReferencePayload(userActor.getUuid()), expiresAt);

        return LoginResult.builder()
                .type(jwtService.getJwtType())
                .token(jwt)
                .tokenHeaderField(HttpHeaderConstants.AUTHENTICATION)
                .build();
    }

    private void assertApplicationIsNotBlockedOrUserHasSpecialRights(AppUserActor actor) {
        if(AppStatusHolder.getInstance().isStatusOk() || actor.hasRight(ECoreActorRight.CORE_SERVER_STATUS_NOT_OK_USER_HAS_ACCESS)) {
            return;
        }

        throw new ForbiddenException(i18n.translate("Technical Problems: Only users with special rights have access").build());
    }

    public JwtAuthentication authenticateByJwt(String jwt) {

        AppToken appToken = tokenService.findTokenByValue(jwt);
        if(!appToken.getType().equals(ETokenType.USER_ACCESS_TOKEN))
            throw new BadRequestException("Invalid token");

        UUID actorUUID = appToken.getTypedPayload(TokenEntityReferencePayload.class).getEntityUUID();
        AppUserActor userActor = actorService.getActorByUUIDAndType(actorUUID, AppUserActor.class);
        userActor.initRights();
        String usernameByToken = jwtService.getUsernameFromToken(jwt);

        if(!userActor.getUniqueName().equals(usernameByToken))
            throw new BadRequestException("Invalid token");

        return new JwtAuthentication(userActor);
    }

    public UnknownSystemAuthentication authenticateUnknownSystem(String ipAddress) {
        return new UnknownSystemAuthentication(actorService.getUnknownActor(ipAddress));
    }

    public boolean isUserPasswordValid(String email, String password) {
        String encodedPassword = new JPAQuery<>(em)
                .select(qAppUser.password)
                .from(qAppUser)
                .where(qAppUser.email.eq(email))
                .fetchOne();

        if(encodedPassword == null)
            return false;

        return passwordEncoder.matches(password, encodedPassword);
    }

    public void initDefaultUsers() {
        if(isUserTableEmpty()) {
            initNewAdminUser();
        }
    }

    private boolean isUserTableEmpty() {
        final Long count = new JPAQuery<>(em)
                .select(qAppUser.uuid.count())
                .from(qAppUser)
                .fetchOne();

        return count == null || count == 0;
    }

    private void initNewAdminUser() {

        final String adminPassword = DevUtils.loadLocalDevProperties()
                .map(LocalDevProperties::getDefaultAdminPassword)
                .orElseGet(this::generateRandomPassword);

        AppUser adminUser = new AppUser();
        adminUser.setEmail(defaultAdminEmail);
        adminUser.setPassword(passwordEncoder.encode(adminPassword));
        adminUser.setFirstname("Admin");
        adminUser.setLastname("Admin");

        AppUserAccessConfig accessConfig = new AppUserAccessConfig();
        accessConfig.setIsAdmin(true);
        accessConfig.setRoles(Set.of(ECoreUserRoles.ADMIN_USER.getEntity()));
        accessConfig.setMainRole(ECoreUserRoles.ADMIN_USER.getEntity());

        adminUser.setUserAccessConfig(accessConfig);

        userCrudService.saveEntity(adminUser);

        log.info("No user found: Please use the admin account to login (E-Mail: {}, Password: {}", defaultAdminEmail, adminPassword);
    }

    private String generateRandomPassword() {
        return UUID.randomUUID().toString();
    }

    public void logoutUser(UserLogoutRequest logoutRequest) {
        final String rawToken = jwtService.getRawToken(logoutRequest.getToken());
        tokenService.deleteByTokenValue(rawToken);
    }

    public AuthenticatedUserDto getOwnUser() {
        AbstractActor currentActor = ContextHolder.getContext().getCurrentActor();

        AuthenticatedUserDto dto = new AuthenticatedUserDto();
        dto.setUsername(currentActor.getUniqueName());
        if(currentActor instanceof AppUserActor appUserActor) {
            AppUser user = appUserActor.getUser();
            dto.setFirstname(user.getFirstname());
            dto.setLastname(user.getLastname());
        }
        dto.setEffectiveRights(currentActor.getEffectiveRights().stream().map(ApplicationRight::name).toList());
        return dto;
    }
}

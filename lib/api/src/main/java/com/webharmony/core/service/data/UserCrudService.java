package com.webharmony.core.service.data;

import com.querydsl.jpa.impl.JPAQuery;
import com.webharmony.core.api.rest.model.UserDto;
import com.webharmony.core.api.rest.model.utils.apiobject.ApiResource;
import com.webharmony.core.api.rest.model.view.user.*;
import com.webharmony.core.context.ArtifactInfo;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.context.FrontendInfo;
import com.webharmony.core.data.enums.ECoreRegistry;
import com.webharmony.core.data.jpa.model.actor.AbstractActor;
import com.webharmony.core.data.jpa.model.actor.AppUserActor;
import com.webharmony.core.data.jpa.model.token.AppToken;
import com.webharmony.core.data.jpa.model.token.ETokenType;
import com.webharmony.core.data.jpa.model.token.TokenEntityReferencePayload;
import com.webharmony.core.data.jpa.model.user.AppUser;
import com.webharmony.core.data.jpa.model.user.QAppUser;
import com.webharmony.core.i18n.I18N;
import com.webharmony.core.i18n.I18nTranslation;
import com.webharmony.core.service.ActorService;
import com.webharmony.core.service.TokenService;
import com.webharmony.core.service.data.mapper.GenericMappingUtils;
import com.webharmony.core.service.data.utils.AbstractEntityCrudService;
import com.webharmony.core.service.email.EmailTemplateService;
import com.webharmony.core.service.email.data.SimpleOneLinkMailData;
import com.webharmony.core.utils.exceptions.BadRequestException;
import com.webharmony.core.utils.exceptions.InternalServerException;
import com.webharmony.core.utils.tuple.Tuple2;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UserCrudService extends AbstractEntityCrudService<UserDto, AppUser> implements I18nTranslation {

    private final I18N i18N = createI18nInstance(UserCrudService.class);

    private static final QAppUser qAppUser = QAppUser.appUser;

    @PersistenceContext
    private EntityManager em;

    private final ActorService actorService;

    private final PasswordEncoder passwordEncoder;

    private final FrontendInfo frontendInfo;

    private final TokenService tokenService;

    private final EmailTemplateService emailTemplateService;

    private final ArtifactInfo artifactInfo;


    public UserCrudService(ActorService actorService, PasswordEncoder passwordEncoder, FrontendInfo frontendInfo, TokenService tokenService, EmailTemplateService emailTemplateService, ArtifactInfo artifactInfo) {
        this.actorService = actorService;
        this.passwordEncoder = passwordEncoder;
        this.frontendInfo = frontendInfo;
        this.tokenService = tokenService;
        this.emailTemplateService = emailTemplateService;
        this.artifactInfo = artifactInfo;
    }

    public AppUser getUserEntityByEmail(String email) {
        final AppUser appUser = new JPAQuery<>(em).select(qAppUser)
                .from(qAppUser)
                .where(qAppUser.email.eq(email))
                .fetchOne();
        return Optional.ofNullable(appUser)
                .orElseThrow(() -> new InternalServerException(i18N.translate("User with email '{email}' not found").add("email", email).build()));
    }

    @Transactional
    @Override
    public void deleteEntity(AppUser entity) {
        actorService.deleteAllReferencesForUser(entity);
        ContextHolder.getSpringContext().getBean(UserRegistrationService.class).deleteAllEntriesConnectedToUser(entity);
        super.deleteEntity(entity);
    }


    public UserPersonalAccountVM getPersonalAccount() {
        final AppUser appUser = getCurrentAppUser();

        final UserPersonalAccountVM vm = new UserPersonalAccountVM();
        vm.setEmail(appUser.getEmail());
        vm.setFirstname(appUser.getFirstname());
        vm.setLastname(appUser.getLastname());

        return vm;
    }

    @Transactional
    public UserPersonalAccountVM updatePersonalUserAccount(UserPersonalAccountVM requestBody) {
        getValidatorService().validate(requestBody);
        final AppUser appUser = getCurrentAppUser();
        appUser.setFirstname(requestBody.getFirstname());
        appUser.setLastname(requestBody.getLastname());
        saveEntity(appUser);
        return getPersonalAccount();
    }

    @Transactional
    public void updateUserPassword(UserPersonalAccountChangePasswordVM requestBody) {
        getValidatorService().validate(requestBody);
        final AppUser appUser = getCurrentAppUser();

        final String newPassword = requestBody.getNewPassword();
        appUser.setPassword(passwordEncoder.encode(newPassword));

        saveEntity(appUser);
    }

    private AppUser getCurrentAppUser() {
        final AbstractActor currentActor = ContextHolder.getContext().getCurrentActor();
        if(currentActor instanceof AppUserActor userActor) {
            final AppUser user = userActor.getUser();
            if(user == null) {
                throw new BadRequestException(i18N.translate("Actor {actorName} is not connected to a user").add("actorName", currentActor.getUniqueName()).build());
            }
            return user;
        } else {
            throw new BadRequestException(i18N.translate("Actor {actorName} is not a user").add("actorName", currentActor.getUniqueName()).build());
        }
    }

    public void requestNewPassword(UserPersonalAccountForgotPasswordVM requestBody) {

        if(requestBody.getEmail() != null) {
            requestBody.setEmail(requestBody.getEmail().toLowerCase());
        }

        getValidatorService().validate(requestBody);

        try {
            final AppUser appUser = getUserEntityByEmail(requestBody.getEmail());
            sendResetPasswordEmail(appUser);
        }catch (InternalServerException e) {
            log.error(e.getMessage());
        }
    }

    private void sendResetPasswordEmail(AppUser appUser) {
        SimpleOneLinkMailData config = new SimpleOneLinkMailData();
        config.setEmailToAddress(appUser.getEmail());
        config.setEmailSubject(i18N.translate("Reset your password for {appName}").add("appName", artifactInfo.getLongName()).build());
        config.setCaption(i18N.translate("Reset Password").build());
        config.setMessage(i18N.translate("You have created a reset request for your password. If you want to change your password click on the link below.").build());
        config.setBtnText(i18N.translate("Reset").build());

        final String token = createNewUserResetPasswordToken(ETokenType.USER_RESET_PASSWORD_TOKEN, appUser.getUuid());
        config.setBtnLink(frontendInfo.buildPathToFrontend("resetPasswordByEmail", Tuple2.of("token", token)));

        emailTemplateService.sendBaseOneLinkMail(config);
    }

    public String createNewUserResetPasswordToken(ETokenType type, UUID entityUUID) {
        final String tokenValue = tokenService.createRandomTokenValue();
        final TokenEntityReferencePayload tokenEntityReferencePayload = new TokenEntityReferencePayload(entityUUID);
        final LocalDateTime expirationDate = LocalDateTime.now().plusDays(ECoreRegistry.USER_RESET_PASSWORD_TOKEN_EXPIRES_IN_DAYS.getTypedValue(Integer.class));
        return tokenService.persistNewToken(tokenValue, type, tokenEntityReferencePayload, expirationDate).getTokenValue();
    }

    public UserPersonalAccountResetPasswordByEmailVM getViewModelForResetPasswordByEmail(String tokenValue) {
        final AppUser userEntity = getValidAppUserByPasswordResetToken(tokenValue);

        UserPersonalAccountResetPasswordByEmailVM vm = new UserPersonalAccountResetPasswordByEmailVM();
        vm.setTokenValue(tokenValue);
        vm.setEmail(userEntity.getEmail());

        return vm;
    }

    @Transactional
    public void resetPassword(UserPersonalAccountResetPasswordByEmailVM viewModel) {
        getValidatorService().validate(viewModel);
        final AppUser appUser = getValidAppUserByPasswordResetToken(viewModel.getTokenValue());
        final String newPassword = viewModel.getPassword();
        appUser.setPassword(passwordEncoder.encode(newPassword));
        saveEntity(appUser);
    }

    @Transactional
    public void deletePersonalUserAccount(UserPersonalAccountDeleteVM deleteAccountVM) {
        getValidatorService().validate(deleteAccountVM);
        final AppUser currentAppUser = this.getCurrentAppUser();
        this.deleteEntity(currentAppUser);
    }

    public ApiResource<UserDto> getCurrentUserAsApiResource() {
        return GenericMappingUtils.createSimpleReferenceApiObjectByEntity(getCurrentAppUser());
    }

    private AppUser getValidAppUserByPasswordResetToken(String tokenValue) {
        final AppToken appToken = tokenService.findTokenByValue(tokenValue);

        if(!tokenService.isTokenValid(appToken, ETokenType.USER_RESET_PASSWORD_TOKEN)) {
            throw new InternalServerException("Invalid token");
        }

        final UUID userId = appToken.getTypedPayload(TokenEntityReferencePayload.class).getEntityUUID();
        return getEntityById(userId);
    }

}

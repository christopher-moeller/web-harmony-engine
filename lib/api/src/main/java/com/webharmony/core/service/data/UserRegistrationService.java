package com.webharmony.core.service.data;

import com.querydsl.jpa.impl.JPAQuery;
import com.webharmony.core.api.rest.jackson.CustomLocalDateTimeSerializer;
import com.webharmony.core.api.rest.model.UserRegistrationDto;
import com.webharmony.core.api.rest.model.view.user.SendUserRegistrationInvitationVM;
import com.webharmony.core.api.rest.model.view.user.UserRegistrationVM;
import com.webharmony.core.api.rest.model.view.user.UserRegistrationWithInvitationVM;
import com.webharmony.core.context.ArtifactInfo;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.context.FrontendInfo;
import com.webharmony.core.data.enums.ECoreActorRight;
import com.webharmony.core.data.enums.ECoreRegistry;
import com.webharmony.core.data.enums.ECoreUserRoles;
import com.webharmony.core.data.jpa.model.actor.AppUserActor;
import com.webharmony.core.data.jpa.model.token.AppToken;
import com.webharmony.core.data.jpa.model.token.ETokenType;
import com.webharmony.core.data.jpa.model.token.TokenEntityReferencePayload;
import com.webharmony.core.data.jpa.model.user.AppUser;
import com.webharmony.core.data.jpa.model.user.AppUserAccessConfig;
import com.webharmony.core.data.jpa.model.userregistration.AppUserRegistration;
import com.webharmony.core.data.jpa.model.userregistration.AppUserRegistrationStateData;
import com.webharmony.core.data.jpa.model.userregistration.NestedUserRegistrationStateDataField;
import com.webharmony.core.data.jpa.model.userregistration.QAppUserRegistration;
import com.webharmony.core.data.jpa.utils.SqlXml;
import com.webharmony.core.i18n.I18N;
import com.webharmony.core.i18n.I18nTranslation;
import com.webharmony.core.service.ActorService;
import com.webharmony.core.service.TokenService;
import com.webharmony.core.service.data.mapper.MappingConfiguration;
import com.webharmony.core.service.data.utils.AbstractEntityCrudService;
import com.webharmony.core.service.data.validation.ValidatorService;
import com.webharmony.core.service.email.EmailTemplateService;
import com.webharmony.core.service.email.data.SimpleOneLinkMailData;
import com.webharmony.core.service.userregistration.EUserRegistrationState;
import com.webharmony.core.service.userregistration.EUserRegistrationWorkflow;
import com.webharmony.core.service.userregistration.RegistrationConfiguration;
import com.webharmony.core.utils.exceptions.InternalServerException;
import com.webharmony.core.utils.exceptions.JavaMethodNotAllowedException;
import com.webharmony.core.utils.tuple.Tuple2;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class UserRegistrationService extends AbstractEntityCrudService<UserRegistrationDto, AppUserRegistration> implements I18nTranslation {

    private final I18N i18N = createI18nInstance(UserRegistrationService.class);

    private final ValidatorService validatorService;

    private final FrontendInfo frontendInfo;

    private final UserCrudService userCrudService;

    private final PasswordEncoder passwordEncoder;

    private final TokenService tokenService;

    private final EmailTemplateService emailTemplateService;

    private final ActorRightService actorRightService;

    private final ActorService actorService;

    private final ArtifactInfo artifactInfo;

    @PersistenceContext
    private EntityManager em;

    public UserRegistrationService(ValidatorService validatorService, FrontendInfo frontendInfo, UserCrudService userCrudService, PasswordEncoder passwordEncoder, TokenService tokenService, EmailTemplateService emailTemplateService, ActorRightService actorRightService, ActorService actorService, ArtifactInfo artifactInfo) {
        this.validatorService = validatorService;
        this.frontendInfo = frontendInfo;
        this.userCrudService = userCrudService;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.emailTemplateService = emailTemplateService;
        this.actorRightService = actorRightService;
        this.actorService = actorService;
        this.artifactInfo = artifactInfo;
    }

    @Override
    protected void configureMapping(MappingConfiguration<UserRegistrationDto, AppUserRegistration> mappingConfiguration) {
        mappingConfiguration.withExtendedToDtoMapper((entity, dto, context) -> {
            Optional.ofNullable(entity.getStateData())
                    .map(SqlXml::getValue)
                    .map(this::writeXmlValueAsString)
                    .ifPresent(dto::setStateData);

            return dto;
        });

        mappingConfiguration.withExtendedToEntityMapper((dto, entity, context) -> {
            NestedUserRegistrationStateDataField nestedUserRegistrationStateDataField = Optional.ofNullable(dto.getStateData())
                    .map(v -> this.readXmlValueFromString(v, AppUserRegistrationStateData.class))
                    .map(NestedUserRegistrationStateDataField::new)
                    .orElse(null);

            entity.setStateData(nestedUserRegistrationStateDataField);
            return entity;
        });
    }

    @Transactional(rollbackFor = Exception.class)
    public void sendInvitation(SendUserRegistrationInvitationVM viewModel) {
        validatorService.validate(viewModel);

        if(!getRegistrationConfiguration().getIsInvitationAllowed())
            throw new JavaMethodNotAllowedException("This method is not allowed for the current registration configuration");

        AppUserRegistration userRegistration = createNewUserRegistration(viewModel.getEmail(), EUserRegistrationState.INVITED);
        userRegistration.getStateData().getValue().setInvitationSendAt(CustomLocalDateTimeSerializer.parseDateToString(LocalDateTime.now()));

        userRegistration = saveEntity(userRegistration);

        sendEmailInvitation(viewModel, userRegistration);
    }

    @Transactional
    public void deleteAllEntriesConnectedToUser(AppUser appUser) {
        final List<AppUserRegistration> relevantEntries = new JPAQuery<>(em)
                .select(QAppUserRegistration.appUserRegistration)
                .from(QAppUserRegistration.appUserRegistration)
                .where(QAppUserRegistration.appUserRegistration.email.eq(appUser.getEmail()))
                .fetch();

        relevantEntries.forEach(this::deleteEntity);
    }

    private AppUserRegistration createNewUserRegistration(String email, EUserRegistrationState initialState) {
        AppUserRegistration userRegistration = new AppUserRegistration();
        userRegistration.setEmail(email);
        userRegistration.setWorkflow(ECoreRegistry.USER_REGISTRATION_WORKFLOW.getTypedValue(EUserRegistrationWorkflow.class));
        userRegistration.setState(initialState);
        userRegistration.setStateData(new NestedUserRegistrationStateDataField(new AppUserRegistrationStateData()));
        return userRegistration;
    }

    @SuppressWarnings("all")
    private void sendEmailInvitation(SendUserRegistrationInvitationVM viewModel, AppUserRegistration userRegistration) {

        SimpleOneLinkMailData config = new SimpleOneLinkMailData();
        config.setEmailToAddress(viewModel.getEmail());
        config.setEmailSubject(i18N.translate("Invitation to {appName}").add("appName", artifactInfo.getLongName()).build());
        config.setCaption(i18N.translate("Invitation").build());
        config.setMessage(viewModel.getMessage());
        config.setBtnText(i18N.translate("Sign Up").build());

        final String token = createNewRegistrationToken(ETokenType.REGISTRATION_INVITATION, userRegistration.getUuid());
        config.setBtnLink(frontendInfo.buildPathToFrontend("registerByInvitation", Tuple2.of("token", token)));

        emailTemplateService.sendBaseOneLinkMail(config);
    }

    public RegistrationConfiguration getRegistrationConfiguration() {
        return ECoreRegistry.USER_REGISTRATION_WORKFLOW.getTypedValue(EUserRegistrationWorkflow.class)
                .getRegistrationConfiguration();
    }

    public UserRegistrationWithInvitationVM getViewModelForRegistrationWithInvitation(String tokenValue) {

        final AppToken appToken = tokenService.findTokenByValue(tokenValue);
        if(!tokenService.isTokenValid(appToken, ETokenType.REGISTRATION_INVITATION)) {
            throw new InternalServerException("Token not valid");
        }

        final UUID userRegistrationUUID = appToken.getTypedPayload(TokenEntityReferencePayload.class).getEntityUUID();

        AppUserRegistration entityById = getEntityById(userRegistrationUUID);

        UserRegistrationWithInvitationVM viewModel = new UserRegistrationWithInvitationVM();
        viewModel.setEmail(entityById.getEmail());
        viewModel.setTokenValue(tokenValue);

        return viewModel;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateRegistrationByInvitation(UserRegistrationWithInvitationVM viewModel) {

        validatorService.validate(viewModel);

        RegistrationConfiguration registrationConfiguration = getRegistrationConfiguration();
        if(!registrationConfiguration.getIsRegistrationByInvitationAllowed())
            throw new JavaMethodNotAllowedException("Registration by invitation is not allowed for selected workflow");

        AppToken token = tokenService.findTokenByValue(viewModel.getTokenValue());
        tokenService.useToken(token, ETokenType.REGISTRATION_INVITATION);
        final UUID userRegistrationUUID = token.getTypedPayload(TokenEntityReferencePayload.class)
                .getEntityUUID();

        AppUserRegistration appUserRegistration = getEntityById(userRegistrationUUID);

        if(!appUserRegistration.getState().equals(EUserRegistrationState.INVITED))
            throw new InternalServerException(String.format(getRegistrationInWrongStateErrorMessage(EUserRegistrationState.INVITED, appUserRegistration.getState())));

        if(!appUserRegistration.getEmail().equals(viewModel.getEmail()))
            throw new InternalServerException(i18N.translate("Wrong email address for registration").build());

        appUserRegistration.setState(EUserRegistrationState.WAITING_FOR_EMAIL_CONFIRMATION);
        mapStateDataAfterRegistrationFormSubmit(appUserRegistration.getStateData().getValue(), viewModel);

        appUserRegistration = saveEntity(appUserRegistration);
        sendConfirmationEmail(appUserRegistration);
    }

    @Transactional(rollbackFor = Exception.class)
    public void createNewRegistrationFromScratch(UserRegistrationVM viewModel) {

        validatorService.validate(viewModel);

        RegistrationConfiguration registrationConfiguration = getRegistrationConfiguration();
        if(!registrationConfiguration.getIsRegistrationFromScratchAllowed())
            throw new JavaMethodNotAllowedException("Registration from scratch is not allowed for selected workflow");

        AppUserRegistration appUserRegistration = createNewUserRegistration(viewModel.getEmail(), EUserRegistrationState.WAITING_FOR_EMAIL_CONFIRMATION);
        mapStateDataAfterRegistrationFormSubmit(appUserRegistration.getStateData().getValue(), viewModel);

        appUserRegistration = saveEntity(appUserRegistration);
        sendConfirmationEmail(appUserRegistration);
    }

    private void mapStateDataAfterRegistrationFormSubmit(AppUserRegistrationStateData stateData, UserRegistrationVM viewModel) {
        stateData.setEmail(viewModel.getEmail());
        stateData.setFirstname(viewModel.getFirstname());
        stateData.setLastname(viewModel.getLastname());
        stateData.setEncryptedPassword(encryptPassword(viewModel.getPassword()));
    }

    private String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }

    private void sendConfirmationEmail(AppUserRegistration appUserRegistration) {

        SimpleOneLinkMailData config = new SimpleOneLinkMailData();
        config.setEmailToAddress(appUserRegistration.getEmail());
        config.setEmailSubject(i18N.translate("Confirm your account").build());
        config.setCaption(i18N.translate("Confirm your E-Mail address").build());
        config.setMessage(i18N.translate("To confirm your account, please click on the link below").build());
        config.setBtnText(i18N.translate("Confirm").build());

        final String token = createNewRegistrationToken(ETokenType.REGISTRATION_EMAIL_CONFIRMATION, appUserRegistration.getUuid());
        config.setBtnLink(frontendInfo.buildPathToFrontend("confirmEmail", Tuple2.of("token", token)));

        emailTemplateService.sendBaseOneLinkMail(config);
    }

    @Transactional(rollbackFor = Exception.class)
    public void confirmEmail(String tokenValue) {

        AppToken token = tokenService.findTokenByValue(tokenValue);
        final UUID userRegistrationUUID = token.getTypedPayload(TokenEntityReferencePayload.class)
                .getEntityUUID();

        tokenService.useToken(token, ETokenType.REGISTRATION_EMAIL_CONFIRMATION);

        RegistrationConfiguration registrationConfiguration = getRegistrationConfiguration();
        if(!registrationConfiguration.getIsEmailConfirmationAllowed())
            throw new JavaMethodNotAllowedException("E-Mail confirmation is not allowed for selected workflow");

        AppUserRegistration appUserRegistration = getEntityById(userRegistrationUUID);
        if(!appUserRegistration.getState().equals(EUserRegistrationState.WAITING_FOR_EMAIL_CONFIRMATION))
            throw new InternalServerException(String.format(getRegistrationInWrongStateErrorMessage(EUserRegistrationState.WAITING_FOR_EMAIL_CONFIRMATION, appUserRegistration.getState())));

        appUserRegistration.getStateData().getValue().setEmailConfirmedAt(CustomLocalDateTimeSerializer.parseDateToString(LocalDateTime.now()));

        if(registrationConfiguration.getIsAdminConfirmationRequired()) {
            appUserRegistration.setState(EUserRegistrationState.WAITING_FOR_ADMIN_CONFIRMATION);
            sendRequestForAdminConfirmationToAdmins(appUserRegistration);
        } else {
            createUserByRegistration(appUserRegistration);
            appUserRegistration.getStateData().getValue().setUserCreatedAt(CustomLocalDateTimeSerializer.parseDateToString(LocalDateTime.now()));
            appUserRegistration.setState(EUserRegistrationState.USER_CREATED);
        }
        saveEntity(appUserRegistration);
    }

    private void sendRequestForAdminConfirmationToAdmins(AppUserRegistration appUserRegistration) {
        final String frontendUrl = frontendInfo.buildPathToFrontend("/users/registrations/"+appUserRegistration.getUuid());
        for (AppUser appUser : actorRightService.findAllUsersWithRight(ECoreActorRight.CORE_REGISTRATION_ADMIN_CONFIRMATION)) {
            SimpleOneLinkMailData config = new SimpleOneLinkMailData();
            config.setEmailToAddress(appUser.getEmail());
            config.setEmailSubject(i18N.translate("Confirm registration request").build());
            config.setCaption(i18N.translate("Registration request for new user").build());
            config.setMessage(i18N.translate("Please confirm the request by clicking on the link below.").build());
            config.setBtnText(i18N.translate("To Request").build());

            config.setBtnLink(frontendUrl);
            emailTemplateService.sendBaseOneLinkMail(config);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void confirmByAdmin(UUID userRegistrationUUID) {

        AppUserRegistration appUserRegistration = getEntityById(userRegistrationUUID);
        if(!appUserRegistration.getState().equals(EUserRegistrationState.WAITING_FOR_ADMIN_CONFIRMATION))
            throw new InternalServerException(String.format(getRegistrationInWrongStateErrorMessage(EUserRegistrationState.WAITING_FOR_ADMIN_CONFIRMATION, appUserRegistration.getState())));

        appUserRegistration.getStateData().getValue().setAdminConfirmedAt(CustomLocalDateTimeSerializer.parseDateToString(LocalDateTime.now()));
        appUserRegistration.getStateData().getValue().setUserCreatedAt(CustomLocalDateTimeSerializer.parseDateToString(LocalDateTime.now()));
        createUserByRegistration(appUserRegistration);
        appUserRegistration.setState(EUserRegistrationState.USER_CREATED);

        saveEntity(appUserRegistration);
    }

    private void createUserByRegistration(AppUserRegistration appUserRegistration) {

        AppUserRegistrationStateData stateData = appUserRegistration.getStateData().getValue();

        AppUser appUser = new AppUser();
        appUser.setEmail(appUserRegistration.getEmail());
        appUser.setFirstname(stateData.getFirstname());
        appUser.setLastname(stateData.getLastname());
        appUser.setPassword(stateData.getEncryptedPassword());

        appUser.setUserAccessConfig(createNewUserAccessConfig());

        userCrudService.saveEntity(appUser);

        AppUserActor userActorByAppUser = actorService.getUserActorByAppUser(appUser);
        if(!Objects.equals(userActorByAppUser.getUser(), appUser)) {
            userActorByAppUser.setUser(appUser);
            actorService.connectUserWithActor(appUser);
            log.info(String.format("User is now connect to actor '%s'", userActorByAppUser.getUuid()));
        }


        sendUserCreatedEmailToUser(appUser);
        sendUserCreateInfoToAdmins(appUser);

    }

    private void sendUserCreatedEmailToUser(AppUser appUser) {
        SimpleOneLinkMailData config = new SimpleOneLinkMailData();
        config.setEmailToAddress(appUser.getEmail());
        config.setEmailSubject(i18N.translate("Your account is now created").build());
        config.setCaption(i18N.translate("Your account is now created").build());
        config.setMessage(i18N.translate("You can now login to the application.").build());
        config.setBtnText(i18N.translate("To {appName}").add("appName", artifactInfo.getLongName()).build());
        config.setBtnLink(frontendInfo.buildPathToFrontend(""));

        emailTemplateService.sendBaseOneLinkMail(config);
    }

    private void sendUserCreateInfoToAdmins(AppUser appUser) {

        for (AppUser adminUser : actorRightService.findAllUsersWithRight(ECoreActorRight.CORE_REGISTRATION_ADMIN_CONFIRMATION)) {
            SimpleOneLinkMailData config = new SimpleOneLinkMailData();
            config.setEmailToAddress(adminUser.getEmail());
            config.setEmailSubject(i18N.translate("New user account created").build());
            config.setCaption(i18N.translate("New user account created").build());
            config.setMessage(i18N.translate("The user with the E-Mail {email} has now an active account.").add("email", appUser.getEmail()).build());
            config.setBtnText(i18N.translate("To {appName}").add("appName", artifactInfo.getLongName()).build());
            config.setBtnLink(frontendInfo.buildPathToFrontend(""));

            emailTemplateService.sendBaseOneLinkMail(config);
        }

    }

    private AppUserAccessConfig createNewUserAccessConfig() {

        AppUserAccessConfig accessConfig = new AppUserAccessConfig();
        accessConfig.setIsAdmin(false);
        accessConfig.setMainRole(ECoreUserRoles.STANDARD_USER.getEntity());
        accessConfig.setRoles(Set.of(ECoreUserRoles.STANDARD_USER.getEntity()));
        accessConfig.setAdditionalRights(new HashSet<>());

        return accessConfig;
    }

    public String createNewRegistrationToken(ETokenType type, UUID entityUUID) {
        final String tokenValue = tokenService.createRandomTokenValue();
        final TokenEntityReferencePayload tokenEntityReferencePayload = new TokenEntityReferencePayload(entityUUID);
        final LocalDateTime expirationDate = LocalDateTime.now().plusDays(ECoreRegistry.USER_REGISTRATION_TOKEN_EXPIRES_IN_DAYS.getTypedValue(Integer.class));
        return tokenService.persistNewToken(tokenValue, type, tokenEntityReferencePayload, expirationDate).getTokenValue();
    }
    private String getRegistrationInWrongStateErrorMessage(EUserRegistrationState expectedState, EUserRegistrationState actualState) {
        return i18N.translate("Registration is in wrong state. Expected: {expected} Actual: {actual}").add("expected", expectedState).add("actual", actualState).build();
    }

    public SendUserRegistrationInvitationVM getRegistrationInvitationTemplate() {

        final String firstnameOfInvitor = ContextHolder.getContext().getCurrentActor().getActorFirstname().orElse(i18N.translate("someone").build());
        final String templateMsg = i18N.translate("Welcome, \n\n{firstnameOfInvitor} invited you to the core app. If you want to accept the invitation you can click on the button bellow.").add("firstnameOfInvitor", firstnameOfInvitor).build();

        SendUserRegistrationInvitationVM template = new SendUserRegistrationInvitationVM();
        template.setMessage(templateMsg);
        return template;
    }
}

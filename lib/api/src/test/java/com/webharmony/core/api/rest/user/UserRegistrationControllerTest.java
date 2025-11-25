package com.webharmony.core.api.rest.user;

import com.querydsl.jpa.impl.JPAQuery;
import com.webharmony.core.AbstractCrudApiTest;
import com.webharmony.core.api.rest.controller.user.UserRegistrationController;
import com.webharmony.core.api.rest.jackson.CustomLocalDateTimeSerializer;
import com.webharmony.core.api.rest.model.ConfirmEmailByTokenBody;
import com.webharmony.core.api.rest.model.UserRegistrationDto;
import com.webharmony.core.api.rest.model.utils.apiobject.GeneralApiResource;
import com.webharmony.core.api.rest.model.view.user.SendUserRegistrationInvitationVM;
import com.webharmony.core.api.rest.model.view.user.UserRegistrationVM;
import com.webharmony.core.api.rest.model.view.user.UserRegistrationWithInvitationVM;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.data.enums.ECoreRegistry;
import com.webharmony.core.data.jpa.model.AppRegistryItem;
import com.webharmony.core.data.jpa.model.token.AppToken;
import com.webharmony.core.data.jpa.model.token.ETokenType;
import com.webharmony.core.data.jpa.model.token.TokenEntityReferencePayload;
import com.webharmony.core.data.jpa.model.userregistration.AppUserRegistration;
import com.webharmony.core.data.jpa.model.userregistration.AppUserRegistrationStateData;
import com.webharmony.core.data.jpa.model.userregistration.NestedUserRegistrationStateDataField;
import com.webharmony.core.data.jpa.model.userregistration.QAppUserRegistration;
import com.webharmony.core.data.jpa.repository.TokenRepository;
import com.webharmony.core.service.data.RegistryItemService;
import com.webharmony.core.service.data.UserCrudService;
import com.webharmony.core.service.data.UserRegistrationService;
import com.webharmony.core.service.userregistration.EUserRegistrationState;
import com.webharmony.core.service.userregistration.EUserRegistrationWorkflow;
import com.webharmony.core.service.userregistration.RegistrationConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserRegistrationControllerTest extends AbstractCrudApiTest<UserRegistrationDto, UserRegistrationController> {

    @Autowired
    private UserRegistrationService userRegistrationService;

    @Autowired
    private RegistryItemService registryItemService;

    @Autowired
    private TokenRepository tokenRepository;


    @BeforeEach
    @Override
    protected void beforeEach(TestInfo testInfo) {
        super.beforeEach(testInfo);
        changeWorkflowConfiguration(EUserRegistrationWorkflow.INVITATION_REGISTRATION_EMAIL_CONFIRMATION);
    }

    private void changeWorkflowConfiguration(EUserRegistrationWorkflow workflow) {
        AppRegistryItem entity = ECoreRegistry.USER_REGISTRATION_WORKFLOW.getEntity();
        entity.setValue(workflow);
        registryItemService.saveEntity(entity);
    }

    @Test
    @Transactional
    void shouldBeAbleToUpdateEntry() {

        initTestElements();

        UUID uuidForTesting = getUUIDForTesting();
        UserRegistrationDto currentDto = getController().getEntryById(uuidForTesting).getData().getData();

        currentDto.setEmail("changed@example.com");
        currentDto.setState(EUserRegistrationState.USER_CREATED);
        currentDto.setWorkflow(EUserRegistrationWorkflow.REGISTRATION_EMAIL);

        final UserRegistrationDto updatedDto = getController().updateEntry(uuidForTesting, currentDto).getData().getData();

        currentDto.setEmail("user@example.com"); // email is read-only
        currentDto.setWorkflow(EUserRegistrationWorkflow.INVITATION_REGISTRATION_EMAIL_CONFIRMATION); // also read-only

        validateRegistration(updatedDto, currentDto);
        validateRegistration(getController().getEntryById(uuidForTesting).getData().getData(), currentDto);


    }

    @Test
    @Transactional
    void shouldBeAbleToDeleteEntry() {
        executeGenericDeleteResourceTest();
    }

    @Test
    void shouldBeAbleToSendInvitation() {

        SendUserRegistrationInvitationVM viewModel = new SendUserRegistrationInvitationVM();
        viewModel.setEmail("test@example.com");
        viewModel.setMessage("test message");

        assertOkResponse(c -> c.sendInvitation(viewModel));
    }

    @Test
    void shouldBeAbleToGetConfiguration() {
        RegistrationConfiguration registrationConfiguration = assertOkResponse(UserRegistrationController::getConfiguration);
        assertThat(registrationConfiguration.getIsInvitationAllowed()).isTrue();
        assertThat(registrationConfiguration.getIsRegistrationByInvitationAllowed()).isTrue();
        assertThat(registrationConfiguration.getIsRegistrationFromScratchAllowed()).isFalse();
        assertThat(registrationConfiguration.getIsEmailConfirmationAllowed()).isTrue();
        assertThat(registrationConfiguration.getIsAdminConfirmationRequired()).isTrue();

    }

    @Test
    void shouldBeAbleToCreateNewRegistrationFromScratch() {

        changeWorkflowConfiguration(EUserRegistrationWorkflow.REGISTRATION_EMAIL);

        UserRegistrationVM registrationVM = new UserRegistrationVM();
        registrationVM.setEmail("chris@example.com");
        registrationVM.setFirstname("my firstname");
        registrationVM.setLastname("my lastname");
        registrationVM.setPassword("*");
        registrationVM.setPasswordAgain("*");

        assertOkResponse(c -> c.createNewRegistrationFromScratch(registrationVM));

        AppUserRegistration entity = getUserRegistrationEntityByEmail("chris@example.com");
        assertThat(entity).isNotNull();
        assertThat(entity.getEmail()).isEqualTo(registrationVM.getEmail());
        assertThat(entity.getState()).isEqualTo(EUserRegistrationState.WAITING_FOR_EMAIL_CONFIRMATION);
        assertThat(entity.getWorkflow()).isEqualTo(EUserRegistrationWorkflow.REGISTRATION_EMAIL);

    }

    @Test
    @Transactional
    void shouldBeAbleToUpdateRegistrationByInvitation() {

        initTestElements();

        UserRegistrationWithInvitationVM registrationVM = new UserRegistrationWithInvitationVM();
        registrationVM.setEmail("user@example.com");
        registrationVM.setFirstname("my firstname");
        registrationVM.setLastname("my lastname");
        registrationVM.setPassword("*");
        registrationVM.setPasswordAgain("*");
        registrationVM.setTokenValue(getTokenByRegistrationUUID(getUUIDForTesting()).getTokenValue());

        assertOkResponse(c -> c.updateRegistrationByInvitation(registrationVM));

        AppUserRegistration entity = getUserRegistrationEntityByEmail("user@example.com");
        assertThat(entity).isNotNull();
        assertThat(entity.getState()).isEqualTo(EUserRegistrationState.WAITING_FOR_EMAIL_CONFIRMATION);
        assertThat(entity.getWorkflow()).isEqualTo(EUserRegistrationWorkflow.INVITATION_REGISTRATION_EMAIL_CONFIRMATION);
    }

    @Test
    @Transactional
    void shouldBeAbleToGetViewModelForRegistrationWithInvitation() {

        initTestElements();
        String tokenValue = getTokenByRegistrationUUID(getUUIDForTesting()).getTokenValue();

        UserRegistrationWithInvitationVM userRegistrationWithInvitationVM = assertOkResponse(c -> c.getViewModelForRegistrationWithInvitation(tokenValue));
        assertThat(userRegistrationWithInvitationVM).isNotNull();
        assertThat(userRegistrationWithInvitationVM.getTokenValue()).isEqualTo(tokenValue);
        assertThat(userRegistrationWithInvitationVM.getEmail()).isEqualTo("user@example.com");

    }

    @Test
    @Transactional
    void shouldBeAbleToConfirmEmailForRegistration() {
        initTestElements();
        AppUserRegistration entity = getUserRegistrationEntityByEmail("user@example.com");
        entity.setState(EUserRegistrationState.WAITING_FOR_EMAIL_CONFIRMATION);
        userRegistrationService.saveEntity(entity);


        String emailConfirmationToken = userRegistrationService.createNewRegistrationToken(ETokenType.REGISTRATION_EMAIL_CONFIRMATION, entity.getUuid());
        ConfirmEmailByTokenBody body = new ConfirmEmailByTokenBody();
        body.setToken(emailConfirmationToken);
        assertOkResponse(c -> c.confirmEmail(body));

        entity = getUserRegistrationEntityByEmail("user@example.com");
        assertThat(entity.getState()).isEqualTo(EUserRegistrationState.WAITING_FOR_ADMIN_CONFIRMATION);

    }

    @Test
    @Transactional
    void shouldBeAbleToSetConfirmedByAdmin() {

        initTestElements();
        AppUserRegistration entity = getUserRegistrationEntityByEmail("user@example.com");
        entity.setState(EUserRegistrationState.WAITING_FOR_ADMIN_CONFIRMATION);
        userRegistrationService.saveEntity(entity);

        assertOkResponse(c -> c.confirmByAdmin(entity.getUuid()));
        AppUserRegistration loadedEntity = getUserRegistrationEntityByEmail("user@example.com");
        assertThat(loadedEntity.getState()).isEqualTo(EUserRegistrationState.USER_CREATED);

        assertThat(ContextHolder.getContext().getBean(UserCrudService.class).getUserEntityByEmail("user@example.com")).isNotNull();
    }

    private AppToken getTokenByRegistrationUUID(UUID registrationUUID) {
        return tokenRepository.findAll()
                .stream()
                .filter(token -> token.getType().equals(ETokenType.REGISTRATION_INVITATION))
                .filter(token -> token.getTypedPayload(TokenEntityReferencePayload.class).getEntityUUID().equals(registrationUUID))
                .findAny()
                .orElseThrow();
    }

    private AppUserRegistration getUserRegistrationEntityByEmail(String email) {
        return new JPAQuery<>(em)
                .select(QAppUserRegistration.appUserRegistration)
                .from(QAppUserRegistration.appUserRegistration)
                .where(QAppUserRegistration.appUserRegistration.email.eq(email))
                .fetchOne();
    }

    private void validateRegistration(UserRegistrationDto newDto, UserRegistrationDto postDto) {
        assertThat(newDto.getId()).isEqualTo(postDto.getId());
        assertThat(newDto.getEmail()).isEqualTo(postDto.getEmail());
        assertThat(newDto.getState()).isEqualTo(postDto.getState());
        assertThat(newDto.getWorkflow()).isEqualTo(postDto.getWorkflow());
    }


    @Override
    protected void initTestElements() {

        AppUserRegistration appUserRegistration = new AppUserRegistration();
        appUserRegistration.setEmail("user@example.com");
        appUserRegistration.setState(EUserRegistrationState.INVITED);
        appUserRegistration.setWorkflow(EUserRegistrationWorkflow.INVITATION_REGISTRATION_EMAIL_CONFIRMATION);

        AppUserRegistrationStateData userStateData = new AppUserRegistrationStateData();
        userStateData.setInvitationSendAt(CustomLocalDateTimeSerializer.parseDateToString(LocalDateTime.now()));

        appUserRegistration.setStateData(new NestedUserRegistrationStateDataField(userStateData));

        AppUserRegistration createdEntity = userRegistrationService.saveEntity(appUserRegistration);

        userRegistrationService.createNewRegistrationToken(ETokenType.REGISTRATION_INVITATION, createdEntity.getUuid());
    }

    @Override
    protected int getExpectedSizeForGetAllApi() {
        return 1;
    }

    @Override
    protected void validateSelectedResourceData(UserRegistrationDto data) {
        assertThat(data.getEmail()).isEqualTo("user@example.com");
        assertThat(data.getState()).isEqualTo(EUserRegistrationState.INVITED);
        assertThat(data.getWorkflow()).isEqualTo(EUserRegistrationWorkflow.INVITATION_REGISTRATION_EMAIL_CONFIRMATION);
        assertThat(data.getStateData()).isNotEmpty();
    }

    @Override
    protected GeneralApiResource<Map<String, Object>> getSelectedGetAllApiResourceForTesting(List<GeneralApiResource<Map<String, Object>>> data) {
        return data.stream()
                .filter(e -> e.getData().get("email").equals("user@example.com"))
                .findAny()
                .orElseThrow();
    }

    @Override
    protected void validateGetAllDataEntry(String key, Object value) {

        if(key.equals("id")) {
            assertThat(value).isNotNull();
        }

        if(key.equals("email")) {
            assertThat(value).isEqualTo("user@example.com");
        }

        if(key.equals("state")) {
            assertThat(value).isEqualTo(EUserRegistrationState.INVITED.name());
        }

        if(key.equals("workflow")) {
            assertThat(value).isEqualTo(EUserRegistrationWorkflow.INVITATION_REGISTRATION_EMAIL_CONFIRMATION.name());
        }
    }

    @Override
    protected List<String> getExpectedGetAllDataKeys() {
        return List.of("id", "email", "state", "workflow", "stateData", "dtoType");
    }
}

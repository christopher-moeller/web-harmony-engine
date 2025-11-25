package com.webharmony.core.api.rest.controller.user;

import com.webharmony.core.api.rest.controller.ApiController;
import com.webharmony.core.api.rest.controller.utils.*;
import com.webharmony.core.api.rest.controller.utils.dispacher.ControllerDispatcher;
import com.webharmony.core.api.rest.controller.utils.dispacher.EntityCrudDispatcher;
import com.webharmony.core.api.rest.model.ConfirmEmailByTokenBody;
import com.webharmony.core.api.rest.model.ECoreRestResources;
import com.webharmony.core.api.rest.model.RestResourceInfo;
import com.webharmony.core.api.rest.model.UserRegistrationDto;
import com.webharmony.core.api.rest.model.utils.ResponseResource;
import com.webharmony.core.api.rest.model.view.user.SendUserRegistrationInvitationVM;
import com.webharmony.core.api.rest.model.view.user.UserRegistrationVM;
import com.webharmony.core.api.rest.model.view.user.UserRegistrationWithInvitationVM;
import com.webharmony.core.configuration.security.ApplicationRight;
import com.webharmony.core.data.enums.ECoreActorRight;
import com.webharmony.core.service.data.UserRegistrationService;
import com.webharmony.core.service.userregistration.RegistrationConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@ApiController("api/userRegistrations")
public class UserRegistrationController extends AbstractCrudController<UserRegistrationDto> {

    private final UserRegistrationService userRegistrationService;

    public UserRegistrationController(UserRegistrationService userRegistrationService) {
        this.userRegistrationService = userRegistrationService;
    }

    @Override
    @MethodNotAllowed
    public ResponseResource<UserRegistrationDto> createNewEntry(UserRegistrationDto dto) {
        return super.createNewEntry(dto);
    }

    @GetMapping("invitations/template")
    @ApiAuthentication(ApiAuthenticationType.PUBLIC)
    public ResponseEntity<SendUserRegistrationInvitationVM> getRegistrationInvitationTemplate() {
        return ResponseEntity.ok(userRegistrationService.getRegistrationInvitationTemplate());
    }

    @PostMapping("invitations")
    @CoreApiAuthorization(ECoreActorRight.CORE_REGISTRATION_SEND_INVITATIONS)
    public ResponseEntity<Void> sendInvitation(@RequestBody SendUserRegistrationInvitationVM viewModel) {
        userRegistrationService.sendInvitation(viewModel);
        return ResponseEntity.ok().build();
    }

    @GetMapping("configuration")
    @ApiAuthentication(ApiAuthenticationType.PUBLIC)
    public ResponseEntity<RegistrationConfiguration> getConfiguration() {
        return ResponseEntity.ok(userRegistrationService.getRegistrationConfiguration());
    }

    @PostMapping("fromScratch")
    @ApiAuthentication(ApiAuthenticationType.PUBLIC)
    public ResponseEntity<Void> createNewRegistrationFromScratch(@RequestBody UserRegistrationVM viewModel) {
        this.userRegistrationService.createNewRegistrationFromScratch(viewModel);
        return ResponseEntity.ok().build();
    }

    @PostMapping("byInvitation")
    @ApiAuthentication(ApiAuthenticationType.PUBLIC)
    public ResponseEntity<Void> updateRegistrationByInvitation(@RequestBody UserRegistrationWithInvitationVM viewModel) {
        this.userRegistrationService.updateRegistrationByInvitation(viewModel);
        return ResponseEntity.ok().build();
    }

    @GetMapping("registrationByInvitationVM/{tokenValue}")
    @ApiAuthentication(ApiAuthenticationType.PUBLIC)
    public ResponseEntity<UserRegistrationWithInvitationVM> getViewModelForRegistrationWithInvitation(@PathVariable("tokenValue") String tokenValue) {
        return ResponseEntity.ok(userRegistrationService.getViewModelForRegistrationWithInvitation(tokenValue));
    }

    @PostMapping("confirmEmail")
    @ApiAuthentication(ApiAuthenticationType.PUBLIC)
    public ResponseEntity<Void> confirmEmail(@RequestBody ConfirmEmailByTokenBody body) {
        this.userRegistrationService.confirmEmail(body.getToken());
        return ResponseEntity.ok().build();
    }

    @PostMapping("confirmByAdmin/{userRegistrationUUID}")
    @CoreApiAuthorization(ECoreActorRight.CORE_REGISTRATION_ADMIN_CONFIRMATION)
    public ResponseEntity<Void> confirmByAdmin(@PathVariable("userRegistrationUUID") UUID userRegistrationUUID) {
        this.userRegistrationService.confirmByAdmin(userRegistrationUUID);
        return ResponseEntity.ok().build();
    }

    @Override
    public RestResourceInfo getRestResource() {
        return ECoreRestResources.USER_REGISTRATIONS;
    }

    @Override
    protected ControllerDispatcher<UserRegistrationDto> loadControllerDispatcherInternal() {
        return new EntityCrudDispatcher<>(UserRegistrationService.class, this);
    }

    @Override
    protected ApplicationRight getGlobalRightForCrudMethods() {
        return ECoreActorRight.CORE_REGISTRATION_CRUD;
    }
}

package com.webharmony.core.api.rest.controller.user;

import com.webharmony.core.api.rest.controller.ApiController;
import com.webharmony.core.api.rest.controller.utils.*;
import com.webharmony.core.api.rest.controller.utils.dispacher.ControllerDispatcher;
import com.webharmony.core.api.rest.controller.utils.dispacher.EntityCrudDispatcher;
import com.webharmony.core.api.rest.model.ECoreRestResources;
import com.webharmony.core.api.rest.model.RestResourceInfo;
import com.webharmony.core.api.rest.model.UserDto;
import com.webharmony.core.api.rest.model.utils.ResponseResource;
import com.webharmony.core.api.rest.model.view.user.*;
import com.webharmony.core.data.enums.ECoreActorRight;
import com.webharmony.core.configuration.security.ApplicationRight;
import com.webharmony.core.service.data.UserCrudService;
import com.webharmony.core.service.searchcontainer.AbstractSearchContainer;
import com.webharmony.core.service.searchcontainer.UserSearchContainer;
import com.webharmony.core.utils.exceptions.MethodNotAllowedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@ApiController("api/users")
public class UserController extends AbstractCrudController<UserDto> {

    private final UserSearchContainer userSearchContainer;

    private final UserCrudService userCrudService;

    public UserController(UserSearchContainer userSearchContainer, UserCrudService userCrudService) {
        this.userSearchContainer = userSearchContainer;
        this.userCrudService = userCrudService;
    }

    @Override
    @MethodNotAllowed
    public ResponseResource<UserDto> createNewEntry(UserDto dto) {
        throw new MethodNotAllowedException();
    }

    @GetMapping("personalAccount")
    @CoreApiAuthorization(ECoreActorRight.CORE_USER_PERSONAL_ACCOUNT_CRUD)
    public ResponseEntity<UserPersonalAccountVM> getPersonalAccount() {
        return ResponseEntity.ok(userCrudService.getPersonalAccount());
    }

    @CoreApiAuthorization(ECoreActorRight.CORE_USER_PERSONAL_ACCOUNT_CRUD)
    @PostMapping("personalAccount")
    public ResponseEntity<UserPersonalAccountVM> savePersonalAccount(@RequestBody UserPersonalAccountVM requestBody) {
        return ResponseEntity.ok(userCrudService.updatePersonalUserAccount(requestBody));
    }

    @CoreApiAuthorization(ECoreActorRight.CORE_USER_PERSONAL_ACCOUNT_CRUD)
    @PostMapping("personalAccount/changePassword")
    public ResponseEntity<Void> changeUserPassword(@RequestBody UserPersonalAccountChangePasswordVM requestBody) {
        userCrudService.updateUserPassword(requestBody);
        return ResponseEntity.ok().build();
    }

    @ApiAuthentication(ApiAuthenticationType.PUBLIC)
    @PostMapping("resetPassword")
    public ResponseEntity<Void> resetPassword(@RequestBody UserPersonalAccountForgotPasswordVM requestBody) {
        userCrudService.requestNewPassword(requestBody);
        return ResponseEntity.ok().build();
    }

    @CoreApiAuthorization(ECoreActorRight.CORE_USER_PERSONAL_ACCOUNT_CRUD)
    @DeleteMapping("personalAccount")
    public ResponseEntity<Void> deleteAccount(@RequestBody UserPersonalAccountDeleteVM deleteAccountVM) {
        userCrudService.deletePersonalUserAccount(deleteAccountVM);
        return ResponseEntity.ok().build();
    }

    @GetMapping("resetPasswordByEmailVM/{tokenValue}")
    @ApiAuthentication(ApiAuthenticationType.PUBLIC)
    public ResponseEntity<UserPersonalAccountResetPasswordByEmailVM> getViewModelForResetPasswordByEmail(@PathVariable("tokenValue") String tokenValue) {
        return ResponseEntity.ok(userCrudService.getViewModelForResetPasswordByEmail(tokenValue));
    }

    @PostMapping("resetPasswordByEmailVM")
    @ApiAuthentication(ApiAuthenticationType.PUBLIC)
    public ResponseEntity<Void> resetPasswordByEmailVM(@RequestBody UserPersonalAccountResetPasswordByEmailVM viewModel) {
        userCrudService.resetPassword(viewModel);
        return ResponseEntity.ok().build();
    }

    @Override
    public RestResourceInfo getRestResource() {
        return ECoreRestResources.USERS;
    }

    @Override
    protected ControllerDispatcher<UserDto> loadControllerDispatcherInternal() {
        return new EntityCrudDispatcher<>(UserCrudService.class, this);
    }

    @Override
    public AbstractSearchContainer getSearchContainer() {
        return userSearchContainer;
    }

    @Override
    protected ApplicationRight getGlobalRightForCrudMethods() {
        return ECoreActorRight.CORE_USERS_CRUD;
    }
}

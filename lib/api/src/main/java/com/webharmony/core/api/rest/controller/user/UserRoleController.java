package com.webharmony.core.api.rest.controller.user;

import com.webharmony.core.api.rest.controller.ApiController;
import com.webharmony.core.api.rest.controller.utils.AbstractCrudController;
import com.webharmony.core.api.rest.controller.utils.dispacher.ControllerDispatcher;
import com.webharmony.core.api.rest.controller.utils.dispacher.EntityCrudDispatcher;
import com.webharmony.core.api.rest.model.ECoreRestResources;
import com.webharmony.core.api.rest.model.RestResourceInfo;
import com.webharmony.core.api.rest.model.UserRoleDto;
import com.webharmony.core.data.enums.ECoreActorRight;
import com.webharmony.core.configuration.security.ApplicationRight;
import com.webharmony.core.service.data.UserRoleService;
import com.webharmony.core.service.searchcontainer.AbstractSearchContainer;
import com.webharmony.core.service.searchcontainer.UserRoleSearchContainer;

@ApiController("api/userRoles")
public class UserRoleController extends AbstractCrudController<UserRoleDto> {

    private final UserRoleSearchContainer userRoleSearchContainer;

    public UserRoleController(UserRoleSearchContainer userRoleSearchContainer) {
        this.userRoleSearchContainer = userRoleSearchContainer;
    }

    @Override
    public RestResourceInfo getRestResource() {
        return ECoreRestResources.ROLES;
    }

    @Override
    protected ControllerDispatcher<UserRoleDto> loadControllerDispatcherInternal() {
        return new EntityCrudDispatcher<>(UserRoleService.class, this);
    }

    @Override
    public AbstractSearchContainer getSearchContainer() {
        return userRoleSearchContainer;
    }

    @Override
    protected ApplicationRight getGlobalRightForCrudMethods() {
        return ECoreActorRight.CORE_USER_ROLES_CRUD;
    }
}

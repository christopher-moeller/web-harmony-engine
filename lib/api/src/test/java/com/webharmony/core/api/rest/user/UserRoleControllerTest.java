package com.webharmony.core.api.rest.user;

import com.webharmony.core.AbstractCrudApiTest;
import com.webharmony.core.api.rest.controller.user.UserRoleController;
import com.webharmony.core.api.rest.model.UserRoleDto;
import com.webharmony.core.api.rest.model.utils.apiobject.ApiResource;
import com.webharmony.core.api.rest.model.utils.apiobject.GeneralApiResource;
import com.webharmony.core.data.enums.ECoreActorRight;
import com.webharmony.core.data.enums.ECoreUserRoles;
import com.webharmony.core.data.jpa.model.user.AppUserRole;
import com.webharmony.core.i18n.I18N;
import com.webharmony.core.utils.exceptions.InternalServerException;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class UserRoleControllerTest extends AbstractCrudApiTest<UserRoleDto, UserRoleController> {

    @Test
    void shouldBeAbleToCreateNewEntry() {

        UserRoleDto userRole = new UserRoleDto();
        userRole.setLabel("new UserRole");
        userRole.setDescription("new UserRole description");
        userRole.setIncludedRights(List.of(new ApiResource<>(ECoreActorRight.CORE_USER_ROLES_CRUD.getEntity().getUuid())));

        ApiResource<UserRoleDto> newCreatedRole = getController().createNewEntry(userRole).getData();

        validateUserRole(newCreatedRole.getData(), userRole);
        validateUserRole(getController().getEntryById(newCreatedRole.getPrimaryKeyAsUUID()).getData().getData(), userRole);

    }

    @Test
    void shouldBeAbleToUpdateEntry() {

        ApiResource<UserRoleDto> currentResource = getController().getEntryById(ECoreUserRoles.ADMIN_USER.getEntity().getUuid()).getData();
        UserRoleDto currentUserRole = currentResource.getData();

        currentUserRole.setLabel("updated Label");
        currentUserRole.setDescription("updated Description");

        validateUserRole(getController().updateEntry(currentResource.getPrimaryKeyAsUUID(), currentUserRole).getData().getData(), currentUserRole);
        validateUserRole(getController().getEntryById(currentResource.getPrimaryKeyAsUUID()).getData().getData(), currentUserRole);

    }

    @Test
    @Transactional
    void shouldNotBeAbleToDeleteEntry() {
        Assert.assertThrows(InternalServerException.class, this::executeGenericDeleteResourceTest);
    }

    private void validateUserRole(UserRoleDto newUserRole, UserRoleDto postUserRole) {
        assertThat(newUserRole.getId()).isNotNull();
        assertThat(newUserRole.getLabel()).isEqualTo(postUserRole.getLabel());
        assertThat(newUserRole.getDescription()).isEqualTo(postUserRole.getDescription());
        assertThat(newUserRole.getIncludedRights()).hasSameSizeAs(postUserRole.getIncludedRights());

    }

    @Override
    protected void initTestElements() {
        // nothing to do here
    }

    @Override
    protected int getExpectedSizeForGetAllApi() {
        return ECoreUserRoles.values().length;
    }

    @Override
    protected void validateSelectedResourceData(UserRoleDto data) {
        assertThat(data.getLabel()).isEqualTo("Admin User");
        assertThat(data.getDescription()).isEqualTo("Contains all rights for admin Users. If a user is marked as admin, he don't need this role, because the flag indicates that the user is a technical admin, which automatically has all rights of the application.'");
        assertThat(data.getIncludedRights()).isEmpty();
        assertThat(data.getId()).isNotNull();
    }

    @Override
    protected GeneralApiResource<Map<String, Object>> getSelectedGetAllApiResourceForTesting(List<GeneralApiResource<Map<String, Object>>> data) {
        AppUserRole entity = ECoreUserRoles.ADMIN_USER.getEntity();
        return data.stream()
                .filter(e -> e.getData().get("label").equals(entity.getLabel().getValueByLanguage(I18N.CODING_LANGUAGE).orElseThrow()))
                .findAny()
                .orElseThrow();
    }

    @Override
    protected void validateGetAllDataEntry(String key, Object value) {

        if(key.equals("id")) {
            assertThat(value).isNotNull();
        }

        if(key.equals("label")) {
            assertThat(value).isEqualTo("Admin User");
        }

        if(key.equals("description")) {
            assertThat(value).isEqualTo("Contains all rights for admin Users. If a user is marked as admin, he don't need this role, because the flag indicates that the user is a technical admin, which automatically has all rights of the application.'");
        }

        if(key.equals("includedRights")) {
            assertThat(value).isEqualTo(0);
        }
    }

    @Override
    protected List<String> getExpectedGetAllDataKeys() {
        return List.of("id", "label", "description", "rightsCount");
    }
}

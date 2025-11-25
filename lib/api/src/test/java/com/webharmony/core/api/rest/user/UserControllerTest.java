package com.webharmony.core.api.rest.user;

import com.webharmony.core.AbstractCrudApiTest;
import com.webharmony.core.api.rest.controller.user.UserController;
import com.webharmony.core.api.rest.model.UserAccessDto;
import com.webharmony.core.api.rest.model.UserDto;
import com.webharmony.core.api.rest.model.utils.ResponseResource;
import com.webharmony.core.api.rest.model.utils.apiobject.ApiResource;
import com.webharmony.core.api.rest.model.utils.apiobject.GeneralApiResource;
import com.webharmony.core.data.jpa.model.user.AppUser;
import com.webharmony.core.testutils.ETestUser;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserControllerTest extends AbstractCrudApiTest<UserDto, UserController> {

    @Test
    void shouldBeAbleToUpdateEntry() {
        UUID uuidForTesting = getUUIDForTesting();

        UserDto userToUpdate = getController().getEntryById(uuidForTesting).getData().getData();
        userToUpdate.setEmail("updated@mail.com");
        userToUpdate.setFirstname("updated firstname");
        userToUpdate.setLastname("updated lastname");
        userToUpdate.getUserAccessConfig().setIsAdmin(false);


        ResponseResource<UserDto> userDtoResponseResource = getController().updateEntry(uuidForTesting, userToUpdate);
        validateUserDto(userDtoResponseResource.getData().getData(), userToUpdate);

        validateUserDto(getController().getEntryById(uuidForTesting).getData().getData(), userToUpdate);
    }

    private void validateUserDto(UserDto newDto, UserDto postUserDto) {
        assertThat(newDto.getId()).isNotNull();
        assertThat(newDto.getFirstname()).isEqualTo(postUserDto.getFirstname());
        assertThat(newDto.getLastname()).isEqualTo(postUserDto.getLastname());
        assertThat(newDto.getEmail()).isEqualTo(postUserDto.getEmail());
        assertThat(newDto.getUserAccessConfig()).isNotNull();

        UserAccessDto newUserAccessConfig = newDto.getUserAccessConfig();
        UserAccessDto postUserAccessConfig = postUserDto.getUserAccessConfig();

        assertThat(newUserAccessConfig.getIsAdmin()).isEqualTo(postUserAccessConfig.getIsAdmin());
        assertThat(newUserAccessConfig.getRoles()).hasSameSizeAs(postUserAccessConfig.getRoles());
        for (ApiResource<?> role : newUserAccessConfig.getRoles()) {
            assertThat(postUserAccessConfig.getRoles()).anyMatch(r -> r.getPrimaryKey().equals(role.getPrimaryKey()));
        }

        assertThat(newUserAccessConfig.getMainRole().getPrimaryKey()).isEqualTo(postUserAccessConfig.getMainRole().getPrimaryKey());
    }

    @Test
    @Transactional
    void shouldBeAbleToDeleteEntry() {
        executeGenericDeleteResourceTest();
    }



    @Override
    protected void initTestElements() {
        // nothing to do here
    }

    @Override
    protected int getExpectedSizeForGetAllApi() {
        return ETestUser.values().length + 1;
    }

    @Override
    protected void validateSelectedResourceData(UserDto data) {
        AppUser testAdmin = ETestUser.ADMIN_USER.loadUser();
        assertThat(data.getId()).isEqualTo(testAdmin.getUuid().toString());
        assertThat(data.getFirstname()).isEqualTo(testAdmin.getFirstname());
        assertThat(data.getLastname()).isEqualTo(testAdmin.getLastname());
        assertThat(data.getUserAccessConfig()).isNotNull();

        UserAccessDto userAccessConfig = data.getUserAccessConfig();
        assertThat(userAccessConfig.getIsAdmin()).isTrue();

    }

    @Override
    protected GeneralApiResource<Map<String, Object>> getSelectedGetAllApiResourceForTesting(List<GeneralApiResource<Map<String, Object>>> data) {
        AppUser testAdmin = ETestUser.ADMIN_USER.loadUser();
        return data.stream().filter(e -> e.getPrimaryKeyAsUUID().equals(testAdmin.getUuid()))
                .findAny()
                .orElseThrow();
    }

    @Override
    protected void validateGetAllDataEntry(String key, Object value) {
        if(key.equals("id")) {
            assertThat(value).isNotNull();
        }

        if(key.equals("firstname")) {
            assertThat(value).isEqualTo("Firstname of admin@webharmony.de");
        }

        if(key.equals("lastname")) {
            assertThat(value).isEqualTo("Lastname of admin@webharmony.de");
        }
    }

    @Override
    protected List<String> getExpectedGetAllDataKeys() {
        return List.of("email", "firstname", "id", "lastname");
    }
}

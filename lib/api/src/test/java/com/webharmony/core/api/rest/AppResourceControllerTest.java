package com.webharmony.core.api.rest;

import com.webharmony.core.AbstractApiTest;
import com.webharmony.core.api.rest.controller.AppResourceController;
import com.webharmony.core.api.rest.model.UserDto;
import com.webharmony.core.api.rest.model.utils.AbstractResourceDto;
import com.webharmony.core.api.rest.model.utils.ComplexTypeSchema;
import com.webharmony.core.api.rest.model.utils.EJsonType;
import com.webharmony.core.api.rest.model.utils.ResourceOverviewTypeSchema;
import com.webharmony.core.api.rest.model.utils.validinput.TextInputSpecification;
import com.webharmony.core.service.appresources.utils.CrudResourceInfoDto;
import com.webharmony.core.service.appresources.utils.CrudResourceInfoWithOverview;
import com.webharmony.core.service.appresources.utils.ResourcePageableOptions;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.assertj.core.api.Assertions.assertThat;

class AppResourceControllerTest extends AbstractApiTest<AppResourceController> {

    @Test
    void shouldGetUsersResource() {
        CrudResourceInfoDto usersInfo = assertOkResponse(c -> c.getResourceByName("users"));
        assertThat(usersInfo).isInstanceOf(CrudResourceInfoWithOverview.class);

        CrudResourceInfoWithOverview usersWithOverviewInfo = (CrudResourceInfoWithOverview) usersInfo;

        ResourceOverviewTypeSchema resourceOverviewTypeSchema = usersWithOverviewInfo.getResourceOverviewTypeSchema();
        assertThat(resourceOverviewTypeSchema.getFields())
                .containsKey("id")
                .containsKey("firstname")
                .containsKey("lastname")
                .containsKey("email");

        assertThat(resourceOverviewTypeSchema.getSortOptions())
                .contains("id")
                .contains("firstname")
                .contains("lastname")
                .contains("email");

        assertThat(resourceOverviewTypeSchema.getFilters())
                .filteredOn(r -> r.getFilterName().equals("search"))
                .isNotEmpty();

        ComplexTypeSchema complexTypeSchema = usersWithOverviewInfo.getComplexTypeSchema();
        assertThat(complexTypeSchema.getFields())
                .containsKey("id")
                .containsKey("firstname")
                .containsKey("lastname")
                .containsKey("email")
                .containsKey("userAccessConfig");

        assertThat(complexTypeSchema.getSimpleType()).isEqualTo(UserDto.class.getSimpleName());
        assertThat(complexTypeSchema.getJavaType()).isEqualTo(UserDto.class.getName());
        assertThat(complexTypeSchema.getJsonType()).isEqualTo(EJsonType.OBJECT.name());

        assertThat(complexTypeSchema.getValidInputSpecification()).isNull();
        assertThat(complexTypeSchema.getFields().get("firstname").getValidInputSpecification()).isInstanceOf(TextInputSpecification.class);

        validateApiLink(usersWithOverviewInfo.getGetAllLink(), RequestMethod.GET, "/api/users");
        validateApiLink(usersWithOverviewInfo.getGetByIdLink(), RequestMethod.GET, "/api/users/{id}");
        assertThat(usersWithOverviewInfo.getCreateNewLink()).isNull();
        validateApiLink(usersWithOverviewInfo.getUpdateLink(), RequestMethod.PUT, "/api/users/{id}");
        validateApiLink(usersWithOverviewInfo.getDeleteLink(), RequestMethod.DELETE, "/api/users/{id}");
        validateApiLink(usersWithOverviewInfo.getGetTemplateLink(), RequestMethod.GET, "/api/appresources/users/template");

        ResourcePageableOptions pageableOptions = usersWithOverviewInfo.getPageableOptions();
        assertThat(pageableOptions.getIsUnPagedRequestAllowed()).isFalse();
        assertThat(pageableOptions.getSuggestedPageOptions()).hasSize(5);
        assertThat(pageableOptions.getHighestPageSize()).isEqualTo(50);

        assertThat(usersWithOverviewInfo.getName()).isEqualTo("users");
    }

    @Test
    void shouldGetInitialTemplateForUsers() {
        AbstractResourceDto resourceDto = assertOkResponse(c -> c.getInitialResourceTemplate("users"));
        assertThat(resourceDto).isNotNull()
                .isInstanceOf(UserDto.class);

        UserDto userDto = (UserDto) resourceDto;

        assertThat(userDto.getEmail()).isNull();
        assertThat(userDto.getFirstname()).isNull();
        assertThat(userDto.getLastname()).isNull();
        assertThat(userDto.getId()).isNull();
        assertThat(userDto.getUserAccessConfig()).isNotNull();
        assertThat(userDto.getUserAccessConfig().getIsAdmin()).isFalse();
    }
}

package com.webharmony.core.api.rest;

import com.webharmony.core.AbstractApiTest;
import com.webharmony.core.api.rest.controller.AppViewModelController;
import com.webharmony.core.api.rest.model.utils.ComplexTypeSchema;
import com.webharmony.core.api.rest.model.utils.EJsonType;
import com.webharmony.core.api.rest.model.utils.ViewModel;
import com.webharmony.core.api.rest.model.view.user.UserLoginVM;
import com.webharmony.core.service.appviewmodels.ViewModelInfoDto;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.assertj.core.api.Assertions.assertThat;

class AppViewModelControllerTest extends AbstractApiTest<AppViewModelController> {

    @Test
    void shouldGetUserLoginViewModel() {
        ViewModelInfoDto userLoginVM = assertOkResponse(c -> c.getViewModelByName("UserLoginVM"));
        assertThat(userLoginVM).isNotNull();

        ComplexTypeSchema schema = userLoginVM.getSchema();
        assertThat(schema.getFields())
                .containsKey("username")
                .containsKey("password");

        assertThat(schema.getSimpleType()).isEqualTo(UserLoginVM.class.getSimpleName());
        assertThat(schema.getJavaType()).isEqualTo(UserLoginVM.class.getName());
        assertThat(schema.getJsonType()).isEqualTo(EJsonType.OBJECT.name());
        assertThat(schema.getValidInputSpecification()).isNull();

        validateApiLink(userLoginVM.getLoadLink(), RequestMethod.GET, "/api/appviewmodels/UserLoginVM/template");
        validateApiLink(userLoginVM.getSaveLink(), RequestMethod.POST, "/api/authentication/userLogin");
    }

    @Test
    void shouldGetInitialVMTemplateForUsers() {
        ViewModel viewModel = assertOkResponse(c -> c.getInitialViewModelTemplate("UserLoginVM"));
        assertThat(viewModel).isNotNull()
                .isInstanceOf(UserLoginVM.class);

        UserLoginVM userLoginVM = (UserLoginVM) viewModel;
        assertThat(userLoginVM.getUsername()).isNull();
        assertThat(userLoginVM.getPassword()).isNull();
    }
}

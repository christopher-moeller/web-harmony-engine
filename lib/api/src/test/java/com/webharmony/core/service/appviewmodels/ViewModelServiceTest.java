package com.webharmony.core.service.appviewmodels;

import com.webharmony.core.AbstractBaseTest;
import com.webharmony.core.utils.reflection.ApiLink;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.assertj.core.api.Assertions.assertThat;

class ViewModelServiceTest extends AbstractBaseTest {

    @Autowired
    private ViewModelService viewModelService;

    @Test
    void shouldGetCorrectApiLinkForValidationRuleConfigurationModel() {
        ViewModelInfoDto validationRuleConfigurationModel = viewModelService.getViewModelByName("UserLoginVM");
        ApiLink loadLink = validationRuleConfigurationModel.getLoadLink();
        assertThat(loadLink.getRequestMethod()).isEqualTo(RequestMethod.GET);
        assertThat(loadLink.getLink()).isEqualTo("/api/appviewmodels/UserLoginVM/template");
        assertThat(loadLink.getPlaceholders()).isEmpty();
    }

}

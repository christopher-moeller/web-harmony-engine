package com.webharmony.core.api.rest.model.view;

import com.webharmony.core.api.rest.model.utils.ViewModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidationRuleConfigurationModel implements ViewModel {

    private String rootType;
    private String path;
    private String validationRuleName;

    private Boolean alwaysReturnInvalid = false;
    private Boolean isActive = true;

}

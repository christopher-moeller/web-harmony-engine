package com.webharmony.core.api.rest.model;

import com.webharmony.core.api.rest.model.utils.AbstractResourceDto;
import com.webharmony.core.api.rest.model.utils.annotations.ReadOnlyAttribute;
import com.webharmony.core.api.rest.validation.UserRegistrationValidation;
import com.webharmony.core.service.data.validation.utils.Validation;
import com.webharmony.core.service.userregistration.EUserRegistrationState;
import com.webharmony.core.service.userregistration.EUserRegistrationWorkflow;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Validation(UserRegistrationValidation.class)
public class UserRegistrationDto extends AbstractResourceDto {

    @ReadOnlyAttribute
    private String email;
    private EUserRegistrationState state;
    @ReadOnlyAttribute
    private EUserRegistrationWorkflow workflow;
    private String stateData;
}

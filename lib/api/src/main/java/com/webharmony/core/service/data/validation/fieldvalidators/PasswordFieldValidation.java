package com.webharmony.core.service.data.validation.fieldvalidators;

import com.webharmony.core.data.enums.ECoreRegistry;
import com.webharmony.core.service.data.validation.fieldvalidators.utils.EPasswordValidationType;
import com.webharmony.core.service.data.validation.utils.NamedValidationInterface;
import com.webharmony.core.service.data.validation.utils.ValidationContext;

public class PasswordFieldValidation<R> implements NamedValidationInterface<String, R> {

    public static final String NAME = "PASSWORD_FIELD_VALIDATOR";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void validate(String password, ValidationContext<R> validationContext) {
        ECoreRegistry.PASSWORD_VALIDATION_TYPE.getTypedValue(EPasswordValidationType.class).validate(password, validationContext);
    }

}

package com.webharmony.core.service.data.validation.fieldvalidators.utils;

import com.webharmony.core.service.data.validation.utils.ValidationContext;

public enum EPasswordValidationType {

    NOT_EMPTY(PasswordValidationMethods.getInstance()::validateNotEmpty),
    COMPLEX_1(PasswordValidationMethods.getInstance()::validateComplex1);

    private final PasswordValidationInterface passwordValidationInterface;

    EPasswordValidationType(PasswordValidationInterface passwordValidationInterface) {
        this.passwordValidationInterface = passwordValidationInterface;
    }

    public void validate(String password, ValidationContext<?> validationContext) {
        this.passwordValidationInterface.validate(password, validationContext);
    }


    private interface PasswordValidationInterface {

        void validate(String password, ValidationContext<?> validationContext);
    }
}

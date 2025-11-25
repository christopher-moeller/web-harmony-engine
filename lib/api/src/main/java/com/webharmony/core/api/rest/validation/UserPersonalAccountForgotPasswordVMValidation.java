package com.webharmony.core.api.rest.validation;

import com.webharmony.core.api.rest.model.view.user.UserPersonalAccountForgotPasswordVM;
import com.webharmony.core.service.data.validation.fieldvalidators.EmailFieldValidator;
import com.webharmony.core.service.data.validation.fieldvalidators.NotEmptyTextFieldValidator;
import com.webharmony.core.service.data.validation.utils.ValidationBuilder;
import com.webharmony.core.service.data.validation.utils.ValidationConfigBuilder;

public class UserPersonalAccountForgotPasswordVMValidation implements ValidationConfigBuilder<UserPersonalAccountForgotPasswordVM> {


    @Override
    public void configureValidationBuilder(ValidationBuilder<UserPersonalAccountForgotPasswordVM, UserPersonalAccountForgotPasswordVM, ?, ? extends ValidationBuilder<UserPersonalAccountForgotPasswordVM, ?, ?, ?>> builder) {
        builder.ofField(UserPersonalAccountForgotPasswordVM::getEmail).withValidation(new NotEmptyTextFieldValidator<>(), new EmailFieldValidator<>());
    }

}

package com.webharmony.core.api.rest.validation;

import com.webharmony.core.api.rest.model.view.user.UserPersonalAccountVM;
import com.webharmony.core.service.data.validation.fieldvalidators.EmailFieldValidator;
import com.webharmony.core.service.data.validation.fieldvalidators.NotEmptyTextFieldValidator;
import com.webharmony.core.service.data.validation.utils.ValidationBuilder;
import com.webharmony.core.service.data.validation.utils.ValidationConfigBuilder;

public class UserPersonalAccountVMValidation implements ValidationConfigBuilder<UserPersonalAccountVM> {

    @Override
    public void configureValidationBuilder(ValidationBuilder<UserPersonalAccountVM, UserPersonalAccountVM, ?, ? extends ValidationBuilder<UserPersonalAccountVM, ?, ?, ?>> builder) {
        builder.ofField(UserPersonalAccountVM::getEmail).withValidation(new NotEmptyTextFieldValidator<>(), new EmailFieldValidator<>());
        builder.ofField(UserPersonalAccountVM::getFirstname).withValidation(new NotEmptyTextFieldValidator<>());
        builder.ofField(UserPersonalAccountVM::getLastname).withValidation(new NotEmptyTextFieldValidator<>());
    }
}

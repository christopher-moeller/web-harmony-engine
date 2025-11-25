package com.webharmony.core.api.rest.validation;

import com.webharmony.core.api.rest.model.view.user.UserPersonalAccountDeleteVM;
import com.webharmony.core.service.data.validation.fieldvalidators.NotEmptyTextFieldValidator;
import com.webharmony.core.service.data.validation.fieldvalidators.UserPasswordValidator;
import com.webharmony.core.service.data.validation.utils.ValidationBuilder;
import com.webharmony.core.service.data.validation.utils.ValidationConfigBuilder;

public class UserPersonalAccountDeleteVMValidation implements ValidationConfigBuilder<UserPersonalAccountDeleteVM> {


    @Override
    public void configureValidationBuilder(ValidationBuilder<UserPersonalAccountDeleteVM, UserPersonalAccountDeleteVM, ?, ? extends ValidationBuilder<UserPersonalAccountDeleteVM, ?, ?, ?>> builder) {
        builder.ofField(UserPersonalAccountDeleteVM::getPassword).withValidation(new NotEmptyTextFieldValidator<>(), new UserPasswordValidator<>());
    }
}

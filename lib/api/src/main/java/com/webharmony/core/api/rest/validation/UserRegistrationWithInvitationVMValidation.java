package com.webharmony.core.api.rest.validation;

import com.webharmony.core.api.rest.model.view.user.UserRegistrationWithInvitationVM;
import com.webharmony.core.service.data.validation.fieldvalidators.*;
import com.webharmony.core.service.data.validation.utils.ValidationBuilder;
import com.webharmony.core.service.data.validation.utils.ValidationConfigBuilder;

public class UserRegistrationWithInvitationVMValidation implements ValidationConfigBuilder<UserRegistrationWithInvitationVM> {

    @Override
    public void configureValidationBuilder(ValidationBuilder<UserRegistrationWithInvitationVM, UserRegistrationWithInvitationVM, ?, ? extends ValidationBuilder<UserRegistrationWithInvitationVM, ?, ?, ?>> builder) {
        new UserRegistrationVMValidation(true).configureValidationBuilderForSubtypes(builder);
        builder.ofField(UserRegistrationWithInvitationVM::getTokenValue).withValidation(new NotEmptyTextFieldValidator<>());
    }
}

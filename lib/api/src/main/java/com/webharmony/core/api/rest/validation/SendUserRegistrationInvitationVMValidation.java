package com.webharmony.core.api.rest.validation;

import com.webharmony.core.api.rest.model.view.user.SendUserRegistrationInvitationVM;
import com.webharmony.core.service.data.validation.fieldvalidators.EmailFieldValidator;
import com.webharmony.core.service.data.validation.fieldvalidators.LimitTextLengthValidator;
import com.webharmony.core.service.data.validation.fieldvalidators.NotEmptyTextFieldValidator;
import com.webharmony.core.service.data.validation.fieldvalidators.UserEmailNotExistsValidator;
import com.webharmony.core.service.data.validation.utils.ValidationBuilder;
import com.webharmony.core.service.data.validation.utils.ValidationConfigBuilder;

public class SendUserRegistrationInvitationVMValidation implements ValidationConfigBuilder<SendUserRegistrationInvitationVM> {

    @Override
    public void configureValidationBuilder(ValidationBuilder<SendUserRegistrationInvitationVM, SendUserRegistrationInvitationVM, ?, ? extends ValidationBuilder<SendUserRegistrationInvitationVM, ?, ?, ?>> builder) {
        builder.ofField(SendUserRegistrationInvitationVM::getEmail)
                .withValidation(new NotEmptyTextFieldValidator<>(), new LimitTextLengthValidator<>(), new EmailFieldValidator<>(), new UserEmailNotExistsValidator<>());

    }
}

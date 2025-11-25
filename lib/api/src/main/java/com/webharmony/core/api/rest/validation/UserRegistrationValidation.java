package com.webharmony.core.api.rest.validation;

import com.webharmony.core.api.rest.model.UserRegistrationDto;
import com.webharmony.core.data.jpa.model.userregistration.AppUserRegistrationStateData;
import com.webharmony.core.service.data.validation.fieldvalidators.*;
import com.webharmony.core.service.data.validation.utils.ValidationBuilder;
import com.webharmony.core.service.data.validation.utils.ValidationConfigBuilder;

public class UserRegistrationValidation implements ValidationConfigBuilder<UserRegistrationDto> {

    @Override
    public void configureValidationBuilder(ValidationBuilder<UserRegistrationDto, UserRegistrationDto, ?, ? extends ValidationBuilder<UserRegistrationDto, ?, ?, ?>> builder) {
        builder.ofField(UserRegistrationDto::getEmail).withValidation(new NotEmptyTextFieldValidator<>(), new LimitTextLengthValidator<>(), new EmailFieldValidator<>(), new UserEmailNotExistsValidator<>());
        builder.ofField(UserRegistrationDto::getStateData).withValidation(new XmlStringFieldValidator<>(AppUserRegistrationStateData.class));
    }
}

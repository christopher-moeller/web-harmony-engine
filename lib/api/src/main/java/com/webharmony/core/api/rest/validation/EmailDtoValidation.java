package com.webharmony.core.api.rest.validation;

import com.webharmony.core.api.rest.model.EmailDto;
import com.webharmony.core.service.data.validation.fieldvalidators.EmailFieldValidator;
import com.webharmony.core.service.data.validation.fieldvalidators.LimitTextLengthValidator;
import com.webharmony.core.service.data.validation.fieldvalidators.NotEmptyTextFieldValidator;
import com.webharmony.core.service.data.validation.utils.ValidationBuilder;
import com.webharmony.core.service.data.validation.utils.ValidationConfigBuilder;

public class EmailDtoValidation implements ValidationConfigBuilder<EmailDto> {

    @Override
    public void configureValidationBuilder(ValidationBuilder<EmailDto, EmailDto, ?, ? extends ValidationBuilder<EmailDto, ?, ?, ?>> builder) {

        builder.ofField(EmailDto::getToEmail).withValidation(new NotEmptyTextFieldValidator<>(), new EmailFieldValidator<>(), new LimitTextLengthValidator<>());
        builder.ofField(EmailDto::getSubject).withValidation(new NotEmptyTextFieldValidator<>(), new LimitTextLengthValidator<>());

    }
}

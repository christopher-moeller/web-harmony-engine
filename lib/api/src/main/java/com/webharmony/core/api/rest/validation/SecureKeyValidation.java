package com.webharmony.core.api.rest.validation;

import com.webharmony.core.api.rest.model.SecureKeyDto;
import com.webharmony.core.service.data.validation.fieldvalidators.LimitTextLengthValidator;
import com.webharmony.core.service.data.validation.fieldvalidators.NotEmptyTextFieldValidator;
import com.webharmony.core.service.data.validation.utils.ValidationBuilder;
import com.webharmony.core.service.data.validation.utils.ValidationConfigBuilder;

public class SecureKeyValidation implements ValidationConfigBuilder<SecureKeyDto> {


    @Override
    public void configureValidationBuilder(ValidationBuilder<SecureKeyDto, SecureKeyDto, ?, ? extends ValidationBuilder<SecureKeyDto, ?, ?, ?>> builder) {

        builder.ofField(SecureKeyDto::getKey).withValidation(new NotEmptyTextFieldValidator<>(), new LimitTextLengthValidator<>());

    }
}

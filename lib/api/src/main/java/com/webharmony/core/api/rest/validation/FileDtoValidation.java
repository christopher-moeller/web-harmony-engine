package com.webharmony.core.api.rest.validation;

import com.webharmony.core.api.rest.model.FileDto;
import com.webharmony.core.service.data.validation.fieldvalidators.LimitTextLengthValidator;
import com.webharmony.core.service.data.validation.fieldvalidators.NotEmptyTextFieldValidator;
import com.webharmony.core.service.data.validation.utils.ValidationBuilder;
import com.webharmony.core.service.data.validation.utils.ValidationConfigBuilder;

public class FileDtoValidation implements ValidationConfigBuilder<FileDto> {
    @Override
    public void configureValidationBuilder(ValidationBuilder<FileDto, FileDto, ?, ? extends ValidationBuilder<FileDto, ?, ?, ?>> builder) {
        builder.ofField(FileDto::getFileName).withValidation(new NotEmptyTextFieldValidator<>(), new LimitTextLengthValidator<>());
    }
}

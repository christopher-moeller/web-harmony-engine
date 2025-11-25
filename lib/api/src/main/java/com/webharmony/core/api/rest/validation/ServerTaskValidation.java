package com.webharmony.core.api.rest.validation;

import com.webharmony.core.api.rest.model.ServerTaskDto;
import com.webharmony.core.service.data.validation.fieldvalidators.LimitTextLengthValidator;
import com.webharmony.core.service.data.validation.fieldvalidators.NotEmptyTextFieldValidator;
import com.webharmony.core.service.data.validation.utils.ValidationBuilder;
import com.webharmony.core.service.data.validation.utils.ValidationConfigBuilder;

public class ServerTaskValidation implements ValidationConfigBuilder<ServerTaskDto> {

    @Override
    public void configureValidationBuilder(ValidationBuilder<ServerTaskDto, ServerTaskDto, ?, ? extends ValidationBuilder<ServerTaskDto, ?, ?, ?>> builder) {
        builder.ofField(ServerTaskDto::getTaskName).withValidation(new NotEmptyTextFieldValidator<>(), new LimitTextLengthValidator<>());
    }
}

package com.webharmony.core.api.rest.validation;

import com.webharmony.core.api.rest.model.webcontent.WebContentAreaDto;
import com.webharmony.core.data.jpa.model.webcontent.QAppWebContentArea;
import com.webharmony.core.service.data.validation.fieldvalidators.NotEmptyTextFieldValidator;
import com.webharmony.core.service.data.validation.fieldvalidators.SelectableOptionValidator;
import com.webharmony.core.service.data.validation.fieldvalidators.UniqueResourceI18nFieldValidator;
import com.webharmony.core.service.data.validation.utils.ValidationBuilder;
import com.webharmony.core.service.data.validation.utils.ValidationConfigBuilder;

public class WebContentAreaDtoValidator implements ValidationConfigBuilder<WebContentAreaDto> {

    private static final QAppWebContentArea qAppWebContentArea = QAppWebContentArea.appWebContentArea;


    @Override
    @SuppressWarnings("all")
    public void configureValidationBuilder(ValidationBuilder<WebContentAreaDto, WebContentAreaDto, ?, ? extends ValidationBuilder<WebContentAreaDto, ?, ?, ?>> builder) {
        builder.ofField(WebContentAreaDto::getLabel).withValidation(new NotEmptyTextFieldValidator<>())
                .ofField(WebContentAreaDto::getLabel).withValidation(new UniqueResourceI18nFieldValidator<>(qAppWebContentArea, q -> q.uuid, q -> q.label))
                .ofField(WebContentAreaDto::getDescription).withValidation(new NotEmptyTextFieldValidator<>())
                .ofField(WebContentAreaDto::getRequiredReadRight).withValidation(new SelectableOptionValidator<>(false))
                .ofField(WebContentAreaDto::getRequiredWriteRight).withValidation(new SelectableOptionValidator<>(false));;
    }
}

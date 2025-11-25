package com.webharmony.core.api.rest.validation;

import com.webharmony.core.api.rest.model.RegistryItemDto;
import com.webharmony.core.data.jpa.model.QAppRegistryItem;
import com.webharmony.core.service.data.validation.fieldvalidators.NotEmptyTextFieldValidator;
import com.webharmony.core.service.data.validation.fieldvalidators.UniqueResourceI18nFieldValidator;
import com.webharmony.core.service.data.validation.utils.ValidationBuilder;
import com.webharmony.core.service.data.validation.utils.ValidationConfigBuilder;

public class RegistryItemValidation implements ValidationConfigBuilder<RegistryItemDto> {

    private static final QAppRegistryItem qAppRegistryItem = QAppRegistryItem.appRegistryItem;

    @Override
    public void configureValidationBuilder(ValidationBuilder<RegistryItemDto, RegistryItemDto, ?, ? extends ValidationBuilder<RegistryItemDto, ?, ?, ?>> builder) {

        builder.ofField(RegistryItemDto::getLabel).withValidation(new NotEmptyTextFieldValidator<>())
                .ofField(RegistryItemDto::getLabel).withValidation(new UniqueResourceI18nFieldValidator<>(qAppRegistryItem, q -> q.uuid, q -> q.label))
                .ofField(RegistryItemDto::getDescription).withValidation(new NotEmptyTextFieldValidator<>());

    }
}

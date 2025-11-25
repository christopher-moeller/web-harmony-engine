package com.webharmony.core.api.rest.validation;

import com.webharmony.core.api.rest.model.view.ApplicationStatusVM;
import com.webharmony.core.service.data.validation.fieldvalidators.NotEmptyTextFieldValidator;
import com.webharmony.core.service.data.validation.utils.ValidationBuilder;
import com.webharmony.core.service.data.validation.utils.ValidationConfigBuilder;

public class ApplicationStatusVMValidation implements ValidationConfigBuilder<ApplicationStatusVM> {

    @Override
    public void configureValidationBuilder(ValidationBuilder<ApplicationStatusVM, ApplicationStatusVM, ?, ? extends ValidationBuilder<ApplicationStatusVM, ?, ?, ?>> builder) {
        builder.ofField(ApplicationStatusVM::getUserMessage).withValidation(new NotEmptyTextFieldValidator<>())
                .ofField(ApplicationStatusVM::getTechnicalMessage).withValidation(new NotEmptyTextFieldValidator<>());
    }
}

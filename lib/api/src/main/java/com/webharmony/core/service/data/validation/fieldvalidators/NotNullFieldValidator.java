package com.webharmony.core.service.data.validation.fieldvalidators;

import com.webharmony.core.i18n.I18N;
import com.webharmony.core.i18n.I18nTranslation;
import com.webharmony.core.service.data.validation.utils.NamedValidationInterface;
import com.webharmony.core.service.data.validation.utils.ValidationContext;

public class NotNullFieldValidator<R, T> implements NamedValidationInterface<T, R>, I18nTranslation {

    public static final String NAME = "NOT_NULL_FIELD_VALIDATOR";

    private final I18N i18N = createI18nInstance(NotNullFieldValidator.class);

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void validate(T value, ValidationContext<R> validationContext) {
        if(value == null)
            validationContext.addValidationError(i18N.translate("Field cannot be empty").build());
    }
}

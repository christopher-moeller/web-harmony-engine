package com.webharmony.core.service.data.validation.fieldvalidators;

import com.webharmony.core.i18n.I18N;
import com.webharmony.core.i18n.I18nTranslation;
import com.webharmony.core.service.data.validation.utils.NamedValidationInterface;
import com.webharmony.core.service.data.validation.utils.ValidationContext;
import com.webharmony.core.utils.StringUtils;

public class NotEmptyTextFieldValidator<R> implements NamedValidationInterface<String, R>, I18nTranslation {

    public static final String NAME = "NOT_EMPTY_TEXT_FIELD_VALIDATOR";

    private final I18N i18N = createI18nInstance(NotEmptyTextFieldValidator.class);

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void validate(String s, ValidationContext<R> validationContext) {
        if(StringUtils.isNullOrEmpty(s))
            validationContext.addValidationError(i18N.translate("Field cannot be empty").build());
    }
}

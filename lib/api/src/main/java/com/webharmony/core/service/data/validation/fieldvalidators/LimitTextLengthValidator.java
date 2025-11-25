package com.webharmony.core.service.data.validation.fieldvalidators;

import com.webharmony.core.i18n.I18N;
import com.webharmony.core.i18n.I18nTranslation;
import com.webharmony.core.service.data.validation.utils.NamedValidationInterface;
import com.webharmony.core.service.data.validation.utils.ValidationContext;
import com.webharmony.core.utils.StringUtils;

public class LimitTextLengthValidator<R> implements NamedValidationInterface<String, R>, I18nTranslation {

    private final I18N i18N = createI18nInstance(LimitTextLengthValidator.class);

    public static final int DEFAULT_MAX_LENGTH = 255;

    public static final String NAME = "LIMIT_TEXT_LENGTH_VALIDATOR";

    private final int maxLength;

    public LimitTextLengthValidator() {
        this.maxLength = DEFAULT_MAX_LENGTH;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void validate(String s, ValidationContext<R> validationContext) {
        if(StringUtils.isNotNullAndNotEmpty(s) && s.length() > maxLength)
            validationContext.addValidationError(i18N.translate("max. text length is {maxLength}").add("maxLength", maxLength).build());
    }
}

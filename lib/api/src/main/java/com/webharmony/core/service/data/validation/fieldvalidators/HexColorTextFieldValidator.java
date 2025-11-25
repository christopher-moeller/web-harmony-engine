package com.webharmony.core.service.data.validation.fieldvalidators;

import com.webharmony.core.i18n.I18N;
import com.webharmony.core.i18n.I18nTranslation;
import com.webharmony.core.service.data.validation.utils.NamedValidationInterface;
import com.webharmony.core.service.data.validation.utils.ValidationContext;
import com.webharmony.core.utils.StringUtils;

public class HexColorTextFieldValidator<R> implements NamedValidationInterface<String, R>, I18nTranslation {

    public static final String NAME = "HEX_COLOR_TEXT_FIELD_VALIDATOR";

    private final I18N i18N = createI18nInstance(HexColorTextFieldValidator.class);

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void validate(String s, ValidationContext<R> validationContext) {
        if(StringUtils.isNullOrEmpty(s)) {
            validationContext.addValidationError(i18N.translate("Field cannot be empty").build());
            return;
        }

        if(!isValidHexColor(s)) {
            validationContext.addValidationError(i18N.translate("Value is not a valid hex color").build());
        }

    }

    public boolean isValidHexColor(String color) {
        return color != null && color.matches("#[0-9a-fA-F]{6}|#[0-9a-fA-F]{3}");
    }
}

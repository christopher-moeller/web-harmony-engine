package com.webharmony.core.service.data.validation.fieldvalidators;

import com.webharmony.core.i18n.I18N;
import com.webharmony.core.i18n.I18nTranslation;
import com.webharmony.core.service.data.validation.utils.NamedValidationInterface;
import com.webharmony.core.service.data.validation.utils.ValidationContext;
import org.apache.commons.validator.routines.EmailValidator;

public class EmailFieldValidator<R> implements NamedValidationInterface<String, R>, I18nTranslation {

    private final I18N i18N = createI18nInstance(EmailFieldValidator.class);

    public static final String NAME = "EMAIL_VALIDATION";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void validate(String email, ValidationContext<R> validationContext) {
        if(!EmailValidator.getInstance().isValid(email))
            validationContext.addValidationError(i18N.translate("E-Mail is not valid!").build());
    }
}

package com.webharmony.core.service.data.validation.fieldvalidators;

import com.webharmony.core.i18n.I18N;
import com.webharmony.core.i18n.I18nTranslation;
import com.webharmony.core.service.data.validation.utils.NamedValidationInterface;
import com.webharmony.core.service.data.validation.utils.ValidationContext;
import org.springframework.scheduling.support.CronExpression;

public class CronTriggerFieldValidator<R> implements NamedValidationInterface<String, R>, I18nTranslation {

    public static final String NAME = "CRON_TRIGGER_FIELD_VALIDATOR";

    private final I18N i18N = createI18nInstance(CronTriggerFieldValidator.class);

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void validate(String value, ValidationContext<R> validationContext) {

        if(!CronExpression.isValidExpression(value)) {
            validationContext.addValidationError(i18N.translate("Not a valid cron expression").build());
        }
    }
}

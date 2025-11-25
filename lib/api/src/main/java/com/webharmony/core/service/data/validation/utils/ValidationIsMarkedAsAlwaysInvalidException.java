package com.webharmony.core.service.data.validation.utils;

import com.webharmony.core.i18n.ECoreI18nStaticTranslations;

public class ValidationIsMarkedAsAlwaysInvalidException extends RuntimeException {

    public ValidationIsMarkedAsAlwaysInvalidException() {
        super(ECoreI18nStaticTranslations.VALIDATION_MARKED_AS_ALWAYS_INVALID.getI18n().build());
    }
}

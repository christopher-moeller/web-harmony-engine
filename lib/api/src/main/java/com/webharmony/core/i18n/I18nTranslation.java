package com.webharmony.core.i18n;

public interface I18nTranslation {

    default I18N createI18nInstance(Class<?> clazz) {
        return I18N.of(clazz);
    }

}

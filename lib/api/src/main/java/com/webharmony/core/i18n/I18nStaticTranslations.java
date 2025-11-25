package com.webharmony.core.i18n;

import com.webharmony.core.utils.RegexUtils;

import java.util.List;

public interface I18nStaticTranslations {

    Class<?> getCallingClass();
    String getDefaultValue();

    String name();

    default I18N.TranslationBuilder getI18n() {
        return I18N.of(getCallingClass()).translate(getDefaultValue());
    }

    default List<String> getPlaceholders() {
        return RegexUtils.getStringsSurroundedBy(getDefaultValue(), "{", "}");
    }

    default String getKeyId() {
        return getDefaultValue().replace(" ", "");
    }

}

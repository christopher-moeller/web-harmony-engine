package com.webharmony.core.i18n;

import com.webharmony.core.utils.exceptions.InternalServerException;
import lombok.Getter;

import java.util.stream.Stream;

@Getter
public enum EI18nLanguage {

    ENGLISH("en", ECoreI18nStaticTranslations.LANGUAGE_ENGLISH, "yyyy-MM-dd"),
    GERMAN("de", ECoreI18nStaticTranslations.LANGUAGE_GERMAN, "dd.MM.yyyy");

    private final String key;
    private final I18nStaticTranslations languageLabel;
    private final String dateFormatTemplate;

    EI18nLanguage(String key, I18nStaticTranslations languageLabel, String dateFormatTemplate) {
        this.key = key;
        this.languageLabel = languageLabel;
        this.dateFormatTemplate = dateFormatTemplate;
    }

    public static EI18nLanguage getByKey(String key) {
        return Stream.of(values())
                .filter(v -> v.key.equals(key))
                .findAny()
                .orElseThrow(() -> new InternalServerException(String.format("Language with key '%s' is not supported", key)));
    }
}

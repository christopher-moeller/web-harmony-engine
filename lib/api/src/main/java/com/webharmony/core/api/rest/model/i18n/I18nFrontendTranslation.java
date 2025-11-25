package com.webharmony.core.api.rest.model.i18n;

import com.webharmony.core.i18n.EI18nLanguage;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class I18nFrontendTranslation {

    private EI18nLanguage language;
    private List<I18nFrontendTranslationEntry> translationEntries;

}

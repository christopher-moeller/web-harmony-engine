package com.webharmony.core.utils.dev.i18n.datatransfer;

import com.webharmony.core.i18n.EI18nLanguage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class I18nTranslationEntryTransferData {

    private EI18nLanguage language;
    private String translation;

}

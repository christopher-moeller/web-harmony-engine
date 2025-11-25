package com.webharmony.core.utils.dev.i18n.datatransfer;

import com.webharmony.core.i18n.EI18nCodeLocation;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class I18nKeyEntryTransferData {

    private String classId;
    private String keyId;
    private List<String> placeholders;
    private String description;
    private EI18nCodeLocation codeLocation;
    private Boolean isCoreEntry;
    private List<I18nTranslationEntryTransferData> translationEntries;


}

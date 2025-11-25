package com.webharmony.core.utils.dev.i18n.datatransfer;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class I18nEntityAttributeTransferData {

    private String classId;
    private String keyId;
    private String uniqueName;
    private Boolean isCoreEntry;
    private List<I18nTranslationEntryTransferData> translations;

}

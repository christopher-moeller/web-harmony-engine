package com.webharmony.core.api.rest.model.i18n;

import com.webharmony.core.api.rest.model.utils.AbstractResourceDto;
import com.webharmony.core.api.rest.model.utils.annotations.ReadOnlyAttribute;
import com.webharmony.core.i18n.EI18nCodeLocation;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class I18nKeyEntryDto extends AbstractResourceDto {

    @ReadOnlyAttribute
    private String classId;
    @ReadOnlyAttribute
    private String key;
    @ReadOnlyAttribute
    private String placeholders;
    @ReadOnlyAttribute
    private EI18nCodeLocation codeLocation;
    @ReadOnlyAttribute
    private String codeLines;

    private String description;

    private List<I18nTranslationDto> translationEntries;

}

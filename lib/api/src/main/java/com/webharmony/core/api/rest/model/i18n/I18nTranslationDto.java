package com.webharmony.core.api.rest.model.i18n;

import com.webharmony.core.api.rest.model.utils.BaseDto;
import com.webharmony.core.i18n.EI18nLanguage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class I18nTranslationDto extends BaseDto {

    private EI18nLanguage language;
    private String translation;

}

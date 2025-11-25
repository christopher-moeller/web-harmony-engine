package com.webharmony.core.utils.dev.i18n;

import com.webharmony.core.i18n.EI18nCodeLocation;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class I18nClassDto {

    private String classId;
    private List<I18nKeyDto> keys;
    private EI18nCodeLocation location;
    private boolean isCoreClass;

}

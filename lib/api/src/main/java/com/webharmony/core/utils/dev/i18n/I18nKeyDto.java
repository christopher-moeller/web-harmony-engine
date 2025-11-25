package com.webharmony.core.utils.dev.i18n;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class I18nKeyDto {

    private String id;
    private List<String> placeholders;
    private Boolean isCoreEntry;
    private String englishDefaultValue;
    private List<String> codeLocations;

}

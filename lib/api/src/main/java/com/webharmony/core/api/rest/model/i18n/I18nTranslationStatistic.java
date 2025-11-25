package com.webharmony.core.api.rest.model.i18n;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class I18nTranslationStatistic {

    private Long countOfAllItems;
    private Long countOfTranslatedItems;
    private Long countOfNotTranslatedItems;

}

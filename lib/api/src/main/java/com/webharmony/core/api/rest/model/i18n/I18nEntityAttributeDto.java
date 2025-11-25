package com.webharmony.core.api.rest.model.i18n;

import com.webharmony.core.api.rest.model.utils.AbstractResourceDto;
import com.webharmony.core.api.rest.model.utils.annotations.ReadOnlyAttribute;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class I18nEntityAttributeDto extends AbstractResourceDto {

    @ReadOnlyAttribute
    private String entityClass;

    @ReadOnlyAttribute
    private String attribute;

    private List<I18nTranslationDto> values;

}

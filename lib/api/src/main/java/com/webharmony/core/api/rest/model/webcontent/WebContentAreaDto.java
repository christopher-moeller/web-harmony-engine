package com.webharmony.core.api.rest.model.webcontent;

import com.webharmony.core.api.rest.model.utils.AbstractResourceDto;
import com.webharmony.core.api.rest.model.utils.annotations.ReadOnlyAttribute;
import com.webharmony.core.api.rest.model.utils.annotations.SelectableOptions;
import com.webharmony.core.api.rest.model.utils.apiobject.ApiResource;
import com.webharmony.core.api.rest.model.utils.optioncontainers.SelectableRightsForWebContentAreaOptionContainer;
import com.webharmony.core.api.rest.validation.WebContentAreaDtoValidator;
import com.webharmony.core.service.data.validation.utils.Validation;
import com.webharmony.core.utils.objects.ObjectsWithLabel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Validation(WebContentAreaDtoValidator.class)
public class WebContentAreaDto extends AbstractResourceDto implements ObjectsWithLabel<String> {

    @ReadOnlyAttribute
    private String uniqueName;
    private String label;
    private String description;

    private Boolean onlyOneContentInstanceAllowed;

    @SelectableOptions(SelectableRightsForWebContentAreaOptionContainer.class)
    private ApiResource<?> requiredReadRight;

    @SelectableOptions(SelectableRightsForWebContentAreaOptionContainer.class)
    private ApiResource<?> requiredWriteRight;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}

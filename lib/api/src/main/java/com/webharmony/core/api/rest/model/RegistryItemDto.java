package com.webharmony.core.api.rest.model;

import com.webharmony.core.api.rest.model.utils.AbstractResourceDto;
import com.webharmony.core.api.rest.model.utils.annotations.ReadOnlyAttribute;
import com.webharmony.core.api.rest.validation.RegistryItemValidation;
import com.webharmony.core.service.data.validation.utils.Validation;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Validation(RegistryItemValidation.class)
public class RegistryItemDto extends AbstractResourceDto {

    @ReadOnlyAttribute
    private String uniqueName;
    private String label;
    private String description;
    private Object value;
    @ReadOnlyAttribute
    private String javaType;
    @ReadOnlyAttribute
    private String javaSpecificationType;
    @ReadOnlyAttribute
    private List<Object> definedSelectableOptions;

}

package com.webharmony.core.api.rest.model;

import com.webharmony.core.api.rest.model.utils.AbstractResourceDto;
import com.webharmony.core.api.rest.model.utils.annotations.ReadOnlyAttribute;
import com.webharmony.core.api.rest.validation.ActorRightDtoValidation;
import com.webharmony.core.service.data.validation.utils.Validation;
import com.webharmony.core.utils.objects.ObjectsWithLabel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Validation(ActorRightDtoValidation.class)
public class ActorRightDto extends AbstractResourceDto implements ObjectsWithLabel<String> {

    private String label;
    private String description;
    @ReadOnlyAttribute
    private Boolean isAllowedForSystemActor;
    private Boolean isAllowedForUnknownPublicActor;

}

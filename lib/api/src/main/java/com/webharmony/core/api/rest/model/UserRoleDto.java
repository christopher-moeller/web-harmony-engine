package com.webharmony.core.api.rest.model;

import com.webharmony.core.api.rest.model.utils.AbstractResourceDto;
import com.webharmony.core.api.rest.model.utils.annotations.SelectableOptions;
import com.webharmony.core.api.rest.model.utils.apiobject.ApiResource;
import com.webharmony.core.api.rest.model.utils.optioncontainers.SelectableRightsApiObjectOptionContainer;
import com.webharmony.core.api.rest.validation.UserRoleDtoValidation;
import com.webharmony.core.service.data.validation.utils.Validation;
import com.webharmony.core.utils.objects.ObjectsWithLabel;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@Validation(UserRoleDtoValidation.class)
public class UserRoleDto extends AbstractResourceDto implements ObjectsWithLabel<String> {

    private String label;
    private String description;

    @SelectableOptions(SelectableRightsApiObjectOptionContainer.class)
    private List<ApiResource<ActorRightDto>> includedRights;

    public static UserRoleDto createInitialTemplate() {
        UserRoleDto dto = new UserRoleDto();
        dto.setIncludedRights(Collections.emptyList());
        return dto;
    }

}

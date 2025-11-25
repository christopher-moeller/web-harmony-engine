package com.webharmony.core.api.rest.model;

import com.webharmony.core.api.rest.model.utils.BaseDto;
import com.webharmony.core.api.rest.model.utils.annotations.SelectableOptions;
import com.webharmony.core.api.rest.model.utils.apiobject.ApiResource;
import com.webharmony.core.api.rest.model.utils.optioncontainers.SelectableRightsApiObjectOptionContainer;
import com.webharmony.core.api.rest.model.utils.optioncontainers.SelectableRolesApiObjectOptionContainer;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserAccessDto extends BaseDto {

    private Boolean isAdmin;

    @SelectableOptions(SelectableRolesApiObjectOptionContainer.class)
    private ApiResource<?> mainRole;

    @SelectableOptions(SelectableRolesApiObjectOptionContainer.class)
    private List<ApiResource<?>> roles;

    @SelectableOptions(SelectableRightsApiObjectOptionContainer.class)
    private List<ApiResource<?>> additionalRights;

}

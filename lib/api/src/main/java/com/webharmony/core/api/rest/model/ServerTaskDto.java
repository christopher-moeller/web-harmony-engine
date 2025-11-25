package com.webharmony.core.api.rest.model;

import com.webharmony.core.api.rest.model.utils.AbstractResourceDto;
import com.webharmony.core.api.rest.model.utils.annotations.ReadOnlyAttribute;
import com.webharmony.core.api.rest.validation.ServerTaskValidation;
import com.webharmony.core.service.data.validation.utils.Validation;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Validation(ServerTaskValidation.class)
public class ServerTaskDto extends AbstractResourceDto {

    @ReadOnlyAttribute
    private String taskId;

    private String taskName;

    @ReadOnlyAttribute
    private String lastExecution;

    @ReadOnlyAttribute
    private Boolean isRequired;

}

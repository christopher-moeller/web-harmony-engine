package com.webharmony.core.api.rest.model;

import com.webharmony.core.api.rest.model.utils.AbstractResourceDto;
import com.webharmony.core.api.rest.model.utils.annotations.ReadOnlyAttribute;
import com.webharmony.core.api.rest.validation.CronJobDtoValidation;
import com.webharmony.core.service.data.validation.utils.Validation;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Validation(CronJobDtoValidation.class)
public class CronJobDto extends AbstractResourceDto {

    private String label;
    private String description;
    @ReadOnlyAttribute
    private String javaClass;
    @ReadOnlyAttribute
    private String lastExecutedAt;
    private Boolean isActivated;
    @ReadOnlyAttribute
    private Boolean isCurrentlyRunning;
    private String cronTrigger;

}

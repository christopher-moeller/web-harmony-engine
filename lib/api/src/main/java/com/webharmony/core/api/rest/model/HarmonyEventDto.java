package com.webharmony.core.api.rest.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.webharmony.core.api.rest.jackson.CustomLocalDateTimeSerializer;
import com.webharmony.core.api.rest.model.utils.AbstractResourceDto;
import com.webharmony.core.api.rest.model.utils.annotations.ReadOnlyAttribute;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class HarmonyEventDto extends AbstractResourceDto {

    @ReadOnlyAttribute
    private String createdBy;
    @ReadOnlyAttribute
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    private LocalDateTime createdAt;
    @ReadOnlyAttribute
    private String javaType;
    @ReadOnlyAttribute
    private String payload;

}

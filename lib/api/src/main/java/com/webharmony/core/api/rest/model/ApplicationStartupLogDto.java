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
public class ApplicationStartupLogDto extends AbstractResourceDto {

    @ReadOnlyAttribute
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    private LocalDateTime startedAt;

    @ReadOnlyAttribute
    private long durationInMillis;

    @ReadOnlyAttribute
    private String durationText;


}

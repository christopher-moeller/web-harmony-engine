package com.webharmony.core.api.rest.model;

import com.webharmony.core.api.rest.model.utils.AbstractResourceDto;
import com.webharmony.core.api.rest.model.utils.annotations.ReadOnlyAttribute;
import com.webharmony.core.utils.exceptions.utils.EApplicationErrorLocation;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationExceptionDto extends AbstractResourceDto {

    @ReadOnlyAttribute
    private String timestamp;
    @ReadOnlyAttribute
    private String exceptionType;
    @ReadOnlyAttribute
    private String message;
    @ReadOnlyAttribute
    private EApplicationErrorLocation codeLocation;
    private String description;
    @ReadOnlyAttribute
    private String stacktrace;
    @ReadOnlyAttribute
    private String log;
}

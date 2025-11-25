package com.webharmony.core.api.rest.error.validation;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ValidationErrorMessageDto {

    private String validationName;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String stackTraceText;


    public ValidationErrorMessageDto(String validationName, String message, String stackTraceText) {
        this.validationName = validationName;
        this.message = message;
        this.stackTraceText = stackTraceText;
    }
}

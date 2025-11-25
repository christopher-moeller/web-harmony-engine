package com.webharmony.core.api.rest.error;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApiJavaExceptionInstance {

    private String message;
    private String stackTraceText;

    public ApiJavaExceptionInstance(String message, String stackTrace) {
        this.message = message;
        this.stackTraceText = stackTrace;
    }
}

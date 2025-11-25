package com.webharmony.core.api.rest.error;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
public class ApiErrorWithJavaExceptionInstance extends ApiError {

    private ApiJavaExceptionInstance javaException;

    public ApiErrorWithJavaExceptionInstance(HttpStatus status, String message, ApiJavaExceptionInstance javaException) {
        super(status, message);
        this.javaException = javaException;
    }
}

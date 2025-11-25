package com.webharmony.core.utils.exceptions;

import org.springframework.http.HttpStatus;

public class ReflectionException extends ApplicationException {


    public ReflectionException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}

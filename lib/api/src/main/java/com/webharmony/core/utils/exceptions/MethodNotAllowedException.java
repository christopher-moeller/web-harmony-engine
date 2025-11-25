package com.webharmony.core.utils.exceptions;

import org.springframework.http.HttpStatus;

public class MethodNotAllowedException extends ApplicationException{

    public MethodNotAllowedException() {
        super("This method is not allowed");
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.METHOD_NOT_ALLOWED;
    }
}

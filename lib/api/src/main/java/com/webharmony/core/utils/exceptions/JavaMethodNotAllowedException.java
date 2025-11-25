package com.webharmony.core.utils.exceptions;

import org.springframework.http.HttpStatus;

public class JavaMethodNotAllowedException extends ApplicationException {

    public JavaMethodNotAllowedException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}

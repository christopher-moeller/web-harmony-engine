package com.webharmony.core.utils.exceptions;

import org.springframework.http.HttpStatus;

public class InternalServerException extends ApplicationException {

    public InternalServerException(String message, Exception e) {
        super(message, e);
    }

    public InternalServerException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}

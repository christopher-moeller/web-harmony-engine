package com.webharmony.core.utils.exceptions;

import org.springframework.http.HttpStatus;

public class TestBackendException extends ApplicationException {

    public TestBackendException() {
        super("TestBackendException");
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}

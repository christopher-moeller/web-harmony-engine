package com.webharmony.core.utils.exceptions;

import org.springframework.http.HttpStatus;

public abstract class ApplicationException extends RuntimeException {

    protected ApplicationException(String message, Exception e) {
        super(message, e);
    }

    protected ApplicationException(String message) {
        super(message);
    }

    public abstract HttpStatus getHttpStatus();

    public boolean shouldLogException() {
        return true;
    }

}

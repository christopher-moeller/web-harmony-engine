package com.webharmony.core.utils.exceptions;

import org.springframework.http.HttpStatus;

public class FileWebDataValidationException extends ApplicationException {
    public FileWebDataValidationException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}

package com.webharmony.core.utils.assertions.exceptions;

import com.webharmony.core.utils.exceptions.ApplicationException;
import org.springframework.http.HttpStatus;

public class AssertionNullException extends ApplicationException {

    public AssertionNullException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}

package com.webharmony.core.utils.assertions.exceptions;

import com.webharmony.core.utils.exceptions.ApplicationException;
import org.springframework.http.HttpStatus;

public class AssertionNotNullException extends ApplicationException {

    public AssertionNotNullException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

}

package com.webharmony.core.utils.exceptions;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends ApplicationException {

    public UnauthorizedException() {
        this("Unauthorized");
    }


    public UnauthorizedException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.UNAUTHORIZED;
    }

    @Override
    public boolean shouldLogException() {
        return false;
    }
}

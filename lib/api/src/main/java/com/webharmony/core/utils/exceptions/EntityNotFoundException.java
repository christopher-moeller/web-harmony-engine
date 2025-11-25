package com.webharmony.core.utils.exceptions;


import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends ApplicationException {


    public EntityNotFoundException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

}

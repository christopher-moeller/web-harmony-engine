package com.webharmony.core.api.rest.model.utils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseDto {

    @Setter(AccessLevel.PACKAGE)
    private String dtoType;
    
    public BaseDto() {
        this.dtoType = this.getClass().getSimpleName();
    }

}

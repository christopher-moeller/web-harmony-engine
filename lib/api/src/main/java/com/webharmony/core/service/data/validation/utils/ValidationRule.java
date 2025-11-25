package com.webharmony.core.service.data.validation.utils;

import lombok.Getter;

import java.util.List;

@Getter
public class ValidationRule<T> {

    private final Class<T> rootType;
    private final String path;
    private final List<NamedValidationInterface<?, T>> validation;

    public ValidationRule(Class<T> rootType, String path, List<NamedValidationInterface<?, T>> validation) {
        this.rootType = rootType;
        this.path = path;
        this.validation = validation;
    }

}

package com.webharmony.core.service.data.validation.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Validation {

    @SuppressWarnings("java:S1452")
    Class<? extends ValidationConfigBuilder<?>> value();

}

package com.webharmony.core.api.rest.controller.utils;


import com.webharmony.core.data.enums.ECoreActorRight;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CoreApiAuthorization {

    ECoreActorRight[] value();
    boolean isOrConnected() default false;

}

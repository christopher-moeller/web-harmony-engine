package com.webharmony.core.api.rest.model.utils.annotations;

import com.webharmony.core.api.rest.model.utils.optioncontainers.utils.AbstractSelectableOptionsContainer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SelectableOptions {

    @SuppressWarnings("java:S1452")
    Class<? extends AbstractSelectableOptionsContainer<?>> value();

}

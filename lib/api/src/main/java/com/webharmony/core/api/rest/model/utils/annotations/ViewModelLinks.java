package com.webharmony.core.api.rest.model.utils.annotations;

import com.webharmony.core.api.rest.model.utils.annotations.utils.ApiLinkSpecification;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ViewModelLinks {

    Class<? extends ApiLinkSpecification> loadLink() default ApiLinkSpecification.class;
    Class<? extends ApiLinkSpecification> saveLink();
}

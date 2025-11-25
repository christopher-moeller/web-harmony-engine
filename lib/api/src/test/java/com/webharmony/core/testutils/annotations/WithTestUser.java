package com.webharmony.core.testutils.annotations;

import com.webharmony.core.testutils.ETestUser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface WithTestUser {

    ETestUser value() default ETestUser.ADMIN_USER;

}

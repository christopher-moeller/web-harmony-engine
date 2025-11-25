package com.webharmony.core.api.rest.interceptors;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.annotation.Annotation;
import java.util.Optional;

public abstract class AbstractHandlerInterceptorAdapter implements HandlerInterceptor {

    @SuppressWarnings("SameParameterValue")
    protected <A extends Annotation> Optional<A> getMethodAnnotation(Object handler, Class<A> annotationClass) {

        if(handler instanceof HandlerMethod handlerMethod) {
            return Optional.ofNullable(handlerMethod.getMethodAnnotation(annotationClass));
        }
        return Optional.empty();
    }

}

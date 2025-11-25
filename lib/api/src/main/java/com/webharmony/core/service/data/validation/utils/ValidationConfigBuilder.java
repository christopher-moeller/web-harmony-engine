package com.webharmony.core.service.data.validation.utils;

import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.utils.reflection.ReflectionUtils;

public interface ValidationConfigBuilder<R> {

    void configureValidationBuilder(ValidationBuilder<R, R, ?, ? extends ValidationBuilder<R, ?, ?, ?>> builder);

    @SuppressWarnings("unchecked")
    default void configureValidationBuilderForSubtypes(ValidationBuilder<? extends R, ? extends R, ?, ? extends ValidationBuilder<? extends R, ?, ?, ?>> builder) {
        configureValidationBuilder((ValidationBuilder<R, R, ?, ? extends ValidationBuilder<R, ?, ?, ?>>) builder);
    }

    @SuppressWarnings("java:S1452")
    default ValidationBuilder<R, R, ?, ? extends ValidationBuilder<R, ?, ?, ?>> createNewBuilder() {
        ValidationBuilder<R, R, ?, ? extends ValidationBuilder<R, ?, ?, ?>> builder = ValidationBuilder.ofRoot(getRootClass());
        configureValidationBuilder(builder);
        return builder;
    }

    default Class<R> getRootClass() {
        return ReflectionUtils.getGenericTypeByClassAndIndexOrThrow(this.getClass(), 0);
    }

    default <T> T getSpringBean(Class<T> type) {
        return ContextHolder.getContext().getBean(type);
    }

}

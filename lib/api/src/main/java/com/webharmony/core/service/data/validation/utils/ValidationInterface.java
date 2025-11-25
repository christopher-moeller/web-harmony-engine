package com.webharmony.core.service.data.validation.utils;

import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.service.EntityService;
import jakarta.persistence.EntityManager;

public interface ValidationInterface<V, R> {

    void validate(V value, ValidationContext<R> validationContext);

    @SuppressWarnings("unchecked")
    default void validateUntyped(Object value, ValidationContext<R> validationContext) {
        validate((V) value, validationContext);
    }

    default EntityManager getEntityManager() {
        return ContextHolder.getContext().getBean(EntityService.class).getEntityManager();
    }

}

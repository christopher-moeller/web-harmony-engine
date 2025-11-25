package com.webharmony.core.api.rest.model.utils.optioncontainers.utils;


import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.configuration.security.ApplicationRight;
import jakarta.persistence.EntityManager;

public interface AbstractSelectableOptionsContainer<T> {

    boolean optionContainsInRange(T option);

    @SuppressWarnings("unchecked")
    default boolean untypedOptionIsInRange(Object option) {
        return optionContainsInRange((T) option);
    }

    default String getName() {
        return this.getClass().getSimpleName();
    }

    default EntityManager getEntityManager() {
        return ContextHolder.getContext().getBean(EntityManager.class);
    }

    default JPAQuery<Tuple> createTupleQuery() {
        return new JPAQuery<>(getEntityManager());
    }

    ApplicationRight getApplicationRight();

}

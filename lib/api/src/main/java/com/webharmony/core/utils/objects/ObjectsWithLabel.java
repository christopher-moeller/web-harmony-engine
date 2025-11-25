package com.webharmony.core.utils.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Objects;

public interface ObjectsWithLabel<T> {

    T getLabel();
    void setLabel(T label);

    @JsonIgnore
    default String getStringLabel() {
        return Objects.toString(getLabel());
    }

    @SuppressWarnings("unchecked")
    default void setStringLabel(String label) {
        setLabel((T) label);
    }

}

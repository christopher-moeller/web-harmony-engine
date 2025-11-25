package com.webharmony.core.service.data.validation.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Arrays;
import java.util.Objects;

public record ValidationError<R>(R rootSource, String path, String validationName, String message,
                                 @JsonIgnore StackTraceElement[] stackTraceElements) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValidationError<?> that = (ValidationError<?>) o;
        return Objects.equals(rootSource, that.rootSource) && Objects.equals(path, that.path) && Objects.equals(validationName, that.validationName) && Objects.equals(message, that.message) && Arrays.equals(stackTraceElements, that.stackTraceElements);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(rootSource, path, validationName, message);
        result = 31 * result + Arrays.hashCode(stackTraceElements);
        return result;
    }

    @Override
    public String toString() {
        return path;
    }
}

package com.webharmony.core.utils;

import com.webharmony.core.utils.assertions.Assert;
import com.webharmony.core.utils.exceptions.InternalServerException;
import jakarta.persistence.Column;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Field;
import java.util.Optional;

public class EntityHelper {

    private EntityHelper() {

    }

    public static String getColumnName(Field field) {

        Assert.hasAnnotation(field, Column.class)
                .withException(() -> new InternalServerException(String.format("Entity field '%s' for class '%s' has missing @Column annotation", field.getName(), field.getDeclaringClass().getName())))
                .verify();

        return Optional.ofNullable(AnnotationUtils.findAnnotation(field, Column.class))
                .map(Column::name)
                .orElse(null);
    }
}

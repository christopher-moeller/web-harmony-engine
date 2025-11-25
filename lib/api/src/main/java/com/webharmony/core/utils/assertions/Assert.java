package com.webharmony.core.utils.assertions;

import com.webharmony.core.utils.StringUtils;
import com.webharmony.core.utils.assertions.exceptions.AssertionNotNullException;
import com.webharmony.core.utils.assertions.exceptions.AssertionNullException;
import com.webharmony.core.utils.exceptions.ApplicationException;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

public class Assert {

    private Assert() {

    }

    public static AssertionBuilder isTrue(boolean source) {
        final AssertionFunction assertionFunction = () -> source;
        return buildSimpleBuilder(assertionFunction, "source is not true");
    }

    public static AssertionBuilder isNull(Object source) {
        final AssertionFunction assertionFunction = () -> source == null;
        return buildSimpleBuilder(assertionFunction, "source is not null");
    }

    public static AssertionBuilder isNotNull(Object source) {
        final AssertionFunction assertionFunction = () -> source != null;
        final Supplier<ApplicationException> exceptionSupplier = () -> new AssertionNotNullException("source is null");
        return new AssertionBuilder(assertionFunction, exceptionSupplier);
    }

    public static AssertionBuilder hasAnnotation(Class<?> source, Class<? extends Annotation> annotation) {
        final AssertionFunction assertionFunction = () -> AnnotationUtils.findAnnotation(source, annotation) != null;
        return buildSimpleBuilder(assertionFunction, "annotation not found");
    }

    public static AssertionBuilder hasAnnotation(Field source, Class<? extends Annotation> annotation) {
        final AssertionFunction assertionFunction = () -> AnnotationUtils.findAnnotation(source, annotation) != null;
        return buildSimpleBuilder(assertionFunction, "annotation not found");
    }

    public static AssertionBuilder isNotEmpty(Collection<?> source) {
        final AssertionFunction assertionFunction = () -> !source.isEmpty();
        return buildSimpleBuilder(assertionFunction, "collection is empty");
    }

    public static AssertionBuilder hasSize(Collection<?> source, int size) {
        final AssertionFunction assertionFunction = () -> source.size() == size;
        return buildSimpleBuilder(assertionFunction, String.format("collection size is not '%s'", size));
    }

    public static AssertionBuilder hasSize(Map<?, ?> source, int size) {
        final AssertionFunction assertionFunction = () -> source.size() == size;
        return buildSimpleBuilder(assertionFunction, String.format("map size is not '%s'", size));
    }

    public static AssertionBuilder isNotEmpty(Object[] source) {
        final AssertionFunction assertionFunction = () -> source.length > 0;
        return buildSimpleBuilder(assertionFunction, "array is empty");
    }

    public static AssertionBuilder isNotEmpty(String source) {
        final AssertionFunction assertionFunction = () -> StringUtils.isNotNullAndNotEmpty(source);
        return buildSimpleBuilder(assertionFunction, "value is empty");
    }

    private static AssertionBuilder buildSimpleBuilder(AssertionFunction assertionFunction, String errorMessage) {
        final Supplier<ApplicationException> exceptionSupplier = () -> new AssertionNullException(errorMessage);
        return new AssertionBuilder(assertionFunction, exceptionSupplier);
    }
}

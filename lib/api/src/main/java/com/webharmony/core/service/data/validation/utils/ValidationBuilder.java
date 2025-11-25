package com.webharmony.core.service.data.validation.utils;

import com.webharmony.core.utils.reflection.ReflectionUtils;
import com.webharmony.core.utils.reflection.proxy.ProxyExecutionMethod;
import lombok.Getter;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.List;

public class ValidationBuilder<R, T, P, B extends ValidationBuilder<R, P, ?, ?>> {

    @Getter
    private final Class<T> targetType;

    @Getter
    private final Class<P> parentType;

    @Getter
    private final Class<R> rootType;

    private final B parentBuilder;

    @Getter
    private final ValidationBuildingContext<R> validationBuildingContext;


    @SuppressWarnings("unchecked")
    private ValidationBuilder(Class<R> rootType, Class<T> targetType, Class<P> parentType, ValidationBuilder<R, P, ?, ?> parentBuilder, ValidationBuildingContext<R> validationBuildingContext) {
        this.rootType = rootType;
        this.targetType = targetType;
        this.parentType = parentType;
        this.parentBuilder = (B) parentBuilder;
        this.validationBuildingContext = validationBuildingContext;
    }

    @SuppressWarnings("java:S1452")
    public static <T> ValidationBuilder<T, T, ?, ?> ofRoot(Class<T> rootTargetType) {
        ValidationBuildingContext<T> newValidationBuildingContext = new ValidationBuildingContext<>(rootTargetType);
        return new ValidationBuilder<>(rootTargetType, rootTargetType, null, null, newValidationBuildingContext);
    }

    public <N, U extends ValidationBuilder<R, T, P, ?>> ValidationBuilder<R, N, T, U> ofField(ProxyExecutionMethod<T, N> method) {
        return navigateToField(method, false);
    }

    public <N, U extends ValidationBuilder<R, T, P, ?>> ValidationBuilder<R, N, T, U> ofField(ProxyExecutionMethod<T, N> method, Class<N> newTargetType) {
        return navigateToField(method, false, newTargetType);
    }

    public <N, U extends ValidationBuilder<R, T, P, ?>> ValidationBuilder<R, N, T, U> ofOptionalListField(ProxyExecutionMethod<T, List<N>> method) {
        return navigateToListField(method, true);
    }

    @SuppressWarnings({"unchecked", "SameParameterValue", "rawtypes"})
    private <N, U extends ValidationBuilder<R, T, P, ?>> ValidationBuilder<R, N, T, U> navigateToField(ProxyExecutionMethod<T, N> method, boolean isNullable) {
        Method javaMethod = ReflectionUtils.getMethodByClass(this.targetType, method);
        Class<?> newTargetType = javaMethod.getReturnType();
        String fieldName = ReflectionUtils.getFieldNameByGetterMethod(javaMethod);
        if(isNullable)
            fieldName = fieldName + "?";
        return new ValidationBuilder<>(this.rootType, newTargetType, targetType, this, new ValidationBuildingContext(this.validationBuildingContext, rootType, fieldName));
    }

    @SuppressWarnings({"unchecked", "SameParameterValue", "rawtypes"})
    private <N, U extends ValidationBuilder<R, T, P, ?>> ValidationBuilder<R, N, T, U> navigateToField(ProxyExecutionMethod<T, N> method, boolean isNullable, Class<?> newTargetType) {
        Method javaMethod = ReflectionUtils.getMethodByClass(this.targetType, method);
        String fieldName = ReflectionUtils.getFieldNameByGetterMethod(javaMethod);
        if(isNullable)
            fieldName = fieldName + "?";
        return new ValidationBuilder<>(this.rootType, newTargetType, targetType, this, new ValidationBuildingContext(this.validationBuildingContext, rootType, fieldName));
    }


    @SneakyThrows
    @SuppressWarnings({"unchecked", "SameParameterValue", "rawtypes"})
    private <N, U extends ValidationBuilder<R, T, P, ?>> ValidationBuilder<R, N, T, U> navigateToListField(ProxyExecutionMethod<T, List<N>> method, boolean isNullable) {
        Method javaMethod = ReflectionUtils.getMethodByClass(this.targetType, method);
        String fieldName = ReflectionUtils.getFieldNameByGetterMethod(javaMethod);
        Class<?> newTargetType = ReflectionUtils.getGenericTypeOfCollection(targetType.getDeclaredField(fieldName));
        if(isNullable)
            fieldName = fieldName + "?";

        fieldName = fieldName + "[forEach]";
        return new ValidationBuilder<>(this.rootType, newTargetType, targetType, this, new ValidationBuildingContext(this.validationBuildingContext, rootType, fieldName));
    }

    public B withValidation(String name, ValidationInterface<T, R> validationInterface) {

        NamedValidationInterface<T, R> namedValidationInterface = new NamedValidationInterface<>() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public void validate(T target, ValidationContext<R> validationContext) {
                validationInterface.validate(target, validationContext);
            }
        };

        return withValidation(namedValidationInterface);
    }

    @SafeVarargs
    public final B withValidation(NamedValidationInterface<T, R>... namedValidationInterfaces) {
        for (NamedValidationInterface<T, R> namedValidationInterface : namedValidationInterfaces) {
            this.validationBuildingContext.addValidation(namedValidationInterface);
        }
        return parentBuilder;
    }

    public B toParent() {
        return parentBuilder;
    }

    @SuppressWarnings({"unchecked", "unused", "java:S1452"})
    public <C> ValidationBuilder<C, R, P, ?> toParent(Class<C> tpClass) {
        ValidationBuilder<?, ?, ?, ?> parent = this;
        while (parent != null && !parent.getTargetType().equals(tpClass))
            parent = parent.toParent();

        return (ValidationBuilder<C, R, P, ?>) parent;
    }
}

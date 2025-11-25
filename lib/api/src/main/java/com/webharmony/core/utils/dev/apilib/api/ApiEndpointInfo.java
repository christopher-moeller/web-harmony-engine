package com.webharmony.core.utils.dev.apilib.api;

import com.webharmony.core.api.rest.controller.utils.AbstractCrudController;
import com.webharmony.core.api.rest.model.utils.ResponseResource;
import com.webharmony.core.api.rest.model.utils.apiobject.ApiResource;
import com.webharmony.core.utils.exceptions.InternalServerException;
import com.webharmony.core.utils.reflection.ReflectionUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

@Getter
@Setter
public class ApiEndpointInfo {

    private String id; // combination of url and method e.g. POST_/api/login
    private Class<?> controllerClass;
    private String methodName;
    private String url;
    private String restMethod;
    private Class<?> requestBodyType;
    private Type responseType;
    private Map<String, Class<?>> pathVariableTypes;
    private Map<String, Class<?>> queryParameterTypes;

    private HandlerMethod handlerMethod;

    public Set<Class<?>> buildListOfUsedSchemas() {

        final Set<Class<?>> resultList = new HashSet<>();

        resultList.addAll(Optional.ofNullable(this.requestBodyType)
                .map(this::buildNestedModelsRecursive)
                .orElseGet(Collections::emptySet));

        resultList.addAll(Optional.ofNullable(this.responseType)
                .map(type -> buildCleanResponseType(type, this.controllerClass))
                .filter(ReflectionUtils::isProjectOrCoreClass)
                .map(this::buildNestedModelsRecursive)
                .orElseGet(Collections::emptySet));

        Optional.ofNullable(pathVariableTypes).map(Map::values).orElseGet(Collections::emptyList).forEach(t -> resultList.addAll(buildNestedModelsRecursive(t)));
        Optional.ofNullable(queryParameterTypes).map(Map::values).orElseGet(Collections::emptyList).forEach(t -> resultList.addAll(buildNestedModelsRecursive(t)));

        return resultList;
    }

    public Set<Class<?>> buildListOfUsedEnums() {
        final Set<Class<?>> resultSet = new HashSet<>();
        resultSet.addAll(findAllNestedEnumClasses(this.requestBodyType));
        resultSet.addAll(findAllNestedEnumClasses(this.responseType));
        Optional.ofNullable(pathVariableTypes).map(Map::values).orElseGet(Collections::emptyList).forEach(t -> resultSet.addAll(findAllNestedEnumClasses(t)));
        Optional.ofNullable(queryParameterTypes).map(Map::values).orElseGet(Collections::emptyList).forEach(t -> resultSet.addAll(findAllNestedEnumClasses(t)));
        return resultSet;
    }

    private Set<Class<?>> findAllNestedEnumClasses(Type type) {
        final Set<Class<?>> resultSet = new HashSet<>();
        if(type instanceof Class<?> clazz) {
            if(clazz.isEnum()) {
                resultSet.add(clazz);
            } else if (ReflectionUtils.isProjectOrCoreClass(clazz)){
                for (Field declaredField : clazz.getDeclaredFields()) {
                    resultSet.addAll(findAllNestedEnumClasses(declaredField.getGenericType()));
                }
            }
        } else if(type instanceof ParameterizedType parameterizedType) {
            for (Type actualTypeArgument : parameterizedType.getActualTypeArguments()) {
                resultSet.addAll(findAllNestedEnumClasses(actualTypeArgument));
            }
        }
        return resultSet;
    }

    private Set<Class<?>> buildNestedModelsRecursive(Class<?> currentSchema) {
        return ReflectionUtils.buildSetNestedOfAllNestedTypes(currentSchema);
    }

    private Class<?> buildCleanResponseType(Type type, Class<?> controllerClass) {
        if(type instanceof ParameterizedType parameterizedType) {
            final Type rawType = parameterizedType.getRawType();
            if(ResponseEntity.class.equals(rawType) || List.class.equals(rawType) || ApiResource.class.equals(rawType)) {
                return buildCleanResponseType(parameterizedType.getActualTypeArguments()[0], controllerClass);
            } else if (ResponseResource.class.equals(rawType)) {
                if(AbstractCrudController.class.isAssignableFrom(controllerClass)) {
                    return (Class<?>) (((ParameterizedType) controllerClass.getGenericSuperclass()).getActualTypeArguments()[0]);
                } else {
                    throw new InternalServerException(createNotSupportedTypeErrorMessage(type));
                }
            } else {
                throw new InternalServerException(createNotSupportedTypeErrorMessage(type));
            }
        } else {
            if(type instanceof Class<?> clazz) {
                return clazz;
            } else {
                throw new InternalServerException(createNotSupportedTypeErrorMessage(type));
            }
        }
    }

    private String createNotSupportedTypeErrorMessage(Type type) {
        return String.format("Type '%s' is not supported", type);
    }

}

package com.webharmony.core.service.data.validation.utils;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class ValidationBuildingContext<R> {

    private final ValidationBuildingContext<R> parentContext;
    private final List<ValidationBuildingContext<R>> children = new ArrayList<>();

    private final List<NamedValidationInterface<?, R>> validations = new ArrayList<>();

    private final Class<R> rootType;

    private final String path;

    public ValidationBuildingContext(Class<R> rootType) {
        this(null, rootType, null);
    }

    public ValidationBuildingContext(ValidationBuildingContext<R> parentContext, Class<R> rootType, String newSubPath) {
        this.parentContext = parentContext;
        this.rootType = rootType;
        this.path = buildNewPath(parentContext, newSubPath);

        if(parentContext != null)
            parentContext.addChildContext(this);
    }

    private String buildNewPath(ValidationBuildingContext<R> parentContext, String newSubPath) {
        if(parentContext == null || parentContext.getPath() == null)
            return newSubPath;
        else
            return String.join(".", parentContext.getPath(), newSubPath);
    }

    private void addChildContext(ValidationBuildingContext<R> childContext) {
        this.children.add(childContext);
    }

    void addValidation(NamedValidationInterface<?, R> validationInterface) {
        this.validations.add(validationInterface);
    }

    public ValidationBuildingContext<R> getRootContext() {
        final int maxDepth = 10;
        int level = 0;
        ValidationBuildingContext<R> context = this;
        while (context.parentContext != null && level < maxDepth) {
            context = this.parentContext;
            level++;
        }
        return context;
    }

    public List<ValidationRule<R>> buildValidationRules() {
        Map<String, List<NamedValidationInterface<?, R>>> resultMap = new HashMap<>();
        buildValidationRulesRecursive(this, resultMap);
        return resultMap.entrySet().stream()
                .map(entry -> new ValidationRule<>(this.getRootType(), entry.getKey(), entry.getValue()))
                .toList();
    }

    private void buildValidationRulesRecursive(ValidationBuildingContext<R> context, Map<String, List<NamedValidationInterface<?, R>>> resultMap) {
        List<NamedValidationInterface<?, R>> validationInterfaces = context.getValidations();
        if(!validationInterfaces.isEmpty()) {
            final String contextPath = context.getPath();
            resultMap.putIfAbsent(contextPath, new ArrayList<>());
            resultMap.get(contextPath).addAll(validationInterfaces);
        }

        for(ValidationBuildingContext<R> childContext : context.getChildren())
            buildValidationRulesRecursive(childContext, resultMap);
    }

}

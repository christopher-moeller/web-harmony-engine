package com.webharmony.core.api.rest.model.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class ComplexTypeSchema extends SimpleFieldTypeSchema {

    private Map<String, SimpleFieldTypeSchema> fields = new HashMap<>();

    @Override
    public ComplexTypeSchema createCopy() {
        ComplexTypeSchema complexTypeSchema = new ComplexTypeSchema();
        complexTypeSchema.setSimpleType(getSimpleType());
        complexTypeSchema.setJavaType(getJavaType());
        complexTypeSchema.setJsonType(getJsonType());
        complexTypeSchema.setValidInputSpecification(getValidInputSpecification());

        Map<String, SimpleFieldTypeSchema> fieldsCopy = new HashMap<>();
        fields.forEach((name, schema) -> fieldsCopy.put(name, schema.createCopy()));

        complexTypeSchema.setFields(fieldsCopy);

        return complexTypeSchema;
    }
}

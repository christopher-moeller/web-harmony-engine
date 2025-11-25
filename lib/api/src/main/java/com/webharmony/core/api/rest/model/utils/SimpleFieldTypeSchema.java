package com.webharmony.core.api.rest.model.utils;

import com.webharmony.core.api.rest.model.utils.validinput.ValidInputSpecification;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimpleFieldTypeSchema {

    private String simpleType;
    private String javaType;
    private String jsonType;

    private ValidInputSpecification validInputSpecification;

    public SimpleFieldTypeSchema createCopy() {
        SimpleFieldTypeSchema simpleFieldTypeSchema = new SimpleFieldTypeSchema();
        simpleFieldTypeSchema.setSimpleType(this.simpleType);
        simpleFieldTypeSchema.setJavaType(this.javaType);
        simpleFieldTypeSchema.setJsonType(this.jsonType);
        return simpleFieldTypeSchema;
    }

    public static SimpleFieldTypeSchema of(Class<?> javaType, EJsonType eJsonType) {
        SimpleFieldTypeSchema typeSchema = new SimpleFieldTypeSchema();
        typeSchema.setSimpleType(javaType.getSimpleName());
        typeSchema.setJavaType(javaType.getName());
        typeSchema.setJsonType(eJsonType.name());
        return typeSchema;
    }

}

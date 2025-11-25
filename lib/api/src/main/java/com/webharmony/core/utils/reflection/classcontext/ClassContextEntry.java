package com.webharmony.core.utils.reflection.classcontext;

import lombok.Getter;
import lombok.Setter;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class ClassContextEntry {

    private Package classPackage;
    private Class<?> clazz;

    private String packageName;
    private String className;

    private Set<Class<? extends Annotation>> annotations = new HashSet<>();
    private Set<String> annotationNames = new HashSet<>();

}

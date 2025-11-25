package com.webharmony.core.service.data.mapper;

import com.webharmony.core.api.rest.model.utils.annotations.ReadOnlyAttribute;
import com.webharmony.core.utils.assertions.Assert;
import com.webharmony.core.utils.reflection.ReflectionUtils;
import com.webharmony.core.utils.tuple.Tuple2;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class GenericObjectMapper<A, B> {

    @Getter
    private final Class<A> classA;
    @Getter
    private final Class<B> classB;

    private final List<FieldMapperEntry<A, B>> fieldMappers;

    public GenericObjectMapper(Class<A> classA, Class<B> classB) {
        this.classA = classA;
        this.classB = classB;
        this.fieldMappers = initFieldBinding();
    }

    public B mapAToB(A source, MappingContext mappingContext) {
        final B target = createNewInstanceOfB();
        return mapAToB(source, target, mappingContext);
    }

    public B mapAToB(A source, B target, MappingContext mappingContext) {
        Assert.isNotNull(mappingContext).verify();
        for (FieldMapperEntry<A, B> fieldMapperEntry : fieldMappers) {
            if(!fieldMapperEntry.isFieldAReadonly())
                fieldMapperEntry.getMapper().mapFromAToB(source, target);
        }
        return target;
    }

    public A mapBToA(B source, MappingContext mappingContext) {
        final A target = createNewInstanceOfA();
        return mapBToA(source, target, mappingContext);
    }

    public A mapBToA(B source, A target, MappingContext mappingContext) {
        Assert.isNotNull(mappingContext).verify();
        for (FieldMapperEntry<A, B> fieldMapperEntry : fieldMappers) {
            if(!fieldMapperEntry.isFieldBReadonly())
                fieldMapperEntry.getMapper().mapFromBToA(target, source);
        }
        return target;
    }

    public A createNewInstanceOfA() {
        return ReflectionUtils.createNewInstanceWithEmptyConstructor(classA);
    }

    public B createNewInstanceOfB() {
        return ReflectionUtils.createNewInstanceWithEmptyConstructor(classB);
    }

    private List<FieldMapperEntry<A, B>> initFieldBinding() {
        final List<Field> fieldsOfA = ReflectionUtils.getAllFields(classA, true);
        final List<Field> fieldsOfB = ReflectionUtils.getAllFields(classB, true);

        final List<Tuple2<Field, Field>> aAndBFields = new ArrayList<>();
        for (Field fieldOfA : fieldsOfA) {
            findMatchingFieldInList(fieldOfA, fieldsOfB)
                    .ifPresent(fieldOfB -> aAndBFields.add(Tuple2.of(fieldOfA, fieldOfB)));
        }

        return initFieldBindersForAToB(aAndBFields);
    }

    private Optional<Field> findMatchingFieldInList(Field sourceField, List<Field> targetFields) {
        for (Field targetField : targetFields) {
            if(sourceField.getName().equals(targetField.getName()) && sourceField.getType().equals(targetField.getType())) {
                return Optional.of(targetField);
            }
        }
        return Optional.empty();
    }

    private List<FieldMapperEntry<A, B>> initFieldBindersForAToB(List<Tuple2<Field, Field>> aAndBFields) {

        final List<FieldMapperEntry<A, B>> mappers = new ArrayList<>();

        for (Tuple2<Field, Field> aAndBField : aAndBFields) {

            final Field fieldOfA = aAndBField.getType1();
            final Field fieldOfB = aAndBField.getType2();

            final Method getterOfA = ReflectionUtils.findGetterByField(fieldOfA, classA).orElse(null);
            final Method setterOfA = ReflectionUtils.findSetterByField(fieldOfA, classA).orElse(null);

            final Method getterOfB = ReflectionUtils.findGetterByField(fieldOfB, classB).orElse(null);
            final Method setterOfB = ReflectionUtils.findSetterByField(fieldOfB, classB).orElse(null);

            if(getterOfA == null || setterOfA == null || getterOfB == null || setterOfB == null)
                continue;

            final FieldMapper<A, B> fieldMapper = new FieldMapper<>() {

                @Override
                @SneakyThrows
                public void mapFromAToB(A a, B b) {
                    Object valueOfA = getterOfA.invoke(a);
                    setterOfB.invoke(b, valueOfA);
                }

                @Override
                @SneakyThrows
                public void mapFromBToA(A a, B b) {
                    Object valueOfB = getterOfB.invoke(b);
                    setterOfA.invoke(a, valueOfB);
                }
            };


            final FieldMapperEntry<A, B> mapperEntry = new FieldMapperEntry<>();
            mapperEntry.setFieldOfClassA(fieldOfA);
            mapperEntry.setFieldOfClassB(fieldOfB);
            mapperEntry.setMapper(fieldMapper);

            mappers.add(mapperEntry);

        }

        return mappers;
    }
    
    @Getter
    @Setter
    private static class FieldMapperEntry<A, B> {
        
        private Field fieldOfClassA;
        private Field fieldOfClassB;
        
        private FieldMapper<A, B> mapper;

        public boolean isFieldAReadonly() {
            return getAnnotationForFieldA(ReadOnlyAttribute.class) != null;
        }

        public boolean isFieldBReadonly() {
            return getAnnotationForFieldB(ReadOnlyAttribute.class) != null;
        }

        public <T extends Annotation> T getAnnotationForFieldA(Class<T> annotationType) {
            return AnnotationUtils.getAnnotation(fieldOfClassA, annotationType);
        }

        public <T extends Annotation> T getAnnotationForFieldB(Class<T> annotationType) {
            return AnnotationUtils.getAnnotation(fieldOfClassB, annotationType);
        }
    }

    public interface FieldMapper<A, B> {
        void mapFromAToB(A a, B b);
        void mapFromBToA(A a, B b);
    }
}

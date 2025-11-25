package com.webharmony.core.service.data.mapper;

import com.webharmony.core.api.rest.model.utils.BaseDto;
import com.webharmony.core.api.rest.model.utils.AbstractResourceDto;
import com.webharmony.core.api.rest.model.utils.annotations.ReadOnlyAttribute;
import com.webharmony.core.api.rest.model.utils.apiobject.ApiResource;
import com.webharmony.core.data.enums.ECoreRegistry;
import com.webharmony.core.data.jpa.model.i18n.AppI18nEntityAttribute;
import com.webharmony.core.data.jpa.model.utils.AbstractBaseEntity;
import com.webharmony.core.i18n.EI18nLanguage;
import com.webharmony.core.utils.exceptions.InternalServerException;
import com.webharmony.core.utils.reflection.ReflectionUtils;
import com.webharmony.core.utils.tuple.Tuple2;
import lombok.SneakyThrows;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

public class GenericEntityMapper<D extends BaseDto, E extends AbstractBaseEntity> implements MappingConfiguration.EntityDtoMapper<E, D> {
    private final GenericObjectMapper<D, E> genericObjectMapper;

    public GenericEntityMapper(Class<D> dtoClass, Class<E> entityClass) {
        this.genericObjectMapper = new GenericObjectMapper<>(dtoClass, entityClass);
    }

    @Override
    public D toDto(E entity, D target, MappingContext mappingContext) {
        genericObjectMapper.mapBToA(entity, target, mappingContext);

        if(target instanceof AbstractResourceDto resourceDto) {
            resourceDto.setId(Objects.toString(entity.getUuid()));
        }

        mapEntityFieldsToApiObject(entity, target);
        mapI18nAttributesToDto(entity, target, mappingContext, ECoreRegistry.I18N_DEFAULT_LANGUAGE.getTypedValue(EI18nLanguage.class));

        if(mappingContext.getMapNestedEntityFields())
            mapEntityFieldsToNestedDto(entity, target, mappingContext);

        return target;
    }

    @Override
    public E toEntity(D dto, E target, MappingContext mappingContext) {
        genericObjectMapper.mapAToB(dto, target, mappingContext);
        mapApiObjectFieldsToEntity(dto, target);
        if(mappingContext.getMapNestedEntityFields())
            mapBaseDtoFieldsToEntity(dto, target, mappingContext);
        mapDtoStringsToI18nAttribute(dto, target, mappingContext);
        return target;
    }

    @SuppressWarnings("unchecked")
    public E toEntityUntyped(BaseDto dto, AbstractBaseEntity target, MappingContext mappingContext) {
        return toEntity((D) dto, (E) target, mappingContext);
    }


    public Class<D> getDtoClass() {
        return this.genericObjectMapper.getClassA();
    }

    @SneakyThrows
    public E createNewEntityInstance(MappingContext mappingContext) {
        return genericObjectMapper.createNewInstanceOfB();
    }

    @SneakyThrows
    public D createNewDtoInstance(MappingContext mappingContext) {
        return genericObjectMapper.createNewInstanceOfA();
    }

    @SuppressWarnings("unchecked")
    public D toDtoUntyped(AbstractBaseEntity entity, BaseDto target, MappingContext mappingContext) {
        return toDto((E) entity, (D) target, mappingContext);
    }

    @SneakyThrows
    private void mapEntityFieldsToApiObject(E entity, D target) {

        FieldMapperHandler handler = (targetField, sourceFieldGetter, targetFieldSetter) -> {
            Object sourceValue = sourceFieldGetter.invoke(entity);
            if(sourceValue == null)
                return;

            if(sourceValue instanceof Collection<?> sourceValueCollection) {
                List<ApiResource<AbstractResourceDto>> apiObjects = sourceValueCollection.stream()
                        .map(AbstractBaseEntity.class::cast)
                        .map(GenericMappingUtils::createSimpleReferenceApiObjectByEntity)
                        .toList();

                targetFieldSetter.invoke(target, apiObjects);
            } else {
                ApiResource<?> resultApiObject = GenericMappingUtils.createSimpleReferenceApiObjectByEntity((AbstractBaseEntity) sourceValue);
                targetFieldSetter.invoke(target, resultApiObject);
            }
        };

        this.mapAdditionalFields(entity.getClass(), target.getClass(), AbstractBaseEntity.class, ApiResource.class, handler);
    }

    private void mapI18nAttributesToDto(E entity, D target, MappingContext mappingContext, EI18nLanguage defaultLanguage) {

        FieldMapperHandler handler = (targetField, sourceFieldGetter, targetFieldSetter) -> {

            final AppI18nEntityAttribute entityAttribute = (AppI18nEntityAttribute) sourceFieldGetter.invoke(entity);
            if(entityAttribute != null) {

                String value = entityAttribute.getValueByLanguage(mappingContext.getMappingLanguage())
                                .orElseGet(() -> entityAttribute.getValueByLanguage(defaultLanguage).orElse(null));

                targetFieldSetter.invoke(target, value);
            }

        };

        this.mapAdditionalFields(entity.getClass(), target.getClass(), AppI18nEntityAttribute.class, String.class, handler);

    }

    @SneakyThrows
    private void mapEntityFieldsToNestedDto(E entity, D target, MappingContext mappingContext) {


        FieldMapperHandler handler = (targetField, sourceFieldGetter, targetFieldSetter) -> {
            final Object source = sourceFieldGetter.invoke(entity);
            if(source instanceof Collection<?> persistedSet) {

                if(Collection.class.isAssignableFrom(targetField.getType())) {
                    final List<BaseDto> resultList = new ArrayList<>();
                    for (Object sourceElement : persistedSet) {
                        AbstractBaseEntity baseEntity = (AbstractBaseEntity) sourceElement;
                        Class<?> genericTypeOfTargetList = (Class<?>) ((ParameterizedType) targetField.getGenericType()).getActualTypeArguments()[0];

                        GenericEntityMapper<? extends BaseDto, ? extends AbstractBaseEntity> subEntityMapper = createSubEntityMapper(genericTypeOfTargetList, baseEntity.getClass());
                        BaseDto baseDto = subEntityMapper.createNewDtoInstance(mappingContext);
                        baseDto = subEntityMapper.toDtoUntyped(baseEntity, baseDto, mappingContext);

                        resultList.add(baseDto);
                    }

                    targetFieldSetter.invoke(target, resultList);
                }
            } else {
                AbstractBaseEntity baseEntity = (AbstractBaseEntity) source;
                if(baseEntity != null) {

                    GenericEntityMapper<? extends BaseDto, ? extends AbstractBaseEntity> subEntityMapper = createSubEntityMapper(targetField.getType(), baseEntity.getClass());
                    BaseDto baseDto = subEntityMapper.createNewDtoInstance(mappingContext);
                    baseDto = subEntityMapper.toDtoUntyped(baseEntity, baseDto, mappingContext);

                    targetFieldSetter.invoke(target, baseDto);
                }
            }
        };

        this.mapAdditionalFields(entity.getClass(), target.getClass(), AbstractBaseEntity.class, BaseDto.class, handler);
    }

    @SneakyThrows
    private void mapAdditionalFields(Class<?> sourceClass, Class<?> targetClass, Class<?> fieldSourcesType, Class<?> fieldTargetType, FieldMapperHandler handler) {
        boolean ignoreReadOnlyFields = !AbstractBaseEntity.class.isAssignableFrom(targetClass);

        final List<Field> fieldSources = findAssignableFieldsInTypeOrInTypeOfCollection(sourceClass, fieldSourcesType, ignoreReadOnlyFields);
        final List<Field> fieldTargets = findAssignableFieldsInTypeOrInTypeOfCollection(targetClass, fieldTargetType, ignoreReadOnlyFields);

        for(Field targetField : fieldTargets) {
            Optional<Tuple2<Method, Method>> getterAndSetterPair = findGetterAndSetterPair(fieldSources, targetField, sourceClass, targetClass);
            if (getterAndSetterPair.isEmpty())
                continue;

            handler.handleField(targetField, getterAndSetterPair.get().getType1(), getterAndSetterPair.get().getType2());
        }
    }

    private List<Field> findAssignableFieldsInTypeOrInTypeOfCollection(Class<?> classOfFields, Class<?> typeOfField, boolean ignoreReadOnly) {
        return ReflectionUtils.getAllFields(classOfFields, true)
                .stream()
                .filter(field -> ReflectionUtils.findAssignableClassInTypeOrInTypeOfCollection(field, typeOfField).isPresent())
                .filter(field -> ignoreReadOnly || AnnotationUtils.getAnnotation(field, ReadOnlyAttribute.class) == null)
                .toList();
    }

    private Optional<Tuple2<Method, Method>> findGetterAndSetterPair(List<Field> fieldSources, Field targetField, Class<?> classOfSourceFields, Class<?> classOfTargetField) {
        Method sourceFieldGetter = fieldSources.stream()
                .filter(f -> f.getName().equals(targetField.getName()))
                .findAny()
                .flatMap(f -> ReflectionUtils.findGetterByField(f, classOfSourceFields))
                .orElse(null);

        Method targetFieldSetter = ReflectionUtils.findSetterByField(targetField, classOfTargetField).orElse(null);

        if(sourceFieldGetter == null || targetFieldSetter == null)
            return Optional.empty();
        else
            return Optional.of(Tuple2.of(sourceFieldGetter, targetFieldSetter));
    }

    @SneakyThrows
    private void mapApiObjectFieldsToEntity(D dto, E target) {

        FieldMapperHandler handler = (targetField, sourceFieldGetter, targetFieldSetter) -> {
            @SuppressWarnings("unchecked")
            Class<? extends AbstractBaseEntity> targetEntityType = (Class<? extends AbstractBaseEntity>) ReflectionUtils.findAssignableClassInTypeOrInTypeOfCollection(targetField, AbstractBaseEntity.class).orElseThrow();

            Object sourceValue = sourceFieldGetter.invoke(dto);
            if(sourceValue == null) {
                targetFieldSetter.invoke(target, (Object) null);
            }else if(sourceValue instanceof Collection<?> sourceValueCollection) {
                Set<AbstractBaseEntity> resultEntities = sourceValueCollection.stream()
                        .map(ApiResource.class::cast)
                        .map(apiObject -> GenericMappingUtils.loadEntityByApiObject(apiObject, targetEntityType))
                        .collect(Collectors.toSet());

                targetFieldSetter.invoke(target, resultEntities);
            } else {
                AbstractBaseEntity resultEntity = GenericMappingUtils.loadEntityByApiObject((ApiResource<?>) sourceValue, targetEntityType);
                targetFieldSetter.invoke(target, resultEntity);
            }
        };

        this.mapAdditionalFields(dto.getClass(), target.getClass(), ApiResource.class, AbstractBaseEntity.class, handler);
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    private void mapBaseDtoFieldsToEntity(D dto, E target, MappingContext mappingContext) {

        FieldMapperHandler handler = (targetField, sourceFieldGetter, targetFieldSetter) -> {

            final Object sourceField = sourceFieldGetter.invoke(dto);
            if(sourceField instanceof Collection<?> sourceValueCollection) {

                if(Collection.class.isAssignableFrom(targetField.getType())) {
                    final Set<AbstractBaseEntity> resultList = new HashSet<>();
                    for (Object sourceElement : sourceValueCollection) {
                        BaseDto baseDto = (BaseDto) sourceElement;
                        Class<?> genericTypeOfTargetList = (Class<?>) ((ParameterizedType) targetField.getGenericType()).getActualTypeArguments()[0];

                        GenericEntityMapper<? extends BaseDto, ? extends AbstractBaseEntity> subEntityMapper = createSubEntityMapper(baseDto.getClass(), (Class<? extends AbstractBaseEntity>) genericTypeOfTargetList);
                        AbstractBaseEntity baseEntity = subEntityMapper.createNewEntityInstance(mappingContext);
                        baseEntity = subEntityMapper.toEntityUntyped(baseDto, baseEntity, mappingContext);

                        resultList.add(baseEntity);
                    }

                    targetFieldSetter.invoke(target, resultList);
                }


            } else {
                BaseDto baseDto = (BaseDto) sourceFieldGetter.invoke(dto);

                Method targetFieldGetter = ReflectionUtils.findGetterByField(targetField, target.getClass()).orElseThrow();
                AbstractBaseEntity baseEntity = (AbstractBaseEntity) Optional.ofNullable(targetFieldGetter.invoke(target))
                        .orElseGet(() -> ReflectionUtils.createNewInstanceWithEmptyConstructor(targetField.getType()));

                GenericEntityMapper<? extends BaseDto, ? extends AbstractBaseEntity> subEntityMapper = createSubEntityMapper(baseDto.getClass(), baseEntity.getClass());
                baseEntity = subEntityMapper.toEntityUntyped(baseDto, baseEntity, mappingContext);

                targetFieldSetter.invoke(target, baseEntity);
            }
        };

        this.mapAdditionalFields(dto.getClass(), target.getClass(), BaseDto.class, AbstractBaseEntity.class, handler);
    }

    private void mapDtoStringsToI18nAttribute(D dto, E target, MappingContext mappingContext) {
        FieldMapperHandler handler = (targetField, sourceFieldGetter, targetFieldSetter) -> {

            final String value = (String) sourceFieldGetter.invoke(dto);

            AppI18nEntityAttribute i18nEntityAttribute = (AppI18nEntityAttribute) ReflectionUtils.findGetterByField(targetField, target.getClass())
                    .orElseThrow(() -> new InternalServerException(String.format("No getter found for field '%s' in class '%s'", targetField.getName(), target.getClass())))
                    .invoke(target);

            if(i18nEntityAttribute == null) {
                i18nEntityAttribute = new AppI18nEntityAttribute();
                targetFieldSetter.invoke(target, i18nEntityAttribute);
            }

            i18nEntityAttribute.putAttributeValue(mappingContext.getMappingLanguage(), value);
        };

        this.mapAdditionalFields(dto.getClass(), target.getClass(), String.class, AppI18nEntityAttribute.class, handler);
    }

    @SuppressWarnings("unchecked")
    private GenericEntityMapper<? extends BaseDto, ? extends AbstractBaseEntity> createSubEntityMapper(Class<?> dtoClass, Class<? extends AbstractBaseEntity> entityClass) {
        return new GenericEntityMapper<>((Class<? extends BaseDto>) dtoClass, entityClass);
    }

    private interface FieldMapperHandler {
        void handleField(Field targetField, Method sourceFieldGetter, Method targetFieldSetter) throws InvocationTargetException, IllegalAccessException;
    }

}

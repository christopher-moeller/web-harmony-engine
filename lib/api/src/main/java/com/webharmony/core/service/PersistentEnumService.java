package com.webharmony.core.service;

import com.webharmony.core.data.enums.utils.PersistenceEnum;
import com.webharmony.core.data.jpa.model.utils.AbstractEnumEntity;
import com.webharmony.core.service.data.utils.DataQueryCondition;
import com.webharmony.core.utils.exceptions.EntityNotFoundException;
import com.webharmony.core.utils.exceptions.InternalServerException;
import com.webharmony.core.utils.reflection.ReflectionUtils;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PersistentEnumService {

    @Getter
    private final Map<Class<? extends AbstractEnumEntity>, List<Class<? extends Enum<?>>>> persistenceEnumEntityMap;
    private final EntityService entityService;


    public PersistentEnumService(EntityService entityService) {
        this.entityService = entityService;
        this.persistenceEnumEntityMap = loadPersistenceEnumClasses();
    }

    @SuppressWarnings("unchecked")
    private Map<Class<? extends AbstractEnumEntity>, List<Class<? extends Enum<?>>>> loadPersistenceEnumClasses() {
        Map<Class<? extends AbstractEnumEntity>, List<Class<? extends Enum<?>>>> resultMap = new HashMap<>();
        for(var persistenceEnumClass : ReflectionUtils.getAllProjectClassesImplementingSuperClass(PersistenceEnum.class)) {
            if(Enum.class.isAssignableFrom(persistenceEnumClass)) {
                Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) persistenceEnumClass;
                Class<? extends AbstractEnumEntity> entityClass = findEntityByEnumClass(enumClass);
                resultMap.computeIfAbsent(entityClass, c -> new ArrayList<>());
                resultMap.get(entityClass).add(enumClass);
            }
        }

        return resultMap;
    }

    public boolean isCreatedByCoreEnum(AbstractEnumEntity enumEntity) {
        PersistenceEnum<AbstractEnumEntity> persistenceEnumByEntity = findPersistenceEnumByEntity(enumEntity);
        return ReflectionUtils.isCoreClass(persistenceEnumByEntity.getClass());
    }

    public <E extends AbstractEnumEntity> PersistenceEnum<E> findPersistenceEnumByEntity(E enumEntity) {
        return findPersistenceEnumByEntityIfExists(enumEntity)
                .orElseThrow(() -> new InternalServerException(String.format("No enum constant for entity %s:%s found", enumEntity.getClass().getName(), enumEntity.getReadableId())));
    }

    @SuppressWarnings("unchecked")
    public <E extends AbstractEnumEntity> Optional<PersistenceEnum<E>> findPersistenceEnumByEntityIfExists(E enumEntity) {
        final List<Class<? extends Enum<?>>> enumClasses = Optional.ofNullable(persistenceEnumEntityMap.get(enumEntity.getClass()))
                .orElseGet(Collections::emptyList);

        final String uniqueEntityName = enumEntity.getUniqueName();
        for (Class<? extends Enum<?>> enumClass : enumClasses) {
            for (Enum<?> enumConstant : enumClass.getEnumConstants()) {
                if(enumConstant.name().equals(uniqueEntityName))
                    return Optional.of((PersistenceEnum<E>) enumConstant);
            }
        }

        return Optional.empty();
    }

    private Class<? extends AbstractEnumEntity> findEntityByEnumClass(Class<? extends Enum<?>> enumClass) {

        Enum<?>[] enumConstants = enumClass.getEnumConstants();
        if(enumConstants.length == 0)
            throw new InternalServerException(String.format("Error for persistence enum '%s': Every persistence enum needs at least one constant", enumClass.getName()));

        PersistenceEnum<?> persistenceEnum = (PersistenceEnum<?>) enumConstants[0];
        return persistenceEnum.getEntityClass();
    }

    @SuppressWarnings("unchecked")
    public void initAllEnumEntities() {
        for (Map.Entry<Class<? extends AbstractEnumEntity>, List<Class<? extends Enum<?>>>> classListEntry : persistenceEnumEntityMap.entrySet()) {
            for(Class<? extends Enum<?>> enumClass : classListEntry.getValue()) {
                Class<? extends PersistenceEnum<?>> enumAsPersistenceEnumClass = (Class<? extends PersistenceEnum<?>>) enumClass;
                for(PersistenceEnum<?> enumConstant : enumAsPersistenceEnumClass.getEnumConstants()) {
                    try {
                        AbstractEnumEntity enumEntity = loadEntityByEnum(enumConstant);
                        if(enumEntity.reInitOnStartUp()) {
                            enumConstant.initUntypedEntity(enumEntity);
                            saveNewInstance(enumEntity);
                        }

                    } catch (EntityNotFoundException e) {
                        AbstractEnumEntity newEntity = createNewInstance(classListEntry.getKey());
                        enumConstant.initUntypedEntity(newEntity);
                        newEntity.setUniqueName(enumConstant.name());
                        saveNewInstance(newEntity);
                    }
                }
            }
        }
    }

    private void saveNewInstance(AbstractEnumEntity newEntity) {
        entityService.saveEntityGeneric(newEntity);
    }

    @SneakyThrows
    private AbstractEnumEntity createNewInstance(Class<? extends AbstractEnumEntity> entityImplementationClass) {
        return entityImplementationClass.getDeclaredConstructor().newInstance();
    }

    public <E extends AbstractEnumEntity> E loadEntityByEnum(PersistenceEnum<E> persistenceEnum) throws EntityNotFoundException {
        Class<? extends E> entityClass = persistenceEnum.getEntityClass();
        String fieldName = ReflectionUtils.getFieldNameByGetterMethod(AbstractEnumEntity.class, AbstractEnumEntity::getUniqueName);
        DataQueryCondition condition = new DataQueryCondition(fieldName, persistenceEnum.name());
        return Optional.ofNullable(entityService.loadSingleEntityByTypeAndConditions(entityClass, condition))
                .orElseThrow(() -> new EntityNotFoundException(String.format("Enum entity of type '%s with name '%s' not found in database'", entityClass.getName(), persistenceEnum.name())));
    }
}

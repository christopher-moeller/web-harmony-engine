package com.webharmony.core.service;

import com.webharmony.core.data.jpa.model.utils.AbstractBaseEntity;
import com.webharmony.core.data.jpa.model.utils.AbstractEnumEntity;
import com.webharmony.core.data.jpa.model.utils.EntityWithReadableId;
import com.webharmony.core.service.data.utils.DataQueryCondition;
import com.webharmony.core.utils.EntityHelper;
import com.webharmony.core.utils.assertions.Assert;
import com.webharmony.core.utils.exceptions.InternalServerException;
import com.webharmony.core.utils.reflection.ReflectionUtils;
import jakarta.persistence.*;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.EntityType;
import lombok.Getter;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EntityService
{
    private final EntityManagerFactory entityManagerFactory;

    @Getter
    @PersistenceContext
    private EntityManager entityManager;
    private final Set<EntityType<?>> entityTypes;

    private final Map<Class<?>, JpaRepository<?, ?>> entityRepositoryMap;

    public EntityService(ApplicationContext applicationContext, ListableBeanFactory factory) {
        this.entityManagerFactory = applicationContext.getBean(EntityManagerFactory.class);
        this.entityTypes = loadEntityTypes();
        this.entityRepositoryMap = loadEntityRepositoryMap(factory);
    }

    private Map<Class<?>, JpaRepository<?,?>> loadEntityRepositoryMap(ListableBeanFactory factory) {

        Map<Class<?>, JpaRepository<?,?>> repositoryMap = new HashMap<>();

        Repositories repositories = new Repositories(factory);
        for(EntityType<?> entityType : entityTypes) {
            Class<?> entityClass = entityType.getJavaType();
            repositories.getRepositoryFor(entityClass)
                    .map(repo -> (JpaRepository<?,?>) repo)
                    .ifPresent(repo -> repositoryMap.put(entityClass, repo));
        }
        return repositoryMap;
    }


    private Set<EntityType<?>> loadEntityTypes() {
        Set<EntityType<?>> types = entityManagerFactory.getMetamodel().getEntities();
        for(EntityType<?> entityType : types) {
            Class<?> entityClass = entityType.getJavaType();
            Assert.hasAnnotation(entityClass, Table.class)
                    .withException(() -> new InternalServerException(String.format("@Table annotation is missing for entity '%s'", entityClass.getName())))
                    .verify();

            String customTableName = Optional.ofNullable(AnnotationUtils.findAnnotation(entityClass, Table.class))
                    .map(Table::name)
                    .orElse(null);

            Assert.isNotEmpty(customTableName)
                    .withException(() -> new InternalServerException(String.format("@Table annotation has a missing name for entity '%s'", entityClass.getName())))
                    .verify();

            for(Attribute<?, ?> attribute : entityType.getAttributes()) {
                Member member = attribute.getJavaMember();
                Field field = ReflectionUtils.getFieldByName(member.getDeclaringClass(), member.getName());

                if(AnnotationUtils.findAnnotation(field, ManyToMany.class) == null && AnnotationUtils.findAnnotation(field, OneToMany.class) == null && AnnotationUtils.findAnnotation(field, ManyToOne.class) == null && AnnotationUtils.findAnnotation(field, OneToOne.class) == null) {
                    Assert.hasAnnotation(field, Column.class)
                            .withException(() -> new InternalServerException(String.format("Entity field '%s' for class '%s' has missing @Column annotation", member.getName(), member.getDeclaringClass().getName())))
                            .verify();

                    String customColumnName = EntityHelper.getColumnName(field);

                    Assert.isNotEmpty(customColumnName)
                            .withException(() -> new InternalServerException(String.format("Entity field '%s' for class '%s' has missing name in @Column annotation", member.getName(), member.getDeclaringClass().getName())))
                            .verify();
                }

            }

        }
        return types;
    }

    @SuppressWarnings("unchecked")
    public <E extends AbstractBaseEntity> Optional<JpaRepository<E, UUID>> findRepositoryForEntity(Class<E> entityClass) {
        return Optional.ofNullable(entityRepositoryMap.get(entityClass))
                .map(repo -> (JpaRepository<E, UUID>) repo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveEntityGeneric(AbstractBaseEntity entity) {
        if(entity.isNew()) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
    }

    @SuppressWarnings("unchecked")
    public <E extends AbstractBaseEntity> List<E> findEntityAllEntitiesByClass(Class<E> entityClass) {
        return entityManager.createQuery(String.format("select e from %s e", entityClass.getSimpleName())).getResultList();
    }

    @SuppressWarnings("unchecked")
    public <E extends AbstractBaseEntity> Optional<E> findEntityByReadableId(Class<E> entityClass, String readableId) {
        if(!EntityWithReadableId.class.isAssignableFrom(entityClass)) {
            throw new IllegalArgumentException("Entity with needs to be of type " + EntityWithReadableId.class);
        }

        final EntityWithReadableId dummyInstance = (EntityWithReadableId) ReflectionUtils.createNewInstanceWithEmptyConstructor(entityClass);
        final EntityWithReadableId entityByReadableId = dummyInstance.getEntityLoader().findEntityByReadableId(entityManager, readableId);
        return Optional.ofNullable((E) entityByReadableId);
    }
    public <E extends AbstractBaseEntity> Optional<E> findEntityById(Class<E> entityClass, UUID id) {
        try {
            return Optional.ofNullable(entityManager.find(entityClass, id));
        }catch (EntityNotFoundException e) {
            return Optional.empty();
        }
    }

    public <E extends AbstractEnumEntity> Optional<E> findEnumEntityByUniqueName(Class<E> entityClass, String uniqueName) {
        return Optional.ofNullable(loadSingleEntityByTypeAndConditions(entityClass, new DataQueryCondition("uniqueName", uniqueName)));
    }

    public <E extends AbstractBaseEntity> E loadSingleEntityByTypeAndConditions(Class<E> entityClass, DataQueryCondition... conditions) {
        final String jpaBasicSelect = String.format("select e from %s e", entityClass.getSimpleName());
        final StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(jpaBasicSelect);

        if(conditions.length > 0) {
            queryBuilder.append(" where ");
            String conditionsPart = Arrays.stream(conditions)
                    .map(DataQueryCondition::getKey)
                    .map(name -> String.format("e.%s = :%s", name, name))
                    .collect(Collectors.joining(" and "));

            queryBuilder.append(conditionsPart);
        }

        TypedQuery<E> typedQuery = entityManager.createQuery(queryBuilder.toString(), entityClass);
        for (DataQueryCondition condition : conditions) {
            typedQuery.setParameter(condition.getKey(), condition.getValue());
        }

        try {
            return typedQuery.getSingleResult();
        }catch (NoResultException e) {
            return null;
        }
    }


}

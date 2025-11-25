package com.webharmony.core.service.data.utils;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.webharmony.core.api.rest.controller.utils.RestRequestParams;
import com.webharmony.core.api.rest.controller.utils.request.RequestContext;
import com.webharmony.core.api.rest.controller.utils.request.SearchRequestContext;
import com.webharmony.core.api.rest.model.utils.BaseDto;
import com.webharmony.core.data.jpa.model.utils.AbstractBaseEntity;
import com.webharmony.core.i18n.I18N;
import com.webharmony.core.i18n.I18nTranslation;
import com.webharmony.core.service.EntityService;
import com.webharmony.core.service.data.mapper.GenericEntityMapper;
import com.webharmony.core.service.data.mapper.MappingConfiguration;
import com.webharmony.core.service.data.mapper.MappingContext;
import com.webharmony.core.service.data.validation.ValidatorService;
import com.webharmony.core.utils.JacksonUtils;
import com.webharmony.core.utils.exceptions.EntityNotFoundException;
import com.webharmony.core.utils.exceptions.ResourceNotFoundException;
import com.webharmony.core.utils.reflection.ReflectionUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class AbstractEntityCrudService<D extends BaseDto, E extends AbstractBaseEntity> implements ApplicationContextAware, I18nTranslation {

    private final I18N i18N = createI18nInstance(AbstractEntityCrudService.class);

    @Getter
    private Class<E> implementationEntityClass;

    @Getter
    private JpaRepository<E, UUID> repository;

    @Getter
    private final Class<D> dtoClass;

    @Getter
    private final Class<E> entityClass;

    private final XmlMapper xmlMapper;

    @Autowired
    @Getter
    private ValidatorService validatorService;

    @PersistenceContext
    @Getter
    private EntityManager entityManager;

    @Getter
    private MappingConfiguration<D, E> mappingConfiguration;


    protected AbstractEntityCrudService() {
        this.dtoClass = loadGenericTypeByIndex(0);
        this.entityClass = loadGenericTypeByIndex(1);
        this.xmlMapper = JacksonUtils.createDefaultXmlMapper();
        this.xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.implementationEntityClass = getEntityClass();
        this.mappingConfiguration = createMappingConfiguration();
        this.repository = loadOrCreateRepository(applicationContext);
        this.buildValidator();
    }

    @Transactional(readOnly = true)
    public List<E> getAllEntities() {
        return repository.findAll();
    }


    @Transactional(readOnly = true)
    public Page<D> getAllEntries(RequestContext requestContext, RestRequestParams.RestSort... sorts) {
        MappingContext mappingContext = MappingContext.of(requestContext);
        mappingContext.setMapNestedEntityFields(false);
        final Page<E> entityPage;
        if(requestContext instanceof SearchRequestContext searchRequestContext) {
            entityPage = repository.findAll(createPageable(searchRequestContext, sorts));
        } else {
            List<E> allEntities = repository.findAll();
            entityPage = new PageImpl<>(allEntities, Pageable.unpaged(), allEntities.size());
        }
        return entityPage
                .map(entity -> mappingConfiguration.getEffectiveToDtoMapper().toDto(entity, mappingConfiguration.getNewDtoInstanceCreator().createNewDtoInstance(mappingContext), mappingContext));
    }

    private Pageable createPageable(SearchRequestContext searchRequestContext, RestRequestParams.RestSort... sorts) {
        return createSpringSort(sorts)
                .map(sort -> createPageRequest(searchRequestContext, sort))
                .orElseGet(() -> createPageRequest(searchRequestContext));
    }

    private PageRequest createPageRequest(SearchRequestContext searchRequestContext) {
        return createPageRequest(searchRequestContext, null);
    }
    private PageRequest createPageRequest(SearchRequestContext searchRequestContext, Sort sort) {
        final boolean isPaged = searchRequestContext.getRestRequestParams().getIsPaged();
        if(isPaged) {
            final int page = searchRequestContext.getRestRequestParams().getPage();
            final int size = searchRequestContext.getRestRequestParams().getSize();
            return PageRequest.of(page, size, sort != null ? sort : Sort.unsorted());
        } else {
            return createCustomNotPagedRequest(sort);
        }

    }

    private PageRequest createCustomNotPagedRequest(Sort sort) {
        return new PageRequest(0, 1, sort != null ? sort : Sort.unsorted()) {
            @Override
            public boolean isUnpaged() {
                return true;
            }

            @Override
            public boolean isPaged() {
                return false;
            }
        };
    }

    private Optional<Sort> createSpringSort(RestRequestParams.RestSort... sorts) {
        if(sorts.length == 0) {
            return Optional.empty();
        }

        List<Sort.Order> springOrders = Arrays.stream(sorts)
                .map(restSort -> new Sort.Order(Sort.Direction.fromString(restSort.getOrder().name()), restSort.getName()))
                .toList();

        return Optional.of(Sort.by(springOrders));
    }

    @Transactional(readOnly = true)
    public D getEntryById(UUID uuid, RequestContext requestContext) {
        return repository.findById(uuid)
                .map(entity -> mapEntityToDto(entity, requestContext))
                .orElseThrow(() -> new ResourceNotFoundException(getDtoClass(), uuid));
    }

    protected D mapEntityToDto(E entity, RequestContext requestContext) {
        final MappingContext mappingContext = MappingContext.of(requestContext);
        mappingContext.setMapNestedEntityFields(mappingConfiguration.getMapNestedFields());
        return mappingConfiguration.getEffectiveToDtoMapper().toDto(entity, mappingConfiguration.getNewDtoInstanceCreator().createNewDtoInstance(mappingContext), MappingContext.of(requestContext));
    }
    public E getEntityById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(i18N.translate("Entity '{entityClass}' with id '{entityId}' not found").add("entityClass", getEntityClass()).add("entityId", id).build()));
    }

    @Transactional
    public D createNewEntry(D dto, RequestContext requestContext) {
        return createNewEntry(dto, requestContext, true);
    }

    @Transactional
    public D createNewEntry(D dto, RequestContext requestContext, boolean withValidation) {

        if(withValidation)
            validateOnSave(dto);

        MappingContext mappingContext = MappingContext.of(requestContext);
        mappingContext.setMapNestedEntityFields(mappingConfiguration.getMapNestedFields());
        E entity = mappingConfiguration.getNewEntityInstanceCreator().createNewEntityInstance(mappingContext);

        entity = mapAndSave(dto, entity, mappingContext);
        return mappingConfiguration.getEffectiveToDtoMapper().toDto(entity, mappingConfiguration.getNewDtoInstanceCreator().createNewDtoInstance(mappingContext), mappingContext);
    }

    @Transactional
    public D updateEntry(UUID uuid, D dto, RequestContext requestContext) {

        validateOnSave(dto);

        MappingContext mappingContext = MappingContext.of(requestContext);
        mappingContext.setMapNestedEntityFields(mappingConfiguration.getMapNestedFields());
        E entity = repository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException(getDtoClass(), uuid));

        entity = mapAndSave(dto, entity, mappingContext);
        return mappingConfiguration.getEffectiveToDtoMapper().toDto(entity, mappingConfiguration.getNewDtoInstanceCreator().createNewDtoInstance(mappingContext), mappingContext);
    }

    @Transactional
    public E mapAndSave(D dto, E originalEntity, MappingContext mappingContext) {
        E mappedEntity = mappingConfiguration.getEffectiveToEntityMapper().toEntity(dto, originalEntity, mappingContext);
        return saveEntity(mappedEntity);
    }

    @Transactional
    public E saveEntity(E entity) {
        return repository.save(entity);
    }

    @Transactional
    @SuppressWarnings("unused")
    public void deleteEntry(UUID uuid, RequestContext requestContext) {
        E entity = getEntityById(uuid);
        deleteEntity(entity);
    }

    @Transactional
    public void deleteEntity(E entity) {
        repository.delete(entity);
    }

    @Transactional
    public void deleteAll() {
        repository.deleteAll();
    }

    private JpaRepository<E, UUID> loadOrCreateRepository(ApplicationContext applicationContext) {
        return applicationContext.getBean(EntityService.class)
                .findRepositoryForEntity(implementationEntityClass)
                .orElseGet(() -> createSimpleRepository(applicationContext));
    }

    private JpaRepository<E, UUID> createSimpleRepository(ApplicationContext applicationContext) {
        EntityManager em = applicationContext.getBean(EntityManager.class);
        return new SimpleJpaRepository<>(implementationEntityClass, em);
    }

    @SuppressWarnings("unchecked")
    private <T> Class<T> loadGenericTypeByIndex(int index) {
        return ReflectionUtils.getGenericTypeByClassOrSuperClassesAndIndex(getClass(), index)
                .map(c -> (Class<T>) c)
                .orElseThrow();
    }

    private MappingConfiguration<D, E> createMappingConfiguration() {
        GenericEntityMapper<D, E> genericEntityMapper = new GenericEntityMapper<>(dtoClass, implementationEntityClass);

        MappingConfiguration<D, E> configuration = MappingConfiguration.of(implementationEntityClass, dtoClass)
                .withNewEntityInstanceCreator(genericEntityMapper)
                .withNewDtoInstanceCreator(genericEntityMapper)
                .withToEntityMapper(genericEntityMapper)
                .withToDtoMapper(genericEntityMapper);


        configureMapping(configuration);

        return configuration;
    }

    protected void configureMapping(MappingConfiguration<D, E> mappingConfiguration) {

    }

    @SneakyThrows
    protected String writeXmlValueAsString(Object value) {
        return this.xmlMapper.writeValueAsString(value);
    }

    @SneakyThrows
    @SuppressWarnings("SameParameterValue")
    protected <T> T readXmlValueFromString(String value, Class<T> targetType) {
        return this.xmlMapper.readValue(value, targetType);
    }

    protected void validateOnSave(D dto) {
        validatorService.validate(dto);
    }

    private void buildValidator() {
        validatorService.precomputeValidatorForClass(getDtoClass());
    }

}

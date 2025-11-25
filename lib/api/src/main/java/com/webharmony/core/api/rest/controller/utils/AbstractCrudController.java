package com.webharmony.core.api.rest.controller.utils;


import com.webharmony.core.api.rest.controller.utils.dispacher.ControllerDispatcher;
import com.webharmony.core.api.rest.controller.utils.request.RequestContext;
import com.webharmony.core.api.rest.model.RestResourceInfo;
import com.webharmony.core.api.rest.model.utils.*;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.configuration.security.ApplicationRight;
import com.webharmony.core.service.searchcontainer.AbstractSearchContainer;
import com.webharmony.core.service.searchcontainer.utils.DefaultEntityCurdSearchContainer;
import com.webharmony.core.service.searchcontainer.utils.SearchContainerAttribute;
import com.webharmony.core.service.searchcontainer.utils.SearchResult;
import com.webharmony.core.utils.events.HarmonyEventCreator;
import com.webharmony.core.utils.reflection.ReflectionUtils;
import lombok.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class AbstractCrudController<D extends AbstractResourceDto> extends AbstractBaseController implements ApplicationContextAware {

    private ControllerDispatcher<D> controllerDispatcher;

    private AbstractSearchContainer searchContainer;

    private ApplicationContext applicationContext;

    private ComplexTypeSchema complexTypeSchema;

    private CrudControllerMethodRights crudControllerMethodRights;


    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        this.crudControllerMethodRights = buildMethodRights();
        this.initSecuredMethods();
    }

    @SuppressWarnings("unchecked")
    private void initSecuredMethods() {

        if(this.crudControllerMethodRights.getAllRight() != null)
            addSecuredMethod(ReflectionUtils.getMethodByClass(AbstractCrudController.class, c -> c.getAllEntries(null)));

        if(this.crudControllerMethodRights.getByIdRight() != null)
            addSecuredMethod(ReflectionUtils.getMethodByClass(AbstractCrudController.class, c -> c.getEntryById(null)));

        if(this.crudControllerMethodRights.createNewRight() != null)
            addSecuredMethod(ReflectionUtils.getMethodByClass(AbstractCrudController.class, c -> c.createNewEntry(null)));

        if(this.crudControllerMethodRights.updateRight() != null)
            addSecuredMethod(ReflectionUtils.getMethodByClass(AbstractCrudController.class, c -> c.updateEntry(ReflectionUtils.getApiLinkMockValue(), null)));

        if(this.crudControllerMethodRights.deleteRight() != null)
            addSecuredMethod(ReflectionUtils.getMethodByClass(AbstractCrudController.class, c -> c.deleteEntry(ReflectionUtils.getApiLinkMockValue())));

    }

    @GetMapping
    public ResponseEntity<SearchResult> getAllEntries(RestRequestParams restRequestParams) {
        ContextHolder.getContext().assertCurrentActorHasRight(crudControllerMethodRights.getAllRight());
        return ResponseEntity.ok(getControllerDispatcher().getAllEntries(RequestContext.search(applicationContext, restRequestParams)));
    }

    @GetMapping("{id}")
    public ResponseResource<D> getEntryById(@PathVariable("id") UUID uuid) {
        ContextHolder.getContext().assertCurrentActorHasRight(crudControllerMethodRights.getByIdRight());
        return ResponseResource.ok(getDataClass(), getControllerDispatcher().getEntryById(uuid, RequestContext.empty(applicationContext)));
    }

    @PostMapping
    public ResponseResource<D> createNewEntry(@RequestBody D dto) {
        ContextHolder.getContext().assertCurrentActorHasRight(crudControllerMethodRights.createNewRight());
        final D newEntry = getControllerDispatcher().createNewEntry(dto, RequestContext.empty(applicationContext));
        HarmonyEventCreator.createResourceCreatedEvent(getRestResource().getId(), newEntry.getId(), newEntry);
        return ResponseResource.ok(getDataClass(), newEntry);
    }

    @PutMapping("{id}")
    public ResponseResource<D> updateEntry(@PathVariable("id") UUID uuid, @RequestBody D dto) {
        ContextHolder.getContext().assertCurrentActorHasRight(crudControllerMethodRights.updateRight());
        final D updateEntry = getControllerDispatcher().updateEntry(uuid, dto, RequestContext.empty(applicationContext));
        HarmonyEventCreator.createResourceUpdatedEvent(getRestResource().getId(), uuid.toString(), dto);
        return ResponseResource.ok(getDataClass(), updateEntry);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteEntry(@PathVariable("id") UUID uuid) {
        ContextHolder.getContext().assertCurrentActorHasRight(crudControllerMethodRights.deleteRight());
        getControllerDispatcher().deleteEntry(uuid, RequestContext.empty(applicationContext));
        HarmonyEventCreator.createResourceDeletedEvent(getRestResource().getId(), uuid.toString());
        return ResponseEntity.ok().build();
    }

    public Class<D> getDataClass() {
        return ReflectionUtils.getGenericTypeByClassAndIndexOrThrow(this.getClass(), 0);
    }

    public abstract RestResourceInfo getRestResource();

    private ComplexTypeSchema createResponseTypeSchema() {

        Class<? extends BaseDto> dtoClass = getDataClass();
        ComplexTypeSchema typeSchema = new ComplexTypeSchema();

        typeSchema.setSimpleType(dtoClass.getSimpleName());
        typeSchema.setJavaType(dtoClass.getName());
        typeSchema.setFields(getFieldsForResource(dtoClass));
        typeSchema.setJsonType(EJsonType.OBJECT.name());

        return typeSchema;
    }

    public ResourceOverviewTypeSchema getResourceOverviewTypeSchema(RequestContext requestContext) {

        final AbstractSearchContainer searchContainerForResource = getSearchContainer();

        ResourceOverviewTypeSchema typeSchema = new ResourceOverviewTypeSchema();
        typeSchema.setFields(getFieldsForResourceOverview(searchContainerForResource, requestContext));
        typeSchema.setSortOptions(searchContainerForResource.getAvailableSortAttributesForCurrentUser(requestContext));

        List<RestFilterInfo> restFilterInfos = searchContainerForResource.getAvailableSearchFilter()
                .stream()
                .map(restFilter -> new RestFilterInfo(restFilter.getFilterName(), restFilter.getFilterType()))
                .toList();

        typeSchema.setFilters(restFilterInfos);

        return typeSchema;
    }

    private Map<String, SimpleFieldTypeSchema> getFieldsForResource(Class<? extends BaseDto> dtoClass) {
        return ReflectionUtils.createFieldMapForType(dtoClass);
    }

    private Map<String, SimpleFieldTypeSchema> getFieldsForResourceOverview(AbstractSearchContainer searchContainer, RequestContext requestContext) {
        return searchContainer.getAvailableAttributesForCurrentUser(requestContext)
                .stream()
                .collect(Collectors.toMap(SearchContainerAttribute::getName, a -> SimpleFieldTypeSchema.of(String.class, EJsonType.FIELD)));
    }

    public ControllerDispatcher<D> getControllerDispatcher() {
        if(controllerDispatcher == null)
            controllerDispatcher = loadControllerDispatcherInternal();

        return controllerDispatcher;
    }

    public AbstractSearchContainer getSearchContainer() {
        if(searchContainer == null)
            searchContainer = loadSearchContainerInternal();

        return searchContainer;
    }

    protected abstract ControllerDispatcher<D> loadControllerDispatcherInternal();

    private AbstractSearchContainer loadSearchContainerInternal() {
        return new DefaultEntityCurdSearchContainer(getDataClass());
    }

    public ComplexTypeSchema getComplexTypeSchema() {
        if(complexTypeSchema == null)
            complexTypeSchema = createResponseTypeSchema();

        return complexTypeSchema;
    }

    private CrudControllerMethodRights buildMethodRights() {

        final ApplicationRight fallBackRight = getGlobalRightForCrudMethods();

        final ApplicationRight getAllRight = Optional.ofNullable(getRightForAllEntriesCrudMethod()).orElse(fallBackRight);
        final ApplicationRight getByIdRight = Optional.ofNullable(getRightForGetByIdCrudMethod()).orElse(fallBackRight);
        final ApplicationRight createNewRight = Optional.ofNullable(getRightForCreateNewCrudMethod()).orElse(fallBackRight);
        final ApplicationRight updateRight = Optional.ofNullable(getRightForUpdateCrudMethod()).orElse(fallBackRight);
        final ApplicationRight deleteRight = Optional.ofNullable(getRightForDeleteCrudMethod()).orElse(fallBackRight);

        return new CrudControllerMethodRights(getAllRight, getByIdRight, createNewRight, updateRight, deleteRight);
    }

    protected ApplicationRight getGlobalRightForCrudMethods() {
        return null;
    }

    protected ApplicationRight getRightForAllEntriesCrudMethod() {
        return null;
    }

    protected ApplicationRight getRightForGetByIdCrudMethod() {
        return null;
    }

    protected ApplicationRight getRightForCreateNewCrudMethod() {
        return null;
    }

    protected ApplicationRight getRightForUpdateCrudMethod() {
        return null;
    }

    protected ApplicationRight getRightForDeleteCrudMethod() {
        return null;
    }
}

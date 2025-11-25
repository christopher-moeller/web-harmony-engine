package com.webharmony.core.service.appresources;

import com.webharmony.core.api.rest.controller.AppResourceController;
import com.webharmony.core.api.rest.controller.utils.AbstractCrudController;
import com.webharmony.core.api.rest.controller.utils.request.RequestContext;
import com.webharmony.core.api.rest.model.RestResourceInfo;
import com.webharmony.core.api.rest.model.utils.AbstractResourceDto;
import com.webharmony.core.api.rest.model.utils.ComplexTypeSchema;
import com.webharmony.core.api.rest.model.utils.ResourceOverviewTypeSchema;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.data.jpa.model.utils.AbstractBaseEntity;
import com.webharmony.core.i18n.I18N;
import com.webharmony.core.i18n.I18nTranslation;
import com.webharmony.core.service.appresources.utils.CrudResourceInfoDto;
import com.webharmony.core.service.appresources.utils.CrudResourceInfoSimpleDto;
import com.webharmony.core.service.appresources.utils.CrudResourceInfoWithOverview;
import com.webharmony.core.service.appresources.utils.ResourcePageableOptions;
import com.webharmony.core.service.data.utils.AbstractEntityCrudService;
import com.webharmony.core.service.searchcontainer.AbstractSearchContainer;
import com.webharmony.core.utils.exceptions.InternalServerException;
import com.webharmony.core.utils.exceptions.NotFoundException;
import com.webharmony.core.utils.reflection.ApiLink;
import com.webharmony.core.utils.reflection.ReflectionUtils;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class AppResourceService implements I18nTranslation {

    private final I18N i18N = createI18nInstance(AppResourceService.class);

    private Map<AbstractCrudController<?>, CrudResourceInfoDto> resourcesApis;
    private final Set<AbstractEntityCrudService<?, ?>> entityCrudServices;

    private final Set<AbstractCrudController<?>> allCrudControllers;

    @Getter
    private final Set<RestResourceInfo> allRestResourceInfos;

    public AppResourceService(Set<AbstractCrudController<?>> allCrudControllers, Set<AbstractEntityCrudService<?, ?>> entityCrudServices) {
        this.entityCrudServices = entityCrudServices;
        this.allCrudControllers = allCrudControllers;
        this.allRestResourceInfos = loadAllRestResourceInfos();
    }

    private Set<RestResourceInfo> loadAllRestResourceInfos() {
        Set<RestResourceInfo> resultSet = new HashSet<>();
        Set<Class<? extends RestResourceInfo>> allResourceInfoClasses = ReflectionUtils.getAllProjectClassesImplementingSuperClass(RestResourceInfo.class);
        for(Class<? extends RestResourceInfo> resourceInfoClass : allResourceInfoClasses) {
            if(resourceInfoClass.isEnum()) {
                resultSet.addAll(Arrays.asList(resourceInfoClass.getEnumConstants()));
            } else {
                throw new InternalServerException("Currently only Enum classes are supported");
            }
        }

        return resultSet;
    }

    private Map<AbstractCrudController<?>, CrudResourceInfoDto> loadCrudResourceInfos(Set<AbstractCrudController<?>> resourcesApis) {
        return resourcesApis.stream()
                .collect(Collectors.toMap(api -> api, this::createResourceInfoByCrudController));
    }

    @SneakyThrows
    public List<CrudResourceInfoSimpleDto> getAllCrudResources() {
        return resourcesApis.values().stream()
                .map(CrudResourceInfoDto::toSimpleDto)
                .toList();
    }

    @SuppressWarnings("unchecked")
    private CrudResourceInfoDto createResourceInfoByCrudController(AbstractCrudController<?> crudController) {

        CrudResourceInfoDto crudResourceInfoDto = new CrudResourceInfoDto();
        crudResourceInfoDto.setName(crudController.getRestResource().getId());
        crudResourceInfoDto.setComplexTypeSchema(getResourceSchemaByCrudController(crudController));

        crudResourceInfoDto.setGetAllLink(ApiLink.of(crudController.getClass(), c -> c.getAllEntries(null)));
        crudResourceInfoDto.setGetByIdLink(ApiLink.of(crudController.getClass(), c -> c.getEntryById(null)));
        crudResourceInfoDto.setCreateNewLink(ApiLink.of(crudController.getClass(), c -> c.createNewEntry(null)));
        crudResourceInfoDto.setUpdateLink(ApiLink.of(crudController.getClass(), c -> c.updateEntry(ReflectionUtils.getApiLinkMockValue(), null)));
        crudResourceInfoDto.setDeleteLink(ApiLink.of(crudController.getClass(), c -> c.deleteEntry(ReflectionUtils.getApiLinkMockValue())));

        crudResourceInfoDto.setGetTemplateLink(ApiLink.of(AppResourceController.class, c -> c.getInitialResourceTemplate(crudResourceInfoDto.getName())));

        crudResourceInfoDto.setPageableOptions(createPageableOptionsBySearchContainer(crudController.getSearchContainer()));

        return crudResourceInfoDto;
    }

    private ResourcePageableOptions createPageableOptionsBySearchContainer(AbstractSearchContainer searchContainer) {
        ResourcePageableOptions options = new ResourcePageableOptions();
        options.setIsUnPagedRequestAllowed(searchContainer.isUnPagedRequestAllowed());
        options.setSuggestedPageOptions(searchContainer.getSuggestedPageOptions());
        searchContainer.getHighestPageSize().ifPresent(options::setHighestPageSize);
        return options;
    }

    private ComplexTypeSchema getResourceSchemaByCrudController(AbstractCrudController<?> crudController) {
        return crudController.getComplexTypeSchema();
    }

    private ResourceOverviewTypeSchema getResourceOverviewSchemaByCrudController(AbstractCrudController<?> crudController, RequestContext requestContext) {
        return crudController.getResourceOverviewTypeSchema(requestContext);
    }

    public ComplexTypeSchema getResourceSchemaByCrudControllerByDataClass(Class<?> dataClass) {
        return resourcesApis.keySet()
                .stream()
                .filter(api -> Objects.equals(api.getDataClass(), dataClass))
                .findAny().orElseThrow()
                .getComplexTypeSchema();
    }


    public CrudResourceInfoDto getResourceByName(String resourceName, RequestContext requestContext) {
        return resourcesApis.entrySet()
                .stream()
                .filter(entry -> entry.getValue().getName().equals(resourceName))
                .findAny()
                .map(entry -> CrudResourceInfoWithOverview.of(entry.getValue(), getResourceOverviewSchemaByCrudController(entry.getKey(), requestContext)))
                .orElseThrow(() -> new NotFoundException(i18N.translate("Resource with name '{resourceName}' not found").add("resourceName", resourceName).build()));
    }

    @SuppressWarnings("unchecked")
    public <T extends AbstractResourceDto> AbstractCrudController<T> getCrudControllerByDataClass(Class<T> dtoClass) {
        return resourcesApis.keySet().stream()
                .filter(c -> dtoClass.isAssignableFrom(c.getDataClass()))
                .findAny()
                .map(controller -> (AbstractCrudController<T>) controller)
                .orElseThrow();
    }

    @SuppressWarnings("unchecked")
    public Optional<Class<? extends AbstractResourceDto>> findResourceClassDtoByEntityClass(Class<? extends AbstractBaseEntity> entityClass) {
       return  entityCrudServices.stream()
               .filter(abstractEntityCrudService -> abstractEntityCrudService.getEntityClass().isAssignableFrom(entityClass))
               .map(AbstractEntityCrudService::getDtoClass)
               .findAny()
               .filter(AbstractResourceDto.class::isAssignableFrom)
               .map(clazz -> (Class<? extends AbstractResourceDto>) clazz);
    }

    public Optional<String> getResourceNameByEntity(Class<? extends AbstractBaseEntity> entityClass) {
        return findResourceClassDtoByEntityClass(entityClass)
                .map(this::getCrudControllerByDataClass)
                .map(AbstractCrudController::getRestResource)
                .map(RestResourceInfo::getId);
    }

    public Optional<CrudResourceInfoDto> getResourceInfoByEntityClass(Class<? extends AbstractBaseEntity> entityClass) {
        return getResourceNameByEntity(entityClass)
                .map(name -> getResourceByName(name, RequestContext.empty(ContextHolder.getContext().getSpringContext())));
    }

    public AbstractResourceDto createInitialResourceTemplateByName(String resourceName) {
        RestResourceInfo eCoreRestResources = RestResourceInfo.getByResourceById(resourceName);

        Supplier<AbstractResourceDto> templateSupplier = eCoreRestResources.getTemplateSupplier();
        if(templateSupplier != null)
            return templateSupplier.get();

        AbstractCrudController<?> controller = resourcesApis.keySet()
                .stream()
                .filter(c -> c.getRestResource().equals(eCoreRestResources))
                .findAny()
                .orElseThrow();

        Class<? extends AbstractResourceDto> dtoClass = controller.getDataClass();
        return ReflectionUtils.createNewInstanceWithEmptyConstructor(dtoClass);
    }

    public void init() {
        this.resourcesApis = loadCrudResourceInfos(allCrudControllers);
    }
}

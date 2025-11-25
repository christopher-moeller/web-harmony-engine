package com.webharmony.core.service.data.mapper;

import com.webharmony.core.api.rest.model.utils.AbstractResourceDto;
import com.webharmony.core.api.rest.model.utils.FileWebData;
import com.webharmony.core.api.rest.model.utils.ResourceLinks;
import com.webharmony.core.api.rest.model.utils.SimpleFieldTypeSchema;
import com.webharmony.core.api.rest.model.utils.apiobject.ApiResource;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.data.jpa.model.files.AppFile;
import com.webharmony.core.data.jpa.model.i18n.AppI18nEntityAttribute;
import com.webharmony.core.data.jpa.model.utils.AbstractBaseEntity;
import com.webharmony.core.service.EntityService;
import com.webharmony.core.service.appresources.AppResourceService;
import com.webharmony.core.service.appresources.utils.CrudResourceInfoDto;
import com.webharmony.core.service.data.FileService;
import com.webharmony.core.utils.CollectionUtils;
import com.webharmony.core.utils.assertions.Assert;
import com.webharmony.core.utils.exceptions.ResourceNotFoundException;
import com.webharmony.core.utils.objects.ObjectsWithLabel;
import com.webharmony.core.utils.reflection.ApiLink;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GenericMappingUtils {

    private GenericMappingUtils() {

    }

    public static <E extends AbstractBaseEntity> E loadEntityByApiObject(ApiResource<?> apiObject, Class<E> entityType) {
        final EntityService entityService = getEntityService();

        Object primaryKeyValue = apiObject.getPrimaryKey();
        Assert.isNotNull(primaryKeyValue).verify();

        final UUID uuid = primaryKeyValue instanceof UUID id ? id : UUID.fromString(primaryKeyValue.toString());

        return entityService.findEntityById(entityType, uuid)
                .orElseThrow(() -> {
                    Class<? extends AbstractResourceDto> dtoClass = findDtoClassByApiObject(apiObject)
                            .orElseGet(() -> findDtoClassByEntityClass(entityType).orElseThrow());
                    return new ResourceNotFoundException(dtoClass, uuid);
                });
    }


    public static <T extends AbstractResourceDto> ApiResource<T> createSimpleReferenceApiObjectByEntity(AbstractBaseEntity entity) {
        ApiResource<T> apiObject = new ApiResource<>();
        apiObject.setPrimaryKey(entity.getUuid());
        CrudResourceInfoDto resourceInfo = getAppResourceService().getResourceInfoByEntityClass(entity.getClass()).orElseThrow();

        final Optional<T> labelOnlyData = createLabelOnlyDataIfPossible(resourceInfo, entity);
        labelOnlyData.ifPresent(apiObject::setData);

        ResourceLinks resourceLinks = new ResourceLinks();

        Optional.ofNullable(resourceInfo.getGetByIdLink())
                .map(link -> link.resolveID(entity.getUuid()))
                .ifPresent(resourceLinks::setSelfLink);

        Optional.ofNullable(resourceInfo.getUpdateLink())
                .map(link -> link.resolveID(entity.getUuid()))
                .ifPresent(resourceLinks::setUpdateLink);

        Optional.ofNullable(resourceInfo.getDeleteLink())
                .map(link -> link.resolveID(entity.getUuid()))
                .ifPresent(resourceLinks::setDeleteLink);

        apiObject.setResourceLinks(resourceLinks);

        return apiObject;
    }

    @SuppressWarnings("unchecked")
    private static <T extends AbstractResourceDto> Optional<T> createLabelOnlyDataIfPossible(CrudResourceInfoDto resourceInfo, AbstractBaseEntity entity) {

        if(!(entity instanceof ObjectsWithLabel<?> entityWithLabel)) {
            return Optional.empty();
        }

        final String targetJavaType = Optional.ofNullable(resourceInfo.getComplexTypeSchema())
                .map(SimpleFieldTypeSchema::getJavaType)
                .orElse(null);

        if(targetJavaType == null) {
            return Optional.empty();
        }

        final T dto;
        try {
            final Class<T> dtoType = (Class<T>) Class.forName(targetJavaType);
            if(!ObjectsWithLabel.class.isAssignableFrom(dtoType)) {
                return Optional.empty();
            }
            dto = dtoType.getDeclaredConstructor().newInstance();
        }catch (Exception e) {
            return Optional.empty();
        }

        final ObjectsWithLabel<String> dtoWithLabel = (ObjectsWithLabel<String>) dto;
        final Object labelObject = entityWithLabel.getLabel();

        if(labelObject == null) {
            return Optional.empty();
        }

        if(labelObject instanceof AppI18nEntityAttribute i18nEntityAttribute) {
            i18nEntityAttribute.getValueByLanguage(ContextHolder.getContext().getContextLanguage())
                    .ifPresent(dtoWithLabel::setStringLabel);
        } else {
            dtoWithLabel.setLabel(Objects.toString(labelObject));
        }

        return Optional.of(dto);
    }

    private static Optional<Class<? extends AbstractResourceDto>> findDtoClassByApiObject(ApiResource<?> apiObject) {
        return Optional.ofNullable(apiObject.getData())
                .map(AbstractResourceDto::getClass);
    }

    private static Optional<Class<? extends AbstractResourceDto>> findDtoClassByEntityClass(Class<? extends AbstractBaseEntity> entityClass) {
        return getAppResourceService().findResourceClassDtoByEntityClass(entityClass);
    }


    private static EntityService getEntityService() {
        return ContextHolder.getContext().getBean(EntityService.class);
    }

    private static AppResourceService getAppResourceService() {
        return ContextHolder.getContext().getBean(AppResourceService.class);
    }

    public static List<FileWebData> mapAppFileToWebData(Set<AppFile> appFiles, Function<AppFile, ApiLink> apiLinkResolver) {
        return CollectionUtils.emptySetIfNull(appFiles)
                .stream()
                .map(f -> mapAppFileToWebData(f, apiLinkResolver.apply(f)))
                .toList();
    }
    public static FileWebData mapAppFileToWebData(AppFile appFile, ApiLink downloadLink) {
        final FileWebData fileWebData = ContextHolder.getContext().getBean(FileService.class)
                .buildWebDataByAppFile(appFile);
        fileWebData.setDownloadLink(downloadLink);
        return fileWebData;
    }

    public static Set<AppFile> mapWebDataToAppFiles(List<FileWebData> webData) {
        return CollectionUtils.emptyListIfNull(webData)
                .stream()
                .map(GenericMappingUtils::mapWebDataToAppFile)
                .collect(Collectors.toSet());
    }
    public static AppFile mapWebDataToAppFile(FileWebData webData) {
        return ContextHolder.getContext().getBean(FileService.class)
                .buildAppFileByWebData(webData);
    }
}

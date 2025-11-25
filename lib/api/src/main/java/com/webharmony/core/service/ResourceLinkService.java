package com.webharmony.core.service;

import com.webharmony.core.api.rest.controller.utils.AbstractCrudController;
import com.webharmony.core.api.rest.controller.utils.request.RequestContext;
import com.webharmony.core.api.rest.model.utils.AbstractResourceDto;
import com.webharmony.core.api.rest.model.utils.ResourceLinks;
import com.webharmony.core.service.appresources.AppResourceService;
import com.webharmony.core.service.appresources.utils.CrudResourceInfoDto;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ResourceLinkService {

    private final AppResourceService appResourceService;
    private final ApplicationContext applicationContext;

    public ResourceLinkService(AppResourceService appResourceService, ApplicationContext applicationContext) {
        this.appResourceService = appResourceService;
        this.applicationContext = applicationContext;
    }

    public ResourceLinks createResourceLinksByResourceDtoAndId(Class<? extends AbstractResourceDto> dtoClass, Object id) {
        AbstractCrudController<?> crudController = appResourceService.getCrudControllerByDataClass(dtoClass);
        CrudResourceInfoDto resourceInfo = appResourceService.getResourceByName(crudController.getRestResource().getId(), RequestContext.empty(applicationContext));
        return buildResourceLinksByCrudResourceInfoAndUuid(resourceInfo, id);
    }

    private ResourceLinks buildResourceLinksByCrudResourceInfoAndUuid(CrudResourceInfoDto resourceInfo, Object id) {
        ResourceLinks resourceLinks = new ResourceLinks();
        Optional.ofNullable(resourceInfo.getGetByIdLink()).map(r -> r.resolveID(id)).ifPresent(resourceLinks::setSelfLink);
        Optional.ofNullable(resourceInfo.getUpdateLink()).map(r -> r.resolveID(id)).ifPresent(resourceLinks::setUpdateLink);
        Optional.ofNullable(resourceInfo.getDeleteLink()).map(r -> r.resolveID(id)).ifPresent(resourceLinks::setDeleteLink);
        return resourceLinks;
    }



}

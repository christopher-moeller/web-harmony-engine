package com.webharmony.core.api.rest.model;

import com.webharmony.core.api.rest.model.utils.AbstractResourceDto;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.i18n.ECoreI18nStaticTranslations;
import com.webharmony.core.service.appresources.AppResourceService;
import com.webharmony.core.utils.exceptions.NotFoundException;

import java.util.function.Supplier;

public interface RestResourceInfo {

    String name();
    String getId();
    Supplier<AbstractResourceDto> getTemplateSupplier();

    static RestResourceInfo getByResourceById(String resourceId) {
        return ContextHolder.getContext().getBean(AppResourceService.class)
                .getAllRestResourceInfos()
                .stream()
                .filter(r -> r.getId().equals(resourceId))
                .findAny()
                .orElseThrow(() -> new NotFoundException(ECoreI18nStaticTranslations.REST_RESOURCE_INFO_NOT_FOUND.getI18n().add("resourceName", resourceId).build()));
    }
}

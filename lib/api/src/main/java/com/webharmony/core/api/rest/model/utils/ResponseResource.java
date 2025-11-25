package com.webharmony.core.api.rest.model.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.webharmony.core.api.rest.model.utils.apiobject.ApiResource;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.service.ResourceLinkService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Getter
@Setter
@Slf4j
public class ResponseResource<D extends AbstractResourceDto> {

    private ApiResource<D> data;

    @Getter
    @JsonIgnore
    private Class<?> dataType;

    public ResponseResource() {

    }

    public ResponseResource(Class<?> dataType, D data) {
        this.data = createApiObject(data);
        this.dataType = dataType;
    }

    private ApiResource<D> createApiObject(D data) {
        ApiResource<D> apiObject = new ApiResource<>();
        apiObject.setPrimaryKey(data.getId());
        apiObject.setData(data);
        tryToBuildResourceLinks(data).ifPresent(apiObject::setResourceLinks);
        return apiObject;
    }

    private Optional<ResourceLinks> tryToBuildResourceLinks(D data) {
        try {
            final ResourceLinkService resourceLinkService = ContextHolder.getContext().getBean(ResourceLinkService.class);
            return Optional.of(resourceLinkService.createResourceLinksByResourceDtoAndId(data.getClass(), data.getId()));
        } catch (Exception e) {
            log.error("Cannot create resource links for class " + data.getClass(), e);
            return Optional.empty();
        }
    }
    public static <T extends AbstractResourceDto> ResponseResource<T> ok() {
        return new ResponseResource<>();
    }

    public static <T extends AbstractResourceDto> ResponseResource<T> ok(Class<?> dataType, T data) {
        return new ResponseResource<>(dataType, data);
    }

    public boolean hasEmptyData() {
        return data == null;
    }

}

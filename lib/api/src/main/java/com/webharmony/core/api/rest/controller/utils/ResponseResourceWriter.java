package com.webharmony.core.api.rest.controller.utils;

import com.webharmony.core.api.rest.model.utils.ComplexTypeSchema;
import com.webharmony.core.api.rest.model.utils.EJsonType;
import com.webharmony.core.api.rest.model.utils.ExtendedResponseResource;
import com.webharmony.core.api.rest.model.utils.ResponseResource;
import com.webharmony.core.service.appresources.AppResourceService;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

@Component
public class ResponseResourceWriter {

    private final MappingJackson2HttpMessageConverter jacksonConverter;
    private final AppResourceService appResourceService;

    public ResponseResourceWriter(AppResourceService appResourceService) {
        this.appResourceService = appResourceService;
        jacksonConverter = new MappingJackson2HttpMessageConverter();
    }

    public void writeMessage(ResponseResource<?> responseResource, MediaType contentType, HttpOutputMessage outputMessage) throws IOException {
        jacksonConverter.write(createExtendedResponseResource(responseResource), contentType, outputMessage);
    }

    private ExtendedResponseResource<?> createExtendedResponseResource(ResponseResource<?> responseResource) {
        ExtendedResponseResource<?> extendedResponseResource = new ExtendedResponseResource<>(responseResource);
        extendedResponseResource.setSchema(createResponseTypeSchema(responseResource));
        return extendedResponseResource;
    }

    private ComplexTypeSchema createResponseTypeSchema(ResponseResource<?> responseResource) {
        ComplexTypeSchema schema = appResourceService.getResourceSchemaByCrudControllerByDataClass(responseResource.getDataType()).createCopy();
        schema.setJsonType(getDataJsonType(responseResource).name());
        return schema;
    }

    private EJsonType getDataJsonType(ResponseResource<?> responseResource) {
        boolean isCollection = Optional.ofNullable(responseResource)
                .map(ResponseResource::getData)
                .map(Object::getClass)
                .map(Collection.class::isAssignableFrom)
                .orElse(false);

        return  isCollection ? EJsonType.ARRAY : EJsonType.OBJECT;
    }

}

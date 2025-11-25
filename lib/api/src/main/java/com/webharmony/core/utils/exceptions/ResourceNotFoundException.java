package com.webharmony.core.utils.exceptions;

import com.webharmony.core.api.rest.model.utils.BaseDto;
import com.webharmony.core.api.rest.model.utils.AbstractResourceDto;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.i18n.ECoreI18nStaticTranslations;
import com.webharmony.core.service.appresources.AppResourceService;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class ResourceNotFoundException extends ApplicationException {

    public ResourceNotFoundException(Class<? extends BaseDto> dtoClass, UUID uuid) {
        super(formatResourceNotFoundMessageForBaseDto(dtoClass, uuid));
    }

    @SuppressWarnings("unchecked")
    private static String formatResourceNotFoundMessageForBaseDto(Class<? extends BaseDto> dtoClass, UUID uuid) {
        if(AbstractResourceDto.class.isAssignableFrom(dtoClass)) {
            return formatResourceNotFoundMessageForResourceDto((Class<? extends AbstractResourceDto>) dtoClass, uuid);
        } else {
            return ECoreI18nStaticTranslations.RESOURCE_NOT_FOUND_EXCEPTION_TEXT.getI18n().add("resourceName", dtoClass.getSimpleName()).add("id", uuid).build();
        }
    }

    private static String formatResourceNotFoundMessageForResourceDto(Class<? extends AbstractResourceDto> dtoClass, UUID uuid) {
        final String resourceId = ContextHolder.getContext().getBean(AppResourceService.class)
                .getCrudControllerByDataClass(dtoClass)
                .getRestResource()
                .getId();

        return String.format("Resource '%s' with id '%s' not found", resourceId, uuid);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }
}

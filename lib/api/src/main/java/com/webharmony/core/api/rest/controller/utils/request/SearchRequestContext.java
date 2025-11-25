package com.webharmony.core.api.rest.controller.utils.request;

import com.webharmony.core.api.rest.controller.utils.RestRequestParams;
import com.webharmony.core.api.rest.model.utils.AbstractResourceDto;
import com.webharmony.core.data.jpa.model.utils.AbstractBaseEntity;
import com.webharmony.core.service.data.utils.AbstractEntityCrudService;
import com.webharmony.core.service.searchcontainer.utils.SearchFilter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationContext;

import java.util.Optional;
import java.util.UUID;

public class SearchRequestContext implements RequestContext {

    @Getter
    private final ApplicationContext applicationContext;

    @Getter
    private final RestRequestParams restRequestParams;

    @Setter
    private AbstractEntityCrudService<?, ?> entityCrudService;

    @Getter
    @Setter
    private Class<? extends AbstractResourceDto> dtoClass;

    public SearchRequestContext(ApplicationContext applicationContext, RestRequestParams restRequestParams) {
        this.applicationContext = applicationContext;
        this.restRequestParams = restRequestParams;
    }

    @SuppressWarnings("unchecked")
    public <D extends AbstractResourceDto, E extends AbstractBaseEntity> Optional<AbstractEntityCrudService<D, E>> getEntityCrudService() {
        return Optional.ofNullable((AbstractEntityCrudService<D, E>) entityCrudService);
    }

    public <T> Optional<T> getFilterValue(SearchFilter searchFilter, Class<T> type) {
        return getRestRequestParams().getParamAsObject(searchFilter.getFilterName())
                .map(v -> mapToTarget(v, type));
    }

    @SuppressWarnings("unchecked")
    private <T> T mapToTarget(Object value, Class<T> type) {
        if(UUID.class.equals(type)) {
            return (T) UUID.fromString(value.toString());
        }

        if(value instanceof String stringValue && (stringValue.equals(Boolean.TRUE.toString()) || stringValue.equals(Boolean.FALSE.toString()))) {
            return (T) Boolean.valueOf(stringValue);
        }

        return (T) value;
    }
}

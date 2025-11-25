package com.webharmony.core.api.rest.controller.utils.dispacher;

import com.webharmony.core.api.rest.controller.utils.AbstractCrudController;
import com.webharmony.core.api.rest.controller.utils.request.RequestContext;
import com.webharmony.core.api.rest.controller.utils.request.SearchRequestContext;
import com.webharmony.core.api.rest.model.utils.AbstractResourceDto;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.service.data.utils.AbstractEntityCrudService;
import com.webharmony.core.service.searchcontainer.utils.SearchResult;
import com.webharmony.core.utils.exceptions.InternalServerException;

import java.util.UUID;

public class EntityCrudDispatcher<D extends AbstractResourceDto, S extends AbstractEntityCrudService<? extends D, ?>> implements ControllerDispatcher<D> {

    private final Class<S> serviceClass;
    private final AbstractCrudController<D> crudController;

    public EntityCrudDispatcher(Class<S> serviceClass, AbstractCrudController<D> crudController) {
        this.serviceClass = serviceClass;
        this.crudController = crudController;
    }

    @Override
    public SearchResult getAllEntries(RequestContext requestContext) {
        if(requestContext instanceof SearchRequestContext searchRequestContext) {
            searchRequestContext.setEntityCrudService(getService());
            return crudController.getSearchContainer().getSearchResultsByRequestParams(searchRequestContext);
        } else {
            throw new InternalServerException("Not implemented yet");
        }
    }

    @Override
    public D getEntryById(UUID uuid, RequestContext requestContext) {
        return getService().getEntryById(uuid, requestContext);
    }

    @Override
    public D createNewEntry(D dto, RequestContext requestContext) {
        return getService().createNewEntry(toSubtype(dto), requestContext);
    }

    @Override
    public D updateEntry(UUID uuid, D dto, RequestContext requestContext) {
        return getService().updateEntry(uuid, toSubtype(dto), requestContext);
    }

    @Override
    public void deleteEntry(UUID uuid, RequestContext requestContext) {
        getService().deleteEntry(uuid, requestContext);
    }

    private S getService() {
        return ContextHolder.getContext().getBean(serviceClass);
    }
}

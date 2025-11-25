package com.webharmony.core.api.rest.controller.utils.dispacher;

import com.webharmony.core.api.rest.controller.utils.request.RequestContext;
import com.webharmony.core.api.rest.model.utils.BaseDto;
import com.webharmony.core.service.searchcontainer.utils.SearchResult;

import java.util.UUID;

public interface ControllerDispatcher<D extends BaseDto> {

    SearchResult getAllEntries(RequestContext requestContext);
    D getEntryById(UUID uuid, RequestContext requestContext);
    D createNewEntry(D dto, RequestContext requestContext);
    D updateEntry(UUID uuid, D dto, RequestContext requestContext);
    void deleteEntry(UUID uuid, RequestContext requestContext);

    @SuppressWarnings("unchecked")
    default <T extends D> T toSubtype(D dto) {
        return (T) dto;
    }

}

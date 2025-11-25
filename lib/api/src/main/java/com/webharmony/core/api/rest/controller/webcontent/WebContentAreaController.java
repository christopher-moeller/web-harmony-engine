package com.webharmony.core.api.rest.controller.webcontent;

import com.webharmony.core.api.rest.controller.ApiController;
import com.webharmony.core.api.rest.controller.utils.AbstractCrudController;
import com.webharmony.core.api.rest.controller.utils.MethodNotAllowed;
import com.webharmony.core.api.rest.controller.utils.dispacher.ControllerDispatcher;
import com.webharmony.core.api.rest.controller.utils.dispacher.EntityCrudDispatcher;
import com.webharmony.core.api.rest.model.ECoreRestResources;
import com.webharmony.core.api.rest.model.RestResourceInfo;
import com.webharmony.core.api.rest.model.utils.ResponseResource;
import com.webharmony.core.api.rest.model.webcontent.WebContentAreaDto;
import com.webharmony.core.configuration.security.ApplicationRight;
import com.webharmony.core.data.enums.ECoreActorRight;
import com.webharmony.core.service.data.WebContentAreaService;
import com.webharmony.core.service.searchcontainer.AbstractSearchContainer;
import com.webharmony.core.service.searchcontainer.WebContentAreaSearchContainer;
import com.webharmony.core.utils.exceptions.MethodNotAllowedException;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@ApiController("api/webContentAreas")
public class WebContentAreaController extends AbstractCrudController<WebContentAreaDto> {

    private final WebContentAreaSearchContainer searchContainer;

    public WebContentAreaController(WebContentAreaSearchContainer searchContainer) {
        this.searchContainer = searchContainer;
    }

    @Override
    public RestResourceInfo getRestResource() {
        return ECoreRestResources.WEB_CONTENT_AREAS;
    }

    @Override
    @MethodNotAllowed
    public ResponseResource<WebContentAreaDto> createNewEntry(WebContentAreaDto dto) {
        throw new MethodNotAllowedException();
    }

    @Override
    @MethodNotAllowed
    public ResponseEntity<Void> deleteEntry(UUID uuid) {
        throw new MethodNotAllowedException();
    }

    @Override
    protected ControllerDispatcher<WebContentAreaDto> loadControllerDispatcherInternal() {
        return new EntityCrudDispatcher<>(WebContentAreaService.class, this);
    }

    @Override
    public AbstractSearchContainer getSearchContainer() {
        return searchContainer;
    }

    @Override
    protected ApplicationRight getGlobalRightForCrudMethods() {
        return ECoreActorRight.CORE_WEB_CONTENT_AREA_CRUD;
    }
}

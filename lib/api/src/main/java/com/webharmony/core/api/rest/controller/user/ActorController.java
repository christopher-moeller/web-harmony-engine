package com.webharmony.core.api.rest.controller.user;

import com.webharmony.core.api.rest.controller.ApiController;
import com.webharmony.core.api.rest.controller.utils.AbstractCrudController;
import com.webharmony.core.api.rest.controller.utils.MethodNotAllowed;
import com.webharmony.core.api.rest.controller.utils.dispacher.ControllerDispatcher;
import com.webharmony.core.api.rest.controller.utils.request.RequestContext;
import com.webharmony.core.api.rest.controller.utils.request.SearchRequestContext;
import com.webharmony.core.api.rest.model.ActorDto;
import com.webharmony.core.api.rest.model.ECoreRestResources;
import com.webharmony.core.api.rest.model.RestResourceInfo;
import com.webharmony.core.api.rest.model.utils.ResponseResource;
import com.webharmony.core.configuration.security.ApplicationRight;
import com.webharmony.core.data.enums.ECoreActorRight;
import com.webharmony.core.service.ActorService;
import com.webharmony.core.service.searchcontainer.utils.ActorsSearchContainer;
import com.webharmony.core.service.searchcontainer.utils.SearchResult;
import com.webharmony.core.utils.exceptions.MethodNotAllowedException;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@ApiController("api/actors")
public class ActorController extends AbstractCrudController<ActorDto> {

    private final ActorService actorService;

    private final ActorsSearchContainer searchContainer;

    public ActorController(ActorService actorService, ActorsSearchContainer searchContainer) {
        this.actorService = actorService;
        this.searchContainer = searchContainer;
    }

    @Override
    @MethodNotAllowed
    public ResponseResource<ActorDto> createNewEntry(ActorDto dto) {
        throw new MethodNotAllowedException();
    }

    @Override
    @MethodNotAllowed
    public ResponseResource<ActorDto> updateEntry(UUID uuid, ActorDto dto) {
        throw new MethodNotAllowedException();
    }

    @Override
    @MethodNotAllowed
    public ResponseEntity<Void> deleteEntry(UUID uuid) {
        throw new MethodNotAllowedException();
    }

    @Override
    public RestResourceInfo getRestResource() {
        return ECoreRestResources.ACTORS;
    }

    @Override
    protected ControllerDispatcher<ActorDto> loadControllerDispatcherInternal() {
        return new ControllerDispatcher<>() {
            @Override
            public SearchResult getAllEntries(RequestContext requestContext) {
                return searchContainer.getSearchResultsByRequestParams((SearchRequestContext) requestContext);
            }

            @Override
            public ActorDto getEntryById(UUID uuid, RequestContext requestContext) {
                return actorService.getActorDtoByUUID(uuid);
            }

            @Override
            public ActorDto createNewEntry(ActorDto dto, RequestContext requestContext) {
                throw new MethodNotAllowedException();
            }

            @Override
            public ActorDto updateEntry(UUID uuid, ActorDto dto, RequestContext requestContext) {
                throw new MethodNotAllowedException();
            }

            @Override
            public void deleteEntry(UUID uuid, RequestContext requestContext) {
                throw new MethodNotAllowedException();
            }
        };
    }

    @Override
    public ActorsSearchContainer getSearchContainer() {
        return searchContainer;
    }

    @Override
    protected ApplicationRight getGlobalRightForCrudMethods() {
        return ECoreActorRight.CORE_ACTOR_READ_ALL_ACTORS;
    }
}

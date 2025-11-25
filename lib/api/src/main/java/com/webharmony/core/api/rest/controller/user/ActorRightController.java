package com.webharmony.core.api.rest.controller.user;

import com.webharmony.core.api.rest.controller.ApiController;
import com.webharmony.core.api.rest.controller.utils.AbstractCrudController;
import com.webharmony.core.api.rest.controller.utils.MethodNotAllowed;
import com.webharmony.core.api.rest.controller.utils.dispacher.ControllerDispatcher;
import com.webharmony.core.api.rest.controller.utils.dispacher.EntityCrudDispatcher;
import com.webharmony.core.api.rest.model.ActorRightDto;
import com.webharmony.core.api.rest.model.ECoreRestResources;
import com.webharmony.core.api.rest.model.RestResourceInfo;
import com.webharmony.core.api.rest.model.utils.ResponseResource;
import com.webharmony.core.data.enums.ECoreActorRight;
import com.webharmony.core.configuration.security.ApplicationRight;
import com.webharmony.core.service.data.ActorRightService;
import com.webharmony.core.service.searchcontainer.AbstractSearchContainer;
import com.webharmony.core.service.searchcontainer.ActorRightSearchContainer;
import com.webharmony.core.utils.exceptions.MethodNotAllowedException;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@ApiController("api/actorRights")
public class ActorRightController extends AbstractCrudController<ActorRightDto> {

    private final ActorRightSearchContainer actorRightSearchContainer;

    public ActorRightController(ActorRightSearchContainer actorRightSearchContainer) {
        this.actorRightSearchContainer = actorRightSearchContainer;
    }

    @Override
    public RestResourceInfo getRestResource() {
        return ECoreRestResources.RIGHTS;
    }

    @Override
    @MethodNotAllowed
    public ResponseResource<ActorRightDto> createNewEntry(ActorRightDto dto) {
        throw new MethodNotAllowedException();
    }

    @Override
    @MethodNotAllowed
    public ResponseEntity<Void> deleteEntry(UUID uuid) {
        throw new MethodNotAllowedException();
    }

    @Override
    protected ControllerDispatcher<ActorRightDto> loadControllerDispatcherInternal() {
        return new EntityCrudDispatcher<>(ActorRightService.class, this);
    }

    @Override
    public AbstractSearchContainer getSearchContainer() {
        return actorRightSearchContainer;
    }

    @Override
    protected ApplicationRight getGlobalRightForCrudMethods() {
        return ECoreActorRight.CORE_ACTOR_RIGHTS_CRUD;
    }
}

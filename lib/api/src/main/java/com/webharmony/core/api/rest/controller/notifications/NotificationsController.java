package com.webharmony.core.api.rest.controller.notifications;

import com.webharmony.core.api.rest.controller.ApiController;
import com.webharmony.core.api.rest.controller.utils.AbstractCrudController;
import com.webharmony.core.api.rest.controller.utils.MethodNotAllowed;
import com.webharmony.core.api.rest.controller.utils.dispacher.ControllerDispatcher;
import com.webharmony.core.api.rest.controller.utils.dispacher.EntityCrudDispatcher;
import com.webharmony.core.api.rest.model.ActorNotificationDto;
import com.webharmony.core.api.rest.model.ECoreRestResources;
import com.webharmony.core.api.rest.model.RestResourceInfo;
import com.webharmony.core.api.rest.model.utils.ResponseResource;
import com.webharmony.core.data.enums.ECoreActorRight;
import com.webharmony.core.configuration.security.ApplicationRight;
import com.webharmony.core.service.data.ActorNotificationCrudService;
import com.webharmony.core.service.searchcontainer.AbstractSearchContainer;
import com.webharmony.core.service.searchcontainer.ActorNotificationSearchContainer;
import com.webharmony.core.utils.exceptions.MethodNotAllowedException;

import java.util.UUID;

@ApiController("api/notifications")
public class NotificationsController extends AbstractCrudController<ActorNotificationDto> {

    private final ActorNotificationSearchContainer searchContainer;

    public NotificationsController(ActorNotificationSearchContainer searchContainer) {
        this.searchContainer = searchContainer;
    }

    @Override
    @MethodNotAllowed
    public ResponseResource<ActorNotificationDto> createNewEntry(ActorNotificationDto dto) {
        throw new MethodNotAllowedException();
    }

    @Override
    @MethodNotAllowed
    public ResponseResource<ActorNotificationDto> updateEntry(UUID uuid, ActorNotificationDto dto) {
        throw new MethodNotAllowedException();
    }

    @Override
    public RestResourceInfo getRestResource() {
        return ECoreRestResources.NOTIFICATIONS;
    }


    @Override
    protected ControllerDispatcher<ActorNotificationDto> loadControllerDispatcherInternal() {
        return new EntityCrudDispatcher<>(ActorNotificationCrudService.class, this);
    }

    @Override
    public AbstractSearchContainer getSearchContainer() {
        return searchContainer;
    }

    @Override
    protected ApplicationRight getGlobalRightForCrudMethods() {
        return ECoreActorRight.CORE_NOTIFICATIONS;
    }
}

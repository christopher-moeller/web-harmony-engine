package com.webharmony.core.api.rest.controller.notifications;

import com.webharmony.core.api.rest.controller.ApiController;
import com.webharmony.core.api.rest.controller.utils.AbstractCrudController;
import com.webharmony.core.api.rest.controller.utils.MethodNotAllowed;
import com.webharmony.core.api.rest.controller.utils.dispacher.ControllerDispatcher;
import com.webharmony.core.api.rest.controller.utils.dispacher.EntityCrudDispatcher;
import com.webharmony.core.api.rest.model.ActorNotificationEventTypeDto;
import com.webharmony.core.api.rest.model.ECoreRestResources;
import com.webharmony.core.api.rest.model.RestResourceInfo;
import com.webharmony.core.api.rest.model.utils.ResponseResource;
import com.webharmony.core.data.enums.ECoreActorRight;
import com.webharmony.core.configuration.security.ApplicationRight;
import com.webharmony.core.service.data.ActorNotificationEventTypeService;
import com.webharmony.core.service.searchcontainer.AbstractSearchContainer;
import com.webharmony.core.service.searchcontainer.ActorNotificationEventTypeSearchContainer;
import com.webharmony.core.utils.exceptions.MethodNotAllowedException;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@ApiController("api/notifications/eventTypes")
public class NotificationEventTypeController extends AbstractCrudController<ActorNotificationEventTypeDto> {

    private final ActorNotificationEventTypeSearchContainer searchContainer;

    public NotificationEventTypeController(ActorNotificationEventTypeSearchContainer searchContainer) {
        this.searchContainer = searchContainer;
    }

    @Override
    @MethodNotAllowed
    public ResponseResource<ActorNotificationEventTypeDto> createNewEntry(ActorNotificationEventTypeDto dto) {
        throw new MethodNotAllowedException();
    }

    @Override
    @MethodNotAllowed
    public ResponseEntity<Void> deleteEntry(UUID uuid) {
        throw new MethodNotAllowedException();
    }

    @Override
    public RestResourceInfo getRestResource() {
        return ECoreRestResources.NOTIFICATION_EVENT_TYPES;
    }


    @Override
    protected ControllerDispatcher<ActorNotificationEventTypeDto> loadControllerDispatcherInternal() {
        return new EntityCrudDispatcher<>(ActorNotificationEventTypeService.class, this);
    }

    @Override
    public AbstractSearchContainer getSearchContainer() {
        return searchContainer;
    }

    @Override
    protected ApplicationRight getGlobalRightForCrudMethods() {
        return ECoreActorRight.CORE_NOTIFICATION_EVENT_TYPE_CRUD;
    }
}

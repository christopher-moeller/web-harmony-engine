package com.webharmony.core.service.data;

import com.webharmony.core.api.rest.model.ActorNotificationEventTypeDto;
import com.webharmony.core.data.jpa.model.notifications.AppActorNotificationEventType;
import com.webharmony.core.service.data.utils.AbstractEntityCrudService;
import org.springframework.stereotype.Service;

@Service
public class ActorNotificationEventTypeService extends AbstractEntityCrudService<ActorNotificationEventTypeDto, AppActorNotificationEventType> {
}

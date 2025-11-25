package com.webharmony.core.utils.notifications;

import com.webharmony.core.data.jpa.model.notifications.AppActorNotificationEventType;

public interface ActorNotificationEventType {

    String name();
    String getLabel();

    AppActorNotificationEventType getEntity();

}

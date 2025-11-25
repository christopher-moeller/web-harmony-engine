package com.webharmony.core.utils.notifications.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.webharmony.core.configuration.security.ApplicationRight;
import com.webharmony.core.utils.notifications.ActorNotificationEventType;

public interface BaseActorNotification {

    String getCaption();

    String getTextMessage();

    @JsonIgnore
    ApplicationRight getNeededApplicationRight();

    @JsonIgnore
    ActorNotificationEventType getType();

}

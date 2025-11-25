package com.webharmony.core.utils.notifications;

import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.data.enums.ECoreNotificationEventType;
import com.webharmony.core.data.jpa.model.actor.AbstractActor;
import com.webharmony.core.service.ActorService;
import com.webharmony.core.service.data.ActorNotificationCrudService;
import com.webharmony.core.service.data.ActorRightService;
import com.webharmony.core.utils.notifications.events.BaseActorNotification;

import java.util.List;
import java.util.stream.Collectors;

public class Notifications {

    private Notifications() {

    }

    public static void send(BaseActorNotification notification) {
        send(notification, getActorsForNotification(notification));
    }

    public static void send(BaseActorNotification notification, List<AbstractActor> recipients) {
        ContextHolder.getContext().getBeanIfExists(ActorNotificationCrudService.class)
                .ifPresent(service -> service.persistNewNotification(notification, ECoreNotificationEventType.APPLICATION_STARTED, recipients));
    }

    @SuppressWarnings("java:S6204")
    private static List<AbstractActor> getActorsForNotification(BaseActorNotification notification) {
        final ActorService actorService = ContextHolder.getContext().getBean(ActorService.class);
        return ContextHolder.getContext().getBean(ActorRightService.class).findAllUsersWithRight(notification.getNeededApplicationRight())
                .stream()
                .map(actorService::getUserActorByAppUser)
                .collect(Collectors.toList());
    }

}

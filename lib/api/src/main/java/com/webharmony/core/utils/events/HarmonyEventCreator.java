package com.webharmony.core.utils.events;

import com.webharmony.core.api.rest.model.utils.AbstractResourceDto;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.data.jpa.model.harmonyevent.AppHarmonyEvent;
import com.webharmony.core.data.jpa.repository.HarmonyEventRepository;
import com.webharmony.core.utils.JacksonUtils;
import com.webharmony.core.utils.events.types.AppResourceHarmonyEvent;
import com.webharmony.core.utils.events.types.HarmonyEvent;
import lombok.SneakyThrows;

public class HarmonyEventCreator {

    private HarmonyEventCreator() {

    }

    @SneakyThrows
    private static void persistEvent(HarmonyEvent event) {

        AppHarmonyEvent entity = new AppHarmonyEvent();
        entity.setJavaType(event.getClass().getName());
        entity.setPayload(JacksonUtils.createDefaultJsonMapper().writeValueAsString(event));

        ContextHolder.getContext().getBean(HarmonyEventRepository.class)
                .save(entity);
    }

    public static void createEvent(HarmonyEvent event) {
        persistEvent(event);
    }

    public static void createResourceCreatedEvent(String resourceName, String resourceId, AbstractResourceDto payload) {
        createResourceEvent(AppResourceHarmonyEvent.CREATED, resourceName, resourceId, payload);
    }

    public static void createResourceUpdatedEvent(String resourceName, String resourceId, AbstractResourceDto payload) {
        createResourceEvent(AppResourceHarmonyEvent.UPDATED, resourceName, resourceId, payload);
    }

    public static void createResourceDeletedEvent(String resourceName, String resourceId) {
        createResourceEvent(AppResourceHarmonyEvent.DELETED, resourceName, resourceId, null);
    }

    public static void createResourceEvent(String actionType, String resourceName, String resourceId, AbstractResourceDto payload) {
        AppResourceHarmonyEvent event = new AppResourceHarmonyEvent();
        event.setAction(actionType);
        event.setResourceName(resourceName);
        event.setResourceId(resourceId);
        event.setPayload(payload);
        createEvent(event);
    }


}

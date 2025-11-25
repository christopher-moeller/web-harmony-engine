package com.webharmony.core.service.data;

import com.webharmony.core.api.rest.jackson.CustomLocalDateTimeSerializer;
import com.webharmony.core.api.rest.model.ActorNotificationDto;
import com.webharmony.core.data.jpa.model.actor.AbstractActor;
import com.webharmony.core.data.jpa.model.notifications.AppActorNotification;
import com.webharmony.core.service.data.mapper.MappingConfiguration;
import com.webharmony.core.service.data.utils.AbstractEntityCrudService;
import com.webharmony.core.utils.JacksonUtils;
import com.webharmony.core.utils.notifications.ActorNotificationEventType;
import com.webharmony.core.utils.notifications.events.BaseActorNotification;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ActorNotificationCrudService extends AbstractEntityCrudService<ActorNotificationDto, AppActorNotification> {


    @Override
    protected void configureMapping(MappingConfiguration<ActorNotificationDto, AppActorNotification> mappingConfiguration) {
        mappingConfiguration.withExtendedToDtoMapper((entity, dto, context) -> {
            dto.setRecipient(Optional.of(entity.getRecipient()).map(AbstractActor::getUniqueName).orElse("-"));
            dto.setSendAt(Optional.ofNullable(entity.getCreatedAt()).map(CustomLocalDateTimeSerializer::parseDateToString).orElse("-"));
            return dto;
        });
    }

    @SneakyThrows
    @Transactional
    public void persistNewNotification(BaseActorNotification notification, ActorNotificationEventType type, List<AbstractActor> recipients) {

        for (AbstractActor recipient : recipients) {
            AppActorNotification entity = new AppActorNotification();
            entity.setCaption(notification.getCaption());
            entity.setRead(false);
            entity.setPayload(JacksonUtils.createDefaultJsonMapper().writeValueAsString(notification));
            entity.setRecipient(recipient);
            entity.setEventType(type.getEntity());
            entity.setTextMessage(notification.getTextMessage());

            saveEntity(entity);
        }
    }

}

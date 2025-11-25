package com.webharmony.core.data.enums;

import com.webharmony.core.data.enums.utils.PersistenceEnum;
import com.webharmony.core.data.jpa.model.notifications.AppActorNotificationEventType;
import com.webharmony.core.i18n.I18N;
import com.webharmony.core.utils.notifications.ActorNotificationEventType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ECoreNotificationEventType implements PersistenceEnum<AppActorNotificationEventType>, ActorNotificationEventType {

    APPLICATION_STARTED("Application started", "Notification if the application is started"),
    APPLICATION_CRON_JOB_EXECUTED("Cron Job executed", "Notification if a cron job was executed");

    @Getter
    private final String label;
    private final String description;


    @Override
    public AppActorNotificationEventType getEntity() {
        return PersistenceEnum.super.getEntity();
    }

    @Override
    public void initEntity(AppActorNotificationEventType entity) {
        entity.setLabel(I18N.entityAttribute(I18N.CODING_LANGUAGE, label));
        entity.setDescription(I18N.entityAttribute(I18N.CODING_LANGUAGE, description));
        entity.setIsActive(true);
    }

    @Override
    public Class<AppActorNotificationEventType> getEntityClass() {
        return AppActorNotificationEventType.class;
    }
}

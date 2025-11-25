package com.webharmony.core.utils.notifications.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.webharmony.core.api.rest.jackson.CustomLocalDateTimeSerializer;
import com.webharmony.core.data.enums.ECoreActorRight;
import com.webharmony.core.data.enums.ECoreNotificationEventType;
import com.webharmony.core.data.jpa.model.AppCronJob;
import com.webharmony.core.i18n.I18N;
import com.webharmony.core.i18n.I18nTranslation;
import com.webharmony.core.configuration.security.ApplicationRight;
import com.webharmony.core.service.cron.AbstractCronJob;
import com.webharmony.core.utils.notifications.ActorNotificationEventType;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class CronJobExecutedNotification implements BaseActorNotification, I18nTranslation {

    @JsonIgnore
    private final I18N i18N = createI18nInstance(CronJobExecutedNotification.class);

    private final UUID id;
    private final String caption;
    private final String textMessage;
    private final Boolean success;
    private final String label;
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    private final LocalDateTime executedAt;

    public CronJobExecutedNotification(AbstractCronJob abstractCronJob, boolean success) {
        this.success = success;
        this.label = abstractCronJob.getLabel();

        final AppCronJob entity = abstractCronJob.getEntity();
        this.executedAt = entity.getLastExecutedAt();
        this.id = entity.getUuid();

        this.caption = createCaption(this.success, this.label);
        this.textMessage = createTextMessage(abstractCronJob);
    }

    private String createCaption(boolean success, String label) {
        return success ? i18N.translate("Cron Job {label} was successfully executed").add("label", label).build()
                : i18N.translate("Cron Job {label} finished with errors").add("label", label).build();
    }

    private String createTextMessage(AbstractCronJob abstractCronJob) {
        return String.join("\n", abstractCronJob.getLogList());
    }

    @Override
    @JsonIgnore
    public ApplicationRight getNeededApplicationRight() {
        return ECoreActorRight.CORE_CRON_JOBS_CRUD;
    }

    @Override
    @JsonIgnore
    public ActorNotificationEventType getType() {
        return ECoreNotificationEventType.APPLICATION_STARTED;
    }
}

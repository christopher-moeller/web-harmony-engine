package com.webharmony.core.service.cron;

import com.webharmony.core.api.rest.jackson.CustomLocalDateTimeSerializer;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.data.jpa.model.AppCronJob;
import com.webharmony.core.service.data.CronJobService;
import com.webharmony.core.utils.notifications.Notifications;
import com.webharmony.core.utils.notifications.events.CronJobExecutedNotification;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.Trigger;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Slf4j
public abstract class AbstractCronJob {

    private boolean isRunning = false;

    private final List<String> logList = new ArrayList<>();

    public abstract String getLabel();
    public abstract String getDescription();

    public void execute() {
        execute(false);
    }

    public void execute(boolean forceExecute) {

        logList.clear();

        AppCronJob entity = getEntity();

        if(isRunning) {
            logInfo(String.format("Cron job '%s' is already running", entity.getLabel()));
            return;
        }

        if(!forceExecute && Boolean.TRUE.equals(!entity.getIsActivated())) {
            logInfo(String.format("Cron job '%s' is currently not activated", entity.getLabel()));
            return;
        }

        final LocalDateTime startedAt = LocalDateTime.now();
        logInfo(String.format("Cron job '%s' started at %s", entity.getLabel(), CustomLocalDateTimeSerializer.parseDateToString(startedAt)));
        this.isRunning = true;

        boolean success = false;
        try {
            executeInternal();
            success = true;
        }catch (Exception e) {
            logError(e);
        }

        this.isRunning = false;
        logInfo(String.format("Cron job '%s' finished at %s", entity.getLabel(), CustomLocalDateTimeSerializer.parseDateToString(LocalDateTime.now())));
        entity = getEntity();
        entity.setLastExecutedAt(startedAt);
        getCronJobService().saveEntity(entity);

        Notifications.send(new CronJobExecutedNotification(this, success));
    }

    protected abstract void executeInternal();

    public abstract Trigger getTrigger();

    public boolean isActivatedByDefault() {
        return true;
    }

    public AppCronJob getEntity() {
        return getCronJobService().getEntityByJavaClass(this.getClass().getName());
    }

    protected CronJobService getCronJobService() {
        return ContextHolder.getContext().getBean(CronJobService.class);
    }

    protected void logInfo(String message) {
        log.info(message);
        logList.add(message);
    }

    protected void logError(Throwable e) {
        log.error(e.getMessage(), e);
    }

    public String getJavaClass() {
        return this.getClass().getName();
    }
}

package com.webharmony.core.data.jpa.model;

import com.querydsl.jpa.impl.JPAQuery;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.data.jpa.model.i18n.AppI18nEntityAttribute;
import com.webharmony.core.data.jpa.model.utils.AbstractModificationInfoEntity;
import com.webharmony.core.data.jpa.model.utils.EntityWithReadableId;
import com.webharmony.core.service.cron.AbstractCronJob;
import com.webharmony.core.service.data.CronJobService;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "app_cron_job")
public class AppCronJob extends AbstractModificationInfoEntity implements EntityWithReadableId {

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "label")
    private AppI18nEntityAttribute label;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "description")
    private AppI18nEntityAttribute description;

    @Column(name = "java_class")
    private String javaClass;

    @Column(name = "last_executed_at")
    private LocalDateTime lastExecutedAt;

    @Column(name = "is_activated")
    private Boolean isActivated;

    @Column(name = "cron_trigger")
    private String cronTrigger;

    public AbstractCronJob getJobInstance() {
        return ContextHolder.getContext().getBean(CronJobService.class)
                .getJobInstanceByJavaClass(this.javaClass);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String getReadableId() {
        return javaClass;
    }

    @Override
    public EntityLoader getEntityLoader() {
        return (em, readableId) -> new JPAQuery<>(em).select(QAppCronJob.appCronJob)
                .from(QAppCronJob.appCronJob)
                .where(QAppCronJob.appCronJob.javaClass.eq(readableId))
                .fetchOne();
    }
}

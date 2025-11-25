package com.webharmony.core.service.data;

import com.webharmony.core.api.rest.jackson.CustomLocalDateTimeSerializer;
import com.webharmony.core.api.rest.model.CronJobDto;
import com.webharmony.core.context.AppContext;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.data.jpa.model.AppCronJob;
import com.webharmony.core.data.jpa.repository.CronJobRepository;
import com.webharmony.core.i18n.I18N;
import com.webharmony.core.service.cron.AbstractCronJob;
import com.webharmony.core.service.data.mapper.MappingConfiguration;
import com.webharmony.core.service.data.mapper.MappingContext;
import com.webharmony.core.service.data.utils.AbstractEntityCrudService;
import com.webharmony.core.utils.exceptions.InternalServerException;
import com.webharmony.core.utils.reflection.ReflectionUtils;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

@Service
public class CronJobService extends AbstractEntityCrudService<CronJobDto, AppCronJob> {

    private Map<AbstractCronJob, ScheduledFuture<?>> cronJobs;

    private final CronJobRepository cronJobRepository;

    public CronJobService(CronJobRepository cronJobRepository) {
        this.cronJobRepository = cronJobRepository;
    }

    @Override
    protected void configureMapping(MappingConfiguration<CronJobDto, AppCronJob> mappingConfiguration) {
        mappingConfiguration.withExtendedToDtoMapper((entity, dto, context) -> {
            dto.setIsCurrentlyRunning(entity.getJobInstance().getIsRunning());
            if(entity.getLastExecutedAt() != null)
                dto.setLastExecutedAt(CustomLocalDateTimeSerializer.parseDateToString(entity.getLastExecutedAt()));
            return dto;
        });
    }

    @Override
    public AppCronJob mapAndSave(CronJobDto dto, AppCronJob originalEntity, MappingContext mappingContext) {
        final String originalTrigger = originalEntity.getCronTrigger();
        final AppCronJob newEntity = super.mapAndSave(dto, originalEntity, mappingContext);
        if(!Objects.equals(newEntity.getCronTrigger(), originalTrigger)) {
            final AbstractCronJob jobInstance = getJobInstanceByJavaClass(newEntity.getJavaClass());
            cronJobs.get(jobInstance).cancel(false);
            cronJobs.put(jobInstance, scheduleCronJob(ContextHolder.getContext(), jobInstance));
        }
        return newEntity;
    }


    public AbstractCronJob getJobInstanceByJavaClass(String javaClass) {
        return cronJobs.keySet()
                .stream()
                .filter(j -> j.getJavaClass().equals(javaClass))
                .findAny()
                .orElseThrow(() -> new InternalServerException(String.format("Cron job with name '%s' does not exist", javaClass)));
    }

    public AppCronJob getEntityByJavaClass(String javaClass) {
        return cronJobRepository.findByJavaClass(javaClass)
                .orElseThrow(() -> new InternalServerException(String.format("Cron job with name '%s' does not exist", javaClass)));
    }

    @Transactional
    public void init(AppContext appContext) {
        Set<AbstractCronJob> allCronJobInstances = getAllCronJobInstances(appContext);
        synchronizeDatabaseEntries(allCronJobInstances);
        this.cronJobs = initCronJobs(appContext, allCronJobInstances);
    }

    private Map<AbstractCronJob, ScheduledFuture<?>> initCronJobs(AppContext appContext, Set<AbstractCronJob> allCronJobInstances) {
        return allCronJobInstances
                .stream()
                .collect(Collectors.toMap(c -> c, c -> scheduleCronJob(appContext, c)));
    }

    @Transactional
    public void synchronizeDatabaseEntries(Set<AbstractCronJob> cronJobs) {
        final List<AppCronJob> allEntities = getAllEntities();
        for (AbstractCronJob cronJob : cronJobs) {
            if(!cronJobEntityExistsInDb(allEntities, cronJob)) {
                final AppCronJob entity = createNewEntityByJob(cronJob);
                saveEntity(entity);
            }
        }

        for (AppCronJob entity : getAllEntities()) {
            if(cronJobs.stream().noneMatch(j -> j.getJavaClass().equals(entity.getJavaClass()))) {
                deleteEntity(entity);
            }
        }
    }

    private boolean cronJobEntityExistsInDb(List<AppCronJob> allEntities, AbstractCronJob cronJob) {
        return allEntities.stream()
                .anyMatch(e -> e.getJavaClass().equals(cronJob.getJavaClass()));
    }

    private AppCronJob createNewEntityByJob(AbstractCronJob cronJob) {
        AppCronJob entity = new AppCronJob();
        entity.setLabel(I18N.entityAttribute(I18N.CODING_LANGUAGE, cronJob.getLabel()));
        entity.setDescription(I18N.entityAttribute(I18N.CODING_LANGUAGE, cronJob.getDescription()));
        entity.setJavaClass(cronJob.getJavaClass());
        entity.setIsActivated(cronJob.isActivatedByDefault());
        entity.setCronTrigger(cronJob.getTrigger().toString());
        return entity;
    }

    private ScheduledFuture<?> scheduleCronJob(AppContext appContext, AbstractCronJob job) {
        final String cronTrigger = getEntityByJavaClass(job.getJavaClass()).getCronTrigger();
        return appContext.getBean(TaskScheduler.class).schedule(job::execute, new CronTrigger(cronTrigger));
    }

    private Set<AbstractCronJob> getAllCronJobInstances(AppContext appContext) {
        return ReflectionUtils.getAllProjectClassesImplementingSuperClass(AbstractCronJob.class)
                .stream()
                .map(c -> getOrInitInstanceByClass(appContext, c))
                .collect(Collectors.toSet());

    }

    private AbstractCronJob getOrInitInstanceByClass(AppContext appContext, Class<? extends AbstractCronJob> jobClass) {
        return appContext.getBeanIfExists(jobClass)
                .map(AbstractCronJob.class::cast)
                .orElseGet(() -> ReflectionUtils.createNewInstanceWithEmptyConstructor(jobClass));
    }

    public void executeByUUID(UUID uuid) {
        final AbstractCronJob jobInstance = getEntityById(uuid).getJobInstance();
        new Thread(() -> jobInstance.execute(true)).start();
    }
}

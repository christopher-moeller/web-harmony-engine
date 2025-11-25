package com.webharmony.core.api.rest.controller;

import com.webharmony.core.api.rest.controller.utils.AbstractCrudController;
import com.webharmony.core.api.rest.controller.utils.CoreApiAuthorization;
import com.webharmony.core.api.rest.controller.utils.MethodNotAllowed;
import com.webharmony.core.api.rest.controller.utils.dispacher.ControllerDispatcher;
import com.webharmony.core.api.rest.controller.utils.dispacher.EntityCrudDispatcher;
import com.webharmony.core.api.rest.model.CronJobDto;
import com.webharmony.core.api.rest.model.ECoreRestResources;
import com.webharmony.core.api.rest.model.RestResourceInfo;
import com.webharmony.core.api.rest.model.utils.ResponseResource;
import com.webharmony.core.data.enums.ECoreActorRight;
import com.webharmony.core.configuration.security.ApplicationRight;
import com.webharmony.core.service.data.CronJobService;
import com.webharmony.core.utils.exceptions.MethodNotAllowedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.UUID;

@ApiController("api/cronJobs")
public class CronJobController extends AbstractCrudController<CronJobDto> {

    private final CronJobService cronJobService;

    public CronJobController(CronJobService cronJobService) {
        this.cronJobService = cronJobService;
    }

    @PostMapping("{uuid}/execute")
    @CoreApiAuthorization(ECoreActorRight.CORE_CRON_JOBS_EXECUTE)
    public ResponseEntity<Void> execute(@PathVariable("uuid") UUID uuid) {
        this.cronJobService.executeByUUID(uuid);
        return ResponseEntity.ok().build();
    }

    @Override
    @MethodNotAllowed
    public ResponseResource<CronJobDto> createNewEntry(CronJobDto dto) {
        throw new MethodNotAllowedException();
    }

    @Override
    @MethodNotAllowed
    public ResponseEntity<Void> deleteEntry(UUID uuid) {
        throw new MethodNotAllowedException();
    }

    @Override
    public RestResourceInfo getRestResource() {
        return ECoreRestResources.CRON_JOBS;
    }

    @Override
    protected ControllerDispatcher<CronJobDto> loadControllerDispatcherInternal() {
        return new EntityCrudDispatcher<>(CronJobService.class, this);
    }

    @Override
    protected ApplicationRight getGlobalRightForCrudMethods() {
        return ECoreActorRight.CORE_CRON_JOBS_CRUD;
    }
}

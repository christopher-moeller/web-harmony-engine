package com.webharmony.core.api.rest.controller.log;

import com.webharmony.core.api.rest.controller.ApiController;
import com.webharmony.core.api.rest.controller.utils.AbstractCrudController;
import com.webharmony.core.api.rest.controller.utils.MethodNotAllowed;
import com.webharmony.core.api.rest.controller.utils.dispacher.ControllerDispatcher;
import com.webharmony.core.api.rest.controller.utils.dispacher.EntityCrudDispatcher;
import com.webharmony.core.api.rest.model.ApplicationStartupLogDto;
import com.webharmony.core.api.rest.model.ECoreRestResources;
import com.webharmony.core.api.rest.model.RestResourceInfo;
import com.webharmony.core.api.rest.model.utils.ResponseResource;
import com.webharmony.core.configuration.security.ApplicationRight;
import com.webharmony.core.data.enums.ECoreActorRight;
import com.webharmony.core.service.data.ApplicationStartupLogService;
import com.webharmony.core.utils.exceptions.MethodNotAllowedException;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@ApiController("api/startupLogs")
public class ApplicationStartupLogController extends AbstractCrudController<ApplicationStartupLogDto> {

    @Override
    @MethodNotAllowed
    public ResponseResource<ApplicationStartupLogDto> createNewEntry(ApplicationStartupLogDto dto) {
        throw new MethodNotAllowedException();
    }

    @Override
    @MethodNotAllowed
    public ResponseResource<ApplicationStartupLogDto> updateEntry(UUID uuid, ApplicationStartupLogDto dto) {
        throw new MethodNotAllowedException();
    }

    @Override
    @MethodNotAllowed
    public ResponseEntity<Void> deleteEntry(UUID uuid) {
        throw new MethodNotAllowedException();
    }

    @Override
    public RestResourceInfo getRestResource() {
        return ECoreRestResources.STARTUP_LOGS;
    }

    @Override
    protected ControllerDispatcher<ApplicationStartupLogDto> loadControllerDispatcherInternal() {
        return new EntityCrudDispatcher<>(ApplicationStartupLogService.class, this);
    }

    @Override
    protected ApplicationRight getGlobalRightForCrudMethods() {
        return ECoreActorRight.CORE_STARTUP_LOGS_CRUD;
    }
}

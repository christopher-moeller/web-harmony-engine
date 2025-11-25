package com.webharmony.core.api.rest.controller;

import com.webharmony.core.api.rest.controller.utils.AbstractCrudController;
import com.webharmony.core.api.rest.controller.utils.CoreApiAuthorization;
import com.webharmony.core.api.rest.controller.utils.MethodNotAllowed;
import com.webharmony.core.api.rest.controller.utils.dispacher.ControllerDispatcher;
import com.webharmony.core.api.rest.controller.utils.dispacher.EntityCrudDispatcher;
import com.webharmony.core.api.rest.model.ECoreRestResources;
import com.webharmony.core.api.rest.model.RestResourceInfo;
import com.webharmony.core.api.rest.model.ServerTaskDto;
import com.webharmony.core.api.rest.model.utils.ResponseResource;
import com.webharmony.core.data.enums.ECoreActorRight;
import com.webharmony.core.configuration.security.ApplicationRight;
import com.webharmony.core.service.data.ServerTaskCrudService;
import com.webharmony.core.service.tasks.ServerTaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.UUID;

@ApiController("api/servertasks")
public class ServerTaskController extends AbstractCrudController<ServerTaskDto> {

    private final ServerTaskService serverTaskService;

    public ServerTaskController(ServerTaskService serverTaskService) {
        this.serverTaskService = serverTaskService;
    }

    @PostMapping("{id}/execute")
    @CoreApiAuthorization(ECoreActorRight.CORE_SERVER_TASKS_EXECUTE)
    public ResponseEntity<Void> executeByUUID(@PathVariable("id") UUID uuid) {
        serverTaskService.executeTaskByUUID(uuid);
        return ResponseEntity.ok().build();
    }

    @Override
    @MethodNotAllowed
    public ResponseResource<ServerTaskDto> createNewEntry(ServerTaskDto dto) {
        return super.createNewEntry(dto);
    }

    @Override
    @MethodNotAllowed
    public ResponseEntity<Void> deleteEntry(UUID uuid) {
        return super.deleteEntry(uuid);
    }

    @Override
    public RestResourceInfo getRestResource() {
        return ECoreRestResources.SERVER_TASKS;
    }

    @Override
    protected ControllerDispatcher<ServerTaskDto> loadControllerDispatcherInternal() {
        return new EntityCrudDispatcher<>(ServerTaskCrudService.class, this);
    }

    @Override
    protected ApplicationRight getGlobalRightForCrudMethods() {
        return ECoreActorRight.CORE_SERVER_TASKS_CRUD;
    }
}

package com.webharmony.core.api.rest.controller.error;

import com.webharmony.core.api.rest.controller.ApiController;
import com.webharmony.core.api.rest.controller.utils.*;
import com.webharmony.core.api.rest.controller.utils.dispacher.ControllerDispatcher;
import com.webharmony.core.api.rest.controller.utils.dispacher.EntityCrudDispatcher;
import com.webharmony.core.api.rest.model.ApplicationExceptionDto;
import com.webharmony.core.api.rest.model.ECoreRestResources;
import com.webharmony.core.api.rest.model.RestResourceInfo;
import com.webharmony.core.api.rest.model.error.FrontendRuntimeError;
import com.webharmony.core.api.rest.model.utils.ResponseResource;
import com.webharmony.core.data.enums.ECoreActorRight;
import com.webharmony.core.configuration.security.ApplicationRight;
import com.webharmony.core.service.data.ApplicationExceptionCrudService;
import com.webharmony.core.service.searchcontainer.ApplicationExceptionSearchContainer;
import com.webharmony.core.utils.exceptions.InternalServerException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@ApiController("api/error/applicationExceptions")
public class ApplicationExceptionController extends AbstractCrudController<ApplicationExceptionDto> {

    private final ApplicationExceptionSearchContainer applicationExceptionSearchContainer;

    private final ApplicationExceptionCrudService applicationExceptionCrudService;

    public ApplicationExceptionController(ApplicationExceptionSearchContainer applicationExceptionSearchContainer, ApplicationExceptionCrudService applicationExceptionCrudService) {
        this.applicationExceptionSearchContainer = applicationExceptionSearchContainer;
        this.applicationExceptionCrudService = applicationExceptionCrudService;
    }

    @Override
    @MethodNotAllowed
    public ResponseResource<ApplicationExceptionDto> createNewEntry(ApplicationExceptionDto dto) {
        throw new InternalServerException("Method not allowed");
    }

    @PostMapping
    @ApiAuthentication(ApiAuthenticationType.PUBLIC)
    public ResponseEntity<Void> reportFrontendException(@RequestBody FrontendRuntimeError frontendException) {
        applicationExceptionCrudService.persistApplicationExceptionByFrontendException(frontendException);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    @CoreApiAuthorization(ECoreActorRight.CORE_APPLICATION_EXCEPTIONS_CRUD)
    public ResponseEntity<Void> deleteAllExceptions() {
        applicationExceptionCrudService.deleteAll();
        return ResponseEntity.ok().build();
    }

    @Override
    public ApplicationExceptionSearchContainer getSearchContainer() {
        return applicationExceptionSearchContainer;
    }

    @Override
    public RestResourceInfo getRestResource() {
        return ECoreRestResources.APPLICATION_EXCEPTIONS;
    }

    @Override
    protected ControllerDispatcher<ApplicationExceptionDto> loadControllerDispatcherInternal() {
        return new EntityCrudDispatcher<>(ApplicationExceptionCrudService.class, this);
    }

    @Override
    protected ApplicationRight getGlobalRightForCrudMethods() {
        return ECoreActorRight.CORE_APPLICATION_EXCEPTIONS_CRUD;
    }
}

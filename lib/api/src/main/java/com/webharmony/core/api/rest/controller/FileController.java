package com.webharmony.core.api.rest.controller;

import com.webharmony.core.api.rest.controller.utils.AbstractCrudController;
import com.webharmony.core.api.rest.controller.utils.CoreApiAuthorization;
import com.webharmony.core.api.rest.controller.utils.MethodNotAllowed;
import com.webharmony.core.api.rest.controller.utils.dispacher.ControllerDispatcher;
import com.webharmony.core.api.rest.controller.utils.dispacher.EntityCrudDispatcher;
import com.webharmony.core.api.rest.controller.utils.request.RequestContext;
import com.webharmony.core.api.rest.model.ECoreRestResources;
import com.webharmony.core.api.rest.model.FileDto;
import com.webharmony.core.api.rest.model.RestResourceInfo;
import com.webharmony.core.api.rest.model.utils.FileWebData;
import com.webharmony.core.api.rest.model.utils.ResponseResource;
import com.webharmony.core.api.rest.model.utils.apiobject.ApiResource;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.data.enums.ECoreActorRight;
import com.webharmony.core.data.jpa.model.files.AppFile;
import com.webharmony.core.configuration.security.ApplicationRight;
import com.webharmony.core.service.data.FileService;
import com.webharmony.core.service.searchcontainer.FileSearchContainer;
import com.webharmony.core.utils.exceptions.MethodNotAllowedException;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@ApiController("api/files")
public class FileController extends AbstractCrudController<FileDto> {

    private final FileService fileService;

    private final FileSearchContainer searchContainer;

    public FileController(FileService fileService, FileSearchContainer searchContainer) {
        this.fileService = fileService;
        this.searchContainer = searchContainer;
    }

    @Override
    public RestResourceInfo getRestResource() {
        return ECoreRestResources.FILES;
    }

    @Override
    @MethodNotAllowed
    public ResponseResource<FileDto> createNewEntry(FileDto dto) {
        throw new MethodNotAllowedException();
    }

    @Override
    protected ControllerDispatcher<FileDto> loadControllerDispatcherInternal() {
        return new EntityCrudDispatcher<>(FileService.class, this);
    }

    @PostMapping("upload")
    @CoreApiAuthorization(ECoreActorRight.CORE_FILES_CRUD)
    public ResponseEntity<List<ApiResource<FileDto>>> handleFileUpload(@RequestBody List<FileWebData> fileWebData) {
        return ResponseEntity.ok(fileService.createNewFileResources(fileWebData, RequestContext.empty(ContextHolder.getContext().getSpringContext())));
    }

    @GetMapping("{uuid}/download")
    @CoreApiAuthorization(ECoreActorRight.CORE_FILES_CRUD)
    public ResponseEntity<Resource> getResourceDownload(@PathVariable("uuid") UUID uuid) {
        final AppFile appFile = fileService.getEntityById(uuid);
        return fileService.getResourceAsResponseEntityByFile(appFile);
    }

    @Override
    protected ApplicationRight getGlobalRightForCrudMethods() {
        return ECoreActorRight.CORE_FILES_CRUD;
    }

    @Override
    public FileSearchContainer getSearchContainer() {
        return searchContainer;
    }
}

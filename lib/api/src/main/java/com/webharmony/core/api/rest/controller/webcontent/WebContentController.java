package com.webharmony.core.api.rest.controller.webcontent;

import com.webharmony.core.api.rest.controller.ApiController;
import com.webharmony.core.api.rest.controller.utils.AbstractCrudController;
import com.webharmony.core.api.rest.controller.utils.ApiAuthentication;
import com.webharmony.core.api.rest.controller.utils.ApiAuthenticationType;
import com.webharmony.core.api.rest.controller.utils.dispacher.ControllerDispatcher;
import com.webharmony.core.api.rest.controller.utils.dispacher.EntityCrudDispatcher;
import com.webharmony.core.api.rest.model.ECoreRestResources;
import com.webharmony.core.api.rest.model.RestResourceInfo;
import com.webharmony.core.api.rest.model.webcontent.WebContentDto;
import com.webharmony.core.configuration.security.ApplicationRight;
import com.webharmony.core.data.enums.ECoreActorRight;
import com.webharmony.core.service.data.WebContentCrudService;
import com.webharmony.core.service.searchcontainer.AbstractSearchContainer;
import com.webharmony.core.service.searchcontainer.WebContentSearchContainer;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@ApiController("api/webContents")
public class WebContentController extends AbstractCrudController<WebContentDto> {

    private final WebContentSearchContainer searchContainer;

    private final WebContentCrudService webContentService;

    public WebContentController(WebContentSearchContainer searchContainer, WebContentCrudService webContentService) {
        this.searchContainer = searchContainer;
        this.webContentService = webContentService;
    }


    @ApiAuthentication(ApiAuthenticationType.PUBLIC)
    @GetMapping("{webContentUUID}/files/{fileUUID}")
    public ResponseEntity<Resource> getFile(@PathVariable("webContentUUID") UUID webContentUUID, @PathVariable("fileUUID") UUID fileUUID) {
        return webContentService.getFileByWebContent(webContentUUID, fileUUID);
    }

    @ApiAuthentication(ApiAuthenticationType.PUBLIC)
    @GetMapping("contentAreas/{area}")
    public ResponseEntity<WebContentDto> loadUniqueWebContentByArea(@PathVariable("area") String uniqueAreaName) {
        return ResponseEntity.ok(webContentService.getUniqueWebContentByArea(uniqueAreaName));
    }

    @Override
    public RestResourceInfo getRestResource() {
        return ECoreRestResources.WEB_CONTENTS;
    }

    @Override
    protected ControllerDispatcher<WebContentDto> loadControllerDispatcherInternal() {
        return new EntityCrudDispatcher<>(WebContentCrudService.class, this);
    }

    @Override
    public AbstractSearchContainer getSearchContainer() {
        return searchContainer;
    }

    @Override
    protected ApplicationRight getGlobalRightForCrudMethods() {
        return ECoreActorRight.CORE_WEB_CONTENT_CRUD;
    }
}

package com.webharmony.core.api.rest.controller;

import com.webharmony.core.api.rest.controller.utils.AbstractBaseController;
import com.webharmony.core.api.rest.controller.utils.ApiAuthentication;
import com.webharmony.core.api.rest.controller.utils.ApiAuthenticationType;
import com.webharmony.core.api.rest.controller.utils.request.RequestContext;
import com.webharmony.core.api.rest.model.utils.AbstractResourceDto;
import com.webharmony.core.service.appresources.AppResourceService;
import com.webharmony.core.service.appresources.utils.CrudResourceInfoDto;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@ApiController("api/appresources")
public class AppResourceController extends AbstractBaseController {

    private final AppResourceService appResourceService;
    private final ApplicationContext applicationContext;

    public AppResourceController(AppResourceService appResourceService, ApplicationContext applicationContext) {
        this.appResourceService = appResourceService;
        this.applicationContext = applicationContext;
    }

    @GetMapping("{resourceName}")
    @ApiAuthentication(ApiAuthenticationType.PUBLIC)
    public ResponseEntity<CrudResourceInfoDto> getResourceByName(@PathVariable("resourceName") String resourceName) {
        return ResponseEntity.ok(appResourceService.getResourceByName(resourceName, RequestContext.empty(applicationContext)));
    }

    @GetMapping("{resourceName}/template")
    @ApiAuthentication(ApiAuthenticationType.PUBLIC)
    public ResponseEntity<AbstractResourceDto> getInitialResourceTemplate(@PathVariable("resourceName") String resourceName) {
        return ResponseEntity.ok(appResourceService.createInitialResourceTemplateByName(resourceName));
    }


}

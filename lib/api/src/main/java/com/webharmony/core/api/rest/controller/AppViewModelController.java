package com.webharmony.core.api.rest.controller;

import com.webharmony.core.api.rest.controller.utils.AbstractBaseController;
import com.webharmony.core.api.rest.controller.utils.ApiAuthentication;
import com.webharmony.core.api.rest.controller.utils.ApiAuthenticationType;
import com.webharmony.core.api.rest.model.utils.ViewModel;
import com.webharmony.core.service.appviewmodels.ViewModelInfoDto;
import com.webharmony.core.service.appviewmodels.ViewModelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@CrossOrigin(origins = "*")
@ApiController("api/appviewmodels")
public class AppViewModelController extends AbstractBaseController {

    private final ViewModelService viewModelService;

    public AppViewModelController(ViewModelService viewModelService) {
        this.viewModelService = viewModelService;
    }

    @CrossOrigin(origins = "*")
    @GetMapping("{viewModelName}")
    @ApiAuthentication(ApiAuthenticationType.PUBLIC)
    public ResponseEntity<ViewModelInfoDto> getViewModelByName(@PathVariable("viewModelName") String viewModelName) {
        return ResponseEntity.ok(viewModelService.getViewModelByName(viewModelName));
    }

    @CrossOrigin(origins = "*")
    @GetMapping("{viewModelName}/template")
    @ApiAuthentication(ApiAuthenticationType.PUBLIC)
    public ResponseEntity<ViewModel> getInitialViewModelTemplate(@PathVariable("viewModelName") String viewModelName) {
        return ResponseEntity.ok(viewModelService.createInitialViewModelTemplateByName(viewModelName));
    }
}

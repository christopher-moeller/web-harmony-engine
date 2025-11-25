package com.webharmony.core.api.rest.controller;

import com.webharmony.core.api.rest.controller.utils.AbstractBaseController;
import com.webharmony.core.api.rest.controller.utils.ApiAuthentication;
import com.webharmony.core.api.rest.controller.utils.ApiAuthenticationType;
import com.webharmony.core.api.rest.controller.utils.CoreApiAuthorization;
import com.webharmony.core.api.rest.model.view.ApplicationStatusVM;
import com.webharmony.core.context.AppStatusHolder;
import com.webharmony.core.data.enums.ECoreActorRight;
import com.webharmony.core.service.data.validation.ValidatorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@ApiController("api/applicationStatus")
public class ApplicationStatusController extends AbstractBaseController {

    private final ValidatorService validatorService;

    public ApplicationStatusController(ValidatorService validatorService) {
        this.validatorService = validatorService;
    }

    @GetMapping
    @ApiAuthentication(ApiAuthenticationType.PUBLIC)
    public ResponseEntity<ApplicationStatusVM> getCurrentStatus() {
        return ResponseEntity.ok(AppStatusHolder.getInstance().getCurrentStatus());
    }

    @PutMapping
    @CoreApiAuthorization(ECoreActorRight.CORE_APPLICATION_STATUS_SAVE)
    public ResponseEntity<Void> saveCurrentStatus(@RequestBody ApplicationStatusVM status) {
        validatorService.validate(status);
        AppStatusHolder.getInstance().setCurrentStatus(status);
        return ResponseEntity.ok().build();
    }

    @PostMapping("reset")
    @CoreApiAuthorization(ECoreActorRight.CORE_APPLICATION_STATUS_SAVE)
    public ResponseEntity<Void> resetStatus() {
        AppStatusHolder.getInstance().resetStatus();
        return ResponseEntity.ok().build();
    }

}

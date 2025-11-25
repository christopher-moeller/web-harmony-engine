package com.webharmony.core.api.rest.controller.error;

import com.webharmony.core.api.rest.controller.ApiController;
import com.webharmony.core.api.rest.controller.utils.AbstractCrudController;
import com.webharmony.core.api.rest.controller.utils.ApiAuthentication;
import com.webharmony.core.api.rest.controller.utils.ApiAuthenticationType;
import com.webharmony.core.api.rest.controller.utils.dispacher.ControllerDispatcher;
import com.webharmony.core.api.rest.controller.utils.dispacher.EntityCrudDispatcher;
import com.webharmony.core.api.rest.model.ECoreRestResources;
import com.webharmony.core.api.rest.model.ReportUserBugRequest;
import com.webharmony.core.api.rest.model.RestResourceInfo;
import com.webharmony.core.api.rest.model.error.ReportedUserErrorDto;
import com.webharmony.core.data.enums.ECoreActorRight;
import com.webharmony.core.configuration.security.ApplicationRight;
import com.webharmony.core.service.error.ReportedUserErrorCrudService;
import com.webharmony.core.service.searchcontainer.ReportUserErrorsSearchContainer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@ApiController("api/error/reportedUserErrors")
public class ReportUserErrorController extends AbstractCrudController<ReportedUserErrorDto> {

    private final ReportUserErrorsSearchContainer searchContainer;

    private final ReportedUserErrorCrudService reportedUserErrorCrudService;

    public ReportUserErrorController(ReportUserErrorsSearchContainer searchContainer, ReportedUserErrorCrudService reportedUserErrorCrudService) {
        this.searchContainer = searchContainer;
        this.reportedUserErrorCrudService = reportedUserErrorCrudService;
    }

    @PostMapping("report")
    @ApiAuthentication(ApiAuthenticationType.PUBLIC)
    public ResponseEntity<Void> reportUserBug(@RequestBody ReportUserBugRequest reportUserBugRequest) {
        reportedUserErrorCrudService.reportUserBug(reportUserBugRequest);
        return ResponseEntity.ok().build();
    }

    @Override
    public ReportUserErrorsSearchContainer getSearchContainer() {
        return searchContainer;
    }

    @Override
    public RestResourceInfo getRestResource() {
        return ECoreRestResources.REPORT_USER_ERRORS;
    }

    @Override
    protected ControllerDispatcher<ReportedUserErrorDto> loadControllerDispatcherInternal() {
        return new EntityCrudDispatcher<>(ReportedUserErrorCrudService.class, this);
    }

    @Override
    protected ApplicationRight getGlobalRightForCrudMethods() {
        return ECoreActorRight.CORE_REPORT_USER_ERRORS_CRUD;
    }
}

package com.webharmony.core.api.rest.controller;

import com.webharmony.core.api.rest.controller.utils.AbstractCrudController;
import com.webharmony.core.api.rest.controller.utils.CoreApiAuthorization;
import com.webharmony.core.api.rest.controller.utils.dispacher.ControllerDispatcher;
import com.webharmony.core.api.rest.controller.utils.dispacher.EntityCrudDispatcher;
import com.webharmony.core.api.rest.model.ECoreRestResources;
import com.webharmony.core.api.rest.model.EmailDto;
import com.webharmony.core.api.rest.model.RestResourceInfo;
import com.webharmony.core.data.enums.ECoreActorRight;
import com.webharmony.core.configuration.security.ApplicationRight;
import com.webharmony.core.service.data.EmailCrudService;
import com.webharmony.core.service.email.EmailService;
import com.webharmony.core.service.searchcontainer.AbstractSearchContainer;
import com.webharmony.core.service.searchcontainer.EmailSearchContainer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.UUID;

@ApiController("api/emails")
public class EmailController extends AbstractCrudController<EmailDto> {

    private final EmailService emailService;

    private final EmailSearchContainer emailSearchContainer;

    public EmailController(EmailService emailService, EmailSearchContainer emailSearchContainer) {
        this.emailService = emailService;
        this.emailSearchContainer = emailSearchContainer;
    }

    @PostMapping("{uuid}/send")
    @CoreApiAuthorization(ECoreActorRight.CORE_EMAIL_SEND_BY_ENTITY)
    public ResponseEntity<Void> sendEmail(@PathVariable("uuid") UUID uuid) {
        emailService.sendEmail(uuid);
        return ResponseEntity.ok().build();
    }

    @Override
    public RestResourceInfo getRestResource() {
        return ECoreRestResources.EMAILS;
    }

    @Override
    protected ControllerDispatcher<EmailDto> loadControllerDispatcherInternal() {
        return new EntityCrudDispatcher<>(EmailCrudService.class, this);
    }

    @Override
    protected ApplicationRight getGlobalRightForCrudMethods() {
        return ECoreActorRight.CORE_EMAIL_CRUD_RIGHTS;
    }

    @Override
    public AbstractSearchContainer getSearchContainer() {
        return emailSearchContainer;
    }
}

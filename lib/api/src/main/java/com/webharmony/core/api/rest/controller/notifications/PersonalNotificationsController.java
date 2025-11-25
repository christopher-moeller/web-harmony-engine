package com.webharmony.core.api.rest.controller.notifications;

import com.webharmony.core.api.rest.controller.ApiController;
import com.webharmony.core.api.rest.controller.utils.AbstractCrudController;
import com.webharmony.core.api.rest.controller.utils.CoreApiAuthorization;
import com.webharmony.core.api.rest.controller.utils.MethodNotAllowed;
import com.webharmony.core.api.rest.controller.utils.dispacher.ControllerDispatcher;
import com.webharmony.core.api.rest.model.ActorNotificationDto;
import com.webharmony.core.api.rest.model.ActorPersonalNotificationInfoDto;
import com.webharmony.core.api.rest.model.ECoreRestResources;
import com.webharmony.core.api.rest.model.RestResourceInfo;
import com.webharmony.core.api.rest.model.utils.ResponseResource;
import com.webharmony.core.data.enums.ECoreActorRight;
import com.webharmony.core.configuration.security.ApplicationRight;
import com.webharmony.core.service.data.ActorPersonalNotificationService;
import com.webharmony.core.utils.exceptions.MethodNotAllowedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@ApiController("api/notifications/personal")
public class PersonalNotificationsController extends AbstractCrudController<ActorNotificationDto> {

    private final ActorPersonalNotificationService personalNotificationService;

    public PersonalNotificationsController(ActorPersonalNotificationService personalNotificationService) {
        this.personalNotificationService = personalNotificationService;
    }

    @CoreApiAuthorization(ECoreActorRight.CORE_PERSONAL_NOTIFICATIONS)
    @GetMapping("info")
    public ResponseEntity<ActorPersonalNotificationInfoDto> getActorPersonalNotificationInfo() {
        return ResponseEntity.ok(personalNotificationService.getActorPersonalNotificationInfo());
    }

    @CoreApiAuthorization(ECoreActorRight.CORE_PERSONAL_NOTIFICATIONS)
    @PutMapping("{id}/readState")
    public ResponseEntity<Void> changeReadStateOfPersonalNotification(@PathVariable("id") UUID id, @RequestParam("read") Boolean read) {
        personalNotificationService.changeReadStateOfPersonalNotification(id, read);
        return ResponseEntity.ok().build();
    }

    @Override
    @MethodNotAllowed
    public ResponseResource<ActorNotificationDto> createNewEntry(ActorNotificationDto dto) {
        throw new MethodNotAllowedException();
    }

    @Override
    @MethodNotAllowed
    public ResponseResource<ActorNotificationDto> updateEntry(UUID uuid, ActorNotificationDto dto) {
        throw new MethodNotAllowedException();
    }

    @Override
    @MethodNotAllowed
    public ResponseEntity<Void> deleteEntry(UUID uuid) {
        throw new MethodNotAllowedException();
    }

    @Override
    public RestResourceInfo getRestResource() {
        return ECoreRestResources.PERSONAL_NOTIFICATIONS;
    }


    @Override
    protected ControllerDispatcher<ActorNotificationDto> loadControllerDispatcherInternal() {
        return this.personalNotificationService;
    }


    @Override
    protected ApplicationRight getGlobalRightForCrudMethods() {
        return ECoreActorRight.CORE_PERSONAL_NOTIFICATIONS;
    }
}

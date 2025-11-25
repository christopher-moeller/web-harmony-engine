package com.webharmony.core.api.rest.controller;

import com.webharmony.core.api.rest.controller.utils.AbstractCrudController;
import com.webharmony.core.api.rest.controller.utils.MethodNotAllowed;
import com.webharmony.core.api.rest.controller.utils.dispacher.ControllerDispatcher;
import com.webharmony.core.api.rest.controller.utils.dispacher.EntityCrudDispatcher;
import com.webharmony.core.api.rest.model.ECoreRestResources;
import com.webharmony.core.api.rest.model.HarmonyEventDto;
import com.webharmony.core.api.rest.model.RestResourceInfo;
import com.webharmony.core.api.rest.model.utils.ResponseResource;
import com.webharmony.core.data.enums.ECoreActorRight;
import com.webharmony.core.configuration.security.ApplicationRight;
import com.webharmony.core.service.data.HarmonyEventCrudService;
import com.webharmony.core.service.searchcontainer.AbstractSearchContainer;
import com.webharmony.core.service.searchcontainer.HarmonyEventSearchContainer;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@ApiController("api/harmonyEvents")
public class HarmonyEventController extends AbstractCrudController<HarmonyEventDto> {

    private final HarmonyEventSearchContainer harmonyEventSearchContainer;

    public HarmonyEventController(HarmonyEventSearchContainer harmonyEventSearchContainer) {
        this.harmonyEventSearchContainer = harmonyEventSearchContainer;
    }

    @Override
    public AbstractSearchContainer getSearchContainer() {
        return harmonyEventSearchContainer;
    }

    @Override
    @MethodNotAllowed
    public ResponseResource<HarmonyEventDto> createNewEntry(HarmonyEventDto dto) {
        return super.createNewEntry(dto);
    }

    @Override
    @MethodNotAllowed
    public ResponseResource<HarmonyEventDto> updateEntry(UUID uuid, HarmonyEventDto dto) {
        return super.updateEntry(uuid, dto);
    }

    @Override
    @MethodNotAllowed
    public ResponseEntity<Void> deleteEntry(UUID uuid) {
        return super.deleteEntry(uuid);
    }

    @Override
    public RestResourceInfo getRestResource() {
        return ECoreRestResources.HARMONY_EVENTS;
    }

    @Override
    protected ControllerDispatcher<HarmonyEventDto> loadControllerDispatcherInternal() {
        return new EntityCrudDispatcher<>(HarmonyEventCrudService.class, this);
    }

    @Override
    protected ApplicationRight getGlobalRightForCrudMethods() {
        return ECoreActorRight.CORE_HARMONY_EVENT_CRUD;
    }
}

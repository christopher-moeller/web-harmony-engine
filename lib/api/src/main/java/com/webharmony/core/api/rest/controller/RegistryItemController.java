package com.webharmony.core.api.rest.controller;

import com.webharmony.core.api.rest.controller.utils.AbstractCrudController;
import com.webharmony.core.api.rest.controller.utils.MethodNotAllowed;
import com.webharmony.core.api.rest.controller.utils.dispacher.ControllerDispatcher;
import com.webharmony.core.api.rest.controller.utils.dispacher.EntityCrudDispatcher;
import com.webharmony.core.api.rest.model.ECoreRestResources;
import com.webharmony.core.api.rest.model.RegistryItemDto;
import com.webharmony.core.api.rest.model.RestResourceInfo;
import com.webharmony.core.api.rest.model.utils.ResponseResource;
import com.webharmony.core.data.enums.ECoreActorRight;
import com.webharmony.core.configuration.security.ApplicationRight;
import com.webharmony.core.service.data.RegistryItemService;
import com.webharmony.core.utils.exceptions.MethodNotAllowedException;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@ApiController("api/registryItems")
public class RegistryItemController extends AbstractCrudController<RegistryItemDto> {

    @Override
    @MethodNotAllowed
    public ResponseResource<RegistryItemDto> createNewEntry(RegistryItemDto dto) {
        throw new MethodNotAllowedException();
    }

    @Override
    @MethodNotAllowed
    public ResponseEntity<Void> deleteEntry(UUID uuid) {
        throw new MethodNotAllowedException();
    }

    @Override
    public RestResourceInfo getRestResource() {
        return ECoreRestResources.REGISTRY_ITEMS;
    }

    @Override
    protected ControllerDispatcher<RegistryItemDto> loadControllerDispatcherInternal() {
        return new EntityCrudDispatcher<>(RegistryItemService.class, this);
    }

    @Override
    protected ApplicationRight getGlobalRightForCrudMethods() {
        return ECoreActorRight.CORE_REGISTRY_CRUD;
    }
}

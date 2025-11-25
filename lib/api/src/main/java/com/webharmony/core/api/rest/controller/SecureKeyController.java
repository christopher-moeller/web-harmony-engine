package com.webharmony.core.api.rest.controller;

import com.webharmony.core.api.rest.controller.utils.AbstractCrudController;
import com.webharmony.core.api.rest.controller.utils.MethodNotAllowed;
import com.webharmony.core.api.rest.controller.utils.dispacher.ControllerDispatcher;
import com.webharmony.core.api.rest.controller.utils.dispacher.InMemoryDtoCrudDispatcher;
import com.webharmony.core.api.rest.controller.utils.request.RequestContext;
import com.webharmony.core.api.rest.model.ECoreRestResources;
import com.webharmony.core.api.rest.model.RestResourceInfo;
import com.webharmony.core.api.rest.model.SecureKeyDto;
import com.webharmony.core.api.rest.model.utils.ResponseResource;
import com.webharmony.core.data.enums.ECoreActorRight;
import com.webharmony.core.configuration.security.ApplicationRight;
import com.webharmony.core.utils.exceptions.MethodNotAllowedException;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@ApiController("api/secureKeys")
public class SecureKeyController extends AbstractCrudController<SecureKeyDto> {

    @Override
    @MethodNotAllowed
    public ResponseResource<SecureKeyDto> createNewEntry(SecureKeyDto dto) {
        throw new MethodNotAllowedException();
    }

    @Override
    @MethodNotAllowed
    public ResponseEntity<Void> deleteEntry(UUID uuid) {
        throw new MethodNotAllowedException();
    }


    @Override
    public RestResourceInfo getRestResource() {
        return ECoreRestResources.SECURE_KEY;
    }

    @Override
    protected ControllerDispatcher<SecureKeyDto> loadControllerDispatcherInternal() {
        return new InMemoryDtoCrudDispatcher<>(this.getDataClass()) {
            @Override
            protected SecureKeyDto mapDto(SecureKeyDto dto) {
                SecureKeyDto newDto = new SecureKeyDto();
                newDto.setId(dto.getId());
                newDto.setName(dto.getName());
                newDto.setKey(buildProtectedKey(dto.getKey()));
                return newDto;
            }

            @Override
            public SecureKeyDto updateEntry(UUID uuid, SecureKeyDto dto, RequestContext requestContext) {
                return super.updateEntry(uuid, dto, requestContext);
            }
        };
    }

    private String buildProtectedKey(String key) {
        if(key == null) {
            return null;
        }
        return "*".repeat(key.length());
    }

    @Override
    protected ApplicationRight getGlobalRightForCrudMethods() {
        return ECoreActorRight.CORE_SECURE_KEYS_CRUD;
    }
}

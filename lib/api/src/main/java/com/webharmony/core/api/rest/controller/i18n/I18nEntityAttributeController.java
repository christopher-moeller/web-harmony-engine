package com.webharmony.core.api.rest.controller.i18n;

import com.webharmony.core.api.rest.controller.ApiController;
import com.webharmony.core.api.rest.controller.utils.AbstractCrudController;
import com.webharmony.core.api.rest.controller.utils.CoreApiAuthorization;
import com.webharmony.core.api.rest.controller.utils.MethodNotAllowed;
import com.webharmony.core.api.rest.controller.utils.dispacher.ControllerDispatcher;
import com.webharmony.core.api.rest.controller.utils.dispacher.EntityCrudDispatcher;
import com.webharmony.core.api.rest.model.ECoreRestResources;
import com.webharmony.core.api.rest.model.RestResourceInfo;
import com.webharmony.core.api.rest.model.i18n.I18nEntityAttributeDto;
import com.webharmony.core.api.rest.model.i18n.I18nTranslationStatistic;
import com.webharmony.core.api.rest.model.utils.FileWebData;
import com.webharmony.core.api.rest.model.utils.ResponseResource;
import com.webharmony.core.data.enums.ECoreActorRight;
import com.webharmony.core.configuration.security.ApplicationRight;
import com.webharmony.core.service.i18n.I18nDataTransferService;
import com.webharmony.core.service.i18n.I18nEntityAttributeService;
import com.webharmony.core.service.searchcontainer.AbstractSearchContainer;
import com.webharmony.core.service.searchcontainer.I18nEntityAttributeSearchContainer;
import com.webharmony.core.utils.dev.i18n.datatransfer.I18nImportResult;
import com.webharmony.core.utils.exceptions.MethodNotAllowedException;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@ApiController("api/i18nEntityAttribute")
public class I18nEntityAttributeController extends AbstractCrudController<I18nEntityAttributeDto> {

    private final I18nEntityAttributeSearchContainer i18nEntityAttributeSearchContainer;

    private final I18nDataTransferService dataTransferService;

    private final I18nEntityAttributeService i18nEntityAttributeService;

    public I18nEntityAttributeController(I18nEntityAttributeSearchContainer i18nEntityAttributeSearchContainer, I18nDataTransferService dataTransferService, I18nEntityAttributeService i18nEntityAttributeService) {
        this.i18nEntityAttributeSearchContainer = i18nEntityAttributeSearchContainer;
        this.dataTransferService = dataTransferService;
        this.i18nEntityAttributeService = i18nEntityAttributeService;
    }

    @GetMapping("export")
    @CoreApiAuthorization(ECoreActorRight.CORE_I18N_ENTITY_ATTRIBUTE_CRUD)
    public ResponseEntity<Resource> exportKeyEntryData(@RequestParam(name = "includeCoreEntries", required = false, defaultValue = "true") boolean includeCoreEntries, @RequestParam(name = "includeSubprojectEntries", required = false, defaultValue = "true") boolean includeSubprojectEntries) {
        return dataTransferService.exportEnumEntityTranslations(includeCoreEntries, includeSubprojectEntries);
    }

    @PostMapping("import")
    @CoreApiAuthorization(ECoreActorRight.CORE_I18N_ENTITY_ATTRIBUTE_CRUD)
    public ResponseEntity<I18nImportResult> importKeyEntryData(@RequestBody FileWebData fileWebData) {
        return ResponseEntity.ok(dataTransferService.importEnumEntityData(fileWebData));
    }

    @GetMapping("frontendTranslations/nextUnresolved")
    @CoreApiAuthorization(ECoreActorRight.CORE_I18N_ENTITY_ATTRIBUTE_CRUD)
    public ResponseEntity<UUID> getIdOfNextEntryToBeResolved() {
        return ResponseEntity.ok(i18nEntityAttributeService.getIdOfNextEntryToBeResolved());
    }

    @GetMapping("statistics")
    @CoreApiAuthorization(ECoreActorRight.CORE_I18N_ENTITY_ATTRIBUTE_CRUD)
    public ResponseEntity<I18nTranslationStatistic> getStatistics() {
        return ResponseEntity.ok(i18nEntityAttributeService.getStatistics());
    }

    @Override
    @MethodNotAllowed
    public ResponseResource<I18nEntityAttributeDto> createNewEntry(I18nEntityAttributeDto dto) {
        throw new MethodNotAllowedException();
    }

    @Override
    @MethodNotAllowed
    public ResponseEntity<Void> deleteEntry(UUID uuid) {
        throw new MethodNotAllowedException();
    }

    @Override
    public RestResourceInfo getRestResource() {
        return ECoreRestResources.I18N_ENTITY_ATTRIBUTE;
    }

    @Override
    protected ControllerDispatcher<I18nEntityAttributeDto> loadControllerDispatcherInternal() {
        return new EntityCrudDispatcher<>(I18nEntityAttributeService.class, this);
    }

    @Override
    protected ApplicationRight getGlobalRightForCrudMethods() {
        return ECoreActorRight.CORE_I18N_ENTITY_ATTRIBUTE_CRUD;
    }

    @Override
    public AbstractSearchContainer getSearchContainer() {
        return i18nEntityAttributeSearchContainer;
    }
}

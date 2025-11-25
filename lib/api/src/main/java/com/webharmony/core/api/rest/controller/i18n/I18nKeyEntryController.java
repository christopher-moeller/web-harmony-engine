package com.webharmony.core.api.rest.controller.i18n;

import com.webharmony.core.api.rest.controller.ApiController;
import com.webharmony.core.api.rest.controller.utils.*;
import com.webharmony.core.api.rest.controller.utils.dispacher.ControllerDispatcher;
import com.webharmony.core.api.rest.controller.utils.dispacher.EntityCrudDispatcher;
import com.webharmony.core.api.rest.model.ECoreRestResources;
import com.webharmony.core.api.rest.model.RestResourceInfo;
import com.webharmony.core.api.rest.model.i18n.I18nFrontendTranslation;
import com.webharmony.core.api.rest.model.i18n.I18nKeyEntryDto;
import com.webharmony.core.api.rest.model.i18n.I18nTranslationStatistic;
import com.webharmony.core.api.rest.model.utils.FileWebData;
import com.webharmony.core.api.rest.model.utils.ResponseResource;
import com.webharmony.core.data.enums.ECoreActorRight;
import com.webharmony.core.i18n.EI18nLanguage;
import com.webharmony.core.configuration.security.ApplicationRight;
import com.webharmony.core.service.i18n.I18nDataTransferService;
import com.webharmony.core.service.i18n.I18nKeyEntryService;
import com.webharmony.core.service.searchcontainer.AbstractSearchContainer;
import com.webharmony.core.service.searchcontainer.I18nKeyEntrySearchContainer;
import com.webharmony.core.utils.dev.i18n.datatransfer.I18nImportResult;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@ApiController("api/i18nKeyEntries")
public class I18nKeyEntryController extends AbstractCrudController<I18nKeyEntryDto> {

    private final I18nKeyEntrySearchContainer i18nKeyEntrySearchContainer;

    private final I18nDataTransferService dataTransferService;

    private final I18nKeyEntryService keyEntryService;


    public I18nKeyEntryController(I18nKeyEntrySearchContainer i18nKeyEntrySearchContainer, I18nDataTransferService dataTransferService, I18nKeyEntryService keyEntryService) {
        this.i18nKeyEntrySearchContainer = i18nKeyEntrySearchContainer;
        this.dataTransferService = dataTransferService;
        this.keyEntryService = keyEntryService;
    }

    @GetMapping("export")
    @CoreApiAuthorization(ECoreActorRight.CORE_I18N_KEY_ENTRY_CRUD)
    public ResponseEntity<Resource> exportKeyEntryData(@RequestParam(name = "includeCoreEntries", required = false, defaultValue = "true") boolean includeCoreEntries, @RequestParam(name = "includeSubprojectEntries", required = false, defaultValue = "true") boolean includeSubprojectEntries) {
        return dataTransferService.exportKeyEntryData(includeCoreEntries, includeSubprojectEntries);
    }

    @PostMapping("import")
    @CoreApiAuthorization(ECoreActorRight.CORE_I18N_KEY_ENTRY_CRUD)
    public ResponseEntity<I18nImportResult> importKeyEntryData(@RequestBody FileWebData fileWebData) {
        return ResponseEntity.ok(dataTransferService.importKeyEntryData(fileWebData));
    }

    @GetMapping("frontendTranslations/{language}")
    @ApiAuthentication(ApiAuthenticationType.PUBLIC)
    public ResponseEntity<I18nFrontendTranslation> getFrontendTranslation(@PathVariable("language") final String language) {
        return ResponseEntity.ok(keyEntryService.getFrontendTranslation(EI18nLanguage.getByKey(language)));
    }



    @GetMapping("frontendTranslations/nextUnresolved")
    @CoreApiAuthorization(ECoreActorRight.CORE_I18N_KEY_ENTRY_CRUD)
    public ResponseEntity<UUID> getIdOfNextEntryToBeResolved() {
        return ResponseEntity.ok(keyEntryService.getIdOfNextEntryToBeResolved());
    }

    @GetMapping("statistics")
    @CoreApiAuthorization(ECoreActorRight.CORE_I18N_KEY_ENTRY_CRUD)
    public ResponseEntity<I18nTranslationStatistic> getStatistics() {
        return ResponseEntity.ok(keyEntryService.getStatistics());
    }

    @Override
    @MethodNotAllowed
    public ResponseResource<I18nKeyEntryDto> createNewEntry(I18nKeyEntryDto dto) {
        return super.createNewEntry(dto);
    }

    @Override
    @MethodNotAllowed
    public ResponseEntity<Void> deleteEntry(UUID uuid) {
        return super.deleteEntry(uuid);
    }

    @Override
    public RestResourceInfo getRestResource() {
        return ECoreRestResources.I18N_KEY_ENTRIES;
    }

    @Override
    protected ControllerDispatcher<I18nKeyEntryDto> loadControllerDispatcherInternal() {
        return new EntityCrudDispatcher<>(I18nKeyEntryService.class, this);
    }

    @Override
    protected ApplicationRight getGlobalRightForCrudMethods() {
        return ECoreActorRight.CORE_I18N_KEY_ENTRY_CRUD;
    }

    @Override
    public AbstractSearchContainer getSearchContainer() {
        return i18nKeyEntrySearchContainer;
    }
}

package com.webharmony.core.service.i18n;

import com.webharmony.core.AbstractBaseTest;
import com.webharmony.core.data.enums.ECoreActorRight;
import com.webharmony.core.data.jpa.model.i18n.AppI18nKeyEntry;
import com.webharmony.core.data.jpa.model.i18n.AppI18nTranslation;
import com.webharmony.core.data.jpa.model.user.AppActorRight;
import com.webharmony.core.i18n.EI18nCodeLocation;
import com.webharmony.core.i18n.EI18nLanguage;
import com.webharmony.core.utils.CollectionUtils;
import com.webharmony.core.utils.dev.i18n.datatransfer.I18nEntityAttributeTransferData;
import com.webharmony.core.utils.dev.i18n.datatransfer.I18nImportResult;
import com.webharmony.core.utils.dev.i18n.datatransfer.I18nKeyEntryTransferData;
import com.webharmony.core.utils.dev.i18n.datatransfer.I18nTranslationEntryTransferData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class I18nDataTransferServiceTest extends AbstractBaseTest {

    @Autowired
    private I18nDataTransferService i18nDataTransferService;

    @Autowired
    private I18nKeyEntryService keyEntryService;

    @Test
    void shouldGetEnumEntityAttributeExportData() {
        List<I18nEntityAttributeTransferData> enumEntityAttributeTransferData = i18nDataTransferService.createEntityAttributeTransferData(true, true);
        assertThat(enumEntityAttributeTransferData).isNotEmpty();

        I18nEntityAttributeTransferData firstEntry = enumEntityAttributeTransferData.iterator().next();
        assertThat(firstEntry.getClassId()).isNotEmpty();
        assertThat(firstEntry.getKeyId()).isNotEmpty();
        assertThat(firstEntry.getUniqueName()).isNotEmpty();
        assertThat(firstEntry.getTranslations()).isNotEmpty();

        I18nTranslationEntryTransferData firstTranslationEntry = firstEntry.getTranslations().iterator().next();
        assertThat(firstTranslationEntry.getLanguage()).isNotNull();
        assertThat(firstTranslationEntry.getTranslation()).isNotEmpty();

    }

    @Test
    void shouldImportDummyEnumEntityTranslationData() {

        final AppActorRight entity = ECoreActorRight.CORE_ACTOR_RIGHTS_CRUD.getEntity();

        final I18nEntityAttributeTransferData i18nEnumEntityAttributeTransferData = new I18nEntityAttributeTransferData();
        i18nEnumEntityAttributeTransferData.setClassId(entity.getClass().getName());
        i18nEnumEntityAttributeTransferData.setKeyId(entity.getLabel().getAttribute());
        i18nEnumEntityAttributeTransferData.setUniqueName(entity.getUniqueName());

        final I18nTranslationEntryTransferData transferDataEntry = new I18nTranslationEntryTransferData();
        transferDataEntry.setLanguage(EI18nLanguage.GERMAN);
        transferDataEntry.setTranslation("deutscher text");

        final List<I18nTranslationEntryTransferData> translationData = new ArrayList<>();
        translationData.add(transferDataEntry);

        i18nEnumEntityAttributeTransferData.setTranslations(translationData);

        final List<I18nEntityAttributeTransferData> importList = new ArrayList<>();
        importList.add(i18nEnumEntityAttributeTransferData);

        final I18nImportResult importResult = i18nDataTransferService.importEnumEntityData(importList);
        assertThat(importResult.getImportedElements()).isOne();
        assertThat(importResult.getIgnoredElements()).isZero();

        final AppActorRight reloadedEntity = ECoreActorRight.CORE_ACTOR_RIGHTS_CRUD.getEntity();
        assertThat(reloadedEntity.getLabel().getValueByLanguage(EI18nLanguage.GERMAN).orElseThrow()).isEqualTo("deutscher text");

    }

    @Test
    @Transactional
    void shouldGetKeyEntryExportData() {

        prepareI18nTestData();

        final List<I18nKeyEntryTransferData> exportData = i18nDataTransferService.createKeyEntryExportData(true, true);
        assertThat(exportData).hasSize(3);

        final I18nKeyEntryTransferData testEntry = exportData.stream()
                .filter(e -> e.getKeyId().equals("Notimplemented{test}yet"))
                .findAny()
                .orElseThrow();

        assertThat(testEntry.getClassId()).isEqualTo("com.webharmony.core.api.rest.controller.utils.dispacher.EntityCrudDispatcher");
        assertThat(testEntry.getKeyId()).isEqualTo("Notimplemented{test}yet");
        assertThat(testEntry.getPlaceholders()).hasSameElementsAs(List.of("test"));
        assertThat(testEntry.getCodeLocation()).isEqualTo(EI18nCodeLocation.BACKEND);
        assertThat(testEntry.getDescription()).isEqualTo("my description");

        final List<I18nTranslationEntryTransferData> translationEntries = testEntry.getTranslationEntries();

        final I18nTranslationEntryTransferData germanEntry = translationEntries.stream().filter(e -> e.getLanguage().equals(EI18nLanguage.GERMAN)).findAny().orElseThrow();
        assertThat(germanEntry.getTranslation()).isEqualTo("deutscher text");

        final I18nTranslationEntryTransferData englishEntry = translationEntries.stream().filter(e -> e.getLanguage().equals(EI18nLanguage.ENGLISH)).findAny().orElseThrow();
        assertThat(englishEntry.getTranslation()).isEqualTo("english text");

    }

    @Test
    @Transactional
    void shouldImportData() {

        prepareI18nTestData();

       final I18nImportResult importResult = i18nDataTransferService.importKeyEntryData(createDummyImportData());
       assertThat(importResult.getImportedElements()).isEqualTo(2);
       assertThat(importResult.getIgnoredElements()).isOne();

       final List<AppI18nKeyEntry> allEntities = keyEntryService.getRepository().findAll();
       assertThat(allEntities).hasSize(3);


       final AppI18nKeyEntry entryToOverride = allEntities.stream()
                .filter(e -> e.getKey().equals("Notimplemented{test}yet"))
                .findAny()
                .orElseThrow();

       assertThat(entryToOverride.getClassId()).isEqualTo("com.webharmony.core.api.rest.controller.utils.dispacher.EntityCrudDispatcher");
       assertThat(Arrays.asList(entryToOverride.getPlaceholders().split(";"))).hasSameElementsAs(List.of("new", "placeholder"));
       assertThat(entryToOverride.getCodeLocation()).isEqualTo(EI18nCodeLocation.FRONTEND);
       assertThat(entryToOverride.getDescription()).isEqualTo("new description");

       final AppI18nTranslation translationDataToOverride = entryToOverride.getTranslations()
               .stream()
               .filter(t -> t.getLanguage().equals(EI18nLanguage.GERMAN))
               .findAny()
               .orElseThrow();

       assertThat(translationDataToOverride.getTranslation()).isEqualTo("anderer text");


        final AppI18nKeyEntry newEntry = allEntities.stream()
                .filter(e -> e.getKey().equals("Notimplementedyet"))
                .findAny()
                .orElseThrow();

        assertThat(newEntry.getClassId()).isEqualTo("com.webharmony.core.api.rest.controller.utils.dispacher.InMemoryDtoCrudDispatcher");
        assertThat(newEntry.getPlaceholders()).isEqualTo("new;placeholder;forNewEntry");
        assertThat(newEntry.getCodeLocation()).isEqualTo(EI18nCodeLocation.BACKEND);
        assertThat(newEntry.getDescription()).isEqualTo("new description for new entry");

        final AppI18nTranslation newTranslationData1 = newEntry.getTranslations()
                .stream()
                .filter(t -> t.getLanguage().equals(EI18nLanguage.GERMAN))
                .findAny()
                .orElseThrow();

        assertThat(newTranslationData1.getTranslation()).isEqualTo("neuer Text");

        final AppI18nTranslation newTranslationData2 = newEntry.getTranslations()
                .stream()
                .filter(t -> t.getLanguage().equals(EI18nLanguage.ENGLISH))
                .findAny()
                .orElseThrow();

        assertThat(newTranslationData2.getTranslation()).isEqualTo("new text");

        final List<AppI18nKeyEntry> i18nKeyEntries = allEntities.stream()
                .filter(e -> e.getClassId().equals("com.ignored.Entry"))
                .toList();

        assertThat(i18nKeyEntries).isEmpty();

    }

    private List<I18nKeyEntryTransferData> createDummyImportData() {

        final I18nKeyEntryTransferData entryToOverride = new I18nKeyEntryTransferData();
        entryToOverride.setClassId("com.webharmony.core.api.rest.controller.utils.dispacher.EntityCrudDispatcher");
        entryToOverride.setKeyId("Notimplemented{test}yet");
        entryToOverride.setPlaceholders(List.of("new", "placeholder"));
        entryToOverride.setCodeLocation(EI18nCodeLocation.FRONTEND);
        entryToOverride.setDescription("new description");
        entryToOverride.setIsCoreEntry(true);

        final I18nTranslationEntryTransferData translationDataToOverride = new I18nTranslationEntryTransferData();
        translationDataToOverride.setLanguage(EI18nLanguage.GERMAN);
        translationDataToOverride.setTranslation("anderer text");
        entryToOverride.setTranslationEntries(List.of(translationDataToOverride));


        final I18nKeyEntryTransferData newEntry = new I18nKeyEntryTransferData();
        newEntry.setIsCoreEntry(true);
        newEntry.setClassId("com.webharmony.core.api.rest.controller.utils.dispacher.InMemoryDtoCrudDispatcher");
        newEntry.setKeyId("Notimplementedyet");
        newEntry.setPlaceholders(List.of("new", "placeholder", "forNewEntry"));
        newEntry.setCodeLocation(EI18nCodeLocation.BACKEND);
        newEntry.setDescription("new description for new entry");

        final I18nTranslationEntryTransferData newTranslationData1 = new I18nTranslationEntryTransferData();
        newTranslationData1.setLanguage(EI18nLanguage.GERMAN);
        newTranslationData1.setTranslation("neuer Text");

        final I18nTranslationEntryTransferData newTranslationData2 = new I18nTranslationEntryTransferData();
        newTranslationData2.setLanguage(EI18nLanguage.ENGLISH);
        newTranslationData2.setTranslation("new text");

        newEntry.setTranslationEntries(List.of(newTranslationData1, newTranslationData2));

        final I18nKeyEntryTransferData ignoredEntry = new I18nKeyEntryTransferData();
        ignoredEntry.setIsCoreEntry(true);
        ignoredEntry.setClassId("com.ignored.Entry");
        ignoredEntry.setKeyId("ignoredEntry");
        ignoredEntry.setPlaceholders(List.of("bla", "blub"));
        ignoredEntry.setCodeLocation(EI18nCodeLocation.BACKEND);
        ignoredEntry.setDescription("bla blub");

        return List.of(entryToOverride, newEntry, ignoredEntry);
    }

    @Transactional
    public void prepareI18nTestData() {

        final List<AppI18nKeyEntry> allEntities = keyEntryService.getAllEntities();
        for (AppI18nKeyEntry entity : allEntities) {
            entity.setDescription("my description");

            final AppI18nTranslation germanTranslation = new AppI18nTranslation();
            germanTranslation.initializeUUID();
            germanTranslation.setLanguage(EI18nLanguage.GERMAN);
            germanTranslation.setTranslation("deutscher text");

            final AppI18nTranslation englishTranslation = new AppI18nTranslation();
            englishTranslation.initializeUUID();
            englishTranslation.setLanguage(EI18nLanguage.ENGLISH);
            englishTranslation.setTranslation("english text");

            Set<AppI18nTranslation> appI18nTranslations = CollectionUtils.emptySetIfNull(entity.getTranslations());
            appI18nTranslations.clear();
            appI18nTranslations.add(germanTranslation);
            appI18nTranslations.add(englishTranslation);

        }

        keyEntryService.getRepository().saveAll(allEntities);

    }

}

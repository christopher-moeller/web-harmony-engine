package com.webharmony.core.service.i18n;

import com.fasterxml.jackson.core.type.TypeReference;
import com.webharmony.core.api.rest.model.utils.FileWebData;
import com.webharmony.core.data.jpa.model.i18n.AppI18nEntityAttribute;
import com.webharmony.core.data.jpa.model.i18n.AppI18nEntityAttributeValue;
import com.webharmony.core.data.jpa.model.i18n.AppI18nKeyEntry;
import com.webharmony.core.data.jpa.model.i18n.AppI18nTranslation;
import com.webharmony.core.data.jpa.model.utils.AbstractBaseEntity;
import com.webharmony.core.data.jpa.model.utils.CombinedCoreAndProjectEntityInstance;
import com.webharmony.core.data.jpa.model.utils.EntityWithReadableId;
import com.webharmony.core.service.EntityService;
import com.webharmony.core.utils.CollectionUtils;
import com.webharmony.core.utils.FileUtils;
import com.webharmony.core.utils.JacksonUtils;
import com.webharmony.core.utils.dev.i18n.datatransfer.I18nEntityAttributeTransferData;
import com.webharmony.core.utils.dev.i18n.datatransfer.I18nImportResult;
import com.webharmony.core.utils.dev.i18n.datatransfer.I18nKeyEntryTransferData;
import com.webharmony.core.utils.dev.i18n.datatransfer.I18nTranslationEntryTransferData;
import com.webharmony.core.utils.reflection.ReflectionUtils;
import com.webharmony.core.utils.tuple.Tuple2;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class I18nDataTransferService {

    private final I18nKeyEntryService keyEntryService;

    private final EntityService entityService;

    @Getter
    @Value("classpath:i18n/translations/static_core_translations.json")
    private Resource staticCoreTranslations;

    @Getter
    @Value("classpath:i18n/translations/static_project_translations.json")
    private Resource staticProjectTranslations;

    @Getter
    @Value("classpath:i18n/translations/entity_attribute_core_translations.json")
    private Resource entityAttributeCoreTranslations;

    @Getter
    @Value("classpath:i18n/translations/entity_attribute_project_translations.json")
    private Resource entityAttributeProjectTranslations;

    public I18nDataTransferService(I18nKeyEntryService keyEntryService, EntityService entityService) {
        this.keyEntryService = keyEntryService;
        this.entityService = entityService;
    }

    public ResponseEntity<Resource> exportKeyEntryData(boolean includeCoreEntries, boolean includeSubprojectEntries) {
        return buildResourceResponseJson("static_translations.json", createKeyEntryExportData(includeCoreEntries, includeSubprojectEntries));
    }

    public ResponseEntity<Resource> exportEnumEntityTranslations(boolean includeCoreEntries, boolean includeSubprojectEntries) {
        return buildResourceResponseJson("entity_attribute_translations.json", createEntityAttributeTransferData(includeCoreEntries, includeSubprojectEntries));
    }

    @SneakyThrows
    private ResponseEntity<Resource> buildResourceResponseJson(String fileName, Object content) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        JacksonUtils.createDefaultJsonMapper()
                .writerWithDefaultPrettyPrinter()
                .writeValue(byteArrayOutputStream, content);

        Resource resource = new ByteArrayResource(byteArrayOutputStream.toByteArray());

        MultiValueMap<String, String> headers = new MultiValueMapAdapter<>(new HashMap<>());
        headers.put("Content-Type", List.of("application/json"));
        headers.put("Access-Control-Expose-Headers", List.of("Content-Disposition"));
        headers.put("Content-Disposition", List.of(fileName));

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

    public List<I18nEntityAttributeTransferData> createEntityAttributeTransferData(boolean includeCoreEntries, boolean includeSubprojectEntries) {

        final List<I18nEntityAttributeTransferData> resultList = new ArrayList<>();
        final List<Class<? extends AbstractBaseEntity>> relevantEntityClasses = getRelevantEntityClasses();
        for (Class<? extends AbstractBaseEntity> relevantEntityClass : relevantEntityClasses) {
            resultList.addAll(createEntityAttributeTransferDataForEntityClass(relevantEntityClass, includeCoreEntries, includeSubprojectEntries));
        }

        return resultList;
    }

    private List<I18nEntityAttributeTransferData> createEntityAttributeTransferDataForEntityClass(Class<? extends AbstractBaseEntity> relevantEntityClass, boolean includeCoreEntries, boolean includeSubprojectEntries) {
        final List<I18nEntityAttributeTransferData> resultList = new ArrayList<>();
        if(!EntityWithReadableId.class.isAssignableFrom(relevantEntityClass)) {
            log.info(String.format("Entity '%s' as i18n attributes to translate but is not a subclass of '%s'. This is needed for a later import. This entity will be ignored.", relevantEntityClass, EntityWithReadableId.class));
        }

        final Map<Field, Method> fieldAndGetters = ReflectionUtils.getAllFields(relevantEntityClass, true)
                .stream()
                .filter(f -> f.getType().equals(AppI18nEntityAttribute.class))
                .collect(Collectors.toMap(f -> f, f -> ReflectionUtils.findGetterByField(f, relevantEntityClass).orElseThrow()));

        for(AbstractBaseEntity entityInstance : entityService.findEntityAllEntitiesByClass(relevantEntityClass)) {
            boolean isCoreClass = entityInstance instanceof CombinedCoreAndProjectEntityInstance ci ? ci.isInstanceFromCore() : ReflectionUtils.isCoreClass(entityInstance.getClass());

            if(isCoreClass && !includeCoreEntries || !isCoreClass && !includeSubprojectEntries || !(entityInstance instanceof EntityWithReadableId))
                continue;

            for (Map.Entry<Field, Method> fieldMethodEntry : fieldAndGetters.entrySet()) {
                final Field field = fieldMethodEntry.getKey();
                final Method method = fieldMethodEntry.getValue();

                final I18nEntityAttributeTransferData transferData = new I18nEntityAttributeTransferData();
                transferData.setIsCoreEntry(isCoreClass);
                transferData.setUniqueName(((EntityWithReadableId) entityInstance).getReadableId());
                transferData.setClassId(entityInstance.getClass().getName());
                transferData.setKeyId(field.getName());
                transferData.setTranslations(createTranslationListByGetterMethodAndInstance(method, entityInstance));

                resultList.add(transferData);
            }
        }

        return resultList;
    }

    @SneakyThrows
    private List<I18nTranslationEntryTransferData> createTranslationListByGetterMethodAndInstance(Method method, AbstractBaseEntity entityInstance) {
        final List<I18nTranslationEntryTransferData> resultList = new ArrayList<>();

        final AppI18nEntityAttribute i18nEntityAttribute = (AppI18nEntityAttribute) method.invoke(entityInstance);
        for (AppI18nEntityAttributeValue value : i18nEntityAttribute.getValues()) {
            final I18nTranslationEntryTransferData transferData = new I18nTranslationEntryTransferData();
            transferData.setTranslation(value.getTranslation());
            transferData.setLanguage(value.getLanguage());
            resultList.add(transferData);
        }

        return resultList;
    }

    private List<Class<? extends AbstractBaseEntity>> getRelevantEntityClasses() {
        final List<Class<? extends AbstractBaseEntity>> resultList = new ArrayList<>();
        for (Class<? extends AbstractBaseEntity> entityClass : ReflectionUtils.getAllProjectClassesImplementingSuperClass(AbstractBaseEntity.class)) {

            if(entityClass.equals(AppI18nEntityAttributeValue.class))
                continue;

            boolean hasAnyI18nField = ReflectionUtils.getAllFields(entityClass, true)
                    .stream()
                    .anyMatch(f -> f.getType().equals(AppI18nEntityAttribute.class));

            if(hasAnyI18nField)
                resultList.add(entityClass);
        }

        return resultList;
    }

    public List<I18nKeyEntryTransferData> createKeyEntryExportData(boolean includeCoreEntries, boolean includeSubprojectEntries) {
        final List<AppI18nKeyEntry> filteredList = new ArrayList<>();
        for (AppI18nKeyEntry entry : keyEntryService.getAllEntities()) {
            boolean entryIsCoreEntry = Boolean.TRUE.equals(entry.getIsCoreEntry());
            if(entryIsCoreEntry && !includeCoreEntries || !entryIsCoreEntry && !includeSubprojectEntries)
                continue;

            filteredList.add(entry);
        }

        return filteredList
                .stream()
                .map(this::mapKeyEntryEntityToTransferData)
                .toList();
    }

    private I18nKeyEntryTransferData mapKeyEntryEntityToTransferData(AppI18nKeyEntry entryEntity) {
        final I18nKeyEntryTransferData transferData = new I18nKeyEntryTransferData();
        transferData.setIsCoreEntry(entryEntity.getIsCoreEntry());
        transferData.setClassId(entryEntity.getClassId());
        transferData.setKeyId(entryEntity.getKey());
        transferData.setPlaceholders(Optional.ofNullable(entryEntity.getPlaceholders()).map(v -> List.of(v.split(";"))).orElseGet(Collections::emptyList));
        transferData.setDescription(entryEntity.getDescription());
        transferData.setCodeLocation(entryEntity.getCodeLocation());
        transferData.setTranslationEntries(CollectionUtils.emptySetIfNull(entryEntity.getTranslations()).stream().map(this::mapEntityToTranslationEntry).toList());
        return transferData;
    }

    private I18nTranslationEntryTransferData mapEntityToTranslationEntry(AppI18nTranslation appI18nTranslation) {
        final I18nTranslationEntryTransferData transferData = new I18nTranslationEntryTransferData();
        transferData.setLanguage(appI18nTranslation.getLanguage());
        transferData.setTranslation(appI18nTranslation.getTranslation());
        return transferData;
    }

    @SneakyThrows
    @Transactional
    public I18nImportResult importKeyEntryData(FileWebData fileWebData) {

        final List<I18nKeyEntryTransferData> importData = JacksonUtils.createDefaultJsonMapper()
                .readValue(FileUtils.extractFileWebData(fileWebData).getType2(), new TypeReference<>() {});

        return importKeyEntryData(importData);
    }

    @SneakyThrows
    @Transactional
    public I18nImportResult importEnumEntityData(FileWebData fileWebData) {

        final Tuple2<String, byte[]> extractedData = FileUtils.extractFileWebData(fileWebData);

        final List<I18nEntityAttributeTransferData> importData = JacksonUtils.createDefaultJsonMapper()
                .readValue(extractedData.getType2(), new TypeReference<>() {});

        return importEnumEntityData(importData);
    }

    @Transactional
    public I18nImportResult importKeyEntryData(List<I18nKeyEntryTransferData> importData) {

        int ignoredEntries = 0;
        int importedElements = 0;

        keyEntryService.clearTranslationCache();
        final List<AppI18nKeyEntry> allEntities = keyEntryService.getAllEntities();

        for (I18nKeyEntryTransferData importDataEntry : importData) {
            final AppI18nKeyEntry keyEntity = allEntities.stream()
                    .filter(e -> e.getClassId().equals(importDataEntry.getClassId()))
                    .filter(e -> e.getKey().equals(importDataEntry.getKeyId()))
                    .findAny()
                    .orElse(null);

            if(keyEntity == null) {
                ignoredEntries++;
                continue;
            }

            keyEntity.setIsCoreEntry(importDataEntry.getIsCoreEntry());
            keyEntity.setPlaceholders(String.join(";", importDataEntry.getPlaceholders()));
            keyEntity.setCodeLocation(importDataEntry.getCodeLocation());
            keyEntity.setDescription(importDataEntry.getDescription());

            final List<AppI18nTranslation> updatedEntries = new ArrayList<>();
            for (I18nTranslationEntryTransferData i18nTranslationEntryTransferData : CollectionUtils.emptyListIfNull(importDataEntry.getTranslationEntries())) {
                final AppI18nTranslation translationEntity = CollectionUtils.emptySetIfNull(keyEntity.getTranslations())
                        .stream()
                        .filter(e -> e.getLanguage().equals(i18nTranslationEntryTransferData.getLanguage()))
                        .findAny()
                        .orElseGet(AppI18nTranslation::new);

                if(translationEntity.isNew())
                    translationEntity.initializeUUID();

                translationEntity.setTranslation(i18nTranslationEntryTransferData.getTranslation());
                translationEntity.setLanguage(i18nTranslationEntryTransferData.getLanguage());
                translationEntity.setI18nKeyEntry(keyEntity);
                updatedEntries.add(translationEntity);
            }

            if(keyEntity.getTranslations() == null) {
                keyEntity.setTranslations(new HashSet<>());
            }

            keyEntity.getTranslations().clear();
            updatedEntries.forEach(e -> keyEntity.getTranslations().add(e));

            keyEntryService.saveEntity(keyEntity);

            importedElements++;
        }

        final I18nImportResult importResult = new I18nImportResult();
        importResult.setImportedElements(importedElements);
        importResult.setIgnoredElements(ignoredEntries);

        log.info("Imported {} static i18n key entries", importResult.getImportedElements());

        return importResult;
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    @Transactional
    public I18nImportResult importEnumEntityData(List<I18nEntityAttributeTransferData> importList) {

        int count = 0;
        final Map<Class<? extends AbstractBaseEntity>, List<Method>> classGetterMap = new HashMap<>();

        for (I18nEntityAttributeTransferData i18nEntityAttributeTransferData : importList) {

            if(i18nEntityAttributeTransferData.getUniqueName() == null)
                continue;

            final Class<? extends AbstractBaseEntity> entityClass = (Class<? extends AbstractBaseEntity>) Class.forName(i18nEntityAttributeTransferData.getClassId());
            classGetterMap.computeIfAbsent(entityClass, c -> ReflectionUtils.getAllFields(entityClass, true)
                    .stream()
                    .filter(f -> f.getType().equals(AppI18nEntityAttribute.class))
                    .map(f -> ReflectionUtils.findGetterByField(f, entityClass).orElseThrow())
                    .toList());


            final Optional<? extends AbstractBaseEntity> optEntity = entityService.findEntityByReadableId(entityClass, i18nEntityAttributeTransferData.getUniqueName());

            if(optEntity.isPresent()) {
                final AbstractBaseEntity entity = optEntity.get();
                for (Method getter : classGetterMap.get(entityClass)) {
                    AppI18nEntityAttribute i18nEntityAttribute = (AppI18nEntityAttribute) getter.invoke(entity);
                    if(i18nEntityAttribute.getAttribute().equals(i18nEntityAttributeTransferData.getKeyId())) {
                        updateTranslationsByImport(i18nEntityAttribute, i18nEntityAttributeTransferData.getTranslations());
                        entityService.saveEntityGeneric(i18nEntityAttribute);
                        count++;
                    }
                }
            }

        }

        final I18nImportResult importResult = new I18nImportResult();
        importResult.setImportedElements(count);
        importResult.setIgnoredElements(0);

        log.info("Imported {} i18n entity attribute entries", importResult.getImportedElements());

        return importResult;
    }

    private void updateTranslationsByImport(AppI18nEntityAttribute i18nEntityAttribute, List<I18nTranslationEntryTransferData> importedTranslations) {
        final Set<AppI18nEntityAttributeValue> currentValues = i18nEntityAttribute.getValues();
        for (I18nTranslationEntryTransferData importedTranslation : importedTranslations) {
            final AppI18nEntityAttributeValue value = currentValues.stream()
                    .filter(e -> e.getLanguage().equals(importedTranslation.getLanguage()))
                    .findAny()
                    .orElseGet(() -> {
                        final AppI18nEntityAttributeValue newEntry = new AppI18nEntityAttributeValue();
                        newEntry.setEntityAttribute(i18nEntityAttribute);
                        newEntry.setLanguage(importedTranslation.getLanguage());
                        currentValues.add(newEntry);
                        return newEntry;
                    });

            value.setTranslation(importedTranslation.getTranslation());
        }
    }

    @Transactional
    public void synchronizeTranslations() {
        synchronizeTranslationsByFiles(staticCoreTranslations, entityAttributeCoreTranslations);
        synchronizeTranslationsByFiles(staticProjectTranslations, entityAttributeProjectTranslations);
    }

    @SneakyThrows
    @Transactional
    public void synchronizeTranslationsByFiles(Resource staticTranslations, Resource entityAttributeTranslations) {

        if(staticTranslations.exists()) {
            final List<I18nKeyEntryTransferData> importData = JacksonUtils.createDefaultJsonMapper()
                    .readValue(staticTranslations.getInputStream(), new TypeReference<>() {});
            this.importKeyEntryData(importData);
        }

        if(entityAttributeTranslations.exists()) {
            final List<I18nEntityAttributeTransferData> importData = JacksonUtils.createDefaultJsonMapper()
                    .readValue(entityAttributeTranslations.getInputStream(), new TypeReference<>() {});

            this.importEnumEntityData(importData);
        }
    }
}

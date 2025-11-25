package com.webharmony.core.service.i18n;

import com.fasterxml.jackson.core.type.TypeReference;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.webharmony.core.api.rest.model.i18n.*;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.data.enums.ECoreRegistry;
import com.webharmony.core.data.jpa.model.i18n.AppI18nKeyEntry;
import com.webharmony.core.data.jpa.model.i18n.AppI18nTranslation;
import com.webharmony.core.data.jpa.model.i18n.QAppI18nKeyEntry;
import com.webharmony.core.data.jpa.model.i18n.QAppI18nTranslation;
import com.webharmony.core.i18n.EI18nLanguage;
import com.webharmony.core.service.data.mapper.MappingConfiguration;
import com.webharmony.core.service.data.utils.AbstractEntityCrudService;
import com.webharmony.core.utils.CollectionUtils;
import com.webharmony.core.utils.JacksonUtils;
import com.webharmony.core.utils.dev.i18n.I18nClassDto;
import com.webharmony.core.utils.dev.i18n.I18nKeyDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Stream;

@Slf4j
@Service
public class I18nKeyEntryService extends AbstractEntityCrudService<I18nKeyEntryDto, AppI18nKeyEntry> {

    private static final QAppI18nKeyEntry qAppI18nKeyEntry = QAppI18nKeyEntry.appI18nKeyEntry;

    @Value("classpath:i18n/i18n_static_core_keys.json")
    private Resource i18nStaticCoreKeysFile;

    @Value("classpath:i18n/i18n_static_project_keys.json")
    private Resource i18nStaticProjectKeysFile;

    @PersistenceContext
    private EntityManager em;

    private final Map<EI18nLanguage, Map<String, String>> translationCache = createEmptyTranslationCache();

    public Optional<String> getTranslation(String classId, String keyId, EI18nLanguage preferredLanguage) {

        final EI18nLanguage activeLanguage = Optional.ofNullable(preferredLanguage).orElseGet(this::getActiveLanguage);

        final Map<String, String> translationMapForLanguage = translationCache.get(activeLanguage);
        final String readableId = buildReadableId(classId, keyId);

        final String cachedTranslation = translationMapForLanguage.get(readableId);
        if(cachedTranslation != null) {
            return Optional.of(cachedTranslation);
        } else {
            final String translationByDb = findTranslationInDatabase(classId, keyId, activeLanguage)
                    .orElse(null);

            if(translationByDb != null) {
                translationMapForLanguage.put(readableId, translationByDb);
                return Optional.of(translationByDb);
            } else {
                return Optional.empty();
            }

        }
    }

    @Override
    public AppI18nKeyEntry saveEntity(AppI18nKeyEntry entity) {
        invalidateCacheForEntry(entity.getReadableIdRepresentation());
        return super.saveEntity(entity);
    }

    private void invalidateCacheForEntry(String readableId) {
        for (EI18nLanguage language : EI18nLanguage.values()) {
            this.translationCache.get(language).remove(readableId);
        }
    }

    public Optional<String> findTranslationInDatabase(String classId, String key, EI18nLanguage language) {
        return Optional.ofNullable(new JPAQuery<>(getEntityManager())
                .select(QAppI18nTranslation.appI18nTranslation.translation)
                .from(QAppI18nKeyEntry.appI18nKeyEntry)
                .join(QAppI18nKeyEntry.appI18nKeyEntry.translations, QAppI18nTranslation.appI18nTranslation)
                .where(QAppI18nKeyEntry.appI18nKeyEntry.classId.eq(classId).and(QAppI18nKeyEntry.appI18nKeyEntry.key.eq(key)))
                .where(QAppI18nTranslation.appI18nTranslation.language.eq(language))
                .fetchOne());
    }

    private EI18nLanguage getActiveLanguage() {
        EI18nLanguage language = null;
        if(ContextHolder.isInitialized()) {
            language = ContextHolder.getContext().getContextLanguage();
        }

        return language != null ? language : ECoreRegistry.I18N_DEFAULT_LANGUAGE.getTypedValue(EI18nLanguage.class);
    }

    @Override
    protected void configureMapping(MappingConfiguration<I18nKeyEntryDto, AppI18nKeyEntry> mappingConfiguration) {
        mappingConfiguration.withExtendedToDtoMapper((entity, target, mappingContext) -> {
            target.setTranslationEntries(getTranslationDTOsByEntity(entity));
            return target;
        });

        mappingConfiguration.withExtendedToEntityMapper((dto, entity, mappingContext) -> {
            entity.setTranslations(getEntityTranslationSetByDto(dto, entity));
            return entity;
        });
    }

    private List<I18nTranslationDto> getTranslationDTOsByEntity(AppI18nKeyEntry entity) {
        return Stream.of(EI18nLanguage.values())
                .map(language -> {
                    I18nTranslationDto dto = new I18nTranslationDto();
                    dto.setLanguage(language);
                    dto.setTranslation(getTranslationByLanguageAndEntityOrNull(language, entity));
                    return dto;
                }).toList();
    }

    private String getTranslationByLanguageAndEntityOrNull(EI18nLanguage language, AppI18nKeyEntry entity) {
        return CollectionUtils.emptySetIfNull(entity.getTranslations())
                .stream()
                .filter(t -> t.getLanguage().equals(language))
                .findAny()
                .map(AppI18nTranslation::getTranslation)
                .orElse(null);
    }

    private Set<AppI18nTranslation> getEntityTranslationSetByDto(I18nKeyEntryDto keyEntryDto, AppI18nKeyEntry entity) {
        final Set<AppI18nTranslation> entityList = CollectionUtils.emptySetIfNull(entity.getTranslations());
        for (I18nTranslationDto i18nTranslationDto : CollectionUtils.emptyListIfNull(keyEntryDto.getTranslationEntries())) {
            AppI18nTranslation translationEntity = entityList.stream().filter(e -> e.getLanguage().equals(i18nTranslationDto.getLanguage())).findAny().orElseGet(AppI18nTranslation::new);
            translationEntity.initializeUUID();
            translationEntity.setI18nKeyEntry(entity);
            translationEntity.setLanguage(i18nTranslationDto.getLanguage());
            translationEntity.setTranslation(Optional.ofNullable(i18nTranslationDto.getTranslation()).filter(t -> !t.isEmpty()).orElse(null));
            entityList.add(translationEntity);
        }

        return entityList;
    }

    @Transactional
    public void synchronizeEntriesWithDatabase() {
        synchronizeEntriesWithDatabase(this.i18nStaticCoreKeysFile, true);
        synchronizeEntriesWithDatabase(this.i18nStaticProjectKeysFile, false);
    }
    @Transactional
    public void synchronizeEntriesWithDatabase(Resource i18nStaticKeysFile, boolean isCore) {
        if(i18nStaticKeysFile.exists()) {
            synchronizeEntriesWithDatabase(readClassDTOsByFile(i18nStaticKeysFile), isCore);
            log.info("All i18n key entries synchronized");
        } else {
            log.info("No i18n key entry file found");
        }
    }

    public I18nFrontendTranslation getFrontendTranslation(EI18nLanguage language) {

        final List<I18nFrontendTranslationEntry> entries = new ArrayList<>();
        for (Tuple tuple : getAllTranslations(language)) {
            final I18nFrontendTranslationEntry entry = new I18nFrontendTranslationEntry();
            entry.setClassId(tuple.get(QAppI18nKeyEntry.appI18nKeyEntry.classId));
            entry.setKeyId(tuple.get(QAppI18nKeyEntry.appI18nKeyEntry.key));
            entry.setValue(tuple.get(QAppI18nTranslation.appI18nTranslation.translation));
            entries.add(entry);
        }

        final I18nFrontendTranslation translation = new I18nFrontendTranslation();
        translation.setTranslationEntries(entries);
        translation.setLanguage(language);

        return translation;
    }

    private List<Tuple> getAllTranslations(EI18nLanguage language) {
        return new JPAQuery<>(getEntityManager())
            .select(QAppI18nKeyEntry.appI18nKeyEntry.classId, QAppI18nKeyEntry.appI18nKeyEntry.key, QAppI18nTranslation.appI18nTranslation.translation)
                .from(QAppI18nKeyEntry.appI18nKeyEntry)
                .leftJoin(QAppI18nKeyEntry.appI18nKeyEntry.translations, QAppI18nTranslation.appI18nTranslation)
                .where(QAppI18nTranslation.appI18nTranslation.language.eq(language))
                .fetch();
    }

    @SneakyThrows
    private List<I18nClassDto> readClassDTOsByFile(Resource i18nStaticKeysFile) {
        return JacksonUtils.createDefaultJsonMapper()
                .readValue(i18nStaticKeysFile.getInputStream(), new TypeReference<>() {});
    }

    @Transactional
    public void synchronizeEntriesWithDatabase(List<I18nClassDto> classDTOs, boolean isCore) {
        final List<AppI18nKeyEntry> currentEntries = this.getAllEntities()
                .stream()
                .filter(e -> e.getIsCoreEntry().equals(isCore))
                .toList();

        final List<String> idsOfCurrentEntries = currentEntries.stream()
                .map(AppI18nKeyEntry::getReadableIdRepresentation)
                .toList();

        final List<String> remainingIds = new ArrayList<>(idsOfCurrentEntries);

        for (I18nClassDto classDTO : classDTOs) {
            for (I18nKeyDto i18nKeyDto : CollectionUtils.emptyListIfNull(classDTO.getKeys())) {
                final String readableId = buildReadableId(classDTO.getClassId(), i18nKeyDto.getId());
                if(!idsOfCurrentEntries.contains(readableId)) {
                    AppI18nKeyEntry keyEntry = new AppI18nKeyEntry();
                    keyEntry.setIsCoreEntry(i18nKeyDto.getIsCoreEntry());
                    keyEntry.setCodeLocation(classDTO.getLocation());
                    keyEntry.setClassId(classDTO.getClassId());
                    keyEntry.setKey(i18nKeyDto.getId());
                    keyEntry.setPlaceholders(String.join(";", i18nKeyDto.getPlaceholders()));
                    keyEntry.setCodeLines(String.join(";", Optional.ofNullable(i18nKeyDto.getCodeLocations()).orElseGet(Collections::emptyList)));

                    keyEntry.setTranslations(new HashSet<>());

                    AppI18nTranslation englishTranslation = new AppI18nTranslation();
                    englishTranslation.setI18nKeyEntry(keyEntry);
                    englishTranslation.setTranslation(i18nKeyDto.getEnglishDefaultValue());
                    englishTranslation.setLanguage(EI18nLanguage.ENGLISH);
                    englishTranslation.setLocalTransientUuid(UUID.randomUUID());

                    keyEntry.getTranslations().add(englishTranslation);
                    saveEntity(keyEntry);
                }

                remainingIds.remove(readableId);
            }
        }

        final List<UUID> uuidsForRemainingEntries = currentEntries.stream()
                .filter(entry -> remainingIds.contains(entry.getReadableIdRepresentation()))
                .map(AppI18nKeyEntry::getUuid)
                .toList();

        this.getRepository().deleteAllById(uuidsForRemainingEntries);

    }

    public static String buildReadableId(String classId, String keyId) {
        return String.format("%s:%s", classId, keyId);
    }

    private Map<EI18nLanguage, Map<String, String>> createEmptyTranslationCache() {
        final Map<EI18nLanguage, Map<String, String>> resultMap = new EnumMap<>(EI18nLanguage.class);
        Stream.of(EI18nLanguage.values()).forEach(e -> resultMap.put(e, new HashMap<>()));
        return resultMap;
    }

    public void clearTranslationCache() {
        this.translationCache.values().forEach(Map::clear);
    }

    public UUID getIdOfNextEntryToBeResolved() {
        return new JPAQuery<>(em)
                .select(qAppI18nKeyEntry.uuid)
                .from(qAppI18nKeyEntry)
                .where(qAppI18nKeyEntry.translations.size().lt(EI18nLanguage.values().length))
                .limit(1)
                .fetchFirst();
    }

    public I18nTranslationStatistic getStatistics() {

        final Long countOfAllEntries = createStatisticsBaseQuery().fetchOne();
        final Long countOfTranslatedEntries = createStatisticsBaseQuery().where(qAppI18nKeyEntry.translations.size().eq(EI18nLanguage.values().length)).fetchOne();
        final Long countOfNotTranslatedEntries = createStatisticsBaseQuery().where(qAppI18nKeyEntry.translations.size().lt(EI18nLanguage.values().length)).fetchOne();


        I18nTranslationStatistic statistic = new I18nTranslationStatistic();
        statistic.setCountOfAllItems(countOfAllEntries);
        statistic.setCountOfTranslatedItems(countOfTranslatedEntries);
        statistic.setCountOfNotTranslatedItems(countOfNotTranslatedEntries);

        return statistic;
    }

    private JPAQuery<Long> createStatisticsBaseQuery() {
        return new JPAQuery<>(em)
                .select(qAppI18nKeyEntry.uuid.count())
                .from(qAppI18nKeyEntry);
    }
}

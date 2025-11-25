package com.webharmony.core.service.i18n;

import com.querydsl.jpa.impl.JPAQuery;
import com.webharmony.core.api.rest.model.i18n.I18nEntityAttributeDto;
import com.webharmony.core.api.rest.model.i18n.I18nTranslationDto;
import com.webharmony.core.api.rest.model.i18n.I18nTranslationStatistic;
import com.webharmony.core.data.enums.ECoreRegistry;
import com.webharmony.core.data.jpa.model.i18n.AppI18nEntityAttribute;
import com.webharmony.core.data.jpa.model.i18n.AppI18nEntityAttributeValue;
import com.webharmony.core.data.jpa.model.i18n.QAppI18nEntityAttribute;
import com.webharmony.core.data.jpa.model.utils.AbstractBaseEntity;
import com.webharmony.core.data.jpa.model.utils.CombinedCoreAndProjectEntityInstance;
import com.webharmony.core.data.jpa.utils.I18nInitTranslatedValueToOtherNotTranslatedEntries;
import com.webharmony.core.i18n.EI18nLanguage;
import com.webharmony.core.i18n.I18N;
import com.webharmony.core.service.data.mapper.MappingConfiguration;
import com.webharmony.core.service.data.utils.AbstractEntityCrudService;
import com.webharmony.core.utils.CollectionUtils;
import com.webharmony.core.utils.exceptions.InternalServerException;
import com.webharmony.core.utils.reflection.ReflectionUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Stream;

@Service
public class I18nEntityAttributeService extends AbstractEntityCrudService<I18nEntityAttributeDto, AppI18nEntityAttribute> {

    private static final QAppI18nEntityAttribute qAppI18nEntityAttribute = QAppI18nEntityAttribute.appI18nEntityAttribute;
    @PersistenceContext
    private EntityManager em;

    @Override
    protected void configureMapping(MappingConfiguration<I18nEntityAttributeDto, AppI18nEntityAttribute> mappingConfiguration) {

        mappingConfiguration
                .withMapNestedFields(false)
                .withExtendedToDtoMapper((entity, target, mappingContext) -> {
                    target.setValues(getTranslationDTOsByEntity(entity));
                    return target;
                }).withExtendedToEntityMapper((dto, target, mappingContext) -> {
                    target.setValues(getEntityTranslationSetByDto(dto, target));
                    return target;
                });
    }

    private List<I18nTranslationDto> getTranslationDTOsByEntity(AppI18nEntityAttribute entity) {
        return Stream.of(EI18nLanguage.values())
                .map(language -> {
                    I18nTranslationDto dto = new I18nTranslationDto();
                    dto.setLanguage(language);
                    dto.setTranslation(getTranslationByLanguageAndEntityOrNull(language, entity));
                    return dto;
                }).toList();
    }

    private String getTranslationByLanguageAndEntityOrNull(EI18nLanguage language, AppI18nEntityAttribute entity) {
        return CollectionUtils.emptySetIfNull(entity.getValues())
                .stream()
                .filter(t -> t.getLanguage().equals(language))
                .findAny()
                .map(AppI18nEntityAttributeValue::getTranslation)
                .orElse(null);
    }

    private Set<AppI18nEntityAttributeValue> getEntityTranslationSetByDto(I18nEntityAttributeDto keyEntryDto, AppI18nEntityAttribute entity) {
        final Set<AppI18nEntityAttributeValue> entityList = CollectionUtils.emptySetIfNull(entity.getValues());
        for (I18nTranslationDto i18nTranslationDto : CollectionUtils.emptyListIfNull(keyEntryDto.getValues())) {
            AppI18nEntityAttributeValue translationEntity = entityList.stream().filter(e -> e.getLanguage().equals(i18nTranslationDto.getLanguage())).findAny().orElseGet(AppI18nEntityAttributeValue::new);
            translationEntity.initializeUUID();
            translationEntity.setEntityAttribute(entity);
            translationEntity.setLanguage(i18nTranslationDto.getLanguage());
            translationEntity.setTranslation(Optional.ofNullable(i18nTranslationDto.getTranslation()).filter(t -> !t.isEmpty()).orElse(null));
            if(translationEntity.getTranslation() == null) {
                entityList.stream().filter(e -> e.getTranslation() == null && e.getLanguage().equals(translationEntity.getLanguage()))
                        .findAny()
                        .ifPresent(entityList::remove);

                continue;
            }
            entityList.add(translationEntity);
        }

        return entityList;
    }

    @SneakyThrows
    public void initializeAttributesForEntity(AbstractBaseEntity entity) {
        final Class<? extends AbstractBaseEntity> entityClass = entity.getClass();

        if(AppI18nEntityAttributeValue.class.isAssignableFrom(entityClass))
            return;

        for (Field attributeField : findAllI18nFields(entityClass)) {
            Method getter = ReflectionUtils.findGetterByField(attributeField, entityClass)
                    .orElseThrow(() -> new InternalServerException(String.format("No getter for field '%s' found", attributeField.getName())));

            AppI18nEntityAttribute entityAttribute = (AppI18nEntityAttribute) getter.invoke(entity);
            if(entityAttribute != null) {
                if(entity instanceof CombinedCoreAndProjectEntityInstance combinedInstance) {
                    entityAttribute.setIsCoreEntry(combinedInstance.isInstanceFromCore());
                } else {
                    entityAttribute.setIsCoreEntry(ReflectionUtils.isCoreClass(entityClass));
                }

                entityAttribute.setAttribute(attributeField.getName());
                entityAttribute.setEntityClass(entityClass.getName());

                if(attributeField.getAnnotation(I18nInitTranslatedValueToOtherNotTranslatedEntries.class) != null) {
                    initEmptyTranslationsIfNeeded(entityAttribute);
                }
            }
        }
    }

    private void initEmptyTranslationsIfNeeded(AppI18nEntityAttribute entityAttribute) {
        final Set<AppI18nEntityAttributeValue> translationValues = entityAttribute.getValues();

        final String bestMatchingTranslation = findBestTranslationIfAvailable(entityAttribute);
        if(bestMatchingTranslation == null) {
            return;
        }

        for (EI18nLanguage language : EI18nLanguage.values()) {
            AppI18nEntityAttributeValue translationValue = translationValues.stream().filter(e -> e.getLanguage().equals(language)).findAny()
                    .orElseGet(() -> {
                        final AppI18nEntityAttributeValue newValue = new AppI18nEntityAttributeValue();
                        newValue.initializeLocalTransientUuid();
                        newValue.setEntityAttribute(entityAttribute);
                        newValue.setLanguage(language);
                        translationValues.add(newValue);
                        return newValue;
                    });


            if(translationValue.getTranslation() == null) {
                translationValue.setTranslation(bestMatchingTranslation);
            }

        }
    }

    private List<Field> findAllI18nFields(Class<? extends AbstractBaseEntity> entityClass) {
        return ReflectionUtils.getAllFields(entityClass, true)
                .stream()
                .filter(field -> field.getType().equals(AppI18nEntityAttribute.class))
                .toList();
    }

    public UUID getIdOfNextEntryToBeResolved() {
        return new JPAQuery<>(em)
                .select(qAppI18nEntityAttribute.uuid)
                .from(qAppI18nEntityAttribute)
                .where(qAppI18nEntityAttribute.values.size().lt(EI18nLanguage.values().length))
                .limit(1)
                .fetchFirst();
    }

    public I18nTranslationStatistic getStatistics() {
        final Long countOfAllEntries = createStatisticsBaseQuery().fetchOne();
        final Long countOfTranslatedEntries = createStatisticsBaseQuery().where(qAppI18nEntityAttribute.values.size().eq(EI18nLanguage.values().length)).fetchOne();
        final Long countOfNotTranslatedEntries = createStatisticsBaseQuery().where(qAppI18nEntityAttribute.values.size().lt(EI18nLanguage.values().length)).fetchOne();

        I18nTranslationStatistic statistic = new I18nTranslationStatistic();
        statistic.setCountOfAllItems(countOfAllEntries);
        statistic.setCountOfTranslatedItems(countOfTranslatedEntries);
        statistic.setCountOfNotTranslatedItems(countOfNotTranslatedEntries);

        return statistic;

    }

    private JPAQuery<Long> createStatisticsBaseQuery() {
        return new JPAQuery<>(em)
                .select(qAppI18nEntityAttribute.uuid.count())
                .from(qAppI18nEntityAttribute);
    }

    @SuppressWarnings("all")
    private String findBestTranslationIfAvailable(AppI18nEntityAttribute entityAttribute) {
        final EI18nLanguage applicationLanguage = ECoreRegistry.I18N_DEFAULT_LANGUAGE.getTypedValue(EI18nLanguage.class);
        final String translation = entityAttribute.getValueByLanguage(applicationLanguage)
                .orElseGet(() -> entityAttribute.getValueByLanguage(I18N.CODING_LANGUAGE).orElse(null));

        if(translation != null) {
            return translation;
        } else {
            return entityAttribute.getValues().stream().map(AppI18nEntityAttributeValue::getTranslation)
                    .filter(Objects::nonNull)
                    .findAny()
                    .orElse(null);
        }
    }
}

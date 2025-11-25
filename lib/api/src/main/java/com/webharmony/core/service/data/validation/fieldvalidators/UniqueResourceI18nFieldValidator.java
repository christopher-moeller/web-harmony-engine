package com.webharmony.core.service.data.validation.fieldvalidators;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.ComparablePath;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.webharmony.core.api.rest.model.utils.AbstractResourceDto;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.data.jpa.model.i18n.QAppI18nEntityAttribute;
import com.webharmony.core.i18n.I18N;
import com.webharmony.core.i18n.I18nTranslation;
import com.webharmony.core.service.data.validation.utils.NamedValidationInterface;
import com.webharmony.core.service.data.validation.utils.ValidationContext;
import com.webharmony.core.utils.QueryDSLUtils;
import jakarta.persistence.EntityManager;

import java.util.UUID;
import java.util.function.Function;

public class UniqueResourceI18nFieldValidator<E, P extends EntityPathBase<E>, R extends AbstractResourceDto> implements NamedValidationInterface<String, R>, I18nTranslation {

    private final I18N i18N = createI18nInstance(UniqueResourceI18nFieldValidator.class);

    private static final String NAME = "UNIQUE_FIELD_VALIDATION";

    private final P entityPathBase;
    private final Function<P, ComparablePath<UUID>> uuidPathResolver;
    private final Function<P, QAppI18nEntityAttribute> uniqueAttributePathResolver;

    public UniqueResourceI18nFieldValidator(P entityPathBase, Function<P, ComparablePath<UUID>> uuidPathResolver, Function<P, QAppI18nEntityAttribute> uniqueAttributePathResolver) {
        this.entityPathBase = entityPathBase;
        this.uuidPathResolver = uuidPathResolver;
        this.uniqueAttributePathResolver = uniqueAttributePathResolver;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void validate(String value, ValidationContext<R> validationContext) {

        if(value == null) {
            return;
        }

        EntityManager em = validationContext.getBean(EntityManager.class);
        UUID ownUUID = validationContext.getRootSource().buildUUIDOrNull();
        final ComparablePath<UUID> uuidPath = uuidPathResolver.apply(entityPathBase);
        final QAppI18nEntityAttribute i18nAttribute = uniqueAttributePathResolver.apply(entityPathBase);
        final StringPath uniqueAttributePath = QueryDSLUtils.buildI18nAttributeValueVarValue(i18nAttribute);

        final JPAQuery<Tuple> tupleQuery = new JPAQuery<>(em)
                .from(entityPathBase)
                .select(uuidPath, uniqueAttributePath);

        QueryDSLUtils.initI18nAttributeJoinExpressions(tupleQuery, i18nAttribute, ContextHolder.getContext().getContextLanguage());
        tupleQuery.where(uniqueAttributePath.eq(value));

        if(ownUUID != null)
            tupleQuery.where(uuidPath.ne(ownUUID));

        boolean isUnique = tupleQuery.fetch().isEmpty();

        if(!isUnique) {
            validationContext.addValidationError(i18N.translate("Field is not unique").build());
        }

    }
}

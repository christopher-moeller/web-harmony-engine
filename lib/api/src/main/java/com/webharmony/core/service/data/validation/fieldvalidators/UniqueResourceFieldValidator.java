package com.webharmony.core.service.data.validation.fieldvalidators;

import com.querydsl.core.types.dsl.ComparablePath;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.webharmony.core.api.rest.model.utils.AbstractResourceDto;
import com.webharmony.core.i18n.I18N;
import com.webharmony.core.i18n.I18nTranslation;
import com.webharmony.core.service.data.validation.utils.NamedValidationInterface;
import com.webharmony.core.service.data.validation.utils.ValidationContext;
import com.webharmony.core.utils.QueryDSLUtils;
import jakarta.persistence.EntityManager;

import java.util.UUID;
import java.util.function.Function;

@SuppressWarnings("unused")
public class UniqueResourceFieldValidator<E, P extends EntityPathBase<E>, R extends AbstractResourceDto> implements NamedValidationInterface<String, R>, I18nTranslation {

    private final I18N i18N = createI18nInstance(UniqueResourceFieldValidator.class);

    private static final String NAME = "UNIQUE_FIELD_VALIDATION";

    private final P entityPathBase;
    private final Function<P, ComparablePath<UUID>> uuidPathResolver;
    private final Function<P, SimpleExpression<String>> uniqueAttributePathResolver;

    public UniqueResourceFieldValidator(P entityPathBase, Function<P, ComparablePath<UUID>> uuidPathResolver, Function<P, SimpleExpression<String>> uniqueAttributePathResolver) {
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
        EntityManager em = validationContext.getBean(EntityManager.class);
        UUID ownUUID = validationContext.getRootSource().buildUUIDOrNull();
        boolean isUnique = QueryDSLUtils.checkIsAttributeUnique(em, entityPathBase, uuidPathResolver, uniqueAttributePathResolver, value, ownUUID);

        if(!isUnique) {
            validationContext.addValidationError(i18N.translate("Field is not unique").build());
        }

    }
}

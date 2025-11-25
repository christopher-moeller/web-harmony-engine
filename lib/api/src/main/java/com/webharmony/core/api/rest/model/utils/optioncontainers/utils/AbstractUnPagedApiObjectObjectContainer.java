package com.webharmony.core.api.rest.model.utils.optioncontainers.utils;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.webharmony.core.api.rest.model.utils.apiobject.ApiObject;
import com.webharmony.core.api.rest.model.utils.apiobject.ApiObjectWithLabel;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.data.jpa.model.i18n.QAppI18nEntityAttribute;
import com.webharmony.core.i18n.EI18nLanguage;
import com.webharmony.core.utils.QueryDSLUtils;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public abstract class AbstractUnPagedApiObjectObjectContainer<T extends ApiObject<?>> extends AbstractUnPagedOptionsContainer<T> {

    @Override
    public boolean optionContainsInRange(T option) {
        List<? extends ApiObject<?>> selectableOptions = getSelectableOptions();
        for(ApiObject<?> selectableOption : selectableOptions) {
            if(selectableOption.getPrimaryKey().toString().equals(option.getPrimaryKey().toString())) {
                return true;
            }
        }
        return false;
    }

    protected ApiObjectWithLabel mapToApiObjectWithLabel(Tuple tuple, Expression<?> primaryKeyExpression, Expression<String> labelExpression) {
        return new ApiObjectWithLabel(tuple.get(primaryKeyExpression), tuple.get(labelExpression));
    }

    protected <E extends EntityPath<?>> List<ApiObjectWithLabel> createSelectableApiObjectsWithI18nLabel(E entityPath, Function<E, Expression<UUID>> uuidExpressionResolver, Function<E, QAppI18nEntityAttribute> labelExpressionResolver) {
        return createSelectableApiObjectsWithI18nLabel(entityPath, uuidExpressionResolver, labelExpressionResolver, ContextHolder.getContext().getContextLanguage());
    }
    protected <E extends EntityPath<?>> List<ApiObjectWithLabel> createSelectableApiObjectsWithI18nLabel(E entityPath, Function<E, Expression<UUID>> uuidExpressionResolver, Function<E, QAppI18nEntityAttribute> labelExpressionResolver, EI18nLanguage language) {
        final Expression<UUID> uuidExpression = uuidExpressionResolver.apply(entityPath);
        final QAppI18nEntityAttribute labelExpression = labelExpressionResolver.apply(entityPath);

        JPAQuery<Tuple> query = createTupleQuery().select(buildSelectExpressionForApiObjectWithLabel(uuidExpression, QueryDSLUtils.buildI18nAttributeValueVarValue(labelExpression)))
                .from(entityPath);

        QueryDSLUtils.initI18nAttributeJoinExpressions(query, labelExpression, language);
        return query.fetch()
                .stream()
                .map(tuple -> mapToApiObjectWithLabel(tuple, uuidExpression, QueryDSLUtils.buildI18nAttributeValueVarValue(labelExpression)))
                .toList();
    }

    protected <E extends EntityPath<?>> List<ApiObjectWithLabel> createSelectableApiObjectsWithLabel(E entityPath, Function<E, Expression<UUID>> uuidExpressionResolver, Function<E, StringPath> labelExpressionResolver) {
        final Expression<UUID> uuidExpression = uuidExpressionResolver.apply(entityPath);
        final StringPath labelExpression = labelExpressionResolver.apply(entityPath);

        JPAQuery<Tuple> query = createTupleQuery().select(buildSelectExpressionForApiObjectWithLabel(uuidExpression, labelExpression))
                .from(entityPath);

        return query.fetch()
                .stream()
                .map(tuple -> mapToApiObjectWithLabel(tuple, uuidExpression, labelExpression))
                .toList();
    }

    @SuppressWarnings("all")
    protected Expression<?>[] buildSelectExpressionForApiObjectWithLabel(Expression<UUID> uuidExpression, Expression<String> labelExpression) {
        return new Expression[] {uuidExpression, labelExpression};
    }
}

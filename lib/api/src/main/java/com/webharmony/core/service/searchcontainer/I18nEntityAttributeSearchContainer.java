package com.webharmony.core.service.searchcontainer;

import com.webharmony.core.api.rest.controller.utils.request.SearchRequestContext;
import com.webharmony.core.data.jpa.model.i18n.AppI18nEntityAttribute;
import com.webharmony.core.data.jpa.model.i18n.AppI18nEntityAttributeValue;
import com.webharmony.core.service.searchcontainer.postgresql.*;
import com.webharmony.core.service.searchcontainer.utils.SearchFilter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class I18nEntityAttributeSearchContainer extends NativePostgresSQLSearchContainer {

    private final NativeTableVar<AppI18nEntityAttribute> nAttribute = new NativeTableVar<>(AppI18nEntityAttribute.class);
    private final NativeTableVar<AppI18nEntityAttributeValue> nAttributeValue = new NativeTableVar<>(AppI18nEntityAttributeValue.class);

    @Getter
    @AllArgsConstructor
    private enum Filter implements SearchFilter {

        ENTITY_CLASS("entityClass", "class id filter", new StringFilter()),
        TEXT("text", "text filter", new StringFilter()),
        ATTRIBUTE("attribute", "attribute filter", new StringFilter()),
        IS_CORE_ENTRY("isCoreEntry", "is core entry filter", new BooleanFilter()),
        IS_PROJECT_ENTRY("isProjectEntry", "is project entry filter", new BooleanFilter());

        private final String filterName;
        private final String filterDescription;
        private final FilterType<?> filterType;
    }

    @Override
    @SuppressWarnings({"java:S1192", "Duplicates"})
    protected void initQuery(NativeQueryBuilder query, SearchRequestContext searchRequestContext) {

        query.from(nAttribute.toString())
                .leftJoin(nAttributeValue + " ON " + nAttributeValue.resolveAttributePath(AppI18nEntityAttributeValue::getEntityAttribute) + " = " + nAttribute.resolveAttributePath(AppI18nEntityAttribute::getUuid))
                .groupBy(nAttribute.resolveAttributePath(AppI18nEntityAttribute::getUuid), nAttribute.resolveAttributePath(AppI18nEntityAttribute::getEntityClass), nAttribute.resolveAttributePath(AppI18nEntityAttribute::getAttribute), nAttribute.resolveAttributePath(AppI18nEntityAttribute::getIsCoreEntry));

        searchRequestContext.getFilterValue(Filter.ENTITY_CLASS, String.class)
                .ifPresent(search -> query.where(PreparedSQLText.of(nAttribute.resolveAttributePath(AppI18nEntityAttribute::getEntityClass) + " LIKE ?", "%" + search + "%")));

        searchRequestContext.getFilterValue(Filter.TEXT, String.class)
                .ifPresent(search -> query.where(PreparedSQLText.of(nAttributeValue.resolveAttributePath(AppI18nEntityAttributeValue::getTranslation) + " LIKE ?", "%" + search + "%")));

        searchRequestContext.getFilterValue(Filter.ATTRIBUTE, String.class)
                .ifPresent(attribute -> query.where(PreparedSQLText.of(nAttribute.resolveAttributePath(AppI18nEntityAttribute::getAttribute) + " LIKE ?", "%" + attribute + "%")));

        searchRequestContext.getFilterValue(Filter.IS_CORE_ENTRY, Boolean.class)
                .ifPresent(value -> {
                    if(Boolean.TRUE.equals(value))
                        query.where(PreparedSQLText.of(nAttribute.resolveAttributePath(AppI18nEntityAttribute::getIsCoreEntry) + " = ?", true));
                });

        searchRequestContext.getFilterValue(Filter.IS_PROJECT_ENTRY, Boolean.class)
                .ifPresent(value -> {
                    if(Boolean.TRUE.equals(value))
                        query.where(PreparedSQLText.of(nAttribute.resolveAttributePath(AppI18nEntityAttribute::getIsCoreEntry) + " = ?", false));
                });
    }

    @Override
    protected NativeSelectExpression getPrimaryKeyExpression() {
        return NativeSelectExpression.of("id", nAttribute.resolveAttributePath(AppI18nEntityAttribute::getUuid));
    }

    @Override
    protected void initSearchExpressions(List<NativeSelectExpression> searchExpressions) {
        searchExpressions.add(NativeSelectExpression.of("entityClass", nAttribute.resolveAttributePath(AppI18nEntityAttribute::getEntityClass)));
        searchExpressions.add(NativeSelectExpression.of("attributeName", nAttribute.resolveAttributePath(AppI18nEntityAttribute::getAttribute)));
        searchExpressions.add(NativeSelectExpression.of("isCoreEntry", nAttribute.resolveAttributePath(AppI18nEntityAttribute::getIsCoreEntry)));
        searchExpressions.add(NativeSelectExpression.of("countOfTranslations", String.format("count(%s)", nAttributeValue.resolveAttributePath(AppI18nEntityAttributeValue::getUuid))));
    }

    @Override
    public List<SearchFilter> getAvailableSearchFilter() {
        return Arrays.asList(Filter.values());
    }
}

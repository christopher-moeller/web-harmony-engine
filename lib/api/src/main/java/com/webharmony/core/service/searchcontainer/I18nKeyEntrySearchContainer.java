package com.webharmony.core.service.searchcontainer;

import com.webharmony.core.api.rest.controller.utils.request.SearchRequestContext;
import com.webharmony.core.data.jpa.model.i18n.AppI18nKeyEntry;
import com.webharmony.core.data.jpa.model.i18n.AppI18nTranslation;
import com.webharmony.core.i18n.EI18nCodeLocation;
import com.webharmony.core.service.searchcontainer.postgresql.*;
import com.webharmony.core.service.searchcontainer.utils.SearchFilter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class I18nKeyEntrySearchContainer extends NativePostgresSQLSearchContainer {

    private final NativeTableVar<AppI18nKeyEntry> nI18nKeyEntry = new NativeTableVar<>(AppI18nKeyEntry.class);
    private final NativeTableVar<AppI18nTranslation> nI18nTranslation = new NativeTableVar<>(AppI18nTranslation.class);

    @Getter
    @AllArgsConstructor
    private enum Filter implements SearchFilter {

        CLASS_ID("classId", "class id filter", new StringFilter()),
        TEXT("text", "text filter", new StringFilter()),
        KEY("key", "key filter", new StringFilter()),
        IS_CORE_ENTRY("isCoreEntry", "is core entry filter", new BooleanFilter()),
        IS_PROJECT_ENTRY("isProjectEntry", "is project entry filter", new BooleanFilter()),
        CODE_LOCATION_FILTER("codeLocation", "filter for code location", new SingleSelectionEnumFilter(EI18nCodeLocation.class));

        private final String filterName;
        private final String filterDescription;
        private final FilterType<?> filterType;
    }

    @Override
    @SuppressWarnings("all")
    protected void initQuery(NativeQueryBuilder query, SearchRequestContext searchRequestContext) {

        query.from(nI18nKeyEntry.toString())
                .leftJoin(nI18nTranslation + " ON " + nI18nTranslation.resolveAttributePath(AppI18nTranslation::getI18nKeyEntry) + " = " + nI18nKeyEntry.resolveAttributePath(AppI18nKeyEntry::getUuid) + " and " + nI18nTranslation.resolveAttributePath(AppI18nTranslation::getTranslation) + " is not null")
                .groupBy(nI18nKeyEntry.resolveAttributePath(AppI18nKeyEntry::getUuid), nI18nKeyEntry.resolveAttributePath(AppI18nKeyEntry::getClassId), nI18nKeyEntry.resolveAttributePath(AppI18nKeyEntry::getKey), nI18nKeyEntry.resolveAttributePath(AppI18nKeyEntry::getPlaceholders), nI18nKeyEntry.resolveAttributePath(AppI18nKeyEntry::getIsCoreEntry));

        searchRequestContext.getFilterValue(Filter.CLASS_ID, String.class)
                .ifPresent(search -> query.where(PreparedSQLText.of(nI18nKeyEntry.resolveAttributePath(AppI18nKeyEntry::getClassId) + " LIKE ?", "%" + search + "%")));

        searchRequestContext.getFilterValue(Filter.TEXT, String.class)
                        .ifPresent(search -> query.where(PreparedSQLText.of(nI18nTranslation.resolveAttributePath(AppI18nTranslation::getTranslation) + " LIKE ?", "%" + search + "%")));

        searchRequestContext.getFilterValue(Filter.KEY, String.class)
                        .ifPresent(key -> query.where(PreparedSQLText.of(nI18nKeyEntry.resolveAttributePath(AppI18nKeyEntry::getKey) + " LIKE ?", "%" + key + "%")));

        searchRequestContext.getFilterValue(Filter.IS_CORE_ENTRY, Boolean.class)
                .ifPresent(value -> {
                    if(Boolean.TRUE.equals(value))
                        query.where(PreparedSQLText.of(nI18nKeyEntry.resolveAttributePath(AppI18nKeyEntry::getIsCoreEntry) + " = ?", true));
                });

        searchRequestContext.getFilterValue(Filter.IS_PROJECT_ENTRY, Boolean.class)
                .ifPresent(value -> {
                    if(Boolean.TRUE.equals(value))
                        query.where(PreparedSQLText.of(nI18nKeyEntry.resolveAttributePath(AppI18nKeyEntry::getIsCoreEntry) + " = ?", false));
                });

        searchRequestContext.getFilterValue(Filter.CODE_LOCATION_FILTER, String.class)
                .ifPresent(codeLocation -> query.where(PreparedSQLText.of(nI18nKeyEntry.resolveAttributePath(AppI18nKeyEntry::getCodeLocation) + " = ?", codeLocation)));

    }

    @Override
    protected NativeSelectExpression getPrimaryKeyExpression() {
        return NativeSelectExpression.of("id", nI18nKeyEntry.resolveAttributePath(AppI18nKeyEntry::getUuid));
    }

    @Override
    protected void initSearchExpressions(List<NativeSelectExpression> searchExpressions) {
        searchExpressions.add(NativeSelectExpression.of("classId", nI18nKeyEntry.resolveAttributePath(AppI18nKeyEntry::getClassId)));
        searchExpressions.add(NativeSelectExpression.of("key", nI18nKeyEntry.resolveAttributePath(AppI18nKeyEntry::getKey)));
        searchExpressions.add(NativeSelectExpression.of("codeLocation", nI18nKeyEntry.resolveAttributePath(AppI18nKeyEntry::getCodeLocation)));
        searchExpressions.add(NativeSelectExpression.of("isCoreEntry", nI18nKeyEntry.resolveAttributePath(AppI18nKeyEntry::getIsCoreEntry)));
        searchExpressions.add(NativeSelectExpression.of("countOfTranslations", String.format("count(%s)", nI18nTranslation.resolveAttributePath(AppI18nTranslation::getUuid))));
    }

    @Override
    public List<SearchFilter> getAvailableSearchFilter() {
        return Arrays.asList(Filter.values());
    }
}

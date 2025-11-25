package com.webharmony.core.service.searchcontainer;

import com.querydsl.jpa.impl.JPAQuery;
import com.webharmony.core.api.rest.controller.utils.request.SearchRequestContext;
import com.webharmony.core.api.rest.jackson.CustomLocalDateTimeSerializer;
import com.webharmony.core.data.jpa.model.files.QAppFile;
import com.webharmony.core.service.searchcontainer.querydsl.AbstractQueryDslContainer;
import com.webharmony.core.service.searchcontainer.querydsl.SearchQueryDslExpression;
import com.webharmony.core.service.searchcontainer.utils.SearchFilter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
public class FileSearchContainer extends AbstractQueryDslContainer<QAppFile, UUID> {

    private static final QAppFile qAppFile = QAppFile.appFile;

    @Getter
    @AllArgsConstructor
    private enum Filter implements SearchFilter {

        SEARCH("search", "global filter", new StringFilter());

        private final String filterName;
        private final String filterDescription;
        private final FilterType<?> filterType;
    }

    @Override
    protected void initQueryFilters(JPAQuery<?> query, SearchRequestContext searchRequestContext) {
        searchRequestContext.getFilterValue(Filter.SEARCH, String.class)
                .ifPresent(search -> query.where(qAppFile.fileName.containsIgnoreCase(search)));
    }

    @Override
    protected void initSearchExpressions(List<SearchQueryDslExpression<?, ?>> searchExpressions) {
        searchExpressions.add(SearchQueryDslExpression.of("createdAt", qAppFile.createdAt).map(CustomLocalDateTimeSerializer::parseDateToString));
        searchExpressions.add(SearchQueryDslExpression.of("createdBy", qAppFile.createdBy));
        searchExpressions.add(SearchQueryDslExpression.of("fileName", qAppFile.fileName));
        searchExpressions.add(SearchQueryDslExpression.of("isTemp", qAppFile.isTemp));
    }

    @Override
    protected SearchQueryDslExpression<UUID, UUID> getPrimaryKeyExpression() {
        return SearchQueryDslExpression.of("id", qAppFile.uuid);
    }

    @Override
    public List<SearchFilter> getAvailableSearchFilter() {
        return Arrays.asList(Filter.values());
    }
}

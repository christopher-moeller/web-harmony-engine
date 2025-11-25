package com.webharmony.core.service.searchcontainer;

import com.querydsl.jpa.impl.JPAQuery;
import com.webharmony.core.api.rest.controller.utils.request.SearchRequestContext;
import com.webharmony.core.data.jpa.model.error.QAppReportedUserError;
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
public class ReportUserErrorsSearchContainer extends AbstractQueryDslContainer<QAppReportedUserError, UUID> {

    @Getter
    @AllArgsConstructor
    private enum Filter implements SearchFilter {

        SEARCH("search", "global filter", new StringFilter());

        private final String filterName;
        private final String filterDescription;
        private final FilterType<?> filterType;
    }

    private static final QAppReportedUserError qAppReportedUserError = QAppReportedUserError.appReportedUserError;

    @Override
    protected void initQueryFilters(JPAQuery<?> query, SearchRequestContext searchRequestContext) {
        searchRequestContext.getFilterValue(Filter.SEARCH, String.class)
                .ifPresent(value -> query.where(qAppReportedUserError.page.contains(value).or(qAppReportedUserError.description.contains(value))));
    }

    @Override
    protected void initSearchExpressions(List<SearchQueryDslExpression<?, ?>> searchExpressions) {
        searchExpressions.add(SearchQueryDslExpression.of("page", qAppReportedUserError.page));
        searchExpressions.add(SearchQueryDslExpression.of("description", qAppReportedUserError.description));
    }

    @Override
    protected SearchQueryDslExpression<UUID, UUID> getPrimaryKeyExpression() {
        return SearchQueryDslExpression.of("id", qAppReportedUserError.uuid);
    }

    @Override
    public boolean isUnPagedRequestAllowed() {
        return true;
    }

    @Override
    public List<SearchFilter> getAvailableSearchFilter() {
        return Arrays.asList(Filter.values());
    }
}

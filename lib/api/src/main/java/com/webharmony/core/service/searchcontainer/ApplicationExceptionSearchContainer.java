package com.webharmony.core.service.searchcontainer;

import com.querydsl.jpa.impl.JPAQuery;
import com.webharmony.core.api.rest.controller.utils.request.SearchRequestContext;
import com.webharmony.core.api.rest.jackson.CustomLocalDateTimeSerializer;
import com.webharmony.core.data.jpa.model.error.QAppApplicationEx;
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
public class ApplicationExceptionSearchContainer extends AbstractQueryDslContainer<QAppApplicationEx, UUID> {

    @Getter
    @AllArgsConstructor
    private enum Filter implements SearchFilter {

        SEARCH("search", "global filter", new StringFilter());

        private final String filterName;
        private final String filterDescription;
        private final FilterType<?> filterType;
    }

    private static final QAppApplicationEx qAppException = QAppApplicationEx.appApplicationEx;

    @Override
    protected void initQueryFilters(JPAQuery<?> query, SearchRequestContext searchRequestContext) {
        searchRequestContext.getFilterValue(Filter.SEARCH, String.class)
                .ifPresent(value -> query.where(qAppException.message.contains(value).or(qAppException.description.contains(value))));
    }

    @Override
    protected void initSearchExpressions(List<SearchQueryDslExpression<?, ?>> searchExpressions) {
        searchExpressions.add(SearchQueryDslExpression.of("exceptionType", qAppException.exceptionType));
        searchExpressions.add(SearchQueryDslExpression.of("message", qAppException.message));
        searchExpressions.add(SearchQueryDslExpression.of("codeLocation", qAppException.codeLocation));
        searchExpressions.add(SearchQueryDslExpression.of("timestamp", qAppException.createdAt).map(CustomLocalDateTimeSerializer::parseDateToString));
    }

    @Override
    protected SearchQueryDslExpression<UUID, UUID> getPrimaryKeyExpression() {
        return SearchQueryDslExpression.of("id", qAppException.uuid);
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

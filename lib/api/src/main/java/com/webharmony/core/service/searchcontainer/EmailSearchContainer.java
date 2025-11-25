package com.webharmony.core.service.searchcontainer;

import com.querydsl.jpa.impl.JPAQuery;
import com.webharmony.core.api.rest.controller.utils.request.SearchRequestContext;
import com.webharmony.core.api.rest.jackson.CustomLocalDateTimeSerializer;
import com.webharmony.core.data.jpa.model.email.QAppEmail;
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
public class EmailSearchContainer extends AbstractQueryDslContainer<QAppEmail, UUID> {

    private static final QAppEmail qAppEmail = QAppEmail.appEmail;

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
                .ifPresent(search -> query.where(qAppEmail.toEmail.containsIgnoreCase(search).or(qAppEmail.subject.containsIgnoreCase(search))));
    }

    @Override
    protected void initSearchExpressions(List<SearchQueryDslExpression<?, ?>> searchExpressions) {
        searchExpressions.add(SearchQueryDslExpression.of("createdAt", qAppEmail.createdAt).map(CustomLocalDateTimeSerializer::parseDateToString));
        searchExpressions.add(SearchQueryDslExpression.of("lastSending", qAppEmail.lastSending).map(CustomLocalDateTimeSerializer::parseDateToString));
        searchExpressions.add(SearchQueryDslExpression.of("state", qAppEmail.state));
        searchExpressions.add(SearchQueryDslExpression.of("fromEmail", qAppEmail.fromEmail));
        searchExpressions.add(SearchQueryDslExpression.of("toEmail", qAppEmail.toEmail));
        searchExpressions.add(SearchQueryDslExpression.of("subject", qAppEmail.subject));
    }

    @Override
    protected SearchQueryDslExpression<UUID, UUID> getPrimaryKeyExpression() {
        return SearchQueryDslExpression.of("id", qAppEmail.uuid);
    }

    @Override
    public List<SearchFilter> getAvailableSearchFilter() {
        return Arrays.asList(Filter.values());
    }
}

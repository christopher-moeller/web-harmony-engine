package com.webharmony.core.service.searchcontainer;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.jpa.impl.JPAQuery;
import com.webharmony.core.api.rest.controller.utils.request.SearchRequestContext;
import com.webharmony.core.data.jpa.model.user.QAppActorRight;
import com.webharmony.core.service.searchcontainer.querydsl.AbstractQueryDslContainer;
import com.webharmony.core.service.searchcontainer.querydsl.SearchQueryDslExpression;
import com.webharmony.core.service.searchcontainer.utils.SearchFilter;
import com.webharmony.core.utils.QueryDSLUtils;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
public class ActorRightSearchContainer extends AbstractQueryDslContainer<QAppActorRight, UUID> {

    @Getter
    @AllArgsConstructor
    private enum Filter implements SearchFilter {

        SEARCH("search", "global filter", new StringFilter());

        private final String filterName;
        private final String filterDescription;
        private final FilterType<?> filterType;
    }

    private static final QAppActorRight qAppActorRight = QAppActorRight.appActorRight;

    @Override
    protected JPAQuery<Tuple> buildJpaQuery(Expression<?>[] selectExpressions, EntityManager entityManager) {
        final JPAQuery<Tuple> query = super.buildJpaQuery(selectExpressions, entityManager);

        QueryDSLUtils.initI18nAttributeJoinExpressions(query, qAppActorRight.label, getContextLanguage());
        QueryDSLUtils.initI18nAttributeJoinExpressions(query, qAppActorRight.description, getContextLanguage());

        return query;
    }

    @Override
    protected void initQueryFilters(JPAQuery<?> query, SearchRequestContext searchRequestContext) {
        searchRequestContext.getFilterValue(Filter.SEARCH, String.class)
                .ifPresent(value -> query.where(QueryDSLUtils.buildI18nAttributeValueVarValue(qAppActorRight.label).contains(value)));
    }

    @Override
    protected void initSearchExpressions(List<SearchQueryDslExpression<?, ?>> searchExpressions) {
        searchExpressions.add(SearchQueryDslExpression.of("label", QueryDSLUtils.buildI18nAttributeValueVarValue(qAppActorRight.label)));
        searchExpressions.add(SearchQueryDslExpression.of("description", QueryDSLUtils.buildI18nAttributeValueVarValue(qAppActorRight.description)));
    }

    @Override
    protected SearchQueryDslExpression<UUID, UUID> getPrimaryKeyExpression() {
        return SearchQueryDslExpression.of("id", qAppActorRight.uuid);
    }

    @Override
    public List<SearchFilter> getAvailableSearchFilter() {
        return Arrays.asList(Filter.values());
    }
}

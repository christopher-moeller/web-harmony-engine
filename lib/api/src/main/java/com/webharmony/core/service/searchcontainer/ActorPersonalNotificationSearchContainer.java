package com.webharmony.core.service.searchcontainer;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.jpa.impl.JPAQuery;
import com.webharmony.core.api.rest.controller.utils.request.SearchRequestContext;
import com.webharmony.core.api.rest.jackson.CustomLocalDateTimeSerializer;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.data.jpa.model.actor.AbstractActor;
import com.webharmony.core.data.jpa.model.notifications.QAppActorNotification;
import com.webharmony.core.data.jpa.model.notifications.QAppActorNotificationEventType;
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
public class ActorPersonalNotificationSearchContainer extends AbstractQueryDslContainer<QAppActorNotification, UUID> {

    @Getter
    @AllArgsConstructor
    private enum Filter implements SearchFilter {

        SEARCH("search", "global filter", new StringFilter());

        private final String filterName;
        private final String filterDescription;
        private final FilterType<?> filterType;
    }

    private static final QAppActorNotification qAppActorNotification = QAppActorNotification.appActorNotification;
    private static final QAppActorNotificationEventType qAppActorNotificationEventType = QAppActorNotificationEventType.appActorNotificationEventType;

    @Override
    protected JPAQuery<Tuple> buildJpaQuery(Expression<?>[] selectExpressions, EntityManager entityManager) {
        final JPAQuery<Tuple> query = super.buildJpaQuery(selectExpressions, entityManager);
        query.leftJoin(qAppActorNotification.eventType, qAppActorNotificationEventType);
        QueryDSLUtils.initI18nAttributeJoinExpressions(query, qAppActorNotificationEventType.label, getContextLanguage());
        return query;
    }

    @Override
    protected void initQueryFilters(JPAQuery<?> query, SearchRequestContext searchRequestContext) {

        final AbstractActor currentActor = ContextHolder.getContext().getCurrentActor();
        query.where(qAppActorNotification.recipient.eq(currentActor));

        searchRequestContext.getFilterValue(Filter.SEARCH, String.class)
                .ifPresent(value -> query.where(qAppActorNotification.textMessage.contains(value)));

    }

    @Override
    protected void initSearchExpressions(List<SearchQueryDslExpression<?, ?>> searchExpressions) {
        searchExpressions.add(SearchQueryDslExpression.of("caption", qAppActorNotification.caption));
        searchExpressions.add(SearchQueryDslExpression.of("read", qAppActorNotification.read));
        searchExpressions.add(SearchQueryDslExpression.of("type", QueryDSLUtils.buildI18nAttributeValueVarValue(qAppActorNotificationEventType.label)));
        searchExpressions.add(SearchQueryDslExpression.of("sendAt", qAppActorNotification.createdAt).map(CustomLocalDateTimeSerializer::parseDateToString));

    }

    @Override
    protected SearchQueryDslExpression<UUID, UUID> getPrimaryKeyExpression() {
        return SearchQueryDslExpression.of("id", qAppActorNotification.uuid);
    }

    @Override
    public List<SearchFilter> getAvailableSearchFilter() {
        return Arrays.asList(Filter.values());
    }
}

package com.webharmony.core.service.searchcontainer.utils;

import com.webharmony.core.api.rest.controller.utils.request.SearchRequestContext;
import com.webharmony.core.api.rest.model.ActorDto;
import com.webharmony.core.api.rest.model.utils.AbstractResourceDto;
import com.webharmony.core.data.jpa.model.actor.AbstractActor;
import com.webharmony.core.service.searchcontainer.postgresql.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ActorsSearchContainer extends NativePostgresSQLSearchContainer {
    private final NativeTableVar<AbstractActor> nAbstractActor = new NativeTableVar<>(AbstractActor.class);
    @Getter
    @AllArgsConstructor
    private enum Filter implements SearchFilter {

        UNIQUE_NAME("uniqueName", "unique Name", new StringFilter());


        private final String filterName;
        private final String filterDescription;
        private final FilterType<?> filterType;
    }

    @Override
    @SuppressWarnings("all")
    protected void initQuery(NativeQueryBuilder query, SearchRequestContext searchRequestContext) {
        query.from(nAbstractActor.toString());
    }

    @Override
    protected NativeSelectExpression getPrimaryKeyExpression() {
        return NativeSelectExpression.of("id", nAbstractActor.resolveAttributePath(AbstractActor::getUuid));
    }

    @Override
    protected void initSearchExpressions(List<NativeSelectExpression> searchExpressions) {
        searchExpressions.add(NativeSelectExpression.of("uniqueName", nAbstractActor.resolveAttributePath(AbstractActor::getUniqueName)));
        searchExpressions.add(NativeSelectExpression.of("type", "actor_type"));
        searchExpressions.add(NativeSelectExpression.of("userId", "app_user"));
    }

    @Override
    public List<SearchFilter> getAvailableSearchFilter() {
        return Arrays.asList(Filter.values());
    }

    @Override
    protected Class<? extends AbstractResourceDto> getResourceDtoClassBySearchRequest(SearchRequestContext searchRequestContext) {
        return ActorDto.class;
    }
}

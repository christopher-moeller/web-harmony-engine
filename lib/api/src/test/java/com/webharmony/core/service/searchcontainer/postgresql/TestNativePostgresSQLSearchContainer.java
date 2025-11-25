package com.webharmony.core.service.searchcontainer.postgresql;

import com.webharmony.core.api.rest.controller.utils.request.SearchRequestContext;
import com.webharmony.core.data.jpa.model.user.AppActorRight;
import com.webharmony.core.service.searchcontainer.utils.SearchFilter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TestNativePostgresSQLSearchContainer extends NativePostgresSQLSearchContainer {

    @Getter
    @AllArgsConstructor
    private enum Filter implements SearchFilter {

        ENUM_NAME("enumName", "enum name", new StringFilter());

        private final String filterName;
        private final String filterDescription;
        @SuppressWarnings("rawtypes")
        private final FilterType filterType;
    }

    protected final NativeTableVar<AppActorRight> nActorRight = new NativeTableVar<>(AppActorRight.class);


    @Override
    protected void initQuery(NativeQueryBuilder query, SearchRequestContext searchRequestContext) {
        query.from(nActorRight.toString());

        searchRequestContext.getFilterValue(Filter.ENUM_NAME, String.class)
                .ifPresent(enumName -> query.where(PreparedSQLText.of(nActorRight.resolveAttributePath(AppActorRight::getUniqueName) +  " = ?", enumName)));
    }

    @Override
    protected NativeSelectExpression getPrimaryKeyExpression() {
        return NativeSelectExpression.of("id", nActorRight.resolveAttributePath(AppActorRight::getUuid));
    }

    @Override
    protected void initSearchExpressions(List<NativeSelectExpression> searchExpressions) {
        searchExpressions.add(NativeSelectExpression.of("enumName", nActorRight.resolveAttributePath(AppActorRight::getUniqueName)));
        searchExpressions.add(NativeSelectExpression.of("label", nActorRight.resolveAttributePath(AppActorRight::getLabel)));
        searchExpressions.add(NativeSelectExpression.of("description", nActorRight.resolveAttributePath(AppActorRight::getDescription)));
    }
}

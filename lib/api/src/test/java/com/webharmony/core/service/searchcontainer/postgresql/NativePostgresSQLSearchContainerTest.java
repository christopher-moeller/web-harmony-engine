package com.webharmony.core.service.searchcontainer.postgresql;

import com.webharmony.core.AbstractBaseTest;
import com.webharmony.core.api.rest.controller.utils.RestRequestParams;
import com.webharmony.core.api.rest.controller.utils.request.SearchRequestContext;
import com.webharmony.core.api.rest.model.utils.apiobject.GeneralApiResource;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.data.enums.ECoreActorRight;
import com.webharmony.core.service.data.ActorRightService;
import com.webharmony.core.service.searchcontainer.utils.SearchResult;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class NativePostgresSQLSearchContainerTest extends AbstractBaseTest {

    @Test
    void shouldExecuteNativeSearchContainer() {
        SearchRequestContext searchRequestContext = new SearchRequestContext(ContextHolder.getContext().getSpringContext(), RestRequestParams.of(new HashMap<>()));
        searchRequestContext.setEntityCrudService(ContextHolder.getContext().getBean(ActorRightService.class));
        NativePostgresSQLSearchContainer exampleSearchContainer = ContextHolder.getContext().getSpringContext().getBean(TestNativePostgresSQLSearchContainer.class);
        SearchResult searchResult = exampleSearchContainer.getSearchResultsByRequestParams(searchRequestContext);

        assertThat(searchResult).isNotNull();
        assertThat(searchResult.getTotalResults()).isPositive();
        assertThat(searchResult.getPage()).isZero();
        assertThat(searchResult.getIsPaged()).isTrue();
        assertThat(searchResult.getSize()).isEqualTo(10L);
        assertThat(searchResult.getSortedBy()).isEmpty();
        assertThat(searchResult.getPrimaryKeyName()).isEqualTo("id");
        assertThat(searchResult.getData())
                .hasSizeLessThanOrEqualTo(10)
                .hasSizeLessThanOrEqualTo((int) searchResult.getTotalResults());

        List<GeneralApiResource<Map<String, Object>>> data = searchResult.getData();

        assertThat(data).hasSize(10);

        final GeneralApiResource<Map<String, Object>> selectedEntry = data.stream()
                .filter(e -> e.getData().get("enumName").equals(ECoreActorRight.CORE_ACTOR_RIGHTS_CRUD.name()))
                .findAny()
                .orElseThrow();

        assertThat(selectedEntry.getPrimaryKey()).isNotNull();
        assertThat(selectedEntry.getData()).containsEntry("enumName", ECoreActorRight.CORE_ACTOR_RIGHTS_CRUD.name());
    }

    @Test
    void shouldExecuteNativeSearchContainerWithActiveFilter() {
        SearchRequestContext searchRequestContext = new SearchRequestContext(ContextHolder.getContext().getSpringContext(), RestRequestParams.of(Map.of("enumName", ECoreActorRight.CORE_ACTOR_RIGHTS_CRUD.name(), "sort", "enumName,notAvailableSort", "attributes", "id, enumName")));
        searchRequestContext.setEntityCrudService(ContextHolder.getContext().getBean(ActorRightService.class));
        NativePostgresSQLSearchContainer exampleSearchContainer = ContextHolder.getContext().getSpringContext().getBean(TestNativePostgresSQLSearchContainer2.class);
        SearchResult searchResult = exampleSearchContainer.getSearchResultsByRequestParams(searchRequestContext);

        assertThat(searchResult.getTotalResults()).isEqualTo(1);
        assertThat(searchResult.getData()).hasSize(1);

        List<GeneralApiResource<Map<String, Object>>> data = searchResult.getData();

        final GeneralApiResource<Map<String, Object>> selectedEntry = data.stream()
                .filter(e -> e.getData().get("enumName").equals(ECoreActorRight.CORE_ACTOR_RIGHTS_CRUD.name()))
                .findAny()
                .orElseThrow();

        assertThat(selectedEntry.getPrimaryKey()).isNotNull();
        assertThat(selectedEntry.getData()).containsEntry("enumName", ECoreActorRight.CORE_ACTOR_RIGHTS_CRUD.name());
        assertThat(selectedEntry.getData()).hasSize(2);
    }
}

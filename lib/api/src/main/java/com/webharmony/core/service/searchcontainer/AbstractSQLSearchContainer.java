package com.webharmony.core.service.searchcontainer;

import com.webharmony.core.api.rest.controller.utils.RestRequestParams;
import com.webharmony.core.api.rest.controller.utils.request.SearchRequestContext;
import com.webharmony.core.api.rest.model.utils.AbstractResourceDto;
import com.webharmony.core.api.rest.model.utils.ResourceLinks;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.service.ResourceLinkService;
import com.webharmony.core.service.data.utils.AbstractEntityCrudService;
import com.webharmony.core.service.searchcontainer.utils.SearchResult;
import com.webharmony.core.utils.tuple.Tuple2;

import java.util.List;
import java.util.Optional;

public abstract class AbstractSQLSearchContainer extends AbstractSearchContainer {

    protected Class<? extends AbstractResourceDto> getResourceDtoClassBySearchRequest(SearchRequestContext searchRequestContext) {

        final Class<? extends AbstractResourceDto> dtoClass = searchRequestContext.getDtoClass();
        if(dtoClass != null) {
            return dtoClass;
        }

        return searchRequestContext.getEntityCrudService()
                .map(AbstractEntityCrudService::getDtoClass)
                .map(c -> (Class<? extends AbstractResourceDto>) c)
                .orElseThrow();
    }

    protected void initMetaData(SearchRequestContext searchRequestContext, SearchResult searchResult) {
        RestRequestParams restRequestParams = searchRequestContext.getRestRequestParams();
        searchResult.setIsPaged(restRequestParams.getIsPaged());
        searchResult.setSize(restRequestParams.getSize());
        searchResult.setPage(restRequestParams.getPage());

        List<String> sortedBy = restRequestParams.getSorts().stream()
                .map(sort -> String.format("%s:%s", sort.getName(), sort.getOrder().name().toLowerCase()))
                .toList();

        searchResult.setSortedBy(sortedBy);
    }

    protected Optional<Tuple2<Long, Long>> getLimitAndOffset(SearchRequestContext searchRequestContext) {
        final RestRequestParams restRequestParams = searchRequestContext.getRestRequestParams();

        if(!restRequestParams.getIsPaged())
            return Optional.empty();

        long page = restRequestParams.getPage();

        long limit = restRequestParams.getSize();
        long offset = page * limit;

        return Optional.of(Tuple2.of(limit, offset));
    }

    protected ResourceLinks createResourceLinksByResourceId(Class<? extends AbstractResourceDto> dtoClass, Object id) {
        final ResourceLinkService resourceLinkService = ContextHolder.getContext().getBean(ResourceLinkService.class);
        return resourceLinkService.createResourceLinksByResourceDtoAndId(dtoClass, id);
    }

}

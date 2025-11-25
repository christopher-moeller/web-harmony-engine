package com.webharmony.core.service.searchcontainer.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webharmony.core.api.rest.controller.utils.RestRequestParams;
import com.webharmony.core.api.rest.controller.utils.request.SearchRequestContext;
import com.webharmony.core.api.rest.model.utils.BaseDto;
import com.webharmony.core.api.rest.model.utils.AbstractResourceDto;
import com.webharmony.core.api.rest.model.utils.ResourceLinks;
import com.webharmony.core.api.rest.model.utils.apiobject.GeneralApiResource;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.service.ResourceLinkService;
import com.webharmony.core.service.searchcontainer.AbstractSearchContainer;
import com.webharmony.core.utils.JacksonUtils;
import com.webharmony.core.utils.reflection.ReflectionUtils;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;

public class DefaultEntityCurdSearchContainer extends AbstractSearchContainer {

    private final ObjectMapper jacksonMapper;
    private final Class<? extends BaseDto> dtoClass;

    public DefaultEntityCurdSearchContainer(Class<? extends BaseDto> dtoClass) {
        this.jacksonMapper = JacksonUtils.createDefaultJsonMapper();
        this.dtoClass = dtoClass;
    }

    @Override
    protected SearchResult getSearchResultsByRequestParamsInternal(SearchRequestContext searchRequestContext) {
        var service = searchRequestContext.getEntityCrudService().orElseThrow();

        Page<? extends BaseDto> dtoResults = service.getAllEntries(searchRequestContext, getAppliedSortsOptions(searchRequestContext).toArray(new RestRequestParams.RestSort[0]));

        SearchResult searchResult = new SearchResult();
        List<GeneralApiResource<Map<String, Object>>> data = convertDtoResults(dtoResults.getContent());
        filterSelectedAttributes(data, searchRequestContext);

        searchResult.setData(data);
        searchResult.setTotalResults(dtoResults.getTotalElements());

        if(dtoResults.getPageable().isPaged()) {
            searchResult.setIsPaged(true);
            searchResult.setPage(dtoResults.getPageable().getPageNumber());
        } else {
            searchResult.setIsPaged(false);
        }

        searchResult.setSize(dtoResults.getSize());

        List<String> sortedBy = dtoResults.getSort().stream()
                .map(order -> String.format("%s:%s", getSearchPropertyNameOrder(order), order.getDirection().toString().toLowerCase()))
                .toList();

        searchResult.setSortedBy(sortedBy);

        searchResult.setPrimaryKeyName(getResponsePrimaryKeyName());

        return searchResult;
    }

    private String getSearchPropertyNameOrder(Sort.Order order) {
        String key = order.getProperty();
        return key.equals("uuid") ? "id" : key;
    }


    private void filterSelectedAttributes(List<GeneralApiResource<Map<String, Object>>> data, SearchRequestContext searchRequestContext) {
        List<String> availableAttributeNames = getAvailableAttributesForCurrentUser(searchRequestContext).stream().map(SearchContainerAttribute::getName).toList();
        List<String> selectedAttributeNames = getSelectedAttributes(searchRequestContext).stream().map(SearchContainerAttribute::getName).toList();

        List<String> attributesToRemove = availableAttributeNames.stream()
                .filter(availableAttributeName -> !selectedAttributeNames.contains(availableAttributeName))
                .toList();

        for(String attributeToRemove : attributesToRemove) {
            data.forEach(e -> e.getData().remove(attributeToRemove));
        }
    }

    @Override
    public List<SearchContainerAttribute> getAvailableAttributes() {
        return ReflectionUtils.getAllFields(this.dtoClass, true)
                .stream()
                .filter(field -> !field.getType().equals(ResourceLinks.class))
                .map(field -> {
                    SearchContainerAttribute attribute = new SearchContainerAttribute();
                    attribute.setName(field.getName());
                    return attribute;
                }).toList();
    }

    private List<GeneralApiResource<Map<String, Object>>> convertDtoResults(List<? extends BaseDto> dtoResults) {
        final ResourceLinkService resourceLinkService = ContextHolder.getContext().getBean(ResourceLinkService.class);
        return dtoResults.stream()
                .map(dto -> {
                    GeneralApiResource<Map<String, Object>> apiObject = new GeneralApiResource<>();
                    apiObject.setData(convertDtoToMap(dto));

                    if(dto instanceof AbstractResourceDto resourceDto) {
                        apiObject.setPrimaryKey(resourceDto.getId());
                        apiObject.setResourceLinks(resourceLinkService.createResourceLinksByResourceDtoAndId(resourceDto.getClass(), resourceDto.getId()));
                    }

                    return apiObject;
                }).toList();
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    private Map<String, Object> convertDtoToMap(BaseDto dto) {
        return jacksonMapper.convertValue(dto, Map.class);
    }

    @Override
    protected List<RestRequestParams.RestSort> getAppliedSortsOptions(SearchRequestContext searchRequestContext) {
        return super.getAppliedSortsOptions(searchRequestContext)
                .stream()
                .map(sort -> sort.getName().equals("id") ? sort.createCopyWithNewName("uuid") : sort)
                .toList();
    }
}

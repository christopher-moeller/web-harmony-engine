package com.webharmony.core.api.rest.controller.utils.dispacher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webharmony.core.api.rest.controller.utils.RestRequestParams;
import com.webharmony.core.api.rest.controller.utils.request.RequestContext;
import com.webharmony.core.api.rest.controller.utils.request.SearchRequestContext;
import com.webharmony.core.api.rest.model.utils.BaseDto;
import com.webharmony.core.api.rest.model.utils.AbstractResourceDto;
import com.webharmony.core.api.rest.model.utils.apiobject.GeneralApiResource;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.i18n.I18N;
import com.webharmony.core.i18n.I18nTranslation;
import com.webharmony.core.service.InMemoryDataService;
import com.webharmony.core.service.ResourceLinkService;
import com.webharmony.core.service.data.mapper.GenericObjectMapper;
import com.webharmony.core.service.data.mapper.MappingContext;
import com.webharmony.core.service.data.validation.ValidatorService;
import com.webharmony.core.service.searchcontainer.utils.SearchResult;
import com.webharmony.core.service.searchcontainer.utils.SortOrder;
import com.webharmony.core.utils.JacksonUtils;
import com.webharmony.core.utils.exceptions.InternalServerException;
import com.webharmony.core.utils.exceptions.NotFoundException;
import com.webharmony.core.utils.reflection.ReflectionUtils;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class InMemoryDtoCrudDispatcher<D extends AbstractResourceDto> implements ControllerDispatcher<D>, I18nTranslation {

    private final I18N i18N = createI18nInstance(InMemoryDtoCrudDispatcher.class);

    private final Class<D> dtoClass;

    private final ObjectMapper jacksonMapper;

    public InMemoryDtoCrudDispatcher(Class<D> dtoClass) {
        this.dtoClass = dtoClass;
        this.jacksonMapper = JacksonUtils.createDefaultJsonMapper();
    }

    @Override
    public SearchResult getAllEntries(RequestContext requestContext) {

        if(requestContext instanceof SearchRequestContext searchRequestContext) {

            final RestRequestParams restRequestParams = searchRequestContext.getRestRequestParams();

            final List<D> entries = new ArrayList<>(getInMemoryDataService().getAllEntries(dtoClass)
                    .stream().map(this::mapDto).toList());
            Optional.of(restRequestParams)
                    .map(RestRequestParams::getSorts)
                    .orElseGet(Collections::emptyList)
                    .stream().findFirst()
                    .ifPresent(restSort -> entries.sort((e1, e2) -> genericSort(e1, e2, dtoClass, restSort)));


            SearchResult searchResult = new SearchResult();
            searchResult.setTotalResults(entries.size());
            searchResult.setIsPaged(restRequestParams.getIsPaged());
            searchResult.setSortedBy(restRequestParams.getSorts().stream().map(RestRequestParams.RestSort::getName).toList());
            searchResult.setPrimaryKeyName("id");

            if(restRequestParams.getIsPaged()) {
                int page = restRequestParams.getPage();
                int size = restRequestParams.getSize();

                int offset = page * size;
                int limit = offset + size;
                searchResult.setData(entries.isEmpty() ? Collections.emptyList() : mapToSearchResultDataList(limitList(entries, offset, limit)));

                searchResult.setPage(page);
                searchResult.setSize(size);
            } else {
                searchResult.setData(mapToSearchResultDataList(entries));
            }

            return searchResult;

        } else {
            throw new InternalServerException("Not implemented yet");
        }
    }

    @Override
    public D getEntryById(UUID uuid, RequestContext requestContext) {
        return getInMemoryDataService().getAllEntries(dtoClass)
                .stream()
                .filter(entry -> entry.buildUUIDOrNull().equals(uuid))
                .findAny()
                .map(this::mapDto)
                .orElseThrow(() -> new NotFoundException(i18N.translate("Entry with id '{id}' not found").add("id", uuid).build()));
    }

    protected D mapDto(D dto) {
        return dto;
    }

    @Override
    public D createNewEntry(D dto, RequestContext requestContext) {
        if(dto.getId() == null)
            dto.setId(UUID.randomUUID().toString());

        getValidatorService().validate(dto);
        getInMemoryDataService().addEntry(dto);
        return dto;
    }

    @Override
    public D updateEntry(UUID uuid, D dto, RequestContext requestContext) {
        getValidatorService().validate(dto);

        D currentEntry = getEntryById(uuid, requestContext);
        dto = new GenericObjectMapper<>(dtoClass, dtoClass)
                .mapAToB(dto, currentEntry, MappingContext.of(requestContext));

        dto.setId(currentEntry.getId());
        getInMemoryDataService().removeEntry(currentEntry);
        getInMemoryDataService().addEntry(dto);

        return dto;
    }

    @Override
    public void deleteEntry(UUID uuid, RequestContext requestContext) {
        D currentEntry = getEntryById(uuid, requestContext);
        getInMemoryDataService().removeEntry(currentEntry);
    }

    private InMemoryDataService getInMemoryDataService() {
        return ContextHolder.getContext().getBean(InMemoryDataService.class);
    }

    private ValidatorService getValidatorService() {
        return ContextHolder.getContext().getBean(ValidatorService.class);
    }

    private <T> int genericSort(T entry1, T entry2, Class<T> dataType, RestRequestParams.RestSort restSort) {
        Field field = ReflectionUtils.getFieldByName(dataType, restSort.getName());
        Method getter = ReflectionUtils.findGetterByField(field, dataType).orElseThrow();

        String stringValue1 = getStringValueByGetter(entry1, getter);
        String stringValue2 = getStringValueByGetter(entry2, getter);

        int result = stringValue1.compareTo(stringValue2);
        return restSort.getOrder().equals(SortOrder.ASC) ? result : result  * -1;
    }

    @SneakyThrows
    private String getStringValueByGetter(Object value, Method getter) {
        final String nullValue = "null";

        if(value == null)
            return nullValue;

        return Optional.ofNullable(getter.invoke(value))
                .map(Objects::toString)
                .orElse(nullValue);
    }

    private List<D> limitList(List<D> originalList, int offset, int limit) {

        if(offset >= originalList.size())
            return Collections.emptyList();

        ArrayList<D> newList = new ArrayList<>(originalList);
        if(limit >= newList.size())
            return newList.subList(offset, newList.size());

        return newList.subList(offset, limit);
    }

    private List<GeneralApiResource<Map<String, Object>>> mapToSearchResultDataList(List<D> entries) {
        final ResourceLinkService resourceLinkService = ContextHolder.getContext().getBean(ResourceLinkService.class);
        return entries.stream()
                .map(dto -> {
                    GeneralApiResource<Map<String, Object>> apiObject = new GeneralApiResource<>();
                    apiObject.setData(convertDtoToMap(dto));
                    apiObject.setPrimaryKey(dto.getId());
                    apiObject.setResourceLinks(resourceLinkService.createResourceLinksByResourceDtoAndId(dto.getClass(), dto.getId()));

                    return apiObject;
                }).toList();
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    private Map<String, Object> convertDtoToMap(BaseDto dto) {
        return jacksonMapper.convertValue(dto, Map.class);
    }
}

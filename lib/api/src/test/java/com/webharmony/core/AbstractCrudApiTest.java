package com.webharmony.core;

import com.webharmony.core.api.rest.controller.utils.AbstractCrudController;
import com.webharmony.core.api.rest.controller.utils.RestRequestParams;
import com.webharmony.core.api.rest.model.utils.AbstractResourceDto;
import com.webharmony.core.api.rest.model.utils.FileWebData;
import com.webharmony.core.api.rest.model.utils.ResourceLinks;
import com.webharmony.core.api.rest.model.utils.ResponseResource;
import com.webharmony.core.api.rest.model.utils.apiobject.ApiResource;
import com.webharmony.core.api.rest.model.utils.apiobject.GeneralApiResource;
import com.webharmony.core.service.searchcontainer.utils.SearchResult;
import com.webharmony.core.utils.exceptions.ResourceNotFoundException;
import com.webharmony.core.utils.reflection.ApiLink;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public abstract class AbstractCrudApiTest<T extends AbstractResourceDto, C extends AbstractCrudController<T>> extends AbstractApiTest<C> {

    @Test
    @Transactional
    void shouldGetAllEntries() {

        initTestElements();
        SearchResult searchResult = getSearchResults();
        assertThat(searchResult).isNotNull();
        assertThat(searchResult.getTotalResults()).isPositive();
        assertThat(searchResult.getPage()).isZero();
        assertThat(searchResult.getIsPaged()).isTrue();
        assertThat(searchResult.getSize()).isEqualTo(10L);
        assertThat(searchResult.getSortedBy()).isNotNull();
        assertThat(searchResult.getPrimaryKeyName()).isEqualTo("id");
        assertThat(searchResult.getData())
                .hasSizeLessThanOrEqualTo(10)
                .hasSizeLessThanOrEqualTo((int) searchResult.getTotalResults());

        validateSearchResultData(searchResult.getData());
    }

    @Test
    @Transactional
    void shouldGetEntryById() {
        initTestElements();

        UUID uuidForTesting = getUUIDForTesting();
        ResponseResource<T> entryById = getController().getEntryById(uuidForTesting);
        assertThat(entryById).isNotNull();

        ApiResource<T> apiResource = entryById.getData();
        ResourceLinks resourceLinks = apiResource.getResourceLinks();

        validateResourceLinksForGetAllEntry(resourceLinks, uuidForTesting);
        assertThat(apiResource.getPrimaryKeyAsUUID()).isEqualTo(uuidForTesting);

        validateSelectedResourceData(apiResource.getData());

    }

    protected Map<String, Object> getFilterForSearchAll() {
        return new HashMap<>();
    }

    protected SearchResult getSearchResults() {
        RestRequestParams requestParams = RestRequestParams.of(getFilterForSearchAll());
        return assertOkResponse(c -> c.getAllEntries(requestParams));
    }

    protected UUID getUUIDForTesting() {
        List<GeneralApiResource<Map<String, Object>>> data = getSearchResults().getData();
        GeneralApiResource<Map<String, Object>> selectedGetAllApiResourceForTesting = getSelectedGetAllApiResourceForTesting(data);
        return selectedGetAllApiResourceForTesting.getPrimaryKeyAsUUID();
    }

    @SuppressWarnings("unchecked")
    protected void validateResourceLinksForGetAllEntry(ResourceLinks resourceLinks, UUID uuid) {
        Optional.ofNullable(ApiLink.of(getController().getClass(), c -> c.getEntryById(uuid))).map(link -> link.resolveID(uuid))
                .ifPresent(link -> validateSingleApiLink(link, resourceLinks.getSelfLink()));

        Optional.ofNullable(ApiLink.of(getController().getClass(), c -> c.updateEntry(uuid, null))).map(link -> link.resolveID(uuid))
                .ifPresent(link -> validateSingleApiLink(link, resourceLinks.getUpdateLink()));

        Optional.ofNullable(ApiLink.of(getController().getClass(), c -> c.deleteEntry(uuid))).map(link -> link.resolveID(uuid))
                .ifPresent(link -> validateSingleApiLink(link, resourceLinks.getDeleteLink()));

    }

    protected void validateSingleApiLink(ApiLink expectedLink, ApiLink actualLink) {
        assertThat(expectedLink.getRequestMethod()).isEqualTo(actualLink.getRequestMethod());
        assertThat(expectedLink.getLink()).isEqualTo(actualLink.getLink());
    }

    @Transactional
    protected abstract void initTestElements();

    protected abstract int getExpectedSizeForGetAllApi();

    protected abstract void validateSelectedResourceData(T data);

    protected abstract GeneralApiResource<Map<String, Object>> getSelectedGetAllApiResourceForTesting(List<GeneralApiResource<Map<String, Object>>> data);

    protected void validateSearchResultData(List<GeneralApiResource<Map<String, Object>>> data) {
        assertThat(data).hasSize(getExpectedSizeForGetAllApi());

        GeneralApiResource<Map<String, Object>> selectedResource = getSelectedGetAllApiResourceForTesting(data);
        assertThat(selectedResource.getPrimaryKey()).isNotNull();

        ResourceLinks resourceLinks = selectedResource.getResourceLinks();
        validateResourceLinksForGetAllEntry(resourceLinks, UUID.fromString(selectedResource.getPrimaryKey().toString()));

        final Map<String, Object> dataForSelectedResource = selectedResource.getData();
        final List<String> expectedGetAllDataKeys = getExpectedGetAllDataKeys();
        assertThat(dataForSelectedResource).hasSameSizeAs(expectedGetAllDataKeys);

        for (String expectedGetAllDataKey : expectedGetAllDataKeys) {
            assertThat(dataForSelectedResource).containsKey(expectedGetAllDataKey);
        }

        dataForSelectedResource.forEach(this::validateGetAllDataEntry);


    }

    protected abstract void validateGetAllDataEntry(String key, Object value);

    protected abstract List<String> getExpectedGetAllDataKeys();


    @Transactional
    @SuppressWarnings("all")
    protected void executeGenericDeleteResourceTest() {
        int currentCountOfEntries = getExpectedSizeForGetAllApi();
        initTestElements();

        UUID uuidForTesting = getUUIDForTesting();
        assertThat(getController().getEntryById(uuidForTesting)).isNotNull();
        assertThat(getSearchResults().getData()).hasSize(currentCountOfEntries);

        assertThat(getController().deleteEntry(uuidForTesting).getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(getSearchResults().getData()).hasSize(currentCountOfEntries - 1);

        assertThatThrownBy(() -> getController().getEntryById(uuidForTesting))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @SneakyThrows
    @SuppressWarnings("all")
    protected FileWebData createTestTextWebData(String fileName, String content) {
        final String mediaType = MediaType.TEXT_PLAIN_VALUE;
        final MockMultipartFile mockFile = new MockMultipartFile("file", fileName, mediaType , content.getBytes());
        final String rawContent = Base64.getEncoder().encodeToString(mockFile.getBytes());
        final String fullContent = String.format("data:%s;base64,%s", mediaType, rawContent);

        final FileWebData fileWebData = new FileWebData();
        fileWebData.setName(fileName);
        fileWebData.setBase64Content(fullContent);
        return fileWebData;
    }

}

package com.webharmony.core.api.rest;

import com.webharmony.core.AbstractCrudApiTest;
import com.webharmony.core.api.rest.controller.FileController;
import com.webharmony.core.api.rest.controller.utils.RestRequestParams;
import com.webharmony.core.api.rest.model.FileDto;
import com.webharmony.core.api.rest.model.utils.FileWebData;
import com.webharmony.core.api.rest.model.utils.ResponseResource;
import com.webharmony.core.api.rest.model.utils.apiobject.GeneralApiResource;
import com.webharmony.core.service.searchcontainer.utils.SearchResult;
import com.webharmony.core.testutils.ETestUser;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class FileControllerTest extends AbstractCrudApiTest<FileDto, FileController> {

    @Test
    @Transactional
    void shouldBeAbleToUpdateEntry() {
        initTestElements();
        UUID uuidForTesting = getUUIDForTesting();
        ResponseResource<FileDto> entryById = getController().getEntryById(uuidForTesting);

        FileDto data = entryById.getData().getData();
        data.setFileName("updatedFile.name");
        data.setIsTemp(true);

        FileDto updatedFile = getController().updateEntry(uuidForTesting, data).getData().getData();
        assertThat(updatedFile.getFileName()).isEqualTo("updatedFile.name");
        assertThat(updatedFile.getIsTemp()).isTrue();

        FileDto loadedData = getController().getEntryById(uuidForTesting).getData().getData();
        assertThat(loadedData.getFileName()).isEqualTo("updatedFile.name");
        assertThat(loadedData.getIsTemp()).isTrue();

    }

    @Test
    @Transactional
    void shouldBeAbleToDeleteEntry() {
        executeGenericDeleteResourceTest();
    }

    @Test
    @Transactional
    void shouldBeAbleToDownloadFile() throws IOException {
        initTestElements();

        ResponseEntity<SearchResult> allEntries = this.getController().getAllEntries(RestRequestParams.of(new HashMap<>()));
        SearchResult body = allEntries.getBody();
        assertThat(body).isNotNull();

        List<GeneralApiResource<Map<String, Object>>> data = body.getData();
        assertThat(data).isNotEmpty();

        GeneralApiResource<Map<String, Object>> firstEntry = data.get(0);
        UUID primaryKeyAsUUID = firstEntry.getPrimaryKeyAsUUID();
        assertThat(primaryKeyAsUUID).isNotNull();

        ResponseEntity<Resource> resourceDownload = getController().getResourceDownload(primaryKeyAsUUID);
        assertThat(Objects.requireNonNull(resourceDownload.getHeaders().get("Content-Type")).get(0)).isEqualTo(MediaType.TEXT_PLAIN_VALUE);
        assertThat(Objects.requireNonNull(resourceDownload.getHeaders().get("Content-Disposition")).get(0)).isEqualTo("hello.txt");
        assertThat(Objects.requireNonNull(resourceDownload.getBody()).getContentAsByteArray()).isNotEmpty();

    }

    @Override
    @SneakyThrows
    protected void initTestElements() {
        FileWebData fileWebData = createTestTextWebData("hello.txt", "Hello, World!");
        getController().handleFileUpload(Collections.singletonList(fileWebData));
    }

    @Override
    protected int getExpectedSizeForGetAllApi() {
        return 1;
    }

    @Override
    protected void validateSelectedResourceData(FileDto data) {
        assertThat(data.getId()).isNotNull();
        assertThat(data.getFileName()).isEqualTo("hello.txt");
        assertThat(data.getIsTemp()).isFalse();
    }

    @Override
    protected GeneralApiResource<Map<String, Object>> getSelectedGetAllApiResourceForTesting(List<GeneralApiResource<Map<String, Object>>> data) {
        return data.get(0);
    }

    @Override
    protected void validateGetAllDataEntry(String key, Object value) {

        if(key.equals("id")) {
            assertThat(value).isNotNull();
        }

        if(key.equals("fileName")) {
            assertThat(value).isEqualTo("hello.txt");
        }

        if(key.equals("isTemp")) {
            assertThat(value).isEqualTo(false);
        }

        if(key.equals("createdBy")) {
            assertThat(value).isEqualTo(ETestUser.ADMIN_USER.getEmail());
        }

        if(key.equals("createdAt")) {
            assertThat(value).isNotNull();
        }

    }

    @Override
    protected List<String> getExpectedGetAllDataKeys() {
        return List.of("id", "fileName", "isTemp", "createdAt", "createdBy");
    }
}

package com.webharmony.core.api.rest;

import com.webharmony.core.AbstractCrudApiTest;
import com.webharmony.core.TestAppInitializer;
import com.webharmony.core.api.rest.controller.SecureKeyController;
import com.webharmony.core.api.rest.model.SecureKeyDto;
import com.webharmony.core.api.rest.model.utils.ResponseResource;
import com.webharmony.core.api.rest.model.utils.apiobject.GeneralApiResource;
import com.webharmony.core.context.AppContext;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.data.enums.ECoreSecureKey;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SecureKeyControllerTest extends AbstractCrudApiTest<SecureKeyDto, SecureKeyController> {

    @Override
    @AfterEach
    protected void afterEach(TestInfo testInfo) {
        super.afterEach(testInfo);
        final AppContext appContext = ContextHolder.getContext();
        appContext.getBean(TestAppInitializer.class).initLocalDevProperties(appContext);
    }

    @Test
    void shouldBeAbleToUpdateEntry() {
        UUID uuidForTesting = getUUIDForTesting();
        ResponseResource<SecureKeyDto> entryById = getController().getEntryById(uuidForTesting);

        SecureKeyDto data = cloneKeyDto(entryById.getData().getData());
        final String oldKey = data.getKey();

        data.setName("changed name");
        data.setKey("changed key");


        SecureKeyDto updatedData = getController().updateEntry(uuidForTesting, data).getData().getData();
        assertThat(updatedData.getName()).isEqualTo(ECoreSecureKey.E_MAIL_SERVER_MAIL_ADDRESS.name());
        assertThat(updatedData.getKey()).isEqualTo(data.getKey());

        entryById.getData().getData().setKey(oldKey);
    }

    private SecureKeyDto cloneKeyDto(SecureKeyDto keyDto) {
        SecureKeyDto clone = new SecureKeyDto();
        clone.setKey(keyDto.getKey());
        clone.setName(keyDto.getName());
        clone.setId(keyDto.getId());
        return clone;
    }

    @Override
    protected void initTestElements() {
        // nothing to do here
    }

    @Override
    protected int getExpectedSizeForGetAllApi() {
        return ECoreSecureKey.values().length;
    }

    @Override
    protected void validateSelectedResourceData(SecureKeyDto data) {
        assertThat(data.getName()).isEqualTo(ECoreSecureKey.E_MAIL_SERVER_MAIL_ADDRESS.name());
        assertThat(data.getKey()).isEqualTo("*********************");
        assertThat(data.getId()).isNotNull();
    }

    @Override
    protected GeneralApiResource<Map<String, Object>> getSelectedGetAllApiResourceForTesting(List<GeneralApiResource<Map<String, Object>>> data) {
        return data.stream().filter(e -> e.getData().get("name").equals(ECoreSecureKey.E_MAIL_SERVER_MAIL_ADDRESS.name())).findAny().orElseThrow();
    }

    @Override
    protected void validateGetAllDataEntry(String key, Object value) {

        if(key.equals("id")) {
            assertThat(value).isNotNull();
        }

        if(key.equals("name")) {
            assertThat(value).isEqualTo(ECoreSecureKey.E_MAIL_SERVER_MAIL_ADDRESS.name());
        }

        if (key.equals("key")) {
            assertThat(value).isEqualTo("*********************");
        }

    }

    @Override
    protected List<String> getExpectedGetAllDataKeys() {
        return List.of("id", "name", "key", "dtoType");
    }
}

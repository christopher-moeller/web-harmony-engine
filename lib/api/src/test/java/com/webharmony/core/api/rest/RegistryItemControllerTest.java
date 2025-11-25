package com.webharmony.core.api.rest;

import com.webharmony.core.AbstractCrudApiTest;
import com.webharmony.core.api.rest.controller.RegistryItemController;
import com.webharmony.core.api.rest.model.RegistryItemDto;
import com.webharmony.core.api.rest.model.utils.ResponseResource;
import com.webharmony.core.api.rest.model.utils.apiobject.GeneralApiResource;
import com.webharmony.core.data.enums.ECoreRegistry;
import com.webharmony.core.data.jpa.model.AppRegistryItem;
import com.webharmony.core.data.jpa.model.QAppRegistryItem;
import com.webharmony.core.i18n.I18N;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RegistryItemControllerTest extends AbstractCrudApiTest<RegistryItemDto, RegistryItemController> {

    @Test
    void shouldBeAbleToUpdateEntry() {
        UUID uuidForTesting = getUUIDForTesting();
        ResponseResource<RegistryItemDto> entryById = getController().getEntryById(uuidForTesting);

        RegistryItemDto data = entryById.getData().getData();
        data.setLabel("changed label");
        data.setDescription("changed description");


        RegistryItemDto updatedData = getController().updateEntry(uuidForTesting, data).getData().getData();
        assertThat(updatedData.getLabel()).isEqualTo(data.getLabel());
        assertThat(updatedData.getDescription()).isEqualTo(data.getDescription());

    }

    @Override
    protected void initTestElements() {
        // nothing to do here
    }

    @Override
    protected int getExpectedSizeForGetAllApi() {
        return getCountOfEntities(QAppRegistryItem.appRegistryItem).intValue();
    }

    @Override
    protected void validateSelectedResourceData(RegistryItemDto data) {

        AppRegistryItem entity = ECoreRegistry.BLOCK_ALL_HTTP_REQUESTS.getEntity();

        assertThat(data.getId()).isEqualTo(entity.getUuid().toString());
        assertThat(data.getUniqueName()).isEqualTo(entity.getUniqueName());
        assertThat(data.getLabel()).isEqualTo(entity.getLabel().getValueByLanguage(I18N.CODING_LANGUAGE).orElseThrow());
        assertThat(data.getDescription()).isEqualTo(entity.getDescription().getValueByLanguage(I18N.CODING_LANGUAGE).orElseThrow());
        assertThat(data.getValue()).isEqualTo(entity.getValue());
        assertThat(data.getJavaType()).isEqualTo(entity.getJavaType());
        assertThat(data.getJavaSpecificationType()).isEqualTo(entity.getJavaTypeClass().getName());
        assertThat(data.getDefinedSelectableOptions()).isNull();

    }

    @Override
    protected GeneralApiResource<Map<String, Object>> getSelectedGetAllApiResourceForTesting(List<GeneralApiResource<Map<String, Object>>> data) {
        return data.stream().filter(e -> e.getData().get("uniqueName").equals(ECoreRegistry.BLOCK_ALL_HTTP_REQUESTS.name())).findAny().orElseThrow();
    }

    @Override
    protected void validateGetAllDataEntry(String key, Object value) {
        AppRegistryItem entity = ECoreRegistry.BLOCK_ALL_HTTP_REQUESTS.getEntity();

        if(key.equals("id")) {
            assertThat(value).isEqualTo(entity.getUuid().toString());
        }

        if(key.equals("uniqueName")) {
            assertThat(value).isEqualTo(entity.getUniqueName());
        }

        if(key.equals("label")) {
            assertThat(value).isEqualTo(entity.getLabel().getValueByLanguage(I18N.CODING_LANGUAGE).orElseThrow());
        }

        if(key.equals("description")) {
            assertThat(value).isEqualTo(entity.getDescription().getValueByLanguage(I18N.CODING_LANGUAGE).orElseThrow());
        }

        if(key.equals("value")) {
            assertThat(value).isEqualTo(entity.getValue());
        }

        if(key.equals("javaType")) {
            assertThat(value).isEqualTo(entity.getJavaType());
        }

        if(key.equals("javaSpecificationType")) {
            assertThat(value).isEqualTo(entity.getJavaTypeClass().getName());
        }

        if(key.equals("definedSelectableOptions")) {
            assertThat(value).isNull();
        }


    }

    @Override
    protected List<String> getExpectedGetAllDataKeys() {
        return List.of("id", "uniqueName", "label", "description", "value", "javaType", "javaSpecificationType", "definedSelectableOptions", "dtoType");
    }
}

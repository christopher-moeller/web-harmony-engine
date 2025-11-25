package com.webharmony.core.api.rest.user;

import com.webharmony.core.AbstractCrudApiTest;
import com.webharmony.core.api.rest.controller.user.ActorRightController;
import com.webharmony.core.api.rest.model.ActorRightDto;
import com.webharmony.core.api.rest.model.utils.ResponseResource;
import com.webharmony.core.api.rest.model.utils.apiobject.GeneralApiResource;
import com.webharmony.core.data.enums.ECoreActorRight;
import com.webharmony.core.data.jpa.model.user.AppActorRight;
import com.webharmony.core.i18n.I18N;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ActorRightControllerTest extends AbstractCrudApiTest<ActorRightDto, ActorRightController> {

    @Test
    void shouldBeAbleToUpdateEntry() {
        UUID uuidForTesting = getUUIDForTesting();
        ResponseResource<ActorRightDto> entryById = getController().getEntryById(uuidForTesting);

        ActorRightDto data = entryById.getData().getData();
        data.setLabel("updated label");
        data.setDescription("updated description");


        ActorRightDto updatedData = getController().updateEntry(uuidForTesting, data).getData().getData();
        assertThat(updatedData.getLabel()).isEqualTo(data.getLabel());
        assertThat(updatedData.getDescription()).isEqualTo(data.getDescription());

    }

    @Override
    protected void initTestElements() {
        // nothing to do here
    }

    @Override
    protected int getExpectedSizeForGetAllApi() {
        return 10;
    }

    @Override
    protected void validateSelectedResourceData(ActorRightDto data) {
        AppActorRight entity = ECoreActorRight.CORE_ACTOR_RIGHTS_CRUD.getEntity();
        assertThat(data.getLabel()).isEqualTo(entity.getLabel().getValueByLanguage(I18N.CODING_LANGUAGE).orElseThrow());
        assertThat(data.getDescription()).isEqualTo(entity.getDescription().getValueByLanguage(I18N.CODING_LANGUAGE).orElseThrow());
        assertThat(data.getIsAllowedForSystemActor()).isEqualTo(entity.getIsAllowedForSystemActor());
        assertThat(data.getIsAllowedForUnknownPublicActor()).isEqualTo(entity.getIsAllowedForUnknownPublicActor());
        assertThat(data.getId()).isEqualTo(entity.getUuid().toString());

    }

    @Override
    protected GeneralApiResource<Map<String, Object>> getSelectedGetAllApiResourceForTesting(List<GeneralApiResource<Map<String, Object>>> data) {
        UUID uuidForSelectedRight = ECoreActorRight.CORE_ACTOR_RIGHTS_CRUD.getEntity().getUuid();
        return data.stream().filter(e -> e.getPrimaryKeyAsUUID().equals(uuidForSelectedRight))
                .findAny()
                .orElseThrow();
    }

    @Override
    protected void validateGetAllDataEntry(String key, Object value) {
        if(key.equals("id")) {
            assertThat(value).isNotNull();
        }

        if(key.equals("label")) {
            assertThat(value).isEqualTo("Actor-Rights CRUD");
        }

        if(key.equals("description")) {
            assertThat(value).isEqualTo("The user is allowed to apply all CRUD operations on Actor-Rights");
        }

        if(key.equals("isAllowedForSystemActor")) {
            assertThat(value).isEqualTo(true);
        }

        if(key.equals("isAllowedForUnknownPublicActor")) {
            assertThat(value).isEqualTo(false);
        }


    }

    protected Map<String, Object> getFilterForSearchAll() {
        return Map.of("sort", "label:asc");
    }

    @Override
    protected List<String> getExpectedGetAllDataKeys() {
        return List.of("id", "label", "description");
    }
}

package com.webharmony.core.api.rest.i18n;

import com.webharmony.core.AbstractCrudApiTest;
import com.webharmony.core.api.rest.controller.i18n.I18nEntityAttributeController;
import com.webharmony.core.api.rest.model.i18n.I18nEntityAttributeDto;
import com.webharmony.core.api.rest.model.i18n.I18nTranslationDto;
import com.webharmony.core.api.rest.model.utils.ResponseResource;
import com.webharmony.core.api.rest.model.utils.apiobject.GeneralApiResource;
import com.webharmony.core.data.enums.ECoreUserRoles;
import com.webharmony.core.data.jpa.model.user.AppUserRole;
import com.webharmony.core.i18n.I18N;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class I18nEntityAttributeControllerTest extends AbstractCrudApiTest<I18nEntityAttributeDto, I18nEntityAttributeController> {

    @Test
    void shouldBeAbleToUpdateEntry() {
        UUID uuidForTesting = getUUIDForTesting();
        ResponseResource<I18nEntityAttributeDto> entryById = getController().getEntryById(uuidForTesting);

        I18nEntityAttributeDto data = entryById.getData().getData();
        data.setAttribute("changed attribute"); // should be updatedable
        data.setEntityClass("changed class"); // should be updatedable
        data.getValues().iterator().next().setTranslation("changed translation");


        I18nEntityAttributeDto updatedData = getController().updateEntry(uuidForTesting, data).getData().getData();
        assertThat(updatedData.getAttribute()).isEqualTo("label");
        assertThat(updatedData.getEntityClass()).isEqualTo(AppUserRole.class.getName());
        assertThat(updatedData.getValues().iterator().next().getTranslation()).isEqualTo("changed translation");

    }

    @Override
    protected void initTestElements() {
        // nothing to do here
    }

    @Override
    protected Map<String, Object> getFilterForSearchAll() {
        return Map.of("entityClass", "com.webharmony.core.data.jpa.model.user.AppUserRole");
    }

    @Override
    protected int getExpectedSizeForGetAllApi() {
        return ECoreUserRoles.values().length * 2;
    }

    @Override
    protected void validateSelectedResourceData(I18nEntityAttributeDto data) {
        assertThat(data.getEntityClass()).isEqualTo(AppUserRole.class.getName());
        assertThat(data.getAttribute()).isEqualTo("label");
        assertThat(data.getValues()).isNotEmpty();

        final I18nTranslationDto translationEntry = data.getValues().stream().filter(e -> e.getLanguage().equals(I18N.CODING_LANGUAGE)).findAny().orElseThrow();
        assertThat(translationEntry.getLanguage()).isEqualTo(I18N.CODING_LANGUAGE);
        assertThat(translationEntry.getTranslation()).isEqualTo(ECoreUserRoles.ADMIN_USER.getLabel());

    }

    @Override
    protected GeneralApiResource<Map<String, Object>> getSelectedGetAllApiResourceForTesting(List<GeneralApiResource<Map<String, Object>>> data) {
        final UUID adminRoleLabelUUID = ECoreUserRoles.ADMIN_USER.getEntity().getLabel().getUuid();
        return data.stream()
                .filter(e -> e.getPrimaryKeyAsUUID().equals(adminRoleLabelUUID))
                .findAny()
                .orElseThrow();
    }

    @Override
    protected void validateGetAllDataEntry(String key, Object value) {

        if(key.equals("id")) {
            assertThat(value).isNotNull();
        }

        if(key.equals("entityClass")) {
            assertThat(value).isEqualTo(AppUserRole.class.getName());
        }

        if(key.equals("attributeName")) {
            assertThat(value).isEqualTo("label");
        }

        if(key.equals("isCoreEntry")) {
            assertThat(value).isEqualTo(true);
        }

    }

    @Override
    protected List<String> getExpectedGetAllDataKeys() {
        return List.of("id", "entityClass", "attributeName", "countOfTranslations", "isCoreEntry");
    }
}

package com.webharmony.core.api.rest.i18n;

import com.webharmony.core.AbstractCrudApiTest;
import com.webharmony.core.api.rest.controller.i18n.I18nKeyEntryController;
import com.webharmony.core.api.rest.model.i18n.I18nFrontendTranslation;
import com.webharmony.core.api.rest.model.i18n.I18nFrontendTranslationEntry;
import com.webharmony.core.api.rest.model.i18n.I18nKeyEntryDto;
import com.webharmony.core.api.rest.model.utils.ResponseResource;
import com.webharmony.core.api.rest.model.utils.apiobject.GeneralApiResource;
import com.webharmony.core.i18n.EI18nCodeLocation;
import com.webharmony.core.i18n.EI18nLanguage;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class I18nKeyEntryControllerTest extends AbstractCrudApiTest<I18nKeyEntryDto, I18nKeyEntryController> {


    @Test
    void shouldBeAbleToUpdateEntry() {
        UUID uuidForTesting = getUUIDForTesting();
        ResponseResource<I18nKeyEntryDto> entryById = getController().getEntryById(uuidForTesting);

        I18nKeyEntryDto data = entryById.getData().getData();
        data.setKey("changed key"); // should be updatedable
        data.setPlaceholders("changed placeholders"); // should be updatedable
        data.setClassId("changed class id"); // should be updatedable


        I18nKeyEntryDto updatedData = getController().updateEntry(uuidForTesting, data).getData().getData();
        assertThat(updatedData.getKey()).isEqualTo("Notimplemented{test}yet");
        assertThat(updatedData.getClassId()).isEqualTo("com.webharmony.core.api.rest.controller.utils.dispacher.EntityCrudDispatcher");
        assertThat(updatedData.getPlaceholders()).isEqualTo("test");

    }

    @Test
    void shouldGetAllFrontendTranslations() {

        final I18nFrontendTranslation i18nFrontendTranslation = assertOkResponse(c -> c.getFrontendTranslation(EI18nLanguage.ENGLISH.getKey()));
        assertThat(i18nFrontendTranslation.getLanguage()).isEqualTo(EI18nLanguage.ENGLISH);
        assertThat(i18nFrontendTranslation.getTranslationEntries()).hasSize(3);

        final I18nFrontendTranslationEntry translationEntry = i18nFrontendTranslation.getTranslationEntries().iterator().next();
        translationEntry.setClassId("UserOverview");
        translationEntry.setKeyId("E-Mail");

        assertThat(translationEntry.getClassId()).isEqualTo("UserOverview");
        assertThat(translationEntry.getKeyId()).isEqualTo("E-Mail");
        assertThat(translationEntry.getValue()).isEqualTo("E-Mail");
    }

    @Override
    protected void initTestElements() {
        // nothing to do here
    }

    @Override
    protected int getExpectedSizeForGetAllApi() {
        return 3;
    }

    @Override
    protected void validateSelectedResourceData(I18nKeyEntryDto data) {
        assertThat(data.getId()).isNotNull();
        assertThat(data.getClassId()).isEqualTo("com.webharmony.core.api.rest.controller.utils.dispacher.EntityCrudDispatcher");
        assertThat(data.getKey()).isEqualTo("Notimplemented{test}yet");
        assertThat(data.getPlaceholders()).isEqualTo("test");
    }

    @Override
    protected GeneralApiResource<Map<String, Object>> getSelectedGetAllApiResourceForTesting(List<GeneralApiResource<Map<String, Object>>> data) {
        return data.stream()
                .filter(e -> e.getData().get("classId").equals("com.webharmony.core.api.rest.controller.utils.dispacher.EntityCrudDispatcher") && e.getData().get("key").equals("Notimplemented{test}yet"))
                .findAny()
                .orElseThrow();
    }

    @Override
    protected void validateGetAllDataEntry(String key, Object value) {

        if(key.equals("id")) {
            assertThat(value).isNotNull();
        }

        if(key.equals("classId")) {
            assertThat(value).isEqualTo("com.webharmony.core.api.rest.controller.utils.dispacher.EntityCrudDispatcher");
        }

        if(key.equals("key")) {
            assertThat(value).isEqualTo("Notimplemented{test}yet");
        }

        if(key.equals("codeLocation")) {
            assertThat(value).isEqualTo(EI18nCodeLocation.BACKEND.toString());
        }

        if(key.equals("countOfTranslations")) {
            assertThat(value).isEqualTo(2L);
        }

        if(key.equals("isCoreEntry")) {
            assertThat(value).isEqualTo(true);
        }

    }

    @Override
    protected List<String> getExpectedGetAllDataKeys() {
        return List.of("id", "classId", "key", "codeLocation", "countOfTranslations", "isCoreEntry");
    }
}

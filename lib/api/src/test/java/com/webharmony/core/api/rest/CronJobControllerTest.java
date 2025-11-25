package com.webharmony.core.api.rest;

import com.webharmony.core.AbstractCrudApiTest;
import com.webharmony.core.api.rest.controller.CronJobController;
import com.webharmony.core.api.rest.model.CronJobDto;
import com.webharmony.core.api.rest.model.utils.ResponseResource;
import com.webharmony.core.api.rest.model.utils.apiobject.GeneralApiResource;
import com.webharmony.core.service.cron.AbstractCronJob;
import com.webharmony.core.service.cron.jobs.TokenCleanCronJob;
import com.webharmony.core.utils.reflection.ReflectionUtils;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CronJobControllerTest extends AbstractCrudApiTest<CronJobDto, CronJobController> {


    private static final String LABEL = "Token Clean Cron Job";
    private static final String DESCRIPTION = "This job cleans all expired tokens";
    private static final String JAVA_CLASS = TokenCleanCronJob.class.getName();
    private static final String LAST_EXECUTED_AT = null;
    private static final Boolean IS_ACTIVATED = true;
    private static final Boolean IS_CURRENTLY_RUNNING = false;
    private static final String CRON_TRIGGER = "0 0 1 * * *";

    @Test
    void shouldBeAbleToUpdateEntry() {
        UUID uuidForTesting = getUUIDForTesting();
        ResponseResource<CronJobDto> entryById = getController().getEntryById(uuidForTesting);

        CronJobDto data = entryById.getData().getData();
        data.setLabel("updated label");
        data.setDescription("updated description");
        data.setCronTrigger("0 0 1 * * *");


        CronJobDto updatedData = getController().updateEntry(uuidForTesting, data).getData().getData();
        assertThat(updatedData.getLabel()).isEqualTo(data.getLabel());
        assertThat(updatedData.getDescription()).isEqualTo(data.getDescription());
        assertThat(updatedData.getCronTrigger()).isEqualTo(data.getCronTrigger());

    }

    @Test
    void shouldBeAbleToExecuteCronJob() {
        UUID uuidForTesting = getUUIDForTesting();
        assertOkResponse(c -> c.execute(uuidForTesting));
    }

    @Override
    protected void initTestElements() {
        // nothing to do, because cron jobs are not already created
    }

    @Override
    protected int getExpectedSizeForGetAllApi() {
        return ReflectionUtils.getAllProjectClassesImplementingSuperClass(AbstractCronJob.class).size();
    }

    @Override
    protected void validateSelectedResourceData(CronJobDto data) {
        assertThat(data.getId()).isNotNull();
        assertThat(data.getLabel()).isEqualTo(LABEL);
        assertThat(data.getDescription()).isEqualTo(DESCRIPTION);
        assertThat(data.getJavaClass()).isEqualTo(JAVA_CLASS);
        assertThat(data.getLastExecutedAt()).isEqualTo(LAST_EXECUTED_AT);
        assertThat(data.getIsActivated()).isEqualTo(IS_ACTIVATED);
        assertThat(data.getIsCurrentlyRunning()).isEqualTo(IS_CURRENTLY_RUNNING);
        assertThat(data.getCronTrigger()).isEqualTo(CRON_TRIGGER);
    }

    @Override
    protected GeneralApiResource<Map<String, Object>> getSelectedGetAllApiResourceForTesting(List<GeneralApiResource<Map<String, Object>>> data) {
        return data.stream()
                .filter(e -> e.getData().get("javaClass").equals(TokenCleanCronJob.class.getName()))
                .findAny()
                .orElseThrow();
    }



    @Override
    protected void validateGetAllDataEntry(String key, Object value) {

        if(key.equals("id")) {
            assertThat(value).isNotNull();
        }

        if(key.equals("label")) {
            assertThat(value).isEqualTo(LABEL);
        }

        if(key.equals("description")) {
            assertThat(value).isEqualTo(DESCRIPTION);
        }

        if(key.equals("javaClass")) {
            assertThat(value).isEqualTo(JAVA_CLASS);
        }

        if(key.equals("lastExecutedAt")) {
            assertThat(value).isEqualTo(LAST_EXECUTED_AT);
        }

        if(key.equals("isActivated")) {
            assertThat(value).isEqualTo(IS_ACTIVATED);
        }

        if(key.equals("isCurrentlyRunning")) {
            assertThat(value).isEqualTo(IS_CURRENTLY_RUNNING);
        }

        if(key.equals("cronTrigger")) {
            assertThat(value).isEqualTo(CRON_TRIGGER);
        }

    }

    @Override
    protected List<String> getExpectedGetAllDataKeys() {
        return List.of("id", "label", "description", "javaClass", "lastExecutedAt", "isActivated", "isCurrentlyRunning", "cronTrigger", "dtoType");
    }

}

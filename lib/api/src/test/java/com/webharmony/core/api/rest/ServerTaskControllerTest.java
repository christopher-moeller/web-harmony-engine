package com.webharmony.core.api.rest;

import com.webharmony.core.AbstractCrudApiTest;
import com.webharmony.core.api.rest.controller.ServerTaskController;
import com.webharmony.core.api.rest.model.ServerTaskDto;
import com.webharmony.core.api.rest.model.utils.ResponseResource;
import com.webharmony.core.api.rest.model.utils.apiobject.GeneralApiResource;
import com.webharmony.core.data.jpa.model.QAppServerTask;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ServerTaskControllerTest extends AbstractCrudApiTest<ServerTaskDto, ServerTaskController> {


    @Test
    void shouldExecuteServerTask() {
        assertOkResponse(c -> c.executeByUUID(getUUIDForTesting()));
    }

    @Test
    void shouldBeAbleToUpdateEntry() {
        UUID uuidForTesting = getUUIDForTesting();
        ResponseResource<ServerTaskDto> entryById = getController().getEntryById(uuidForTesting);

        ServerTaskDto data = entryById.getData().getData();
        data.setTaskName("changedName");
        data.setTaskId("bla"); // should not be updated
        data.setLastExecution("updated"); // should not be updated


        ServerTaskDto updatedData = getController().updateEntry(uuidForTesting, data).getData().getData();
        assertThat(updatedData.getTaskName()).isEqualTo(data.getTaskName());
        assertThat(updatedData.getTaskId()).isNotEqualTo(data.getTaskName());
        assertThat(updatedData.getLastExecution()).isNotEqualTo(data.getLastExecution());

    }

    @Override
    protected void initTestElements() {
        // nothing to do here
    }

    @Override
    protected int getExpectedSizeForGetAllApi() {
        return getCountOfEntities(QAppServerTask.appServerTask).intValue();
    }

    @Override
    protected void validateSelectedResourceData(ServerTaskDto data) {
        assertThat(data.getId()).isNotNull();
        assertThat(data.getTaskId()).isEqualTo("com.webharmony.core.service.tasks.CoreServerTasks:clearHarmonyEvents");
        assertThat(data.getTaskName()).isEqualTo("Clear harmony events");
        assertThat(data.getLastExecution()).isEqualTo("Not executed yet");
        assertThat(data.getIsRequired()).isFalse();
    }

    @Override
    protected GeneralApiResource<Map<String, Object>> getSelectedGetAllApiResourceForTesting(List<GeneralApiResource<Map<String, Object>>> data) {
        return data.stream()
                .filter(e -> e.getData().get("taskId").equals("com.webharmony.core.service.tasks.CoreServerTasks:clearHarmonyEvents"))
                .findAny()
                .orElseThrow();
    }

    @Override
    protected void validateGetAllDataEntry(String key, Object value) {
        if(key.equals("id")) {
            assertThat(value).isNotNull();
        }

        if(key.equals("isRequired")) {
            assertThat(value).isEqualTo(false);
        }

        if(key.equals("lastExecution")) {
            assertThat(value).isEqualTo("Not executed yet");
        }

        if(key.equals("taskId")) {
            assertThat(value).isEqualTo("com.webharmony.core.service.tasks.CoreServerTasks:clearHarmonyEvents");
        }

        if(key.equals("taskName")) {
            assertThat(value).isEqualTo("Clear harmony events");
        }
    }

    @Override
    protected List<String> getExpectedGetAllDataKeys() {
        return List.of("id", "isRequired", "lastExecution", "taskId", "taskName", "dtoType");
    }
}

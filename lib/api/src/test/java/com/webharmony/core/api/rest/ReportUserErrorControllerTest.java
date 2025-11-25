package com.webharmony.core.api.rest;

import com.webharmony.core.AbstractApiTest;
import com.webharmony.core.api.rest.controller.error.ReportUserErrorController;
import com.webharmony.core.api.rest.error.validation.ValidationResultDto;
import com.webharmony.core.api.rest.model.ReportUserBugRequest;
import com.webharmony.core.api.rest.model.utils.FileWebData;
import com.webharmony.core.data.jpa.model.error.AppReportedUserError;
import com.webharmony.core.data.jpa.model.files.AppFile;
import com.webharmony.core.service.error.ReportedUserErrorCrudService;
import com.webharmony.core.utils.TestValidationErrorConstants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ReportUserErrorControllerTest extends AbstractApiTest<ReportUserErrorController> {

    @Autowired
    private ReportedUserErrorCrudService reportedUserErrorCrudService;

    @Test
    void shouldPersistANewUserBugRequest() {
        final ReportUserBugRequest body = new ReportUserBugRequest();
        body.setPage("Test");
        body.setDescription("Test description");
        body.setAttachments(createListOfFileWebData(2, createValidFileContent()));
        executeMethodAsRestCall(c -> c.reportUserBug(body), Void.class, HttpStatus.OK);


        final List<AppReportedUserError> allEntities = reportedUserErrorCrudService.getAllEntities();
        assertThat(allEntities).hasSize(1);

        final AppReportedUserError entity = allEntities.get(0);
        assertThat(entity.getPage()).isEqualTo(body.getPage());
        assertThat(entity.getDescription()).isEqualTo(body.getDescription());

        final Set<AppFile> entityFiles = entity.getAttachments();
        assertThat(entityFiles).hasSize(2);

        final AppFile fileEntity = entityFiles.stream().filter(f -> f.getFileName().equals("File0"))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format("No file with name '%s' found", "File0")));

        assertThat(fileEntity.getFileAsByte()).isNotEmpty();
    }

    @Test
    void shouldThrowValidationErrorBecauseOfMissingDescription() {
        final ReportUserBugRequest body = new ReportUserBugRequest();
        ValidationResultDto validationResultDto = executeMethodAsRestCallAndExpectValidationError(c -> c.reportUserBug(body));
        assertValidationResultAsFieldError(validationResultDto, "description", TestValidationErrorConstants.EMPTY_TEXT_VALUE);
    }

    @Test
    void shouldThrowValidationErrorBecauseOfInvalidAttachments() {
        final ReportUserBugRequest body = new ReportUserBugRequest();

        body.setAttachments(createListOfFileWebData(5, createToLargeFileContent()));
        ValidationResultDto validationResultDto = executeMethodAsRestCallAndExpectValidationError(c -> c.reportUserBug(body));
        assertValidationResultAsFieldError(validationResultDto, "attachments", TestValidationErrorConstants.WEB_DATA_OVERALL_TO_LARGE);
        assertValidationResultAsFieldError(validationResultDto, "attachments[0]", TestValidationErrorConstants.WEB_DATA_TO_LARGE);
        assertValidationResultAsFieldError(validationResultDto, "attachments[1]", TestValidationErrorConstants.WEB_DATA_TO_LARGE);
        assertValidationResultAsFieldError(validationResultDto, "attachments[2]", TestValidationErrorConstants.WEB_DATA_TO_LARGE);
        assertValidationResultAsFieldError(validationResultDto, "attachments[3]", TestValidationErrorConstants.WEB_DATA_TO_LARGE);
        assertValidationResultAsFieldError(validationResultDto, "attachments[4]", TestValidationErrorConstants.WEB_DATA_TO_LARGE);
    }

    private List<FileWebData> createListOfFileWebData(int size, String data) {
        final List<FileWebData> result = new ArrayList<>();
        for(int i = 0; i < size; i++) {
            FileWebData fileWebData = new FileWebData();
            fileWebData.setName("File"+i);
            fileWebData.setBase64Content(data);
            result.add(fileWebData);
        }
        return result;
    }

    private String createToLargeFileContent() {
        final StringBuilder builder = new StringBuilder();
        for(long i = 0; i < 4000001L; i++) {
            builder.append("T");
        }
        return "testType," + Base64.getEncoder().encodeToString(builder.toString().getBytes());
    }

    private String createValidFileContent() {
        final StringBuilder builder = new StringBuilder();
        for(long i = 0; i < 100L; i++) {
            builder.append("T");
        }
        return "testType," + Base64.getEncoder().encodeToString(builder.toString().getBytes());
    }

}
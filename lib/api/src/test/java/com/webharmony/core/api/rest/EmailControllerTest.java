package com.webharmony.core.api.rest;

import com.webharmony.core.AbstractCrudApiTest;
import com.webharmony.core.api.rest.controller.EmailController;
import com.webharmony.core.api.rest.model.EmailDto;
import com.webharmony.core.api.rest.model.utils.ResponseResource;
import com.webharmony.core.api.rest.model.utils.apiobject.GeneralApiResource;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.data.jpa.model.email.AppEmail;
import com.webharmony.core.data.jpa.model.email.EMailState;
import com.webharmony.core.service.data.EmailCrudService;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EmailControllerTest extends AbstractCrudApiTest<EmailDto, EmailController> {

    @Test
    void shouldBeAbleToCreateNewEmail() {

        EmailDto emailDto = new EmailDto();
        emailDto.setFromEmail("should.not.be@persisted.com");
        emailDto.setToEmail("testmail@@example.com");
        emailDto.setSubject("Test Subject");
        emailDto.setHtmlMessage("<p>Test Content</p>");

        ResponseResource<EmailDto> newEntry = getController().createNewEntry(emailDto);
        EmailDto data = newEntry.getData().getData();
        validateSelectedResourceData(data);

        ResponseResource<EmailDto> freshLoadedData = getController().getEntryById(newEntry.getData().getPrimaryKeyAsUUID());
        validateSelectedResourceData(freshLoadedData.getData().getData());

    }

    @Test
    @Transactional
    void shouldBeAbleToUpdateEntry() {
        initTestElements();
        UUID uuidForTesting = getUUIDForTesting();
        ResponseResource<EmailDto> entryById = getController().getEntryById(uuidForTesting);

        EmailDto data = entryById.getData().getData();
        data.setToEmail("updated@email.com");
        data.setSubject("updated subject");
        data.setHtmlMessage("updated message");

        data.setState(EMailState.SEND); // should be updatedable


        EmailDto updatedData = getController().updateEntry(uuidForTesting, data).getData().getData();
        assertThat(updatedData.getToEmail()).isEqualTo("updated@email.com");
        assertThat(updatedData.getSubject()).isEqualTo("updated subject");
        assertThat(updatedData.getHtmlMessage()).isEqualTo("updated message");
        assertThat(updatedData.getState()).isEqualTo(EMailState.CREATED);

    }

    @Test
    @Transactional
    void shouldBeAbleToDeleteEntry() {
        executeGenericDeleteResourceTest();
    }

    @Test
    @Transactional
    void shouldNotBeAbleToSendEmail() {
        initTestElements();
        assertThat(getController().sendEmail(getUUIDForTesting()).getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Override
    protected void initTestElements() {
        EmailCrudService emailCrudService = ContextHolder.getContext().getBean(EmailCrudService.class);

        AppEmail appEmail = new AppEmail();
        appEmail.setState(EMailState.CREATED);
        appEmail.setFromEmail("testmail@@example.com");
        appEmail.setToEmail("testmail@@example.com");
        appEmail.setSubject("Test Subject");
        appEmail.setHtmlMessage("<p>Test Content</p>");


        emailCrudService.saveEntity(appEmail);
    }

    @Override
    protected int getExpectedSizeForGetAllApi() {
        return 1;
    }

    @Override
    protected void validateSelectedResourceData(EmailDto data) {
        assertThat(data.getState()).isEqualTo(EMailState.CREATED);
        assertThat(data.getFromEmail()).isEqualTo("testmail@@example.com");
        assertThat(data.getToEmail()).isEqualTo("testmail@@example.com");
        assertThat(data.getSubject()).isEqualTo("Test Subject");
        assertThat(data.getHtmlMessage()).isEqualTo("<p>Test Content</p>");
        assertThat(data.getEventLog()).isNull();
        assertThat(data.getLastSending()).isEqualTo("-");
        assertThat(data.getAttachments()).isEmpty();
        assertThat(data.getId()).isNotEmpty();
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

        if(key.equals("state")) {
            assertThat(value).isEqualTo(EMailState.CREATED);
        }

        if(key.equals("fromEmail")) {
            assertThat(value).isEqualTo("testmail@@example.com");
        }

        if(key.equals("toEmail")) {
            assertThat(value).isEqualTo("testmail@@example.com");
        }

        if(key.equals("subject")) {
            assertThat(value).isEqualTo("Test Subject");
        }

        if(key.equals("lastSending")) {
            assertThat(value).isNull();
        }

        if(key.equals("createdAt")) {
            assertThat(value).isNotNull();
        }
    }

    @Override
    protected List<String> getExpectedGetAllDataKeys() {
        return Arrays.asList("id", "state", "fromEmail", "toEmail", "subject", "lastSending", "createdAt");
    }
}

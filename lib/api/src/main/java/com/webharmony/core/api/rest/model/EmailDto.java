package com.webharmony.core.api.rest.model;

import com.webharmony.core.api.rest.model.utils.AbstractResourceDto;
import com.webharmony.core.api.rest.model.utils.annotations.ReadOnlyAttribute;
import com.webharmony.core.api.rest.model.utils.apiobject.ApiResource;
import com.webharmony.core.api.rest.validation.EmailDtoValidation;
import com.webharmony.core.data.enums.ECoreSecureKey;
import com.webharmony.core.data.jpa.model.email.EMailState;
import com.webharmony.core.service.data.validation.utils.Validation;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Validation(EmailDtoValidation.class)
public class EmailDto extends AbstractResourceDto {

    @ReadOnlyAttribute
    private EMailState state;
    @ReadOnlyAttribute
    private String fromEmail;
    @ReadOnlyAttribute
    private String createdAt;

    private String toEmail;
    private String subject;
    private String htmlMessage;

    @ReadOnlyAttribute
    private String eventLog;
    @ReadOnlyAttribute
    private String lastSending;

    public static EmailDto createInitialTemplate() {
        EmailDto dto = new EmailDto();
        dto.setFromEmail(ECoreSecureKey.E_MAIL_SERVER_MAIL_ADDRESS.getNotEmptyKey());
        return dto;
    }

    private List<ApiResource<FileDto>> attachments;

}

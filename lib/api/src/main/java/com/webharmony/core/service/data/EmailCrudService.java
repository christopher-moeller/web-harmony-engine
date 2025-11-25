package com.webharmony.core.service.data;

import com.webharmony.core.api.rest.jackson.CustomLocalDateTimeSerializer;
import com.webharmony.core.api.rest.model.EmailDto;
import com.webharmony.core.api.rest.model.FileDto;
import com.webharmony.core.api.rest.model.utils.apiobject.ApiResource;
import com.webharmony.core.data.enums.ECoreSecureKey;
import com.webharmony.core.data.jpa.model.email.AppEmail;
import com.webharmony.core.data.jpa.model.email.EMailState;
import com.webharmony.core.data.jpa.model.files.AppFile;
import com.webharmony.core.service.data.mapper.GenericMappingUtils;
import com.webharmony.core.service.data.mapper.MappingConfiguration;
import com.webharmony.core.service.data.utils.AbstractEntityCrudService;
import com.webharmony.core.utils.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class EmailCrudService extends AbstractEntityCrudService<EmailDto, AppEmail> {

    @Override
    protected void configureMapping(MappingConfiguration<EmailDto, AppEmail> mappingConfiguration) {
        mappingConfiguration.withExtendedToEntityMapper((dto, entity, context) -> {
            entity.setFromEmail(ECoreSecureKey.E_MAIL_SERVER_MAIL_ADDRESS.getNotEmptyKey());
            if(entity.getState() == null)
                entity.setState(EMailState.CREATED);
            return entity;
        }).withExtendedToDtoMapper((entity, dto, mappingContext) -> {
            final String lastSending = Optional.ofNullable(entity.getLastSending())
                    .map(CustomLocalDateTimeSerializer::parseDateToString)
                    .orElse("-");

            dto.setLastSending(lastSending);

            final String createdAt = Optional.ofNullable(entity.getCreatedAt())
                    .map(CustomLocalDateTimeSerializer::parseDateToString)
                    .orElse("-");

            dto.setCreatedAt(createdAt);

            dto.setAttachments(mapToAttachmentsWithData(entity.getAttachments()));
            return dto;
        });
    }

    private List<ApiResource<FileDto>> mapToAttachmentsWithData(Set<AppFile> appFiles) {
        if(CollectionUtils.isNullOrEmpty(appFiles))
            return Collections.emptyList();

        return appFiles.stream()
                .map(this::mapAttachmentWithData)
                .toList();
    }

    private ApiResource<FileDto> mapAttachmentWithData(AppFile appFile) {
        ApiResource<FileDto> apiResource = GenericMappingUtils.createSimpleReferenceApiObjectByEntity(appFile);

        FileDto data = new FileDto();
        data.setId(String.valueOf(appFile.getUuid()));
        data.setFileName(appFile.getFileName());
        data.setIsTemp(appFile.getIsTemp());

        apiResource.setData(data);

        return apiResource;
    }
}

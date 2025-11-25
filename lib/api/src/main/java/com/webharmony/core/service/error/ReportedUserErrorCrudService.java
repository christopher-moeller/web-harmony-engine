package com.webharmony.core.service.error;

import com.webharmony.core.api.rest.controller.FileController;
import com.webharmony.core.api.rest.controller.utils.request.RequestContext;
import com.webharmony.core.api.rest.model.ReportUserBugRequest;
import com.webharmony.core.api.rest.model.error.ReportedUserErrorDto;
import com.webharmony.core.api.rest.model.utils.FileWebData;
import com.webharmony.core.data.jpa.model.error.AppReportedUserError;
import com.webharmony.core.data.jpa.model.files.AppFile;
import com.webharmony.core.service.data.FileService;
import com.webharmony.core.service.data.mapper.GenericMappingUtils;
import com.webharmony.core.service.data.mapper.MappingConfiguration;
import com.webharmony.core.service.data.utils.AbstractEntityCrudService;
import com.webharmony.core.service.data.validation.ValidatorService;
import com.webharmony.core.utils.CollectionUtils;
import com.webharmony.core.utils.reflection.ApiLink;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReportedUserErrorCrudService extends AbstractEntityCrudService<ReportedUserErrorDto, AppReportedUserError> {

    private final FileService fileService;

    private final ValidatorService validatorService;

    public ReportedUserErrorCrudService(FileService fileService, ValidatorService validatorService) {
        this.fileService = fileService;
        this.validatorService = validatorService;
    }

    @Override
    protected void configureMapping(MappingConfiguration<ReportedUserErrorDto, AppReportedUserError> mappingConfiguration) {
        mappingConfiguration.withExtendedToDtoMapper((entity, dto, context) -> {
            dto.setAttachments(GenericMappingUtils.mapAppFileToWebData(entity.getAttachments(), file -> ApiLink.of(FileController.class, c -> c.getResourceDownload(file.getUuid()))));
            return dto;
        });

        mappingConfiguration.withExtendedToEntityMapper((dto, entity, context) -> {
            entity.setAttachments(GenericMappingUtils.mapWebDataToAppFiles(dto.getAttachments()));
            return entity;
        });
    }

    @Transactional
    public void reportUserBug(ReportUserBugRequest userBugRequest) {
        AppReportedUserError appReportedUserError = new AppReportedUserError();
        appReportedUserError.setPage(userBugRequest.getPage());
        appReportedUserError.setDescription(userBugRequest.getDescription());

        validatorService.validate(userBugRequest);
        appReportedUserError.setAttachments(CollectionUtils.emptyListIfNull(userBugRequest.getAttachments())
                .stream()
                .map(fileService::buildAppFileByWebData)
                .collect(Collectors.toSet())
        );

        saveEntity(appReportedUserError);
    }

    @Override
    @Transactional
    public ReportedUserErrorDto updateEntry(UUID uuid, ReportedUserErrorDto dto, RequestContext requestContext) {
        final AppReportedUserError entityById = getEntityById(uuid);

        // delete removed files
        final List<AppFile> filesToDelete = new ArrayList<>();
        for (AppFile appFile : CollectionUtils.emptySetIfNull(entityById.getAttachments())) {
            final FileWebData fileWebData = CollectionUtils.emptyListIfNull(dto.getAttachments())
                    .stream()
                    .filter(a -> Objects.equals(a.getId(), appFile.getUuid().toString()))
                    .findAny()
                    .orElse(null);

            if(fileWebData == null) {
                filesToDelete.add(appFile);
            }
        }

        final ReportedUserErrorDto result = super.updateEntry(uuid, dto, requestContext);
        filesToDelete.forEach(fileService::deleteEntity);

        return result;
    }

    @Override
    @Transactional
    public void deleteEntity(AppReportedUserError entity) {
        CollectionUtils.emptySetIfNull(entity.getAttachments()).forEach(fileService::deleteEntity);
        super.deleteEntity(entity);
    }
}

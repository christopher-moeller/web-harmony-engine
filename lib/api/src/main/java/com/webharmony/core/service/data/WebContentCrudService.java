package com.webharmony.core.service.data;

import com.querydsl.jpa.impl.JPAQuery;
import com.webharmony.core.api.rest.controller.utils.request.RequestContext;
import com.webharmony.core.api.rest.controller.webcontent.WebContentController;
import com.webharmony.core.api.rest.model.utils.FileWebData;
import com.webharmony.core.api.rest.model.webcontent.WebContentDto;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.data.jpa.model.files.AppFile;
import com.webharmony.core.data.jpa.model.files.QAppFile;
import com.webharmony.core.data.jpa.model.user.AppActorRight;
import com.webharmony.core.data.jpa.model.webcontent.AppWebContent;
import com.webharmony.core.data.jpa.model.webcontent.AppWebContentArea;
import com.webharmony.core.data.jpa.model.webcontent.QAppWebContent;
import com.webharmony.core.service.data.mapper.MappingConfiguration;
import com.webharmony.core.service.data.utils.AbstractEntityCrudService;
import com.webharmony.core.service.webcontent.model.AbstractWebContent;
import com.webharmony.core.service.webcontent.model.AbstractWebContentWithChildren;
import com.webharmony.core.service.webcontent.model.ImageWebContent;
import com.webharmony.core.utils.CollectionUtils;
import com.webharmony.core.utils.JacksonUtils;
import com.webharmony.core.utils.exceptions.NotFoundException;
import com.webharmony.core.utils.reflection.ApiLink;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
public class WebContentCrudService extends AbstractEntityCrudService<WebContentDto, AppWebContent> {

    private static final QAppWebContent qAppWebContent = QAppWebContent.appWebContent;

    private final FileService fileService;
    private final ActorRightService actorRightService;
    private final WebContentAreaService webContentAreaService;

    @Value("classpath:webcontent/default/**/*.json")
    private Resource[] defaultWebContents;

    @PersistenceContext
    private EntityManager em;

    public WebContentCrudService(FileService fileService, ActorRightService actorRightService, WebContentAreaService webContentAreaService) {
        this.fileService = fileService;
        this.actorRightService = actorRightService;
        this.webContentAreaService = webContentAreaService;
    }

    @Override
    protected void configureMapping(MappingConfiguration<WebContentDto, AppWebContent> mappingConfiguration) {
        mappingConfiguration.withExtendedToEntityMapper((dto, entity, context) -> {
            final AbstractWebContent content = dto.getContent();
            final Set<AppFile> imageFiles = resolveImagesAndBuildFileList(content);
            entity.setFiles(imageFiles);
            entity.setJsonContent(convertToJsonString(content));
            return entity;
        });

        mappingConfiguration.withExtendedToDtoMapper((entity, dto, context) -> {
            dto.setContent(convertStringToContent(entity.getJsonContent()));
            return dto;
        });
    }

    @SneakyThrows
    private String convertToJsonString(AbstractWebContent content) {
        return content != null ? JacksonUtils.createDefaultJsonMapper().writeValueAsString(content) : null;
    }

    private Set<AppFile> resolveImagesAndBuildFileList(AbstractWebContent content) {
        final HashSet<AppFile> resultSet = new HashSet<>();
        final List<ImageWebContent> imageWebContents = findAllInstancesByType(content, ImageWebContent.class);
        for (ImageWebContent imageWebContent : imageWebContents) {
            final FileWebData fileWebData = imageWebContent.getFileWebData();
            final AppFile appFile = fileService.createNewFileByUpload(fileWebData, false);
            fileWebData.setDownloadLink(buildApiLinkForImage(appFile));
            fileWebData.setSimpleDownloadLink(fileWebData.getDownloadLink().getLink());
            fileWebData.setBase64Content(null);
            fileWebData.setId(appFile.getUuid().toString());
            resultSet.add(appFile);
        }
        return resultSet;
    }

    private AbstractWebContent convertStringToContent(String jsonContent) {
        if(jsonContent == null) {
            return null;
        }
        try {
            return JacksonUtils.createDefaultJsonMapper().readValue(jsonContent, AbstractWebContent.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private ApiLink buildApiLinkForImage(AppFile imageFile) {
        return ApiLink.of(WebContentController.class, c -> c.getFile(null, imageFile.getUuid()));
    }

    @SuppressWarnings("unchecked")
    private <T extends AbstractWebContent> List<T> findAllInstancesByType(AbstractWebContent root, Class<T> type) {
        final List<T> resultList = new ArrayList<>();

        if(type.equals(root.getClass())) {
            resultList.add((T) root);
        }

        if(root instanceof AbstractWebContentWithChildren webContentWithChildren) {
            for (AbstractWebContent child : CollectionUtils.emptyListIfNull(webContentWithChildren.getChildren())) {
                resultList.addAll(findAllInstancesByType(child, type));
            }
        }

        return resultList;
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Resource> getFileByWebContent(UUID webContentUUID, UUID fileUUID) {
        final AppWebContent webContent = this.getEntityById(webContentUUID);
        ensureAccessRightsForWebContent(webContent, true, false);

        final AppFile appFile = new JPAQuery<>(em).select(QAppFile.appFile)
                .from(QAppWebContent.appWebContent)
                .join(QAppWebContent.appWebContent.files, QAppFile.appFile)
                .where(QAppWebContent.appWebContent.uuid.eq(webContentUUID).and(QAppFile.appFile.uuid.eq(fileUUID)))
                .fetchOne();

        if(appFile == null) {
            throw new NotFoundException(String.format("File with uuid %s not found for web content with uuid %s", webContentUUID, fileUUID));
        }

        return fileService.getResourceAsResponseEntityByFile(appFile);
    }

    private void ensureAccessRightsForWebContent(AppWebContent webContent, boolean ensureRead, boolean ensureWrite) {
        final AppWebContentArea area = webContent.getArea();
        if(ensureRead) {
            ensureForOptionalRight(area.getRequiredReadRight());
        }
        if (ensureWrite) {
            ensureForOptionalRight(area.getRequiredWriteRight());
        }
    }

    private void ensureForOptionalRight(AppActorRight requiredRight) {
        if(requiredRight != null) {
            actorRightService.getApplicationRightByEntity(requiredRight)
                    .ifPresent(right -> ContextHolder.getContext().assertCurrentActorHasRight(right));

        }
    }

    @Transactional(readOnly = true)
    public AppWebContent getUniqueWebContentEntityByArea(String uniqueAreaName) {
        final AppWebContentArea webContentArea = webContentAreaService.findAreaByUniqueNameOrThrow(uniqueAreaName);

        return new JPAQuery<>(em)
                .select(qAppWebContent)
                .from(qAppWebContent)
                .where(qAppWebContent.area.eq(webContentArea))
                .fetchOne();
    }

    @Transactional(readOnly = true)
    public WebContentDto getUniqueWebContentByArea(String uniqueAreaName) {
        final AppWebContent webContent = getUniqueWebContentEntityByArea(uniqueAreaName);
        return this.mapEntityToDto(webContent, RequestContext.empty(ContextHolder.getSpringContext()));
    }

    @SneakyThrows
    @Transactional
    public void initDefaultWebContents() {
        for (Resource defaultWebContent : defaultWebContents) {

            final String filename = defaultWebContent.getFilename();
            if(filename == null)
                continue;

            final String areID = filename.replace(".json", "");
            final AppWebContent webContent = getUniqueWebContentEntityByArea(areID);

            if(webContent == null) {
                createNewWebContentForArea(areID, defaultWebContent);
            }

        }
    }

    @Transactional
    @SneakyThrows
    public void createNewWebContentForArea(String areaId, Resource defaultWebContent) {

        AppWebContent webContent = new AppWebContent();
        webContent.setLabel(areaId);
        webContent.setArea(this.webContentAreaService.findAreaByUniqueNameOrThrow(areaId));

        AbstractWebContent abstractWebContent = JacksonUtils.createDefaultJsonMapper().readValue(defaultWebContent.getContentAsByteArray(), AbstractWebContent.class);
        webContent.setJsonContent(JacksonUtils.createDefaultJsonMapper().writeValueAsString(abstractWebContent));
        saveEntity(webContent);
    }
}

package com.webharmony.core.service.data;

import com.webharmony.core.api.rest.controller.FileController;
import com.webharmony.core.api.rest.controller.utils.request.RequestContext;
import com.webharmony.core.api.rest.model.FileDto;
import com.webharmony.core.api.rest.model.utils.FileWebData;
import com.webharmony.core.api.rest.model.utils.apiobject.ApiResource;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.data.jpa.model.files.AppFile;
import com.webharmony.core.i18n.I18N;
import com.webharmony.core.i18n.I18nTranslation;
import com.webharmony.core.service.ResourceLinkService;
import com.webharmony.core.service.data.utils.AbstractEntityCrudService;
import com.webharmony.core.utils.FileUtils;
import com.webharmony.core.utils.StringUtils;
import com.webharmony.core.utils.exceptions.FileWebDataValidationException;
import com.webharmony.core.utils.reflection.ApiLink;
import com.webharmony.core.utils.tuple.Tuple2;
import lombok.SneakyThrows;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URLConnection;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Service
public class FileService extends AbstractEntityCrudService<FileDto, AppFile> implements I18nTranslation {

    public static final long MAX_SIZE_FOR_DIRECT_DOWNLOAD = 4000000L;
    public static final long MAX_SIZE_FOR_SINGLE_FILE_UPLOAD = 4000000L;
    public static final long MAX_SIZE_FOR_BATCH_FILE_UPLOAD_DOWNLOAD = 16000000L;

    private final I18N i18N = createI18nInstance(FileService.class);

    @Transactional
    public List<ApiResource<FileDto>> createNewFileResources(List<FileWebData> fileWebData, RequestContext requestContext) {
        return createNewFileResources(fileWebData, requestContext, false);
    }
    @Transactional
    public List<ApiResource<FileDto>> createNewFileResources(List<FileWebData> fileWebData, RequestContext requestContext, boolean isTemp) {

        final List<ApiResource<FileDto>> result = new ArrayList<>();
        for (FileWebData webFile : fileWebData) {
            final AppFile appFile = createNewFileByUpload(webFile, isTemp);
            final FileDto fileDto = mapEntityToDto(appFile, requestContext);

            final ApiResource<FileDto> apiResource = new ApiResource<>();
            apiResource.setPrimaryKey(appFile.getUuid());
            apiResource.setData(fileDto);
            apiResource.setResourceLinks(ContextHolder.getContext().getBean(ResourceLinkService.class).createResourceLinksByResourceDtoAndId(fileDto.getClass(), fileDto.getId()));

            result.add(apiResource);
        }

        return result;
    }

    @SneakyThrows
    @Transactional
    public AppFile createNewFileByUpload(FileWebData webFile, boolean isTemp) {
        AppFile appFile = buildAppFileByWebData(webFile);
        appFile.setIsTemp(isTemp);
        return saveEntity(appFile);
    }

    @SneakyThrows
    public ResponseEntity<Resource> getResourceAsResponseEntityByFile(AppFile appFile) {
        Resource resource = getResourceByFile(appFile);

        String mimeType =  URLConnection.guessContentTypeFromName(appFile.getFileName());
        MultiValueMap<String, String> headers = new MultiValueMapAdapter<>(new HashMap<>());
        headers.put("Content-Type", List.of(mimeType));
        headers.put("Access-Control-Expose-Headers", List.of("Content-Disposition"));
        headers.put("Content-Disposition", List.of(appFile.getFileName()));

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);

    }
    public ByteArrayResource getResourceByFile(AppFile appFile) {
        byte[] bytes = unzipFileByByteArray(appFile.getFileAsByte());
        return new ByteArrayResource(bytes);
    }

    @SneakyThrows
    private byte[] zipFileByByteArray(byte[] file) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            GZIPOutputStream gzip = new GZIPOutputStream(bos);
            gzip.write(file);
            gzip.flush();
            gzip.close();
            return bos.toByteArray();
        }
    }

    @SneakyThrows
    private byte[] unzipFileByByteArray(byte[] bytes) {

        ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes);

        try (GZIPInputStream gis = new GZIPInputStream(byteInputStream); ByteArrayOutputStream fos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1];
            int len;
            while ((len = gis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            return fos.toByteArray();
        }
    }

    @Transactional
    public void deleteAllById(List<UUID> allExpiredTmpFiles) {
        getRepository().deleteAllById(allExpiredTmpFiles);
    }

    @SneakyThrows
    public AppFile buildAppFileByWebData(FileWebData fileWebData) {

        if(!isIsValidUUID(fileWebData.getId())) {
            final Tuple2<String, byte[]> extractedData = FileUtils.extractFileWebData(fileWebData);
            byte[] bytesFromZipFile = zipFileByByteArray(extractedData.getType2());

            AppFile appFile = new AppFile();
            appFile.initializeLocalTransientUuid();
            appFile.setFileAsByte(bytesFromZipFile);
            appFile.setFileName(fileWebData.getName());
            appFile.setSize(extractedData.getType2().length);
            appFile.setWebType(extractedData.getType1());
            appFile.setIsTemp(false);
            appFile.setType(fileWebData.getType());
            return appFile;
        } else {
            final UUID uuid = UUID.fromString(fileWebData.getId());
            return getEntityById(uuid);
        }
    }

    private boolean isIsValidUUID(String id) {
        try {
            UUID.fromString(id);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @SneakyThrows
    public FileWebData buildWebDataByAppFile(AppFile appFile) {

        final FileWebData fileWebData = new FileWebData();
        fileWebData.setSize(appFile.getSize());
        fileWebData.setId(appFile.getUuid().toString());
        fileWebData.setName(appFile.getFileName());
        fileWebData.setType(appFile.getType());
        fileWebData.setLastModified(Optional.ofNullable(appFile.getUpdatedAt()).orElse(appFile.getCreatedAt()));
        fileWebData.setDownloadLink(ApiLink.of(FileController.class, c -> c.getResourceDownload(appFile.getUuid())));

        if(appFile.getSize() < MAX_SIZE_FOR_DIRECT_DOWNLOAD) {
            final byte[] fileAsBytes = unzipFileByByteArray(appFile.getFileAsByte());
            fileWebData.setBase64Content(String.format("%s,%s", appFile.getWebType(), Base64.getEncoder().encodeToString(fileAsBytes)));
        }

        return fileWebData;
    }

    public void validateOverallSizeOfFileWebData(List<FileWebData> fileWebData) {
        long overallSize = 0L;
        for (FileWebData singleData : fileWebData) {
            Tuple2<String, byte[]> extractFileWebData = FileUtils.extractFileWebData(singleData);
            overallSize += extractFileWebData.getType2().length;
        }

        if(overallSize > MAX_SIZE_FOR_BATCH_FILE_UPLOAD_DOWNLOAD) {
            throw new FileWebDataValidationException(i18N.translate("Size of all files is too large").build());
        }
    }

    public void validateSizeOfFileWebData(FileWebData fileWebData) {
        Tuple2<String, byte[]> data = FileUtils.extractFileWebData(fileWebData);
        if(data.getType2().length > MAX_SIZE_FOR_SINGLE_FILE_UPLOAD) {
            throw new FileWebDataValidationException(i18N.translate("File '{fileName} is too large'").add("fileName", fileWebData.getName()).build());
        }
    }

}

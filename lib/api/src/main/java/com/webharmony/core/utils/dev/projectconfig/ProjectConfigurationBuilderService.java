package com.webharmony.core.utils.dev.projectconfig;

import com.webharmony.core.context.ArtifactInfo;
import com.webharmony.core.utils.JacksonUtils;
import com.webharmony.core.utils.dev.fepages.PageProviderService;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
public class ProjectConfigurationBuilderService {

    private final PageProviderService pageProviderService;

    private final ArtifactInfo artifactInfo;

    public ProjectConfigurationBuilderService(PageProviderService pageProviderService, ArtifactInfo artifactInfo) {
        this.pageProviderService = pageProviderService;
        this.artifactInfo = artifactInfo;
    }

    @SneakyThrows
    public String buildProjectConfigurationJson() {
        return JacksonUtils.createDefaultJsonMapper()
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(buildFrontendProjectConfiguration());
    }

    private FrontendProjectConfiguration buildFrontendProjectConfiguration() {
        final FrontendProjectConfiguration configuration = new FrontendProjectConfiguration();
        configuration.setRouterPages(pageProviderService.buildRouterPages());
        configuration.setProjectMeta(getProjectMetaData());
        return configuration;
    }

    private ProjectMetaData getProjectMetaData() {
        final ProjectMetaData metaData = new ProjectMetaData();
        metaData.setProjectLongName(artifactInfo.getLongName());
        metaData.setProjectShortName(artifactInfo.getShortName());
        return metaData;
    }
}

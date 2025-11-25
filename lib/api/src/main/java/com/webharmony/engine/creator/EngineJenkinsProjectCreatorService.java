package com.webharmony.engine.creator;

import com.webharmony.core.utils.StringUtils;
import com.webharmony.core.utils.tuple.Tuple2;
import com.webharmony.engine.form.CreateProjectDto;
import com.webharmony.engine.utils.EngineRootConfiguration;

import java.nio.file.Path;

public class EngineJenkinsProjectCreatorService extends AbstractEngineProjectCreatorService {

    protected EngineJenkinsProjectCreatorService(EngineRootConfiguration rootConfiguration, CreateProjectDto createProjectDto, Path projectDirectory) {
        super(rootConfiguration, createProjectDto, projectDirectory);
    }

    @Override
    public void create() {
        final Path jenkinsDirectory = createSubDirectory(getProjectDirectory(), "jenkins");
        simpleCopyTemplateContentToFile(jenkinsDirectory, "jenkins.docker-compose.yaml.template", "docker-compose.yaml");
        simpleCopyTemplateContentToFile(jenkinsDirectory, "jenkins.Dockerfile.template", "Dockerfile");
        simpleCopyTemplateContentToFile(jenkinsDirectory, "jenkins-entrypoint.sh.template", "entrypoint.sh");
        simpleCopyTemplateContentToFile(jenkinsDirectory, "jenkins-init_pipeline_jobs.groovy.template", "init_pipeline_jobs.groovy");
        createApiBuildJobDefinitionFile(jenkinsDirectory);
        createUiBuildJobDefinitionFile(jenkinsDirectory);
        createNginxBuildJobDefinitionFile(jenkinsDirectory);
        simpleCopyTemplateContentToFile(jenkinsDirectory, "jenkins-plugins.txt.template", "plugins.txt");
        simpleCopyTemplateContentToFile(jenkinsDirectory, "jenkins-Readme.md.template", "Readme.md");
    }

    @SuppressWarnings("all")
    private void createApiBuildJobDefinitionFile(Path jenkinsDirectory) {
        final String content = this.loadAndResolveTemplateContent("jenkins-api-build-pipeline.groovy.template",
                Tuple2.of("projectName", StringUtils.firstLetterToLowerCase(getCreateProjectDto().getProjectTechnicalName())),
                Tuple2.of("imageName", getCreateProjectDto().getApiImageName())
        );

        createNewFile(jenkinsDirectory.resolve("api-build-pipeline.groovy"), content);
    }

    @SuppressWarnings("all")
    private void createUiBuildJobDefinitionFile(Path jenkinsDirectory) {
        final String content = this.loadAndResolveTemplateContent("jenkins-ui-build-pipeline.groovy.template",
                Tuple2.of("projectName", StringUtils.firstLetterToLowerCase(getCreateProjectDto().getProjectTechnicalName())),
                Tuple2.of("imageName", getCreateProjectDto().getUiImageName())
        );

        createNewFile(jenkinsDirectory.resolve("ui-build-pipeline.groovy"), content);
    }

    @SuppressWarnings("all")
    private void createNginxBuildJobDefinitionFile(Path jenkinsDirectory) {
        final String content = this.loadAndResolveTemplateContent("jenkins-nginx-build-pipeline.groovy.template",
                Tuple2.of("projectName", StringUtils.firstLetterToLowerCase(getCreateProjectDto().getProjectTechnicalName())),
                Tuple2.of("imageName", getCreateProjectDto().getNginxImageName())
        );

        createNewFile(jenkinsDirectory.resolve("nginx-build-pipeline.groovy"), content);
    }
}

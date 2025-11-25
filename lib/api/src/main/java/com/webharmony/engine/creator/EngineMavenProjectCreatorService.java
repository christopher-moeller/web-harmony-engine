package com.webharmony.engine.creator;

import com.webharmony.engine.form.CreateProjectDto;
import com.webharmony.engine.utils.EngineRootConfiguration;

import java.nio.file.Path;

public class EngineMavenProjectCreatorService extends AbstractEngineProjectCreatorService {

    protected EngineMavenProjectCreatorService(EngineRootConfiguration rootConfiguration, CreateProjectDto createProjectDto, Path projectDirectory) {
        super(rootConfiguration, createProjectDto, projectDirectory);
    }

    @Override
    public void create() {
        final Path mvnDirectory = createSubDirectory(getProjectDirectory().resolve("api"), ".mvn");
        final Path wrapperDirectory = createSubDirectory(mvnDirectory, "wrapper");
        this.simpleCopyTemplateContentToFile(wrapperDirectory, "api-project-maven-wrapper.properties.template", "maven-wrapper.properties");
    }
}

package com.webharmony.engine.creator;

import com.webharmony.engine.form.CreateProjectDto;
import com.webharmony.engine.utils.EngineRootConfiguration;

import java.io.File;
import java.nio.file.Path;

public class EngineProjectCreator {

    private final CreateProjectDto createProjectDto;
    private final EngineRootConfiguration rootConfiguration;

    public EngineProjectCreator(CreateProjectDto createProjectDto, EngineRootConfiguration rootConfiguration) {
        this.createProjectDto = createProjectDto;
        this.rootConfiguration = rootConfiguration;
    }

    public void createProject() {
        final Path projectDirectoryPath = createNewProjectDirectory();

        final EngineApiProjectCreatorService apiCreator = new EngineApiProjectCreatorService(this.rootConfiguration, this.createProjectDto, projectDirectoryPath);
        final EngineUiProjectCreatorService uiCreator = new EngineUiProjectCreatorService(this.rootConfiguration, this.createProjectDto, projectDirectoryPath);
        final EngineDockerProjectCreatorService dockerCreator = new EngineDockerProjectCreatorService(this.rootConfiguration, this.createProjectDto, projectDirectoryPath);
        final EngineMavenProjectCreatorService mavenCreator = new EngineMavenProjectCreatorService(this.rootConfiguration, this.createProjectDto, projectDirectoryPath);
        final EngineJenkinsProjectCreatorService jenkinsCreator = new EngineJenkinsProjectCreatorService(this.rootConfiguration, this.createProjectDto, projectDirectoryPath);

        apiCreator.create();
        uiCreator.create();
        dockerCreator.create();
        mavenCreator.create();
        jenkinsCreator.create();
    }

    private Path createNewProjectDirectory() {
        final String projectDirectoryName = this.createProjectDto.getProjectTechnicalName().toLowerCase();
        final Path newProjectDirectoryPath = this.rootConfiguration.getProjectsDirectoryPath().resolve(projectDirectoryName);

        final File directory = newProjectDirectoryPath.toFile();
        if(directory.exists()) {
            throw new IllegalStateException("Project directory " + projectDirectoryName + " already exists");
        }

        if(!directory.mkdirs()) {
            throw new IllegalStateException("Project directory " + projectDirectoryName + " could not be created");
        }

        return newProjectDirectoryPath;
    }

}

package com.webharmony.engine.creator;

import com.webharmony.core.utils.StringUtils;
import com.webharmony.core.utils.tuple.Tuple2;
import com.webharmony.engine.form.CreateProjectDto;
import com.webharmony.engine.utils.EngineRootConfiguration;

import java.nio.file.Path;

public class EngineDockerProjectCreatorService extends AbstractEngineProjectCreatorService {

    protected EngineDockerProjectCreatorService(EngineRootConfiguration rootConfiguration, CreateProjectDto createProjectDto, Path projectDirectory) {
        super(rootConfiguration, createProjectDto, projectDirectory);
    }

    @Override
    public void create() {
        final Path dockerDirectory = createSubDirectory(getProjectDirectory(), "docker");
        createLocalDockerDirectory(dockerDirectory);
        createServerDockerDirectory(dockerDirectory);
    }

    private void createLocalDockerDirectory(Path rootDockerDirectory) {
        final Path directory = createSubDirectory(rootDockerDirectory, "local");
        createLocalDockerComposeFile(directory);
    }

    private void createLocalDockerComposeFile(Path directory) {
        @SuppressWarnings("unchecked")
        final String content = this.loadAndResolveTemplateContent("api-local-docker-compose.yml.template",
                Tuple2.of("postgresDB", StringUtils.firstLetterToLowerCase(getCreateProjectDto().getProjectTechnicalName())),
                Tuple2.of("postgresUser", getCreateProjectDto().getDatasourceUsername()),
                Tuple2.of("postgresPassword", getCreateProjectDto().getDatasourcePassword())
        );

        createNewFile(directory.resolve("docker-compose.yml"), content);
    }

    private void createServerDockerDirectory(Path rootDockerDirectory) {
        final Path directory = createSubDirectory(rootDockerDirectory, "server");
        createNginxDirectory(directory);
        createServerEnvDirectory(directory);
        createInitRemoteScript(directory);
        simpleCopyTemplateContentToFile(directory, "api.Dockerfile.template", "api.Dockerfile");
        simpleCopyTemplateContentToFile(directory, "api.ui.Dockerfile.template", "ui.Dockerfile");
    }

    private void createInitRemoteScript(Path directory) {
        @SuppressWarnings("unchecked")
        final String content = this.loadAndResolveTemplateContent("server-init-remote-server-env.sh.template",
                Tuple2.of("appName", StringUtils.firstLetterToLowerCase(getCreateProjectDto().getProjectTechnicalName()))
        );

        createNewFile(directory.resolve("init-remote-server-env.sh"), content);
    }

    private void createServerEnvDirectory(Path serverDirectory) {
        final Path envDirectory = createSubDirectory(serverDirectory, "server-env");
        simpleCopyTemplateContentToFile(envDirectory, "server-env-deploy_application.sh.template", "deploy_application.sh");
        createDBBackupScript(envDirectory);
        createServerDockerComposeFile(envDirectory);
    }

    private void createDBBackupScript(Path envDirectory) {
        @SuppressWarnings("unchecked")
        final String content = this.loadAndResolveTemplateContent("server-env-create-db-backup.sh.template",
                Tuple2.of("dbName", StringUtils.firstLetterToLowerCase(getCreateProjectDto().getProjectTechnicalName())),
                Tuple2.of("dbUserName", getCreateProjectDto().getDatasourceUsername())
        );

        createNewFile(envDirectory.resolve("create-db-backup.sh"), content);
    }

    private void createNginxDirectory(Path rootDockerDirectory) {
        final Path directory = createSubDirectory(rootDockerDirectory, "nginx");
        simpleCopyTemplateContentToFile(directory, "nginx-Dockerfile.template", "Dockerfile");
        simpleCopyTemplateContentToFile(directory, "nginx.conf.template", "nginx.conf");
        simpleCopyTemplateContentToFile(directory, "nginx.htpasswd.template", ".htpasswd");
    }


    private void createServerDockerComposeFile(Path directory) {
        @SuppressWarnings("unchecked")
        final String content = this.loadAndResolveTemplateContent("server-docker-compose.yml.template",
                Tuple2.of("postgresDB", StringUtils.firstLetterToLowerCase(getCreateProjectDto().getProjectTechnicalName())),
                Tuple2.of("postgresUser", getCreateProjectDto().getDatasourceUsername()),
                Tuple2.of("postgresPassword", getCreateProjectDto().getDatasourcePassword()),
                Tuple2.of("apiImageName", getCreateProjectDto().getApiImageName()),
                Tuple2.of("uiImageName", getCreateProjectDto().getUiImageName()),
                Tuple2.of("nginxImageName", getCreateProjectDto().getNginxImageName())
        );

        createNewFile(directory.resolve("docker-compose.yml"), content);
    }
}

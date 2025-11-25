package com.webharmony.engine.creator;

import com.webharmony.core.utils.StringUtils;
import com.webharmony.core.utils.tuple.Tuple2;
import com.webharmony.engine.form.CreateProjectDto;
import com.webharmony.engine.utils.EngineRootConfiguration;

import java.io.File;
import java.nio.file.Path;

public class EngineApiProjectCreatorService extends AbstractEngineProjectCreatorService {


    public EngineApiProjectCreatorService(EngineRootConfiguration rootConfiguration, CreateProjectDto dto, Path projectDirectory) {
        super(rootConfiguration, dto, projectDirectory);
    }

    @Override
    public void create() {
        final Path apiProjectDirectory = createSubDirectory(this.getProjectDirectory(), "api");
        createApiSrcStructure(apiProjectDirectory);
        simpleCopyTemplateContentToFile(this.getProjectDirectory(), "api.gitignore.template", ".gitignore");
        createPomXmlFile(apiProjectDirectory);
        createLocalDevOnlyProperties(apiProjectDirectory);
    }

    private void createApiSrcStructure(Path apiDirectory) {
        final Path srcDirectory = createSubDirectory(apiDirectory, "src");
        createApiSrcMainStructure(srcDirectory);
        createApiSrcTestStructure(srcDirectory);
    }

    private void createApiSrcMainStructure(Path apiDirectory) {
        final Path mainDirectory = createSubDirectory(apiDirectory, "main");
        final Path javaDirectory = createSubDirectory(mainDirectory, "java");
        final Path resourcesDirectory = createSubDirectory(mainDirectory, "resources");
        final Path targetPackage = createBaseTargetPackageStructure(javaDirectory);

        createProjectApplicationMainFile(targetPackage);
        createProjectApplicationYamlFile(resourcesDirectory);

        simpleCopyTemplateContentToFile(resourcesDirectory, "apiprojectversion.json.template");
    }

    private void createProjectApplicationMainFile(Path targetPackage) {
        final String packagePath = String.format("com.webharmony.%s", getCreateProjectDto().getProjectTechnicalName().toLowerCase());
        final String className = StringUtils.firstLetterToUpperCase(getCreateProjectDto().getProjectTechnicalName()) + "Application";
        @SuppressWarnings("all")
        final String content = loadAndResolveTemplateContent("ProjectApplicationMain.java.template",
                Tuple2.of("packagePath", packagePath),
                Tuple2.of("className", className)
        );
        final Path targetPath = targetPackage.resolve(String.format("%s.java", className));
        createNewFile(targetPath, content);
    }

    @SuppressWarnings("all")
    private File createProjectApplicationYamlFile(Path targetPackage) {

        final String datasourceUrl = String.format("jdbc:postgresql://localhost:%s/%s", this.getCreateProjectDto().getDatasourcePort(), StringUtils.firstLetterToLowerCase(this.getCreateProjectDto().getProjectTechnicalName()));
        final String content = loadAndResolveTemplateContent("application.yaml.template",
                Tuple2.of("dataSourceUrl", datasourceUrl),
                Tuple2.of("dataSourceUsername", this.getCreateProjectDto().getDatasourceUsername()),
                Tuple2.of("dataSourcePassword", this.getCreateProjectDto().getDatasourcePassword()),
                Tuple2.of("appShortName", this.getCreateProjectDto().getProjectShortName()),
                Tuple2.of("appLongName", this.getCreateProjectDto().getProjectLongName())
        );
        final Path targetPath = targetPackage.resolve("application.yaml");
        return createNewFile(targetPath, content);
    }

    private void createPomXmlFile(Path targetDirectory) {

        final String groupId = String.format("com.webharmony.%s", getCreateProjectDto().getProjectTechnicalName().toLowerCase());
        @SuppressWarnings("all")
        final String content = loadAndResolveTemplateContent("pom.xml.template",
                Tuple2.of("groupId", groupId),
                Tuple2.of("artifactId", StringUtils.firstLetterToLowerCase(getCreateProjectDto().getProjectTechnicalName())),
                Tuple2.of("appName", getCreateProjectDto().getProjectLongName())
        );
        final Path targetPath = targetDirectory.resolve("pom.xml");
        createNewFile(targetPath, content);
    }

    private void createLocalDevOnlyProperties(Path targetDirectory) {

        final String frontendCoreDevPath = getRootConfiguration().getEngineDirectoryPath().resolve("ui").toAbsolutePath().toString();
        final String frontendProjectDevPath = getRootConfiguration().getProjectsDirectoryPath().resolve(StringUtils.firstLetterToLowerCase(this.getCreateProjectDto().getProjectTechnicalName())).resolve("ui").toAbsolutePath().toString();

        @SuppressWarnings("all")
        final String content = loadAndResolveTemplateContent("LOCAL_DEV_ONLY_PROPERTY_VALUES.json.template",
                Tuple2.of("frontendCoreDevPath", frontendCoreDevPath),
                Tuple2.of("frontendProjectDevPath", frontendProjectDevPath)
        );
        final Path targetPath = targetDirectory.resolve("LOCAL_DEV_ONLY_PROPERTY_VALUES.json");
        createNewFile(targetPath, content);
    }


    private void createApiSrcTestStructure(Path apiDirectory) {
        final Path testDirectory = createSubDirectory(apiDirectory, "test");
        final Path javaDirectory = createSubDirectory(testDirectory, "java");
        createBaseTargetPackageStructure(javaDirectory);
    }

    private Path createBaseTargetPackageStructure(Path rootDir) {
        final Path comDirectory = createSubDirectory(rootDir, "com");
        final Path webharmonyDirectory = createSubDirectory(comDirectory, "webharmony");
        return createSubDirectory(webharmonyDirectory, getCreateProjectDto().getProjectTechnicalName().toLowerCase());
    }

}

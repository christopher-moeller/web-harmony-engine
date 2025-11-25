package com.webharmony.starter;

import com.webharmony.core.service.data.validation.ValidatorService;
import com.webharmony.starter.api.rest.model.CreateHarmonyProjectRequest;
import com.webharmony.starter.builder.FileSystemBuilder;
import com.webharmony.starter.builder.ProjectBuildingContext;
import com.webharmony.starter.builder.VirtualDir;
import com.webharmony.starter.builder.files.api.ApiApplicationYamlFile;
import com.webharmony.starter.builder.files.api.ApiPomXmlFile;
import com.webharmony.starter.builder.files.api.ApiVersionJsonFile;
import com.webharmony.starter.builder.files.api.JavaMainApplicationClassFile;
import com.webharmony.starter.builder.files.ui.*;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;

@Service
public class ProjectCreationService {

    private final ValidatorService validatorService;

    public ProjectCreationService(ValidatorService validatorService) {
        this.validatorService = validatorService;
    }

    public void createProject(CreateHarmonyProjectRequest request) {
        validatorService.validate(request);

        final ProjectBuildingContext projectBuildingContext = createProjectBuildingContext(request);

        final VirtualDir root = new VirtualDir(projectBuildingContext.getProjectRootPath().toFile().getName());
        root.addChild(createApiDir(projectBuildingContext));
        root.addChild(createUiDir(projectBuildingContext));

        FileSystemBuilder fileSystemBuilder = new FileSystemBuilder(projectBuildingContext.getProjectRootPath().getParent(), root);
        fileSystemBuilder.create(projectBuildingContext);

        root.printTree(0);

    }

    private Path createTargetDir(String pathToProjectFolder) {
        final Path path = Path.of(pathToProjectFolder);
        if(path.toFile().exists()) {
            removeRecursively(path.toFile());
        } else {
            path.toFile().mkdirs();
        }
        return path;
    }

    private void removeRecursively(File file) {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                removeRecursively(child);
            }
        }
        if (!file.delete()) {
            throw new RuntimeException("Failed to delete file: " + file.getAbsolutePath());
        }
    }

    private VirtualDir createUiDir(ProjectBuildingContext projectBuildingContext) {
        final VirtualDir uiDir = new VirtualDir("ui");
        uiDir.addChild(createUiAssetsDir(projectBuildingContext));
        uiDir.addChild(createUiComponentsDir(projectBuildingContext));
        uiDir.addChild(createUiComposablesDir(projectBuildingContext));
        uiDir.addChild(createUIPagesDir(projectBuildingContext));
        uiDir.addChild(createUIPublicDir(projectBuildingContext));
        uiDir.addChild(createUIServerDir(projectBuildingContext));

        uiDir.addFile(new UIGitignoreFile());
        uiDir.addFile(new UINpmrcFile());
        uiDir.addFile(new UIAppFile());
        uiDir.addFile(new UIImportReplacementFile());
        uiDir.addFile(new UINuxtConfigFile());
        
        return uiDir;
    }

    private VirtualDir createUIServerDir(ProjectBuildingContext projectBuildingContext) {
        final VirtualDir uiServerDir = new VirtualDir("server");
        uiServerDir.addFile(new UIServerTsConfigFile());
        return uiServerDir;
    }

    private VirtualDir createUIPublicDir(ProjectBuildingContext projectBuildingContext) {
        final VirtualDir uiPublicDir = new VirtualDir("public");
        final VirtualDir imgDir = new VirtualDir("img");
        imgDir.addFile(new UIPublicLogoSvgFile());
        uiPublicDir.addChild(imgDir);

        uiPublicDir.addFile(new UIPublicProjectVersionFile());

        return uiPublicDir;
    }

    private VirtualDir createUIPagesDir(ProjectBuildingContext projectBuildingContext) {
        final VirtualDir uiPagesDir = new VirtualDir("pages");
        final VirtualDir appDir = new VirtualDir("app");
        appDir.addFile(new UIPagesAppHomeFile());

        uiPagesDir.addChild(appDir);
        uiPagesDir.addFile(new UIPagesIndexFile());
        return uiPagesDir;
    }

    private VirtualDir createUiAssetsDir(ProjectBuildingContext projectBuildingContext) {
        final VirtualDir uiAssetsDir = new VirtualDir("assets");
        final VirtualDir cssDir = new VirtualDir("css");
        cssDir.addFile(new UiAssetsCssFile());
        uiAssetsDir.addChild(cssDir);


        return uiAssetsDir;
    }

    private VirtualDir createUiComponentsDir(ProjectBuildingContext projectBuildingContext) {
        final VirtualDir uiComponentsDir = new VirtualDir("components");
        final VirtualDir viewDir = new VirtualDir("view");
        viewDir.addFile(new UIViewProjectViewLogoFile());
        uiComponentsDir.addChild(viewDir);

        return uiComponentsDir;
    }

    private VirtualDir createUiComposablesDir(ProjectBuildingContext projectBuildingContext) {
        final VirtualDir uiComposableDir = new VirtualDir("composables");

        uiComposableDir.addFile(new UIComposableUseProjectApiFile());

        return uiComposableDir;
    }

    private VirtualDir createApiDir(ProjectBuildingContext context) {
        VirtualDir apiDir = new VirtualDir("api");
        VirtualDir srcDir = new VirtualDir("src");
        apiDir.addChild(srcDir);
        VirtualDir mainDir = new VirtualDir("main");
        srcDir.addChild(mainDir);
        mainDir.addChild(createJavaDir(context));
        mainDir.addChild(createResourcesDir());


        apiDir.addFile(new ApiPomXmlFile());
        return apiDir;
    }

    private VirtualDir createJavaDir(ProjectBuildingContext context) {
        VirtualDir javaDir = new VirtualDir("java");
        VirtualDir comDir = new VirtualDir("com");
        javaDir.addChild(comDir);
        VirtualDir webharmonyDir = new VirtualDir("webharmony");
        comDir.addChild(webharmonyDir);


        VirtualDir projectPackageDir = new VirtualDir(context.getTechnicalName());
        webharmonyDir.addChild(projectPackageDir);
        projectPackageDir.addFile(new JavaMainApplicationClassFile(context));
        return javaDir;
    }

    private VirtualDir createResourcesDir() {
        final VirtualDir resourcesDir = new VirtualDir("resources");
        resourcesDir.addFile(new ApiVersionJsonFile());
        resourcesDir.addFile(new ApiApplicationYamlFile());
        return resourcesDir;
    }

    private String createTechnicalName(CreateHarmonyProjectRequest request) {
        return request.getProjectLongName().toLowerCase().replaceAll("[^a-z0-9]", "-");
    }

    private ProjectBuildingContext createProjectBuildingContext(CreateHarmonyProjectRequest request) {

        final String technicalName = createTechnicalName(request);

        final Path targetPath = createTargetDir(request.getPathToProject());

        return ProjectBuildingContext.builder()
                .harmonyCoreLibraryPath(findCoreLibraryPath())
                .projectRootPath(targetPath)
                .projectShortName(request.getProjectShortName())
                .projectLongName(request.getProjectLongName())
                .technicalName(technicalName)
                .useH2Database(request.getUseH2Database())
                .build();
    }

    private Path findCoreLibraryPath() {

        final String pathOfMainClass = StarterApplication.class.getResource("").getPath();

        final Path webharmonyRootPath = Path.of(pathOfMainClass.substring(0, pathOfMainClass
                .indexOf("web-harmony-engine") - 1));

        return webharmonyRootPath.resolve("web-harmony-engine").resolve("lib");
    }
}

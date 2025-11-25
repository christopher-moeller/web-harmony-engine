package com.webharmony.engine.creator;

import com.webharmony.core.utils.JacksonUtils;
import com.webharmony.core.utils.StringUtils;
import com.webharmony.core.utils.dev.fepages.CorePageProvider;
import com.webharmony.core.utils.dev.projectconfig.FrontendProjectConfiguration;
import com.webharmony.core.utils.dev.projectconfig.ProjectMetaData;
import com.webharmony.core.utils.tuple.Tuple2;
import com.webharmony.engine.form.CreateProjectDto;
import com.webharmony.engine.utils.EngineRootConfiguration;
import lombok.SneakyThrows;

import java.nio.file.Path;

public class EngineUiProjectCreatorService extends AbstractEngineProjectCreatorService {


    public EngineUiProjectCreatorService(EngineRootConfiguration rootConfiguration, CreateProjectDto dto, Path projectDirectory) {
        super(rootConfiguration, dto, projectDirectory);
    }

    @Override
    public void create() {
        final Path uiProjectDirectory = createSubDirectory(getProjectDirectory(), "ui");
        createAssetsDirectory(uiProjectDirectory);
        createComponentsDirectory(uiProjectDirectory);
        createPagesDirectory(uiProjectDirectory);
        createPublicDirectory(uiProjectDirectory);
        createServerDirectory(uiProjectDirectory);
        createRootDirectoryFiles(uiProjectDirectory);
    }

    private void createAssetsDirectory(final Path uiProjectDirectory) {
        final Path assetsDirectory = createSubDirectory(uiProjectDirectory, "assets");
        final Path cssDirectory = createSubDirectory(assetsDirectory, "css");

        final Path absolutePathOfCoreCssFile = getRootConfiguration().getEngineDirectoryPath()
                .resolve("ui")
                .resolve("assets")
                .resolve("css")
                .resolve("main.css")
                .toAbsolutePath();

        final Path relativePathOfCoreCssFile = cssDirectory.normalize().relativize(absolutePathOfCoreCssFile.normalize());
        @SuppressWarnings("all")
        final String content = this.loadAndResolveTemplateContent("ui-main.css.template", Tuple2.of("relativePathOfCoreCssFile", relativePathOfCoreCssFile));
        this.createNewFile(cssDirectory.resolve("main.css"), content);
    }

    private void createComponentsDirectory(final Path uiProjectDirectory) {
        createSubDirectory(uiProjectDirectory, "components");
    }

    private void createPagesDirectory(final Path uiProjectDirectory) {
        createSubDirectory(uiProjectDirectory, "pages");
    }

    private void createPublicDirectory(final Path uiProjectDirectory) {
        final Path publicDirectory = createSubDirectory(uiProjectDirectory, "public");
        this.simpleCopyTemplateContentToFile(publicDirectory, "uiprojectversion.json.template");
    }

    private void createServerDirectory(final Path uiProjectDirectory) {
        final Path publicDirectory = createSubDirectory(uiProjectDirectory, "server");
        this.simpleCopyTemplateContentToFile(publicDirectory, "ui-server-tsconfig.json.template", "tsconfig.json");
    }

    private void createRootDirectoryFiles(final Path uiProjectDirectory) {
        createEnvFile(uiProjectDirectory);
        this.simpleCopyTemplateContentToFile(uiProjectDirectory, "ui-gitignore.template", ".gitignore");
        this.simpleCopyTemplateContentToFile(uiProjectDirectory, "ui-npmrc.template", ".npmrc");
        this.simpleCopyTemplateContentToFile(uiProjectDirectory, "ui-app.vue.template", "app.vue");
        this.simpleCopyTemplateContentToFile(uiProjectDirectory, "ui-import-replacements.json.template", "import-replacements.json");
        createNuxtConfigFile(uiProjectDirectory);
        this.simpleCopyTemplateContentToFile(uiProjectDirectory, "ui-package.json.template", "package.json");
        createProjectConfigurationJsonFile(uiProjectDirectory);
        this.simpleCopyTemplateContentToFile(uiProjectDirectory, "ui-tsconfig.json.template", "tsconfig.json");
    }

    private void createEnvFile(final Path uiProjectDirectory) {
        @SuppressWarnings("all")
        final String content = this.loadAndResolveTemplateContent("ui-env.template", Tuple2.of("projectId", StringUtils.firstLetterToLowerCase(getCreateProjectDto().getProjectTechnicalName())));
        createNewFile(uiProjectDirectory.resolve(".env"), content);
    }

    private void createNuxtConfigFile(final Path uiProjectDirectory) {

        final Path absolutePathToCoreConfigFile = getRootConfiguration().getEngineDirectoryPath()
                .resolve("ui")
                .resolve("nuxt.config.ts")
                .toAbsolutePath();

        final Path relativePathToCoreConfigFile = uiProjectDirectory.normalize().relativize(absolutePathToCoreConfigFile.normalize());
        @SuppressWarnings("all")
        final String content = this.loadAndResolveTemplateContent("ui-nuxt.config.ts.template", Tuple2.of("extendsCorePath", relativePathToCoreConfigFile));
        createNewFile(uiProjectDirectory.resolve("nuxt.config.ts"), content);
    }

    @SneakyThrows
    private void createProjectConfigurationJsonFile(final Path uiProjectDirectory) {

        final ProjectMetaData metaData = new ProjectMetaData();
        metaData.setProjectLongName(getCreateProjectDto().getProjectLongName());
        metaData.setProjectShortName(getCreateProjectDto().getProjectShortName());

        final FrontendProjectConfiguration configuration = new FrontendProjectConfiguration();
        configuration.setRouterPages(new CorePageProvider().createJson());
        configuration.setProjectMeta(metaData);

        final String content = JacksonUtils.createDefaultJsonMapper()
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(configuration);

        createNewFile(uiProjectDirectory.resolve("project-configuration.json"), content);
    }

}

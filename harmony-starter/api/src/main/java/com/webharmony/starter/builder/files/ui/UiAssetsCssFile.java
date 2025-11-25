package com.webharmony.starter.builder.files.ui;

import com.webharmony.core.utils.tuple.Tuple2;
import com.webharmony.starter.builder.ProjectBuildingContext;
import com.webharmony.starter.builder.VirtualFile;

import java.nio.file.Path;

public class UiAssetsCssFile extends VirtualFile {

    private static final String TEMPLATE = """
            @import url({{pathToCoreCssFile}});
                        
            :root {
                --harmony-primary: rgb(53, 58, 140);
            }
            """;

    public UiAssetsCssFile() {
        super("main.css");
    }

    @Override
    public String createFileContent(ProjectBuildingContext projectBuildingContext) {

        final String pathToCoreCssFile = createPathToCoreCssFile(projectBuildingContext);

        return resolveTemplateContent(TEMPLATE,
                Tuple2.of("pathToCoreCssFile", pathToCoreCssFile)
                );
    }

    private String createPathToCoreCssFile(ProjectBuildingContext projectBuildingContext) {

        final Path libCssPath = projectBuildingContext.getHarmonyCoreLibraryPath()
                .resolve("ui")
                .resolve("assets")
                .resolve("css")
                .resolve("main.css");

        final Path localCssPath = projectBuildingContext.getProjectRootPath().getParent().resolve(this.getFullPath());

        return localCssPath.getParent().normalize().relativize(libCssPath.normalize()).toString();
    }
}

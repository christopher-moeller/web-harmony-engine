package com.webharmony.starter.builder.files.ui;

import com.webharmony.starter.builder.ProjectBuildingContext;
import com.webharmony.starter.builder.VirtualFile;

public class UIPublicProjectVersionFile extends VirtualFile {

    private static final String TEMPLATE = """
            {
              "build" : 0,
              "version" : "0.0.1"
            }
            """;

    public UIPublicProjectVersionFile() {
        super("uiprojectversion.json");
    }

    @Override
    public String createFileContent(ProjectBuildingContext projectBuildingContext) {
        return TEMPLATE;
    }
}

package com.webharmony.starter.builder.files.ui;

import com.webharmony.starter.builder.ProjectBuildingContext;
import com.webharmony.starter.builder.VirtualFile;

public class UIServerTsConfigFile extends VirtualFile {

    private static final String TEMPLATE = """
            {
              "extends": "../.nuxt/tsconfig.server.json"
            }
                        
            """;

    public UIServerTsConfigFile() {
        super("tsconfig.json");
    }

    @Override
    public String createFileContent(ProjectBuildingContext projectBuildingContext) {
        return TEMPLATE;
    }
}

package com.webharmony.starter.builder.files.api;

import com.webharmony.starter.builder.ProjectBuildingContext;
import com.webharmony.starter.builder.VirtualFile;

public class ApiVersionJsonFile extends VirtualFile {

    private static final String TEMPLATE = """
            {
              "build" : 0,
              "version" : "0.0.1"
            }
            """;
    public ApiVersionJsonFile() {
        super("apiprojectversion.json");
    }

    @Override
    public String createFileContent(ProjectBuildingContext projectBuildingContext) {
        return TEMPLATE;
    }
}

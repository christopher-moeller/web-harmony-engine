package com.webharmony.starter.builder.files.ui;

import com.webharmony.starter.builder.ProjectBuildingContext;
import com.webharmony.starter.builder.VirtualFile;

public class UINpmrcFile extends VirtualFile {

    private static final String TEMPLATE = """
            shamefully-hoist=true
            strict-peer-dependencies=false
                 
            """;

    public UINpmrcFile() {
        super(".npmrc");
    }

    @Override
    public String createFileContent(ProjectBuildingContext projectBuildingContext) {
        return TEMPLATE;
    }
}

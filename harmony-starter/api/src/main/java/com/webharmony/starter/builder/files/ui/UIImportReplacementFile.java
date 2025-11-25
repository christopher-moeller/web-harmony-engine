package com.webharmony.starter.builder.files.ui;

import com.webharmony.starter.builder.ProjectBuildingContext;
import com.webharmony.starter.builder.VirtualFile;

public class UIImportReplacementFile extends VirtualFile {

    private static final String TEMPLATE = """
            [
              {
                "coreImport": "/components/view/HarmonyViewLogo.vue",
                "projectImport": "/components/view/ProjectViewLogo.vue"
              }
            ]
            """;

    public UIImportReplacementFile() {
        super("import-replacement.json");
    }

    @Override
    public String createFileContent(ProjectBuildingContext projectBuildingContext) {
        return TEMPLATE;
    }
}

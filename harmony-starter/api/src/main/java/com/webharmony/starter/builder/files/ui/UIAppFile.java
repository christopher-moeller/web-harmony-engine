package com.webharmony.starter.builder.files.ui;

import com.webharmony.starter.builder.ProjectBuildingContext;
import com.webharmony.starter.builder.VirtualFile;

public class UIAppFile extends VirtualFile {

    private static final String TEMPLATE = """
            <template>
              <harmony-root />
            </template>
                        
                        
            <script setup lang="ts">
            import HarmonyRoot from "@core/components/view/harmony-root.vue"
                        
                        
            </script>
            """;

    public UIAppFile() {
        super("app.vue");
    }

    @Override
    public String createFileContent(ProjectBuildingContext projectBuildingContext) {
        return TEMPLATE;
    }
}

package com.webharmony.starter.builder.files.ui;

import com.webharmony.starter.builder.ProjectBuildingContext;
import com.webharmony.starter.builder.VirtualFile;

public class UIPagesAppHomeFile extends VirtualFile {

    private static final String TEMPLATE = """
            <template>
              <harmony-button caption="Open Modal" @click="isModalOpen = !isModalOpen" />
            </template>
                        
            <script lang="ts" setup>
            import HarmonyButton from "@core/components/base/HarmonyButton.vue"
                        
                        
            const isModalOpen = ref(false)
                        
                        
            </script>
                        
            <style scoped>
                        
            </style>
            """;

    public UIPagesAppHomeFile() {
        super("home.vue");
    }

    @Override
    public String createFileContent(ProjectBuildingContext projectBuildingContext) {
        return TEMPLATE;
    }
}

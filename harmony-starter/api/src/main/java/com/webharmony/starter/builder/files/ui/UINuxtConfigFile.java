package com.webharmony.starter.builder.files.ui;

import com.webharmony.starter.builder.ProjectBuildingContext;
import com.webharmony.starter.builder.VirtualFile;

public class UINuxtConfigFile extends VirtualFile {

    private static final String TEMPLATE = """
            // https://nuxt.com/docs/api/configuration/nuxt-config
                        
            export default defineNuxtConfig({
              // @ts-ignore
              extends: "../../lib/ui/nuxt.config.ts",
            })         
            """;

    public UINuxtConfigFile() {
        super("nuxt.config.ts");
    }

    @Override
    public String createFileContent(ProjectBuildingContext projectBuildingContext) {
        return TEMPLATE;
    }
}

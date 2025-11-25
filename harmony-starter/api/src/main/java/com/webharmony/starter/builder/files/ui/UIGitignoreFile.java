package com.webharmony.starter.builder.files.ui;

import com.webharmony.starter.builder.ProjectBuildingContext;
import com.webharmony.starter.builder.VirtualFile;

public class UIGitignoreFile extends VirtualFile {

    private static final String TEMPLATE = """
            # Nuxt dev/build outputs
            .output
            .nuxt
            .nitro
            .cache
            dist
                        
            # Node dependencies
            node_modules
                        
            # Logs
            logs
            *.log
                        
            # Misc
            .DS_Store
            .fleet
            .idea
                        
            # Local env files
            .env
            .env.*
            !.env.example
                        
            """;

    public UIGitignoreFile() {
        super(".gignore");
    }

    @Override
    public String createFileContent(ProjectBuildingContext projectBuildingContext) {
        return TEMPLATE;
    }
}

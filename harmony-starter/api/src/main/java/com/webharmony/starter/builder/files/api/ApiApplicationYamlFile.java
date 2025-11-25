package com.webharmony.starter.builder.files.api;

import com.webharmony.core.utils.tuple.Tuple2;
import com.webharmony.starter.builder.ProjectBuildingContext;
import com.webharmony.starter.builder.VirtualFile;

public class ApiApplicationYamlFile extends VirtualFile {

    private static final String TEMPLATE = """
            spring:
              main:
                allow-bean-definition-overriding: true
              profiles:
                default: prod
              datasource:
                url: jdbc:h2:mem:harmonydb
                driver-class-name: org.h2.Driver
                username: sa
                password: password
              jpa:
                hibernate:
                  ddl-auto: update
                open-in-view: true
                database-platform: org.hibernate.dialect.H2Dialect
              flyway:
                enabled: false
                        
            app:
              version: @project.version@
              artifactId: @project.artifactId@
              name: @project.name@
              shortName: {{shortName}}
              longName: {{longName}}
                        
            email:
              sendEmails: false
            """;
    public ApiApplicationYamlFile() {
        super("application.yaml");
    }

    @Override
    public String createFileContent(ProjectBuildingContext projectBuildingContext) {
        return resolveTemplateContent(TEMPLATE,
                Tuple2.of("shortName", projectBuildingContext.getProjectShortName()),
                Tuple2.of("longName", projectBuildingContext.getProjectLongName())
        );
    }
}



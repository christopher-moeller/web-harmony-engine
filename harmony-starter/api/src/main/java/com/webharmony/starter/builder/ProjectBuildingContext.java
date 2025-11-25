package com.webharmony.starter.builder;

import lombok.Builder;
import lombok.Data;

import java.nio.file.Path;

@Data
@Builder
public class ProjectBuildingContext {

    private String projectShortName;
    private String projectLongName;
    private String technicalName;
    private Boolean useH2Database;

    private Path harmonyCoreLibraryPath;
    private Path projectRootPath;

}

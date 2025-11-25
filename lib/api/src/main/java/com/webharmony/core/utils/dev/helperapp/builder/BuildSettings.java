package com.webharmony.core.utils.dev.helperapp.builder;

import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;

@Getter
@Setter
public class BuildSettings {

    private Path webharmonyRootFolder;
    private Path apiCoreFolder;
    private Path apiProjectFolder;
    private Path uiCoreFolder;
    private Path uiProjectFolder;

}

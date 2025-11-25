package com.webharmony.engine.utils;

import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;

@Getter
@Setter
public class EngineRootConfiguration {
    private Path rootDirectory;
    private Path engineDirectoryPath;
    private Path projectsDirectoryPath;
    private Path templatesDirectoryPath;
}

package com.webharmony.starter.builder;

import com.webharmony.core.utils.FileUtils;
import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class FileSystemBuilder {

    private final Path targetPath;

    private final VirtualDir rootDir;

    public FileSystemBuilder(Path targetPath, VirtualDir rootDir) {
        this.targetPath = targetPath;
        this.rootDir = rootDir;
    }

    public void create(ProjectBuildingContext projectBuildingContext) {
        createDir(rootDir, projectBuildingContext);
    }

    private void createDir(VirtualDir rootDir, ProjectBuildingContext projectBuildingContext) {
        final Path dirPath = targetPath.resolve(rootDir.getFullPath());

        if (!dirPath.toFile().exists()) {
            dirPath.toFile().mkdirs();
        }
        for (VirtualDir child : rootDir.getChildren()) {
            createDir(child, projectBuildingContext);
        }
        for (VirtualFile file : rootDir.getFiles()) {
            createFile(file, projectBuildingContext);
        }
    }

    @SneakyThrows
    private void createFile(VirtualFile file, ProjectBuildingContext projectBuildingContext) {
        final Path filePath = targetPath.resolve(file.getFullPath());
        if (!filePath.toFile().exists()) {
            FileUtils.writeToFile(filePath, file.createFileContent(projectBuildingContext).getBytes(StandardCharsets.UTF_8));
        } else {
            throw new IllegalStateException("File already exists: " + filePath);
        }
    }
}

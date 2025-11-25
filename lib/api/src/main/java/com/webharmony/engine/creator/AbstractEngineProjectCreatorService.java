package com.webharmony.engine.creator;

import com.webharmony.core.utils.FileUtils;
import com.webharmony.core.utils.tuple.Tuple2;
import com.webharmony.engine.form.CreateProjectDto;
import com.webharmony.engine.utils.EngineRootConfiguration;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@Getter
public abstract class AbstractEngineProjectCreatorService {

    private final CreateProjectDto createProjectDto;
    private final Path projectDirectory;

    private final EngineRootConfiguration rootConfiguration;

    protected AbstractEngineProjectCreatorService(EngineRootConfiguration rootConfiguration, CreateProjectDto createProjectDto, Path projectDirectory) {
        this.createProjectDto = createProjectDto;
        this.projectDirectory = projectDirectory;
        this.rootConfiguration = rootConfiguration;
    }

    public abstract void create();

    @SuppressWarnings("all")
    protected String loadAndResolveTemplateContent(String templateName, Tuple2<String, Object>... resolvers) {
        String content = loadTemplateContent(templateName);
        for (Tuple2<String, Object> resolver : resolvers) {
            final String key = resolver.getType1();
            final String value = Objects.toString(resolver.getType2());

            content = content.replace("{{"+ key +"}}", value);
        }
        return content;
    }
    @SneakyThrows
    protected String loadTemplateContent(String templateName) {
        final Path templateFilePath = rootConfiguration.getTemplatesDirectoryPath().resolve(templateName);
        final File templateFile = templateFilePath.toFile();
        if(!templateFile.exists()) {
            throw new IllegalStateException("Template file " + templateName + " does not exist");
        }
        return Files.readString(templateFilePath);
    }

    protected Path createSubDirectory(Path directory, String subDirectoryName) {
        final Path subDirectorPath = directory.resolve(subDirectoryName);
        final File newDirectory = subDirectorPath.toFile();
        if(newDirectory.exists()) {
            throw new IllegalStateException("Sub-directory directory " + subDirectoryName + " already exists");
        }
        if(!newDirectory.mkdirs()) {
            throw new IllegalStateException("Sub-directory directory " + subDirectoryName + " could not be created");
        }
        return subDirectorPath;
    }

    protected File createNewFile(Path path, String content) {
        final File file = path.toFile();
        if(file.exists()) {
            throw new IllegalStateException(String.format("File already exists: %s", path));
        }
        FileUtils.writeToFile(path, content.getBytes(StandardCharsets.UTF_8));
        return file;
    }

    @SuppressWarnings("all")
    protected File simpleCopyTemplateContentToFile(Path directoryPath, String templateFileName) {
        final String newFileName = templateFileName.replace(".template", "");
        return simpleCopyTemplateContentToFile(directoryPath, templateFileName, newFileName);
    }
    protected File simpleCopyTemplateContentToFile(Path directoryPath, String templateFileName, String newFileName) {
        final String content = loadTemplateContent(templateFileName);
        final Path targetPath = directoryPath.resolve(newFileName);
        return createNewFile(targetPath, content);
    }


}

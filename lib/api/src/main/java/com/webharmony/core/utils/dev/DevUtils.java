package com.webharmony.core.utils.dev;

import com.webharmony.core.AbstractAppMain;
import com.webharmony.core.utils.CollectionUtils;
import com.webharmony.core.utils.JacksonUtils;
import com.webharmony.core.utils.reflection.ReflectionUtils;
import lombok.SneakyThrows;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;

public class DevUtils {

    private DevUtils() {

    }

    public static Path getPathToProjectRootFolder() {
        Set<Class<? extends AbstractAppMain>> implementingAppClasses = ReflectionUtils.getAllProjectClassesImplementingSuperClass(AbstractAppMain.class);

        if(implementingAppClasses.isEmpty()) {
            return Path.of("");
        }

        Class<? extends AbstractAppMain> mainClass = CollectionUtils.getOnlyElement(implementingAppClasses);
        Path pathOfCompiledTarget = Path.of(mainClass.getProtectionDomain().getCodeSource().getLocation().getPath());
        return pathOfCompiledTarget.getParent().getParent();
    }

    public static Path getPathToCoreRootFolder() {
        Path pathOfCompiledTarget = Path.of(AbstractAppMain.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        return pathOfCompiledTarget.getParent().getParent();
    }

    @SneakyThrows
    public static Optional<LocalDevProperties> loadLocalDevProperties() {

        final Path pathToPropertiesFile = getPathToProjectRootFolder()
                .resolve("LOCAL_DEV_ONLY_PROPERTY_VALUES.json")
                .toAbsolutePath();

        File file = pathToPropertiesFile.toFile();
        if(!file.exists()) {
            createDefaultLocalDevPropertiesFile(pathToPropertiesFile);
            return Optional.empty();
        }

        return Optional.ofNullable(JacksonUtils.createDefaultJsonMapper().readValue(file, LocalDevProperties.class));
    }

    @SneakyThrows
    private static void createDefaultLocalDevPropertiesFile(Path pathToPropertiesFile) {

        final LocalDevProperties localDevProperties = new LocalDevProperties();

        final String content = JacksonUtils.createDefaultJsonMapper()
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(localDevProperties);

        Files.writeString(pathToPropertiesFile, content);
    }
}

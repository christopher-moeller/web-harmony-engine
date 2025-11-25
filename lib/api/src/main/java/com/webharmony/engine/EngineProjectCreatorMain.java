package com.webharmony.engine;

import com.webharmony.core.utils.JacksonUtils;
import com.webharmony.engine.utils.EngineRootConfiguration;
import com.webharmony.engine.utils.EngineRootConfigurationJson;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.io.File;
import java.nio.file.Path;

@Slf4j
public class EngineProjectCreatorMain {

    @SuppressWarnings("all")
    public static void main(String[] args) {
        try {
            start();
        }catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private static void start() {

        log.info("Starting engine project creator");
        log.info("Searching for root configuration file...");

        final File currentDirectory = Path.of("").toAbsolutePath().toFile();

        final JFileChooser fileChooser = new JFileChooser(currentDirectory);
        fileChooser.setDialogTitle("Select root engine configuration file...");
        int result = fileChooser.showDialog(null, "Select");

        if (result == JFileChooser.APPROVE_OPTION) {
            final File selectedFile = fileChooser.getSelectedFile();
            SwingUtilities.invokeLater(() -> new EngineMainFrame(createRootConfiguration(selectedFile)));
        } else {
            throw new IllegalStateException("No file selected");
        }
    }

    @SneakyThrows
    private static EngineRootConfiguration createRootConfiguration(File file) {
        final EngineRootConfigurationJson engineRootConfigurationJson = JacksonUtils.createDefaultJsonMapper()
                .readValue(file, EngineRootConfigurationJson.class);

        final Path rootDirectory = file.getParentFile().toPath();


        final EngineRootConfiguration engineRootConfiguration = new EngineRootConfiguration();
        engineRootConfiguration.setRootDirectory(rootDirectory);
        engineRootConfiguration.setEngineDirectoryPath(rootDirectory.resolve(engineRootConfigurationJson.getEngineDirectoryPath()).normalize().toAbsolutePath());
        engineRootConfiguration.setProjectsDirectoryPath(rootDirectory.resolve(engineRootConfigurationJson.getProjectsDirectoryPath()).normalize().toAbsolutePath());
        engineRootConfiguration.setTemplatesDirectoryPath(rootDirectory.resolve(engineRootConfigurationJson.getTemplatesDirectoryPath()).normalize().toAbsolutePath());

        return engineRootConfiguration;
    }
}

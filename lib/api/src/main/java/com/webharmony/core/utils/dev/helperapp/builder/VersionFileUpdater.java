package com.webharmony.core.utils.dev.helperapp.builder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webharmony.core.utils.exceptions.InternalServerException;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class VersionFileUpdater {

    private final File apiCoreVersionFile;
    private final File apiProjectVersionFile;
    private final File uiCoreVersionFile;
    private final File uiProjectVersionFile;

    public VersionFileUpdater(BuildSettings buildSettings) {
        apiCoreVersionFile = buildVersionFilePath(buildSettings.getApiCoreFolder().toFile(), "src", "main", "resources", "apicoreversion.json");
        apiProjectVersionFile = buildVersionFilePath(buildSettings.getApiProjectFolder().toFile(), "src", "main", "resources", "apiprojectversion.json");
        uiCoreVersionFile = buildVersionFilePath(buildSettings.getUiCoreFolder().toFile(), "public", "uicoreversion.json");
        uiProjectVersionFile = buildVersionFilePath(buildSettings.getUiProjectFolder().toFile(), "public", "uiprojectversion.json");
    }


    public void writeVersionFiles(VersionSummary versionSummary) {

        writeNewVersionDetailToFile(apiCoreVersionFile, versionSummary.getApiCoreVersion());
        writeNewVersionDetailToFile(apiProjectVersionFile, versionSummary.getApiProjectVersion());

        writeNewVersionDetailToFile(uiCoreVersionFile, versionSummary.getUiCoreVersion());
        writeNewVersionDetailToFile(uiProjectVersionFile, versionSummary.getUiProjectVersion());
    }

    private void writeNewVersionDetailToFile(File file, VersionDetail versionDetail) {

        final ObjectMapper objectMapper = new ObjectMapper();

        try {
            final String jsonAsString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(versionDetail);
            Files.writeString(file.toPath(), jsonAsString);
        } catch (Exception e) {
            throw new InternalServerException("Error during writing new versions to "+file.getPath(), e);
        }
    }

    public VersionSummary getCurrentVersionSummary() {
        final VersionSummary summary = new VersionSummary();
        summary.setApiCoreVersion(validateAndReadVersionFileContent(apiCoreVersionFile));
        summary.setApiProjectVersion(validateAndReadVersionFileContent(apiProjectVersionFile));
        summary.setUiCoreVersion(validateAndReadVersionFileContent(uiCoreVersionFile));
        summary.setUiProjectVersion(validateAndReadVersionFileContent(uiProjectVersionFile));

        return summary;
    }


    private File buildVersionFilePath(File rootDir, String... pathSteps) {
        Path currentPath = rootDir.toPath();
        for (String pathStep : pathSteps) {
            currentPath = currentPath.resolve(pathStep);
        }

        final File file = currentPath.toFile();
        if(!file.exists())
            throw new IllegalStateException(String.format("Version file %s does not exist", file.getPath()));

        return file;
    }

    private VersionDetail validateAndReadVersionFileContent(File file) {

        try {
            final String jsonAsString = Files.readString(file.toPath());
            VersionDetail versionDetail = new ObjectMapper().readValue(jsonAsString, VersionDetail.class);
            if(versionDetail.getBuild() < 0)
                throw new IllegalStateException(String.format("Build number cannot be negative in file %s", file.getPath()));

            if(versionDetail.getVersion() == null || versionDetail.getVersion().isEmpty())
                throw new IllegalStateException(String.format("Version cannot be empty in file %s", file.getPath()));

            return versionDetail;

        }catch (Exception e) {
            throw new InternalServerException("Validation error in version file "+file.getPath(), e);
        }
    }

}

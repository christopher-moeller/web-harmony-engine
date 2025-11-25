package com.webharmony.core.context;

import com.webharmony.core.configuration.utils.EnvironmentConstants;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;


@Configuration
public class ArtifactInfo {

    @Getter
    @Value("${" + EnvironmentConstants.ENV_APP_VERSION + ":}")
    private String version;

    @Getter
    @Value("${" + EnvironmentConstants.ENV_APP_ARTIFACT_ID + ":}")
    private String artifactId;

    @Getter
    @Value("${" + EnvironmentConstants.ENV_APP_NAME + ":}")
    private String projectName;

    @Value("${" + EnvironmentConstants.ENV_APP_SHORT_NAME + ":}")
    private String internalShortName;

    @Value("${" + EnvironmentConstants.ENV_APP_LONG_NAME + ":}")
    private String internalLongName;

    public String getShortName() {
        return Optional.ofNullable(this.internalShortName)
                .orElseGet(this::getProjectName);
    }

    public String getLongName() {
        return Optional.ofNullable(this.internalLongName)
                .orElseGet(this::getShortName);
    }
}

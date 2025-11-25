package com.webharmony.core.configuration;

import com.webharmony.core.configuration.utils.EnvironmentConstants;
import lombok.Getter;

@Getter
public enum EProfile {

    DEV(EnvironmentConstants.PROFILE_DEV),
    PROD(EnvironmentConstants.PROFILE_PROD),
    TEST(EnvironmentConstants.PROFILE_TEST);

    private final String springName;

    EProfile(String springName) {
        this.springName = springName;
    }

}

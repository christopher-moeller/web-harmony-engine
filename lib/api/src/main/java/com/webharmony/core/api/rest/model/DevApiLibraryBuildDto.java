package com.webharmony.core.api.rest.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DevApiLibraryBuildDto {

    private String projectSourceCode;
    private String coreSourceCode;
    private String projectConfigurationJson;

}

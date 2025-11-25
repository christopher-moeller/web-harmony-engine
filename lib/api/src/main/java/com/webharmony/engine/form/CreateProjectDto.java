package com.webharmony.engine.form;

import com.webharmony.engine.utils.EngineFormField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProjectDto {

    @EngineFormField("Technical Name")
    private String projectTechnicalName;
    @EngineFormField("Short Name")
    private String projectShortName;
    @EngineFormField("Long Name")
    private String projectLongName;

    @EngineFormField("Datasource Port")
    private String datasourcePort;
    @EngineFormField("Datasource Username")
    private String datasourceUsername;
    @EngineFormField("Datasource Password")
    private String datasourcePassword;
    private String apiImageName;
    private String uiImageName;
    private String nginxImageName;

}

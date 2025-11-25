package com.webharmony.core.api.rest.model;

import com.webharmony.core.configuration.EProfile;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServerApiBaseInfo {

    private boolean serverIsAvailable;
    private String apiBasePath;
    private EProfile profile;

}

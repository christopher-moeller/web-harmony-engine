package com.webharmony.core.api.rest.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExtendedServerInfo {

    private String swaggerUIUrl;
    private String databaseUrl;
    private String databaseUser;
    private String databasePassword;

}

package com.webharmony.core.utils.dev.fepages.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FrontendComponentJson {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String componentPath;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isCoreComponent;

}

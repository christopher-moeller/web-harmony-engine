package com.webharmony.core.utils.dev.fepages.json;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageJson {

    private String id;
    private Boolean isCorePage;
    private String path;
    private String englishLabel;
    private FrontendComponentJson frontendComponent;
    private AccessRuleConfigJson accessRuleConfigJson;


}

package com.webharmony.core.utils.dev.fepages.json;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NavigationItemJson {

    private String pageId;
    private String englishLabel;
    private String link;
    private String icon;
    private AccessRuleConfigJson accessRule;
    private List<NavigationItemJson> children;

}

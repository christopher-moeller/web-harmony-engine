package com.webharmony.core.utils.dev.fepages.json;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AccessRuleConfigJson {

    private boolean isPublic;
    private boolean isAndConnected;
    private List<String> rights;

}

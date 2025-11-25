package com.webharmony.core.utils.dev.fepages.model;

import com.webharmony.core.configuration.security.ApplicationRight;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AccessRuleConfig {

    private boolean isPublic;
    private boolean isAndConnected;
    private List<ApplicationRight> rights;

}

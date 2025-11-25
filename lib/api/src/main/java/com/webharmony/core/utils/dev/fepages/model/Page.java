package com.webharmony.core.utils.dev.fepages.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class Page {

    @Getter
    private final String id;

    private final String subPath;

    @Getter
    private final String englishLabel;
    @Getter
    private final FrontendComponent frontendComponent;

    private final PageAccessRule accessRule;

    @Getter
    private final List<Page> subPages;

    @Getter
    private final boolean isCorePage;

    @Getter
    private final boolean isVirtualPage;

    @Getter
    @Setter
    private Page parent;


    private Page(String id, String subPath, String englishLabel, FrontendComponent frontendComponent, PageAccessRule accessRule, boolean isCorePage, boolean isVirtualPage) {
        this.id = id;
        this.subPath = subPath;
        this.englishLabel = englishLabel;
        this.frontendComponent = frontendComponent;
        this.accessRule = accessRule;
        this.isCorePage = isCorePage;
        this.isVirtualPage = isVirtualPage;
        this.subPages = new ArrayList<>();
    }

    public static Page of(String id, String subPath, String englishLabel, FrontendComponent frontendComponent, PageAccessRule accessRule) {
        boolean isCorePage = Thread.currentThread().getStackTrace()[2].getClassName().startsWith("com.webharmony.core");
        return new Page(id, subPath, englishLabel, frontendComponent ,accessRule, isCorePage, false);
    }

    public static Page virtual(String id, String subPath) {
        boolean isCorePage = Thread.currentThread().getStackTrace()[2].getClassName().startsWith("com.webharmony.core");
        return new Page(id, subPath, null, null, null, isCorePage, true);
    }


    public Page withSubPages(Page... subPages) {
        this.subPages.clear();
        for (Page subPage : subPages) {
            subPage.setParent(this);
            this.subPages.add(subPage);
        }
        return this;
    }

    public String getFullPath() {
        if(this.parent != null) {
            return String.format("%s/%s", this.parent.getFullPath(), this.subPath);
        } else {
            return String.format("/%s", this.subPath);
        }
    }

    public AccessRuleConfig getAccessRuleConfig() {

        if(this.accessRule instanceof PageAccessRule.CollectFromChildrenAccessRule collectFromChildrenAccessRule) {
            collectFromChildrenAccessRule.init(this.getSubPages());
        }

        final AccessRuleConfig config = new AccessRuleConfig();
        config.setIsPublic(this.accessRule.isPublic());
        config.setIsAndConnected(this.accessRule.isAndConnected());
        config.setRights(this.accessRule.getRights());
        return config;
    }
}

package com.webharmony.core.utils.dev.fepages.model;


import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class NavigationItem {

    private final Page page;

    private final String icon;

    private final List<NavigationItem> children;

    public NavigationItem(Page page, String icon) {
        this.page = page;
        this.icon = icon;
        this.children = new ArrayList<>();
    }

    public static NavigationItem toPage(Page page, String icon) {
        return new NavigationItem(page, icon);
    }

    public NavigationItem withChildren(NavigationItem... children) {
        this.children.clear();
        this.children.addAll(Arrays.asList(children));
        return this;
    }

}

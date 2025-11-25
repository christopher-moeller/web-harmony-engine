package com.webharmony.core.utils.dev.fepages.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class NavigationTree {

    private final List<NavigationItem> items;

    public NavigationTree(List<NavigationItem> items) {
        this.items = items;
    }

    public static NavigationTree ofItems(NavigationItem... items) {
        return NavigationTree.ofItems(new ArrayList<>(List.of(items)));
    }

    public static NavigationTree ofItems(List<NavigationItem> items) {
        return new NavigationTree(items);
    }

}

package com.webharmony.core.service.searchcontainer.utils;

import java.util.stream.Stream;

public enum SortOrder {

    ASC,
    DESC;

    private static final SortOrder DEFAULT_ORDER = SortOrder.ASC;


    public static SortOrder getOrDefault(String value) {
        if(value == null)
            return getDefault();

        return Stream.of(SortOrder.values()).filter(v -> v.name().equalsIgnoreCase(value))
                .findAny()
                .orElse(getDefault());
    }

    public static SortOrder getDefault() {
        return DEFAULT_ORDER;
    }
}

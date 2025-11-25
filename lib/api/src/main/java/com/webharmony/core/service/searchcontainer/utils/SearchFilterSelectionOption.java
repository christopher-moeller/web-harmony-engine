package com.webharmony.core.service.searchcontainer.utils;

import lombok.Getter;

@Getter
public class SearchFilterSelectionOption {

    private final String label;
    private final String value;

    public SearchFilterSelectionOption(String label, String value) {
        this.label = label;
        this.value = value;
    }
}

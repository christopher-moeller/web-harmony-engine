package com.webharmony.core.api.rest.model.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.service.SelectableOptionContainerService;
import com.webharmony.core.service.searchcontainer.utils.SearchFilter;
import com.webharmony.core.service.searchcontainer.utils.SearchFilterSelectionOption;
import com.webharmony.core.utils.reflection.ApiLink;
import lombok.Getter;

import java.util.List;

public class RestFilterInfo {

    @Getter
    private final String filterName;
    private final SearchFilter.FilterType<?> type;

    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ApiLink apiLinkForOptions;

    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<SearchFilterSelectionOption> options;


    public RestFilterInfo(String filterName, SearchFilter.FilterType<?> type) {
        this.filterName = filterName;
        this.type = type;
        this.initPossibleValues();
    }

    private void initPossibleValues() {
        if(this.type instanceof SearchFilter.SingleSelectionFilter singleSelectionFilter) {
            this.apiLinkForOptions = ContextHolder.getContext().getBean(SelectableOptionContainerService.class)
                    .buildApiLinkForOptions(singleSelectionFilter.optionsContainerClass(), true);
        }

        if(this.type instanceof SearchFilter.SingleSelectionEnumFilter selectionEnumFilter) {
            this.options = selectionEnumFilter.getOptions();
        }
    }

    @JsonProperty("filterType")
    public String getFilterType() {
        return type.getClass().getSimpleName();
    }

}

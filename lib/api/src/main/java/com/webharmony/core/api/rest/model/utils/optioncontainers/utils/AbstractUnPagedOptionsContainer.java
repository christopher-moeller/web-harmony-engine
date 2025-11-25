package com.webharmony.core.api.rest.model.utils.optioncontainers.utils;

import com.webharmony.core.api.rest.model.utils.apiobject.ApiObject;
import com.webharmony.core.api.rest.model.utils.apiobject.ApiObjectWithLabel;
import com.webharmony.core.service.searchcontainer.utils.SearchFilterSelectionOption;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractUnPagedOptionsContainer<T> implements AbstractSelectableOptionsContainer<T> {

    public abstract List<T> getSelectableOptions();

    public List<SearchFilterSelectionOption> getSelectableOptionsAsFilterOptions() {
        final List<SearchFilterSelectionOption> resultList = new ArrayList<>();
        for(Object option : this.getSelectableOptions()) {
            if(option instanceof ApiObject<?> apiObject) {
                final String value = apiObject.getPrimaryKey().toString();
                final String label = apiObject instanceof ApiObjectWithLabel apiObjectWithLabel ? apiObjectWithLabel.getLabel() : String.valueOf(apiObject.getData());
                resultList.add(new SearchFilterSelectionOption(label, value));
            } else {
                final String labelAndValue = option.toString();
                resultList.add(new SearchFilterSelectionOption(labelAndValue, labelAndValue));
            }
        }
        return resultList;
    }

    @Override
    public boolean optionContainsInRange(T option) {
        return getSelectableOptions().contains(option);
    }
}

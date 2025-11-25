package com.webharmony.core.api.rest.model.utils.apiobject;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.Map;

public class ApiObjectWithLabel extends ApiObject<Map<String, String>> {

    private static final String LABEL = "label";

    public ApiObjectWithLabel(Object primaryKey, String label) {
        setPrimaryKey(primaryKey);

        final Map<String, String> dataMap = new HashMap<>();
        dataMap.put(LABEL, label);

        setData(dataMap);
    }

    @JsonIgnore
    public String getLabel() {
        return getData().get(LABEL);
    }

}

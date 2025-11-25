package com.webharmony.core.api.rest.model.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ResourceOverviewTypeSchema {

    private Map<String, SimpleFieldTypeSchema> fields;
    private List<String> sortOptions;
    private List<RestFilterInfo> filters;

}

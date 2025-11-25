package com.webharmony.core.service.searchcontainer.utils;

import com.webharmony.core.api.rest.model.utils.apiobject.GeneralApiResource;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class SearchResult {

    private long totalResults = 0;
    private long page = 0;
    private boolean isPaged = true;
    private long size = 0;
    private List<String> sortedBy;
    private List<GeneralApiResource<Map<String, Object>>> data = new ArrayList<>();
    private String primaryKeyName;

}

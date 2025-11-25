package com.webharmony.core.service.appresources.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResourcePageableOptions {

    private boolean isUnPagedRequestAllowed;
    private List<Integer> suggestedPageOptions;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer highestPageSize;

}

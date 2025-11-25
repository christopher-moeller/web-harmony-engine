package com.webharmony.core.api.rest.model.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.webharmony.core.utils.reflection.ApiLink;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResourceLinks {

    @JsonProperty(value = "self", access = JsonProperty.Access.READ_ONLY)
    private ApiLink selfLink;

    @JsonProperty(value = "update", access = JsonProperty.Access.READ_ONLY)
    private ApiLink updateLink;

    @JsonProperty(value = "delete", access = JsonProperty.Access.READ_ONLY)
    private ApiLink deleteLink;

}

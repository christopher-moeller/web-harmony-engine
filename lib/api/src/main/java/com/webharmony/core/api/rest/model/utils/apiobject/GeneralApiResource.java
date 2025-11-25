package com.webharmony.core.api.rest.model.utils.apiobject;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.webharmony.core.api.rest.model.utils.ResourceLinks;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GeneralApiResource<T> extends ApiObject<T> {

    private ResourceLinks resourceLinks;

}

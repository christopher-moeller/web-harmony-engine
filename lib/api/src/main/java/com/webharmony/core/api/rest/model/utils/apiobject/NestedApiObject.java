package com.webharmony.core.api.rest.model.utils.apiobject;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.webharmony.core.utils.reflection.ApiLink;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NestedApiObject<T> extends ApiObject<T> {

    private ApiLink selfLink;

    public static <T> NestedApiObject<T> of(T data) {
        NestedApiObject<T> nestedApiObject = new NestedApiObject<>();
        nestedApiObject.setData(data);
        return nestedApiObject;
    }

}

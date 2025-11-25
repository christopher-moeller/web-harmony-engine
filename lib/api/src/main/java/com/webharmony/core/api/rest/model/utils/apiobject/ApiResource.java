package com.webharmony.core.api.rest.model.utils.apiobject;

import com.webharmony.core.api.rest.model.utils.AbstractResourceDto;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ApiResource<R extends AbstractResourceDto> extends GeneralApiResource<R> {

    public ApiResource(Object primaryKey) {
        setPrimaryKey(primaryKey);
    }
    public ApiResource(R resource) {
        setData(resource);
    }

}

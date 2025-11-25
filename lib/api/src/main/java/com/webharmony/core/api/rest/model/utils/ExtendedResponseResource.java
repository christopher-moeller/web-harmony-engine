package com.webharmony.core.api.rest.model.utils;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ExtendedResponseResource<D extends AbstractResourceDto> extends ResponseResource<D> {

    public ExtendedResponseResource(ResponseResource<D> responseResource) {
        setData(responseResource.getData());
    }

    private ComplexTypeSchema schema;

}

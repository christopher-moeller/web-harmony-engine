package com.webharmony.core.api.rest.model.utils.annotations.utils;

import com.webharmony.core.utils.reflection.ApiLink;
import lombok.SneakyThrows;

public interface ApiLinkSpecification {

    ApiLink getLink();

    @SneakyThrows
    static ApiLinkSpecification instanceOf(Class<? extends ApiLinkSpecification> specificationClass) {
        return specificationClass.getDeclaredConstructor().newInstance();
    }
}

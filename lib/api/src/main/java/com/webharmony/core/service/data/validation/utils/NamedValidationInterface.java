package com.webharmony.core.service.data.validation.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

public interface NamedValidationInterface<V, R> extends ValidationInterface<V, R> {

    @JsonProperty("name")
    String getName();

    @Getter
    @Setter
    class NamedValidationInterfaceConfiguration {

        private boolean alwaysReturnInvalid = false;
        private boolean isActive = true;

    }

}

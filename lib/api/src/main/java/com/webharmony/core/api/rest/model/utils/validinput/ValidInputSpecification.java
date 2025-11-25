package com.webharmony.core.api.rest.model.utils.validinput;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class ValidInputSpecification {

    @JsonProperty("type")
    public String getType() {
        return this.getClass().getSimpleName();
    }

    private boolean isReadOnly = false;

}

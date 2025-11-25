package com.webharmony.core.api.rest.model.utils.apiobject;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.webharmony.core.utils.assertions.Assert;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ApiObject<T> {

    private Object primaryKey;
    private T data;

    @JsonIgnore
    public UUID getPrimaryKeyAsUUID() {
        Assert.isNotNull(primaryKey).verify();
        return UUID.fromString(primaryKey.toString());
    }

}

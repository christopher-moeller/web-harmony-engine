package com.webharmony.core.api.rest.model.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
public abstract class AbstractResourceDto extends BaseDto {

    private String id;

    public UUID buildUUIDOrNull() {
        return id != null ? UUID.fromString(id) : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractResourceDto that = (AbstractResourceDto) o;
        return Objects.equals(id, that.id) && Objects.equals(getDtoType(), that.getDtoType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, getDtoType());
    }
}

package com.webharmony.core.service.searchcontainer.postgresql;

import lombok.Getter;

import java.util.Objects;

@Getter
public class NativeSelectExpression {

    private final String label;
    private final String selectExpression;
    private NativeSelectExpression(String label, String selectExpression) {
        this.label = label;
        this.selectExpression = selectExpression;
    }

    public static NativeSelectExpression of(String label, String selectExpression) {
        return new NativeSelectExpression(label, selectExpression);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null)
            return false;

        if (this.getClass() != o.getClass())
            return false;

        NativeSelectExpression that = (NativeSelectExpression) o;
        return Objects.equals(label, that.label);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}

package com.webharmony.core.utils.assertions;

@FunctionalInterface
public interface AssertionFunction {

    @SuppressWarnings("all")
    boolean check();

}

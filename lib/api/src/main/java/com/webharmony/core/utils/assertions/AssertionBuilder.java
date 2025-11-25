package com.webharmony.core.utils.assertions;

import com.webharmony.core.utils.SilentExceptionHandler;
import com.webharmony.core.utils.exceptions.ApplicationException;

import java.util.function.Supplier;

public class AssertionBuilder {

    private final AssertionFunction assertionFunction;
    private Supplier<ApplicationException> exceptionSupplier;

    public AssertionBuilder(AssertionFunction assertionFunction, Supplier<ApplicationException> exceptionSupplier) {
        this.assertionFunction = assertionFunction;
        this.exceptionSupplier = exceptionSupplier;
    }

    public void verify() {
        if(!assertionFunction.check())
            throw exceptionSupplier.get();
    }

    public AssertionBuilder withException(Supplier<ApplicationException> exceptionSupplier) {
        this.exceptionSupplier = exceptionSupplier;
        return this;
    }

    public void verifyWithSilentException() {
        if(!assertionFunction.check())
            SilentExceptionHandler.handleException(exceptionSupplier.get());
    }
}

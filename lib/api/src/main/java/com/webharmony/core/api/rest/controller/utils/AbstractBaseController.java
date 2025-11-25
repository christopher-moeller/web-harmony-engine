package com.webharmony.core.api.rest.controller.utils;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractBaseController {

    private final Set<Method> securedMethods = new HashSet<>();

    protected void addSecuredMethod(Method method) {
        this.securedMethods.add(method);
    }

    public boolean isSecuredApiMethod(Method method) {
        return this.securedMethods.contains(method);
    }

}

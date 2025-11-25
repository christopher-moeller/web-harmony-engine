package com.webharmony.core.utils.reflection.proxy;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Method;

@Getter
@AllArgsConstructor
public class ProxyMethodResult {

    private Object proxy;
    private Method method;
    private Object[] args;

}

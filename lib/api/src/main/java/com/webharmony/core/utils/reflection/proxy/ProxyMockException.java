package com.webharmony.core.utils.reflection.proxy;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Method;

@Getter
@AllArgsConstructor
public class ProxyMockException extends RuntimeException {

    private final transient Object proxy;
    private final transient Method nativeMethod;
    private final transient Object[] args;

}

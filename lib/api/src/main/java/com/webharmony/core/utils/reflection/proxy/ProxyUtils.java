package com.webharmony.core.utils.reflection.proxy;

public class ProxyUtils {

    private ProxyUtils() {

    }

    public static <T> ProxyExecutionBuilder<T> proxyClazz(Class<T> clazz) {
        return new ProxyExecutionBuilder<>(clazz);
    }

}

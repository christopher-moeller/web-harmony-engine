package com.webharmony.core.utils.reflection.proxy;

public interface ProxyExecutionMethod<P, R> {

    R invokeMethod(P proxy);

}

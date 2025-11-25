package com.webharmony.core.utils.reflection.proxy;

import com.webharmony.core.utils.exceptions.InternalServerException;
import com.webharmony.core.utils.exceptions.ReflectionException;
import org.springframework.cglib.proxy.Enhancer;

import java.lang.reflect.Constructor;

public class ProxyExecutionBuilder<T> {

    private final Class<T> objectClass;
    private ProxyExecutionMethod<T, ?> method;

    public ProxyExecutionBuilder(Class<T> objectClass) {
        this.objectClass = objectClass;
    }

    public ProxyExecutionBuilder<T> withMethod(ProxyExecutionMethod<T, ?> method) {
        this.method = method;
        return this;
    }

    public ProxyMethodResult build() {

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(objectClass);
        enhancer.setCallback((org.springframework.cglib.proxy.InvocationHandler) (o, nativeMethod, objects) -> {
            throw new ProxyMockException(o, nativeMethod, objects);
        });

        try {
            var result = method.invokeMethod(createEnhancerInstance(enhancer));
            if(result != null) {
                throw new InternalServerException("Should never get here");
            }
        }catch (ProxyMockException e) {
            return new ProxyMethodResult(e.getProxy(), e.getNativeMethod(), e.getArgs());
        }

        throw new ReflectionException("Exception 'ProxyMockException' was not thrown during method execution");
    }

    @SuppressWarnings("unchecked")
    private T createEnhancerInstance(Enhancer enhancer) {
        Class<?>[] argumentTypes = getConstructorArgumentTypes();
        if(argumentTypes.length == 0) {
            return (T) enhancer.create();
        } else {
            Object[] nullArguments = createNullArrayByArgumentTypes(argumentTypes);
            return (T) enhancer.create(argumentTypes, nullArguments);
        }
    }

    private Class<?>[] getConstructorArgumentTypes() {
        Constructor<?>[] allConstructors = objectClass.getDeclaredConstructors();
        Constructor<?> bestCandidate = allConstructors[0];
        for(Constructor<?> candidate : allConstructors) {
            if(candidate.getParameterCount() < bestCandidate.getParameterCount())
                bestCandidate = candidate;
        }
        return bestCandidate.getParameterTypes();
    }

    private Object[] createNullArrayByArgumentTypes(Class<?>[] argumentTypes) {
        final Object[] resultArray = new Object[argumentTypes.length];
        for(int i=0; i<argumentTypes.length; i++) {
            resultArray[i] = null;
        }
        return resultArray;
    }


}

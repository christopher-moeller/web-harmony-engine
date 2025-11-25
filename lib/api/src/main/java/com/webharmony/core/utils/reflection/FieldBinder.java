package com.webharmony.core.utils.reflection;

import com.webharmony.core.utils.assertions.Assert;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class FieldBinder<B, V> {

    @lombok.Getter
    private final Class<B> beanType;
    @lombok.Getter
    private final Class<V> valueType;
    private final Getter<B, V> getter;
    private final Setter<B, V> setter;

    private FieldBinder(Class<B> beanType, Class<V> valueType, Getter<B, V> getter, Setter<B, V> setter) {
        this.beanType = beanType;
        this.valueType = valueType;
        this.getter = getter;
        this.setter = setter;
    }

    @SuppressWarnings("unchecked")
    public static <B, V> FieldBinderBuilder<B, V> of(Class<B> tClass, V vInstance) {
        return of(tClass, (Class<V>) vInstance.getClass());
    }

    public static <B, V> FieldBinderBuilder<B, V> of(Class<B> tClass, Class<V> vClass) {
        return new FieldBinderBuilder<>(tClass, vClass);
    }

    public V getValue(B bean) {
        return this.getter.apply(bean);
    }

    public void setValue(B bean, V value) {
        this.setter.accept(bean, value);
    }

    public static class FieldBinderBuilder<B, V> {

        private Getter<B, V> getter;
        private Setter<B, V> setter;
        private final Class<B> beanType;
        private final Class<V> valueType;

        public FieldBinderBuilder(Class<B> beanType, Class<V> valueType) {
            this.beanType = beanType;
            this.valueType = valueType;
        }

        public FieldBinderBuilder<B, V> withGetter(Getter<B, V> getter) {
            this.getter = getter;
            return this;
        }

        public FieldBinderBuilder<B, V> withSetter(Setter<B, V> setter) {
            this.setter = setter;
            return this;
        }

        public FieldBinder<B, V> build() {
            Assert.isNotNull(getter).verify();
            Assert.isNotNull(setter).verify();
            return new FieldBinder<>(beanType, valueType, getter, setter);
        }

    }

    @FunctionalInterface
    public interface Getter<S, T> extends Function<S, T> {
        T apply(S source);
    }

    @FunctionalInterface
    public interface Setter<B, V> extends BiConsumer<B, V> {
        void accept(B bean, V value);
    }

}

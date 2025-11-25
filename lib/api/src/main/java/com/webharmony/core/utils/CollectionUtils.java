package com.webharmony.core.utils;

import com.webharmony.core.utils.assertions.Assert;

import java.util.*;

public class CollectionUtils {

    private CollectionUtils() {

    }

    public static boolean isNullOrEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static <T> List<T> emptyListIfNull(List<T> list) {
        return Optional.ofNullable(list).orElseGet(Collections::emptyList);
    }

    public static <T> Set<T> emptySetIfNull(Set<T> set) {
        return Optional.ofNullable(set).orElseGet(Collections::emptySet);
    }

    public static <T> List<T> toList(T[] array) {
        return Optional.ofNullable(array)
                .map(List::of)
                .orElseGet(Collections::emptyList);
    }

    public static <T> T getOnlyElement(Collection<T> collection) {
        Assert.hasSize(collection, 1);
        return collection.iterator().next();
    }
}

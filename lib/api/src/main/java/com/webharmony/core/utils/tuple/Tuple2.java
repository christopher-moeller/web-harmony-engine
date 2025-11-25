package com.webharmony.core.utils.tuple;

import lombok.Getter;

@Getter
public class Tuple2<A, B> {

    private final A type1;
    private final B type2;

    private Tuple2(A type1, B type2) {
        this.type1 = type1;
        this.type2 = type2;
    }

    public static <T1, T2> Tuple2<T1, T2> of(T1 type1, T2 type2) {
        return new Tuple2<>(type1, type2);
    }
}

package com.webharmony.core.utils;

import com.webharmony.core.utils.assertions.Assert;
import com.webharmony.core.utils.reflection.ReflectionUtils;
import com.webharmony.core.utils.tuple.Tuple2;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class StringUtils {

    private StringUtils() {

    }

    @SuppressWarnings("unchecked")
    private static final ObjectConverter<?>[] toValueConverters = {
            ObjectConverter.of(Boolean.class, Boolean::parseBoolean, Object::toString),
            ObjectConverter.of(Long.class, Long::parseLong, Objects::toString),
            ObjectConverter.of(Integer.class, Integer::parseInt, Objects::toString),
            ObjectConverter.ofMapperWithType(Enum.class, t -> ReflectionUtils.getEnumConstant((Class<? extends Enum<?>>) t.getType2(), t.getType1()), Enum::name)
    };

    public static String mapToString(Object value) {

        if(value == null) {
            return null;
        }

        return getObjectConverterByType(value.getClass())
                .map(c -> c.mapToString(value))
                .orElseGet(() -> Objects.toString(value));
    }

    public static Object mapToObject(String s, Class<?> type) {
        if(s == null)
            return null;

        return getObjectConverterByType(type)
                .map(c -> c.mapToObject(s, type))
                .orElse(s);
    }

    private static Optional<ObjectConverter<?>> getObjectConverterByType(Class<?> type) {
        ObjectConverter<?> converter = Arrays.stream(toValueConverters).filter(c -> c.getType().equals(type)).findAny()
                .orElseGet(() -> Arrays.stream(toValueConverters).filter(c -> c.getType().isAssignableFrom(type)).findAny().orElse(null));

        return Optional.ofNullable(converter);
    }

    public static String firstLetterToLowerCase(String value) {
        Assert.isNotNull(value).verify();
        return Character.toLowerCase(value.charAt(0)) + value.substring(1);
    }

    public static String firstLetterToUpperCase(String value) {
        Assert.isNotNull(value).verify();
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }

    public static boolean isNotNullAndNotEmpty(String value) {
        return !isNullOrEmpty(value);
    }

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }

    public static String convertToHtmlString(String javaString) {
        if(isNullOrEmpty(javaString))
            return javaString;

        return javaString.replace("\n", "<br>");
    }

    @Getter
    public static final class ObjectConverter<T> {

        private final Class<T> type;
        private final Function<String, T> toObjectFunction;

        private final Function<Tuple2<String, Class<?>>, T> toObjectWithTypeFunction;

        private final Function<T, String> toStringFunction;

        private ObjectConverter(Class<T> type, Function<String, T> toObjectFunction, Function<T, String> toStringFunction, Function<Tuple2<String, Class<?>>, T> toObjectWithTypeFunction) {
            this.type = type;
            this.toObjectFunction = toObjectFunction;
            this.toStringFunction = toStringFunction;
            this.toObjectWithTypeFunction = toObjectWithTypeFunction;
        }

        public static <G> ObjectConverter<G> of(Class<G> type, Function<String, G> toObjectFunction, Function<G, String> toStringFunction) {
            return new ObjectConverter<>(type, toObjectFunction, toStringFunction, null);
        }

        public static <G> ObjectConverter<G> ofMapperWithType(Class<G> type, Function<Tuple2<String, Class<?>>, G> toObjectWithTypeFunction, Function<G, String> toStringFunction) {
            return new ObjectConverter<>(type, null, toStringFunction, toObjectWithTypeFunction);
        }

        @SuppressWarnings("unchecked")
        public String mapToString(Object o) {
            return toStringFunction.apply((T) o);
        }

        public Object mapToObject(String s, Class<?> mapToType) {
            if(toObjectWithTypeFunction != null) {
                return toObjectWithTypeFunction.apply(Tuple2.of(s, mapToType));
            } else {
                return toObjectFunction.apply(s);
            }
        }
    }

}

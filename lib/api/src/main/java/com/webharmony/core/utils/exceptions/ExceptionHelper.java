package com.webharmony.core.utils.exceptions;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class ExceptionHelper {

    private ExceptionHelper() {

    }

    public static String buildStacktraceText(Throwable throwable) {
        return convertThrowableToString(throwable) +
                "\n" +
                convertThrowableToString(throwable.getCause());
    }

    private static String convertThrowableToString(Throwable throwable) {
        if (throwable == null)
            return "";

        String type = throwable.getClass().getName();
        String message = throwable.getMessage();
        return buildStacktraceText(type, message, throwable.getStackTrace());
    }

    public static String buildStacktraceText(String type, String message, StackTraceElement[] traceElements) {
        String trace = Arrays.stream(traceElements)
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n\t"));

        return type + ": " +message + "\n\t" + trace;
    }

    public static Optional<String> getThrowableMessage(Throwable throwable) {
        final String message = throwable.getMessage();
        if(message != null) {
            return Optional.of(message);
        } else {
            Throwable cause = throwable.getCause();
            if(cause != null) {
                return getThrowableMessage(cause);
            } else {
                return Optional.empty();
            }
        }
    }

}

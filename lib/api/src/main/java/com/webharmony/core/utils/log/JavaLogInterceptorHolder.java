package com.webharmony.core.utils.log;

import com.webharmony.core.utils.exceptions.InternalServerException;
import org.springframework.lang.NonNull;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class JavaLogInterceptorHolder extends PrintStream {

    private static JavaLogInterceptorHolder instance = null;
    private final PrintStream originalOutputStream;

    private final Map<String, Consumer<String>> logInterceptors;

    private JavaLogInterceptorHolder(PrintStream originalOutputStream) {
        super(originalOutputStream);
        this.originalOutputStream = originalOutputStream;
        this.logInterceptors = new HashMap<>();
    }

    @SuppressWarnings("all")
    public static JavaLogInterceptorHolder getInstance() {
        if(instance == null) {
            instance = new JavaLogInterceptorHolder(System.out);
        }

        return instance;
    }

    public void addInterceptor(String id, Consumer<String> interceptor) {
        if(logInterceptors.containsKey(id))
            throw new InternalServerException(String.format("Interceptor with id '%s' already exists", interceptor));

        logInterceptors.put(id, interceptor);
    }

    @Override
    public void write(@NonNull byte[] buf, int off, int len) {
        String message = new String(buf, off, len);
        logInterceptors.values().forEach(li -> li.accept(message));
        originalOutputStream.write(buf, off, len);
    }
}

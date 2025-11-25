package com.webharmony.core.configuration;

import com.webharmony.core.api.rest.controller.utils.ResponseResourceWriter;
import com.webharmony.core.api.rest.model.utils.ResponseResource;
import lombok.NonNull;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ResponseResourceConverter implements HttpMessageConverter<ResponseResource<?>> {


    private final ResponseResourceWriter responseResourceWriter;

    public ResponseResourceConverter(ResponseResourceWriter responseResourceWriter) {
        this.responseResourceWriter = responseResourceWriter;
    }

    @Override
    public boolean canRead(@NonNull Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
    public boolean canWrite(@NonNull Class<?> clazz, MediaType mediaType) {
        return ResponseResource.class.equals(clazz);
    }

    @Override
    @NonNull
    public List<MediaType> getSupportedMediaTypes() {
        return Collections.singletonList(MediaType.ALL);
    }

    @Override
    @NonNull
    public ResponseResource<?> read(@NonNull Class<? extends ResponseResource<?>> clazz, @NonNull HttpInputMessage inputMessage) throws HttpMessageNotReadableException {
        return new ResponseResource<>();
    }

    @Override
    public void write(ResponseResource<?> responseResource, MediaType contentType, @NonNull HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        if(!responseResource.hasEmptyData())
            responseResourceWriter.writeMessage(responseResource, contentType, outputMessage);
    }
}

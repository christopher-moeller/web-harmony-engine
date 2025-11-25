package com.webharmony.core.configuration;

import com.webharmony.core.api.rest.controller.utils.AbstractCrudController;
import com.webharmony.core.api.rest.model.utils.BaseDto;
import lombok.NonNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

public class GenericEntityArgumentResolver implements HandlerMethodArgumentResolver {

    private final List<HttpMessageConverter<?>> converters;

    public GenericEntityArgumentResolver(List<HttpMessageConverter<?>> converters) {
        this.converters = converters;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return AbstractCrudController.class.isAssignableFrom(parameter.getContainingClass()) && BaseDto.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(@NonNull MethodParameter parameter, ModelAndViewContainer mavContainer, @NonNull NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        return getRequestResponseBodyMethodProcessor(converters).resolveArgument(parameter, mavContainer, webRequest, binderFactory);
    }

    private RequestResponseBodyMethodProcessor getRequestResponseBodyMethodProcessor(List<HttpMessageConverter<?>> converters) {
        return new RequestResponseBodyMethodProcessor(converters) {
            @Override
            protected Object readWithMessageConverters(@NonNull NativeWebRequest webRequest, @NonNull MethodParameter parameter, @NonNull Type paramType) throws IOException, HttpMediaTypeNotSupportedException, HttpMessageNotReadableException {
                return super.readWithMessageConverters(webRequest, parameter, getParamTypeOrDefault(parameter, paramType));
            }
        };
    }

    private Type getParamTypeOrDefault(MethodParameter parameter, Type defaultType) {
        return Optional.of((Type) parameter.getParameterType())
                .orElse(defaultType);
    }


}

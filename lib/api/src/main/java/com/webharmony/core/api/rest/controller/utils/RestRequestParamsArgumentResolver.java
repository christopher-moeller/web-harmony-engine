package com.webharmony.core.api.rest.controller.utils;

import lombok.NonNull;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RestRequestParamsArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return RestRequestParams.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(@NonNull MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, @NonNull NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        return RestRequestParams.of(getParameterMapFromWebRequest(webRequest));
    }

    private Map<String, Object> getParameterMapFromWebRequest(NativeWebRequest webRequest){

        Map<String, Object> resultMap = new HashMap<>();

        Iterator<String> iterator = webRequest.getParameterNames();
        while(iterator.hasNext()){
            String key = iterator.next();
            Object value = webRequest.getParameter(key);
            resultMap.put(key, value);
        }

        return resultMap;

    }

}

package com.webharmony.core.utils;

import jakarta.servlet.http.HttpServletRequest;

public class HttpUtils {

    private HttpUtils() {

    }

    public static String getRemoteIpAddress(HttpServletRequest request) {
        return request.getRemoteAddr();
    }

}

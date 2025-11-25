package com.webharmony.core.utils;

import com.webharmony.core.utils.exceptions.ApplicationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SilentExceptionHandler {

    private SilentExceptionHandler() {

    }
    public static void handleException(ApplicationException e) {
        log.error(e.getMessage());
    }
}

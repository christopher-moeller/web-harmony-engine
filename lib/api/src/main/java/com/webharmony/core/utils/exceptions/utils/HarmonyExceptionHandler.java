package com.webharmony.core.utils.exceptions.utils;

import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.service.data.ApplicationExceptionCrudService;
import com.webharmony.core.utils.exceptions.ApplicationException;

public class HarmonyExceptionHandler {

    private HarmonyExceptionHandler() {

    }

    public static void handle(Throwable throwable) {

        if(throwable instanceof ApplicationException applicationException && !applicationException.shouldLogException()) {
            return;
        }

        ContextHolder.getContext().getBeanIfExists(ApplicationExceptionCrudService.class)
                .ifPresent(service -> service.persistApplicationExceptionByThrowable(throwable));

    }
}

package com.webharmony.core.service.tasks.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.lang.reflect.Method;

@Getter
@Setter
public class ServerTask {

    private String taskId;

    private AbstractServerTaskHandler taskHandler;
    private Method method;

    private String taskName;
    private String handlerName;
    private boolean isRequired;

    @SneakyThrows
    public void execute() {
        method.invoke(taskHandler);
    }

}

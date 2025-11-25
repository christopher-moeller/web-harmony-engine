package com.webharmony.core.context;

import com.webharmony.core.AbstractAppMain;
import com.webharmony.core.utils.assertions.Assert;
import com.webharmony.core.utils.exceptions.InternalServerException;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.context.ApplicationContext;

public class ContextHolder {

    private static ContextHolder instance = null;

    @Getter(AccessLevel.PRIVATE)
    private final AppContext appContext;

    private ContextHolder(final AppContext appContext) {
        this.appContext = appContext;
    }

    private static ContextHolder getInstance() {
        Assert.isNotNull(instance).withException(() -> new InternalServerException("INSTANCE is null, please call initialize() first")).verify();
        return instance;
    }

    public static void initialize(final AppContext appContext) {
        Assert.isNull(instance).withException(() -> new InternalServerException("INSTANCE is already initialized")).verify();
        instance = new ContextHolder(appContext);
    }

    public static boolean isInitialized() {
        return instance != null;
    }

    public static AppContext getContext() {
        return getInstance().getAppContext();
    }

    public static ApplicationContext getSpringContext() {
        return isInitialized() ? getContext().getSpringContext() : AbstractAppMain.getPreSpringContext();
    }

}

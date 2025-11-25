package com.webharmony.core;

import com.webharmony.core.configuration.security.ApiAnnotationAuthorizationResolver;
import lombok.Getter;

import java.lang.annotation.Annotation;

public class HarmonyWebAppBuilder {

    private final AbstractAppMain appMain;

    @Getter
    private ApiAnnotationAuthorizationResolver<?> apiAnnotationAuthorizationResolver = null;

    @Getter
    private Class<? extends Annotation> localApiAuthorizationAnnotation = null;

    public HarmonyWebAppBuilder(AbstractAppMain appMain) {
        this.appMain = appMain;
    }

    public void run(String... args) {
        appMain.run(this, args);
    }

    public <A extends Annotation> HarmonyWebAppBuilder registerApiAuthorizationAnnotation(Class<A> annotationType, ApiAnnotationAuthorizationResolver<A> resolver) {
        this.apiAnnotationAuthorizationResolver = resolver;
        this.localApiAuthorizationAnnotation = annotationType;
        return this;
    }

}

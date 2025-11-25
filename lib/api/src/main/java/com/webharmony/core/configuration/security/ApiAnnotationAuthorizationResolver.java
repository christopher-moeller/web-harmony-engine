package com.webharmony.core.configuration.security;

import java.lang.annotation.Annotation;

public interface ApiAnnotationAuthorizationResolver<A extends Annotation> {

    ApplicationAccessRule resolve(A annotation);

    @SuppressWarnings("unchecked")
    default ApplicationAccessRule resolveUntyped(Annotation annotation) {
        return resolve((A) annotation);
    }

}

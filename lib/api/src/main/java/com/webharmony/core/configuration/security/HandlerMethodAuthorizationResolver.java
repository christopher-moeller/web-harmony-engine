package com.webharmony.core.configuration.security;

import com.webharmony.core.api.rest.controller.utils.ApiAuthentication;
import com.webharmony.core.api.rest.controller.utils.ApiAuthenticationType;
import org.springframework.web.method.HandlerMethod;

import java.util.Optional;

public interface HandlerMethodAuthorizationResolver {


    ApplicationAccessRule buildAccessRuleForAuthenticationAnnotation(HandlerMethod handlerMethod);

    default ApplicationAccessRule buildAccessRule(HandlerMethod handlerMethod) {

        if(Optional.ofNullable(handlerMethod.getMethodAnnotation(ApiAuthentication.class)).filter(a -> !a.value().equals(ApiAuthenticationType.PUBLIC)).isPresent())
            return ApplicationAccessRule.ofUnrestricted();

        final ApiAuthentication apiAuthenticationAnnotation = handlerMethod.getMethodAnnotation(ApiAuthentication.class);
        if(apiAuthenticationAnnotation != null && apiAuthenticationAnnotation.value().equals(ApiAuthenticationType.PUBLIC))
            return ApplicationAccessRule.ofUnrestricted();
        else
            return buildAccessRuleForAuthenticationAnnotation(handlerMethod);
    }

}

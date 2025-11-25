package com.webharmony.core.api.rest.interceptors;

import com.webharmony.core.api.rest.controller.utils.ApiAuthentication;
import com.webharmony.core.api.rest.controller.utils.ApiAuthenticationType;
import com.webharmony.core.configuration.utils.EnvironmentConstants;
import com.webharmony.core.configuration.utils.HttpHeaderConstants;
import com.webharmony.core.i18n.EI18nLanguage;
import com.webharmony.core.service.authentication.AuthenticationService;
import com.webharmony.core.service.authentication.JwtService;
import com.webharmony.core.service.authentication.types.AbstractAuthentication;
import com.webharmony.core.service.authentication.types.JwtAuthentication;
import com.webharmony.core.utils.HttpUtils;
import com.webharmony.core.utils.exceptions.UnauthorizedException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import java.util.Arrays;
import java.util.Optional;

@Component
@Slf4j
public class AuthenticationInterceptor extends AbstractHandlerInterceptorAdapter {

    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    @Value( "${" + EnvironmentConstants.ENV_AUTHENTICATION_JWT_ALLOW_COOKIE + "}" )
    private boolean allowJwtInCookie;

    public AuthenticationInterceptor(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {

        Authentication authentication = getValidJwtToken(request)
                .orElseGet(() -> getUnknownSystemAuthenticationIfAllowed(request, handler));

        initActorLanguage(authentication, request);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        setDefaultResponseHeaders(response);

        return true;
    }

    private void setDefaultResponseHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD, PATCH");
        response.setHeader("Access-Control-Allow-Headers", "*");
    }

    private void initActorLanguage(Authentication authentication, HttpServletRequest request) {
        final AbstractAuthentication abstractAuthentication = (AbstractAuthentication) authentication;
        Optional.ofNullable(request.getHeader(HttpHeaderConstants.LANGUAGE))
                .map(EI18nLanguage::getByKey)
                .ifPresent(abstractAuthentication::setActorLanguage);
    }

    private Optional<Authentication> getValidJwtToken(HttpServletRequest request) {
        try {
            return getAuthenticationToken(request)
                    .map(this::getAuthentication);
        }catch (Exception e) {
            return Optional.empty();
        }
    }

    private Authentication getUnknownSystemAuthenticationIfAllowed(HttpServletRequest request, Object handler) {
        if (isPublicRoute(handler) || isAllowedPreFlightRequest(request)) {
            return authenticationService.authenticateUnknownSystem(HttpUtils.getRemoteIpAddress(request));
        } else {
            log.error("Unauthorized request detected");
            if(HttpMethod.OPTIONS.name().equals(request.getMethod())) {
                log.error("For preflight requests please check if the frontend server is defined as own server");
            }
            throw new UnauthorizedException();
        }
    }

    private JwtAuthentication getAuthentication(String authenticationToken) {
        try {
            return authenticationService.authenticateByJwt(authenticationToken);
        } catch (Exception e) {
            throw new UnauthorizedException(e.getMessage());
        }
    }

    private boolean isAllowedPreFlightRequest(HttpServletRequest request) {
        return HttpMethod.OPTIONS.name().equals(request.getMethod());
    }

    private boolean isPublicRoute(Object handler) {
        if(isPublicByMethodAnnotation(handler)) {
            return true;
        } else {
            return isPublicByFEResource(handler);
        }
    }

    private boolean isPublicByMethodAnnotation(Object handler) {
        return getMethodAnnotation(handler, ApiAuthentication.class)
                .map(ApiAuthentication::value)
                .map(ApiAuthenticationType.PUBLIC::equals)
                .orElse(false);
    }

    private boolean isPublicByFEResource(Object handler) {
        return handler instanceof ParameterizableViewController || handler instanceof ResourceHttpRequestHandler;
    }

    private Optional<String> getAuthenticationToken(HttpServletRequest request) {
        String authenticationValue = request.getHeader(HttpHeaderConstants.AUTHENTICATION);
        if(authenticationValue == null && this.allowJwtInCookie) {
            authenticationValue = Arrays.stream(request.getCookies()).filter(c -> c.getName().equalsIgnoreCase(HttpHeaderConstants.AUTHENTICATION)).map(Cookie::getValue).findAny().orElse(null);
        }
        return authenticationValue != null ? Optional.of(jwtService.getRawToken(authenticationValue)) : Optional.empty();
    }

}

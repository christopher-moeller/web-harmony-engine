package com.webharmony.core.configuration;

import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webharmony.core.api.rest.controller.utils.MethodNotAllowed;
import com.webharmony.core.api.rest.controller.utils.ResponseResourceWriter;
import com.webharmony.core.api.rest.controller.utils.RestRequestParamsArgumentResolver;
import com.webharmony.core.api.rest.interceptors.AuthenticationInterceptor;
import com.webharmony.core.api.rest.interceptors.AuthorizationInterceptor;
import com.webharmony.core.api.rest.interceptors.LogInterceptor;
import com.webharmony.core.api.rest.interceptors.StatusInterceptor;
import com.webharmony.core.utils.JacksonUtils;
import lombok.Getter;
import lombok.NonNull;
import org.springdoc.webmvc.ui.SwaggerIndexTransformer;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class SpringWebConfiguration extends DelegatingWebMvcConfiguration implements ApplicationListener<ServletWebServerInitializedEvent> {

    public static final String BASE_MAPPING = "/";

    private final ResponseResourceWriter responseResourceWriter;

    private final SwaggerIndexTransformer swaggerIndexTransformer;

    private final AuthenticationInterceptor authenticationInterceptor;

    private final AuthorizationInterceptor authorizationInterceptor;

    private final LogInterceptor logInterceptor;

    private final StatusInterceptor statusInterceptor;

    @Getter
    private WebServer webServer;

    public SpringWebConfiguration(ResponseResourceWriter responseResourceWriter, SwaggerIndexTransformer swaggerIndexTransformer, AuthenticationInterceptor authenticationInterceptor, AuthorizationInterceptor authorizationInterceptor, LogInterceptor logInterceptor, StatusInterceptor statusInterceptor) {
        this.responseResourceWriter = responseResourceWriter;
        this.swaggerIndexTransformer = swaggerIndexTransformer;
        this.authenticationInterceptor = authenticationInterceptor;
        this.authorizationInterceptor = authorizationInterceptor;
        this.logInterceptor = logInterceptor;
        this.statusInterceptor = statusInterceptor;
    }


    @Override
    protected void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.defaultContentType(MediaType.APPLICATION_JSON);
    }

    @Override
    @NonNull
    protected RequestMappingHandlerMapping createRequestMappingHandlerMapping() {
        return new RequestMappingHandlerMapping() {
            @Override
            protected void registerHandlerMethod(@NonNull Object handler, @NonNull Method method, @NonNull RequestMappingInfo mapping) {
                if(isMethodAllowed(method)) {
                    super.registerHandlerMethod(handler, method, mapping);
                }
            }
        };
    }

    private boolean isMethodAllowed(Method method) {
        return AnnotationUtils.findAnnotation(method, MethodNotAllowed.class) == null;
    }

    @Override
    @NonNull
    protected RequestMappingHandlerAdapter createRequestMappingHandlerAdapter() {
        return new RequestMappingHandlerAdapter() {
            @Override
            public void afterPropertiesSet() {
                super.afterPropertiesSet();
                final List<HandlerMethodArgumentResolver> defaultArgumentResolvers = super.getArgumentResolvers();
                if(defaultArgumentResolvers != null) {
                    List<HandlerMethodArgumentResolver> combinedArgumentResolvers = new ArrayList<>();
                    combinedArgumentResolvers.add(new GenericEntityArgumentResolver(super.getMessageConverters()));
                    combinedArgumentResolvers.addAll(defaultArgumentResolvers);
                    super.setArgumentResolvers(combinedArgumentResolvers);
                }
            }
        };
    }


    @Override
    protected void configureMessageConverters(@NonNull List<HttpMessageConverter<?>> converters) {
        super.configureMessageConverters(converters);
        converters.add(new ResponseResourceConverter(responseResourceWriter));

        ObjectMapper objectMapper = JacksonUtils.createDefaultJsonMapper();
        objectMapper.getFactory().setStreamReadConstraints(StreamReadConstraints.builder().maxStringLength(50000000).build());
        converters.add(new MappingJackson2HttpMessageConverter(objectMapper));

        addDefaultHttpMessageConverters(converters);
    }


    @Override
    protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new RestRequestParamsArgumentResolver());
    }

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.setOrder(1);
        final ApplicationContext applicationContext = getApplicationContext();
        assert applicationContext != null;
        if(Arrays.asList(applicationContext.getEnvironment().getActiveProfiles()).contains(EProfile.DEV.getSpringName())) {
            registry.addResourceHandler("/swagger-ui" + "*/**")
                    .addResourceLocations("classpath:/META-INF/resources/webjars/")
                    .resourceChain(false)
                    .addTransformer(this.swaggerIndexTransformer);
        }
    }


    @Override
    protected void addInterceptors(@NonNull InterceptorRegistry registry) {

        final ApplicationContext applicationContext = getApplicationContext();

        final String[] excludedPaths;
        if(applicationContext != null && List.of(applicationContext.getEnvironment().getActiveProfiles()).contains(EProfile.DEV.getSpringName())) {
            excludedPaths = new String[]{ "/v3/api-docs/**" };
        } else {
            excludedPaths = new String[]{};
        }

        registry.addInterceptor(authenticationInterceptor)
                .order(1)
                .excludePathPatterns(excludedPaths);

        registry.addInterceptor(authorizationInterceptor)
                .order(2)
                .excludePathPatterns(excludedPaths);

        registry.addInterceptor(logInterceptor)
                .order(3)
                .excludePathPatterns(excludedPaths);

        registry.addInterceptor(statusInterceptor)
                .order(4)
                .excludePathPatterns(excludedPaths);
    }

    @Override
    public void onApplicationEvent(ServletWebServerInitializedEvent event) {
        this.webServer = event.getWebServer();
    }
}




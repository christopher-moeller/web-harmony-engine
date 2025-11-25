package com.webharmony.core;

import com.webharmony.core.configuration.EProfile;
import com.webharmony.core.configuration.security.ApiAnnotationAuthorizationResolver;
import com.webharmony.core.configuration.security.ApplicationAccessRule;
import com.webharmony.core.configuration.security.HandlerMethodAuthorizationResolver;
import com.webharmony.core.configuration.utils.EnvironmentConstants;
import com.webharmony.core.context.*;
import com.webharmony.core.data.jpa.model.AppApplicationStartupLog;
import com.webharmony.core.service.data.ApplicationStartupLogService;
import com.webharmony.core.utils.DateUtils;
import com.webharmony.core.utils.assertions.Assert;
import com.webharmony.core.utils.exceptions.InternalServerException;
import com.webharmony.core.utils.log.JavaLogInterceptorHolder;
import com.webharmony.core.utils.log.LogWatcher;
import com.webharmony.core.utils.log.StopWatch;
import com.webharmony.core.utils.reflection.classcontext.ClassContextHolder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.method.HandlerMethod;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Configuration
@ComponentScan("com.webharmony")
@EnableJpaRepositories("com.webharmony")
@EntityScan("com.webharmony")
@PropertySource("classpath:core.properties")
public abstract class AbstractAppMain {


    private static final String APPLICATION_SIGNAL_START_SETUP = "START_SETUP";
    private static final String APPLICATION_SIGNAL_SETUP_FINISHED = "SETUP_FINISHED";
    private static final String APPLICATION_SIGNAL_TERMINATED = "TERMINATED";


    private static ConfigurableApplicationContext preConstructedSpringContext = null;

    public static void setPreSpringContext(ConfigurableApplicationContext preSpringContext) {
        preConstructedSpringContext = preSpringContext;
    }

    public static ApplicationContext getPreSpringContext() {
        return preConstructedSpringContext;
    }

    protected void run(HarmonyWebAppBuilder webAppBuilder, String... args) {
        System.setOut(JavaLogInterceptorHolder.getInstance());
        LogWatcher.getInstance().start();

        final String springProfile = findSelectedProfile(args).orElseGet(() -> isStartingInDevContext() ? EnvironmentConstants.PROFILE_DEV : EnvironmentConstants.PROFILE_PROD);
        final EProfile profile = Arrays.stream(EProfile.values())
                .filter(p -> p.getSpringName().equals(springProfile))
                .findAny()
                .orElseThrow(() -> new InternalServerException(String.format("Profiel %s is not allowed", springProfile)));

        System.setProperty(EnvironmentConstants.ENV_SPRING_PROFILES_ACTIVE, springProfile);

        run(this.getClass(), profile, webAppBuilder, args);
    }

    private Optional<String> findSelectedProfile(String... runtimeArgs) {
        final String key = EnvironmentConstants.ENV_SPRING_PROFILES_ACTIVE;
        final String value = Arrays.stream(runtimeArgs)
                .filter(a -> a.contains(key))
                .map(a -> a.replace("--"+key, ""))
                .map(a -> a.replace(key, ""))
                .map(a -> a.replace("=", ""))
                .findAny()
                .orElseGet(() -> System.getProperty(key));

        return Optional.ofNullable(value);
    }

    private void run(Class<?> primarySource, EProfile profile, HarmonyWebAppBuilder webAppBuilder, String... args) {
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        printApplicationEvent(APPLICATION_SIGNAL_START_SETUP);
        AppStatusHolder.getInstance().resetStatus();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> printApplicationEvent(APPLICATION_SIGNAL_TERMINATED)));
        Assert.hasAnnotation(primarySource, SpringBootApplication.class).verify();
        this.initClassContextHolder();
        final ApplicationContext springContext = createSpringContext(primarySource, args, webAppBuilder);
        final AppContext appContext = new AppContext(springContext, profile);
        ContextHolder.initialize(appContext);
        springContext.getBean(AppInitializationRunner.class).initialize(appContext);
        AppStatusHolder.getInstance().resetStatus();
        printServerInfo(springContext);
        printApplicationEvent(APPLICATION_SIGNAL_SETUP_FINISHED);
        stopWatch.stop();
        logApplicationStartupTime(stopWatch, appContext);
    }

    private static synchronized ApplicationContext createSpringContext(Class<?> mainClass, String[] args, HarmonyWebAppBuilder webAppBuilder) {
        Class<?>[] primarySources = {mainClass, AbstractAppMain.class};
        return (new SpringApplication(primarySources) {
            @Override
            protected ConfigurableApplicationContext createApplicationContext() {
                ConfigurableApplicationContext applicationContext = super.createApplicationContext();
                applicationContext.getBeanFactory().registerSingleton("projectHandlerMethodAuthorizationResolver", (HandlerMethodAuthorizationResolver) handlerMethod -> {
                    ApiAnnotationAuthorizationResolver<?> apiAnnotationAuthorizationResolver = webAppBuilder.getApiAnnotationAuthorizationResolver();
                    if(apiAnnotationAuthorizationResolver != null) {
                        Annotation methodAnnotation = handlerMethod.getMethodAnnotation(webAppBuilder.getLocalApiAuthorizationAnnotation());
                        if(methodAnnotation == null)
                            return null;
                        return apiAnnotationAuthorizationResolver.resolveUntyped(methodAnnotation);
                    }
                    return ApplicationAccessRule.ofUnrestricted();
                });
                setPreSpringContext(applicationContext);
                return applicationContext;
            }
        }).run(args);
    }

    protected void initClassContextHolder() {
        ClassContextHolder.init(this.getClass().getPackageName(), AbstractAppMain.class.getPackageName());
    }

    protected void test() {

    }

    protected HarmonyWebAppBuilder prepare() {
        return new HarmonyWebAppBuilder(this);
    }

    private void printServerInfo(ApplicationContext springContext) {
        final FrontendInfo frontendInfo = springContext.getBean(FrontendInfo.class);
        springContext.getBean(InstanceInfoPresenter.class).printInfo(frontendInfo);
    }

    private static void printApplicationEvent(String event) {
        log.info("APPLICATION_SIGNAL[{}]", event);
    }

    private static boolean isStartingInDevContext() {
        final String resourcePath = Objects.requireNonNull(AbstractAppMain.class.getResource(AbstractAppMain.class.getSimpleName() + ".class")).toString();
        return resourcePath.startsWith("file");
    }

    protected void logApplicationStartupTime(StopWatch stopWatch, AppContext appContext) {
        log.info("Application is started in " + stopWatch.getDurationAsReadableString());
        final AppApplicationStartupLog logEntity = new AppApplicationStartupLog(DateUtils.getDateTimeNow(), stopWatch.getDurationMillis(), stopWatch.getDurationAsReadableString());
        appContext.getBean(ApplicationStartupLogService.class).saveEntity(logEntity);
    }



}

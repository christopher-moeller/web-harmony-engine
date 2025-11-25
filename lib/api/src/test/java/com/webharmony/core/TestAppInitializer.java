package com.webharmony.core;

import com.webharmony.core.api.rest.model.SecureKeyDto;
import com.webharmony.core.configuration.EApplicationStatus;
import com.webharmony.core.configuration.EProfile;
import com.webharmony.core.context.AppContext;
import com.webharmony.core.context.AppInitializationRunner;
import com.webharmony.core.context.AppStatusHolder;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.data.enums.ECoreRegistry;
import com.webharmony.core.data.jpa.model.AppRegistryItem;
import com.webharmony.core.service.EntityService;
import com.webharmony.core.service.InMemoryDataService;
import com.webharmony.core.testutils.TestFixturesInitializer;
import com.webharmony.core.utils.CollectionUtils;
import com.webharmony.core.utils.JacksonUtils;
import com.webharmony.core.utils.dev.LocalDevProperties;
import com.webharmony.core.utils.dev.SecureKeysProperties;
import com.webharmony.core.utils.reflection.classcontext.ClassContextHolder;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;


@EnableAutoConfiguration
@Configuration
public class TestAppInitializer implements ApplicationContextAware {

    @Value("classpath:LOCAL_TEST_PROPERTIES.json")
    private Resource localTestProperties;


    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        ClassContextHolder.init(TestAppInitializer.class.getPackageName(), AbstractAppMain.class.getPackageName());
        AppContext appContext = createAppContext(applicationContext);
        ContextHolder.initialize(appContext);
        applicationContext.getBean(AppInitializationRunner.class).initialize(appContext);
        activateHttpRequestsInRegistry(applicationContext);
        initLocalDevProperties(appContext);
        AppStatusHolder.getInstance().setNewStatus(EApplicationStatus.OK, "");
        appContext.getBean(TestFixturesInitializer.class).init();
    }

    private void activateHttpRequestsInRegistry(ApplicationContext applicationContext) {
        final AppRegistryItem entity = ECoreRegistry.BLOCK_ALL_HTTP_REQUESTS.getEntity();
        entity.setValue(false);
        applicationContext.getBean(EntityService.class).saveEntityGeneric(entity);
    }

    private AppContext createAppContext(ApplicationContext springContext) {
        return new AppContext(springContext, EProfile.TEST);
    }

    @SneakyThrows
    public void initLocalDevProperties(AppContext appContext) {

        InMemoryDataService inMemoryDataService = appContext.getBean(InMemoryDataService.class);

        final SecureKeysProperties secureKeys = JacksonUtils.createDefaultJsonMapper().readValue(localTestProperties.getFile(), LocalDevProperties.class)
                .getSecureKeys();

        for (SecureKeyDto secureKeyDto : CollectionUtils.emptyListIfNull(secureKeys.getEntries())) {
            inMemoryDataService.getAllEntries(SecureKeyDto.class)
                    .stream()
                    .filter(e -> e.getName().equals(secureKeyDto.getName()))
                    .findAny()
                    .ifPresent(e -> e.setKey(secureKeyDto.getKey()));
        }

    }
}

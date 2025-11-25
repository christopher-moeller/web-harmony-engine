package com.webharmony.core.context;

import com.webharmony.core.configuration.EProfile;
import com.webharmony.core.configuration.security.WebSecurityConfiguration;
import com.webharmony.core.service.*;
import com.webharmony.core.service.appresources.AppResourceService;
import com.webharmony.core.service.appviewmodels.ViewModelService;
import com.webharmony.core.service.authentication.AuthenticationService;
import com.webharmony.core.service.authentication.JwtService;
import com.webharmony.core.service.data.ActorRightService;
import com.webharmony.core.service.data.CronJobService;
import com.webharmony.core.service.data.WebContentCrudService;
import com.webharmony.core.service.i18n.I18nDataTransferService;
import com.webharmony.core.service.i18n.I18nKeyEntryService;
import com.webharmony.core.service.tasks.ServerTaskService;
import com.webharmony.core.utils.dev.helperapp.DevHelperApp;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class AppInitializationRunner {

    public void initialize(AppContext appContext) {
        this.clearUserCache(appContext);
        this.initJwtContext(appContext);
        this.initDevContext();
        this.initPersistenceEnums(appContext);
        this.initSecureKeys(appContext);
        this.initCronJobs(appContext);
        this.initI18nKeyEntries(appContext);
        this.initI18nTranslations(appContext);
        this.validateWebApiSecurity(appContext);
        this.initOptionContainers(appContext);
        this.initWebContents(appContext);
        this.deleteActiveTokens(appContext);
        this.initServerTasks(appContext);
        this.initServerInfo(appContext);
    }

    private void clearUserCache(AppContext appContext) {
        appContext.getBean(ActorService.class).clearCache();
    }

    private void initJwtContext(AppContext appContext) {
        appContext.getBean(JwtService.class).initKey(appContext.getSpringContext());
    }

    private void initDevContext() {
        if(ContextHolder.getContext().isProfileActive(EProfile.DEV)) {
            DevHelperApp.getInstance().show();
        }
    }


    private void initPersistenceEnums(AppContext appContext) {
        final PersistentEnumService persistentEnumService = appContext.getBean(PersistentEnumService.class);
        persistentEnumService.initAllEnumEntities();
        appContext.getBean(AuthenticationService.class).initDefaultUsers();
        appContext.getBean(ActorRightService.class).initRightCache(persistentEnumService.getPersistenceEnumEntityMap());
    }

    private void initSecureKeys(AppContext appContext) {
        appContext.getBean(SecureKeyService.class).init();
    }

    private void initI18nKeyEntries(AppContext appContext) {
        appContext.getBean(I18nKeyEntryService.class).synchronizeEntriesWithDatabase();
    }

    private void initI18nTranslations(AppContext appContext) {
        appContext.getBean(I18nDataTransferService.class).synchronizeTranslations();
    }

    private void validateWebApiSecurity(AppContext appContext) {
        appContext.getBean(WebSecurityConfiguration.class).validateRestApis(appContext.getSpringContext());
    }

    private void initOptionContainers(AppContext appContext) {
        appContext.getBean(SelectableOptionContainerService.class).initOptionContainers(appContext);
        appContext.getBean(AppResourceService.class).init();
        appContext.getBean(ViewModelService.class).init();
    }

    private void initWebContents(AppContext appContext) {
        appContext.getBean(WebContentCrudService.class).initDefaultWebContents();
    }

    private void deleteActiveTokens(AppContext appContext) {
        if(!ContextHolder.getContext().isProfileActive(EProfile.DEV)) {
            appContext.getBean(TokenService.class).deleteAllTokens();
        }
    }

    private void initCronJobs(AppContext appContext) {
        appContext.getBean(CronJobService.class).init(appContext);
    }

    private void initServerTasks(AppContext appContext) {
        appContext.getBean(ServerTaskService.class).init();
    }

    private void initServerInfo(AppContext appContext) {
        final ServerInfo serverInfo = appContext.getBean(ServerInfo.class);
        serverInfo.init(appContext);
        final FrontendInfo frontendInfo = appContext.getBean(FrontendInfo.class);
        final Environment environment = appContext.getSpringContext().getEnvironment();
        frontendInfo.init(environment, serverInfo);
    }

}

package com.webharmony.core.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webharmony.core.api.rest.model.LanguageInfo;
import com.webharmony.core.api.rest.model.VersionDetail;
import com.webharmony.core.configuration.EProfile;
import com.webharmony.core.configuration.SpringWebConfiguration;
import com.webharmony.core.configuration.SwaggerConfiguration;
import com.webharmony.core.configuration.utils.EnvironmentConstants;
import com.webharmony.core.configuration.utils.ResourceConstants;
import com.webharmony.core.i18n.EI18nLanguage;
import com.webharmony.core.utils.assertions.Assert;
import com.webharmony.core.utils.exceptions.InternalServerException;
import com.webharmony.core.utils.exceptions.NotFoundException;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.server.WebServer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Stream;

@Component
public class ServerInfo {

    @Getter
    private Boolean usesSSL;

    @Getter
    private String protocol;

    @Getter
    private Boolean hasCustomDomain;

    @Getter
    private String callableDomainOrIP;

    @Getter
    private Integer port;

    @Getter
    private String backendUrl;

    @Getter
    private String swaggerUrl;

    @Getter
    private boolean serverIsActive = false;

    @Value("classpath:" + ResourceConstants.PATH_API_CORE_VERSION_JSON)
    private Resource apiCoreVersionFile;

    @Value("classpath:" + ResourceConstants.PATH_API_PROJECT_VERSION_JSON)
    private Resource apiProjectVersionFile;

    @Getter
    @Value("classpath:" + ResourceConstants.PATH_LOGO_SVG)
    private Resource logoImageFile;

    private void init(Environment environment, WebServer webServer) {
        this.usesSSL = createUsesSsl(environment);
        this.protocol = createProtocol(this.usesSSL);
        this.hasCustomDomain = createHasCustomDomain(environment);
        this.callableDomainOrIP = createCallableDomainOrIP(this.hasCustomDomain, environment);
        this.port = createPort(webServer);
        this.backendUrl = createBackendUrl(this.protocol, this.callableDomainOrIP, port);
        this.swaggerUrl = createSwaggerUrl(this.backendUrl);
        this.serverIsActive = true;
    }

    private boolean createUsesSsl(Environment environment) {
        return Boolean.TRUE.equals(environment.getProperty(EnvironmentConstants.ENV_BACKEND_USE_SSL, Boolean.class));
    }

    private String createProtocol(boolean usesSSL) {
        return usesSSL ? "https" : "http";
    }

    private boolean createHasCustomDomain(Environment environment) {
        return Boolean.TRUE.equals(environment.getProperty(EnvironmentConstants.ENV_BACKEND_USE_CUSTOM_DOMAIN, Boolean.class));
    }

    @SneakyThrows
    private String createCallableDomainOrIP(boolean hasCustomDomain, Environment environment) {
        return hasCustomDomain ? environment.getProperty(EnvironmentConstants.ENV_BACKEND_CUSTOM_DOMAIN, String.class) : getLocalIp();
    }

    @SneakyThrows
    private String getLocalIp() {
        String defaultAddress = InetAddress.getLocalHost().getHostAddress();
        if(!defaultAddress.startsWith("127"))
            return defaultAddress;

        Enumeration<?> en = NetworkInterface.getNetworkInterfaces();
        while(en.hasMoreElements()){
            NetworkInterface ni=(NetworkInterface) en.nextElement();
            Enumeration<?> ee = ni.getInetAddresses();
            while(ee.hasMoreElements()) {
                InetAddress ia= (InetAddress) ee.nextElement();
                if(ia instanceof Inet4Address) {
                    String hostAddress = ia.getHostAddress();
                    if(!hostAddress.startsWith("127"))
                        return ia.getHostAddress();
                }
            }
        }

        throw new InternalServerException("Cannot find local ip address");
    }

    private int createPort(WebServer webServer) {
        final Integer localPort;
        if(webServer != null) {
            localPort = webServer.getPort();
        } else {
            localPort = ContextHolder.getContext().getSpringContext().getBean(ServerProperties.class).getPort();
        }
        Assert.isNotNull(localPort).verify();
        return localPort;
    }

    private String createBackendUrl(String protocol, String domain, int port) {
        final String effectiveDomain = ContextHolder.getContext().isProfileActive(EProfile.DEV) ? "localhost" : domain;
        return String.format("%s://%s%s", protocol, effectiveDomain, port == 80 ? "" : ":"+port);
    }

    private String createSwaggerUrl(String backendUrl) {
        return String.join("/", backendUrl, SwaggerConfiguration.SWAGGER_UI_PATH);
    }

    public void init(AppContext appContext) {
        final Environment environment = appContext.getSpringContext().getEnvironment();
        final WebServer webServer = appContext.getBean(SpringWebConfiguration.class).getWebServer();
        this.init(environment, webServer);
    }

    public VersionDetail getApiCoreVersionDetail() {
        return readVersionDetailByResource(this.apiCoreVersionFile);
    }

    public VersionDetail getApiProjectVersionDetail() {
        return readVersionDetailByResource(this.apiProjectVersionFile);
    }

    @SneakyThrows
    private VersionDetail readVersionDetailByResource(Resource resource) {
        return new ObjectMapper().readValue(resource.getInputStream(), VersionDetail.class);
    }

    public List<LanguageInfo> getAvailableLanguages() {
        return Stream.of(EI18nLanguage.values()).map(this::createLanguageInfoByI18nEnum).toList();
    }

    public LanguageInfo getLanguageByIdOrThrow(String languageId) {
        return getAvailableLanguages()
                .stream()
                .filter(language -> language.getId().equals(languageId))
                .findAny()
                .orElseThrow(() -> new NotFoundException(String.format("Language with id '%s' not found", languageId)));
    }

    private LanguageInfo createLanguageInfoByI18nEnum(EI18nLanguage i18nLanguage) {
        final LanguageInfo languageInfo = new LanguageInfo();
        languageInfo.setId(i18nLanguage.getKey());
        languageInfo.setLabel(i18nLanguage.getLanguageLabel().getI18n().build());
        languageInfo.setDateFormatTemplate(i18nLanguage.getDateFormatTemplate());
        return languageInfo;
    }

}

package com.webharmony.core.api.rest.controller;

import com.webharmony.core.api.rest.controller.utils.AbstractBaseController;
import com.webharmony.core.api.rest.controller.utils.ApiAuthentication;
import com.webharmony.core.api.rest.controller.utils.ApiAuthenticationType;
import com.webharmony.core.api.rest.model.LanguageInfo;
import com.webharmony.core.api.rest.model.ServerApiBaseInfo;
import com.webharmony.core.api.rest.model.VersionDetail;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.context.ServerInfo;
import com.webharmony.core.data.enums.ECoreRegistry;
import com.webharmony.core.i18n.EI18nLanguage;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@ApiController("api/serverInfo")
public class ServerInfoController extends AbstractBaseController {

    private static final String SLASH = "/";

    private final ServerInfo serverInfo;

    public ServerInfoController(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    @CrossOrigin(origins = "*")
    @GetMapping("baseApi")
    @ApiAuthentication(ApiAuthenticationType.PUBLIC)
    public ResponseEntity<ServerApiBaseInfo> baseApiInfo() {

        ServerApiBaseInfo baseApiInfo = new ServerApiBaseInfo();
        final String apiBasePath = serverInfo.getBackendUrl() + SLASH;
        baseApiInfo.setApiBasePath(apiBasePath);
        baseApiInfo.setProfile(ContextHolder.getContext().getProfile());

        baseApiInfo.setServerIsAvailable(serverInfo.getServerIsActive());

        return ResponseEntity.ok(baseApiInfo);
    }

    @GetMapping(value = "version/core", produces="application/json")
    @ApiAuthentication(ApiAuthenticationType.PUBLIC)
    public ResponseEntity<VersionDetail> getCoreVersion() {
        return ResponseEntity.ok(serverInfo.getApiCoreVersionDetail());
    }

    @GetMapping(value = "version/project", produces="application/json")
    @ApiAuthentication(ApiAuthenticationType.PUBLIC)
    public ResponseEntity<VersionDetail> getProjectVersion() {
        return ResponseEntity.ok(serverInfo.getApiProjectVersionDetail());
    }

    @GetMapping(value = "logo", produces = "image/svg+xml")
    @ApiAuthentication(ApiAuthenticationType.PUBLIC)
    public ResponseEntity<Resource> getProjectLogo() {
        Resource logoImageFile = serverInfo.getLogoImageFile();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "image/svg+xml");
        return new ResponseEntity<>(logoImageFile, headers, HttpStatus.OK);
    }

    @GetMapping(value = "languages", produces="application/json")
    @ApiAuthentication(ApiAuthenticationType.PUBLIC)
    public ResponseEntity<List<LanguageInfo>> getLanguages() {
        return ResponseEntity.ok(serverInfo.getAvailableLanguages());
    }

    @GetMapping(value = "languages/{id}", produces="application/json")
    @ApiAuthentication(ApiAuthenticationType.PUBLIC)
    public ResponseEntity<LanguageInfo> getLanguageById(@PathVariable("id") String id) {
        return ResponseEntity.ok(serverInfo.getLanguageByIdOrThrow(id));
    }

    @GetMapping(value = "languages/default", produces="application/json")
    @ApiAuthentication(ApiAuthenticationType.PUBLIC)
    public ResponseEntity<LanguageInfo> getDefaultLanguage() {
        final EI18nLanguage applicationLanguage = ECoreRegistry.I18N_DEFAULT_LANGUAGE.getTypedValue(EI18nLanguage.class);
        return ResponseEntity.ok(serverInfo.getLanguageByIdOrThrow(applicationLanguage.getKey()));
    }
}

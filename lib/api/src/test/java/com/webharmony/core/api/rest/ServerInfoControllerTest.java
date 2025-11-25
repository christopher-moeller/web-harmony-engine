package com.webharmony.core.api.rest;

import com.webharmony.core.AbstractApiTest;
import com.webharmony.core.api.rest.controller.ServerInfoController;
import com.webharmony.core.api.rest.model.ServerApiBaseInfo;
import com.webharmony.core.api.rest.model.VersionDetail;
import com.webharmony.core.configuration.EProfile;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ServerInfoControllerTest extends AbstractApiTest<ServerInfoController> {

    @Test
    void shouldGetBaseInfo() {
        ServerApiBaseInfo serverApiBaseInfo = assertOkResponse(ServerInfoController::baseApiInfo);
        assertThat(serverApiBaseInfo.getServerIsAvailable()).isTrue();
        assertThat(serverApiBaseInfo.getApiBasePath()).isNotEmpty();
        assertThat(serverApiBaseInfo.getProfile()).isEqualTo(EProfile.TEST);
    }

    @Test
    void shouldGetCoreVersion() {
        VersionDetail versionDetail = assertOkResponse(ServerInfoController::getCoreVersion);
        assertThat(versionDetail.getVersion()).isEqualTo("0.0.1");
        assertThat(versionDetail.getBuild()).isOne();
    }

    @Test
    void shouldGetProjectVersion() {
        VersionDetail versionDetail = assertOkResponse(ServerInfoController::getProjectVersion);
        assertThat(versionDetail.getVersion()).isEqualTo("0.0.1");
        assertThat(versionDetail.getBuild()).isOne();
    }

}

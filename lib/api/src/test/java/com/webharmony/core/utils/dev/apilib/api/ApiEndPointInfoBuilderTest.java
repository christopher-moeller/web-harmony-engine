package com.webharmony.core.utils.dev.apilib.api;

import com.webharmony.core.AbstractBaseTest;
import com.webharmony.core.api.rest.controller.AppResourceController;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ApiEndPointInfoBuilderTest extends AbstractBaseTest {

    @Test
    void shouldGetAllApiEndpoints() {

        final ApiEndPointInfoBuilder builder = new ApiEndPointInfoBuilder();
        final List<ApiEndpointInfo> allApiEndpoints = builder.getAllApiEndpoints();

        final ApiEndpointInfo appresourcesEndpoint = allApiEndpoints.stream()
                .filter(apiEndpointInfo -> apiEndpointInfo.getId().equals("GET_/api/appresources/{resourceName}"))
                .findAny()
                .orElseThrow();

        assertThat(appresourcesEndpoint.getControllerClass()).isEqualTo(AppResourceController.class);
        assertThat(appresourcesEndpoint.getMethodName()).isEqualTo("getResourceByName");
        assertThat(appresourcesEndpoint.getRequestBodyType()).isNull();
        assertThat(appresourcesEndpoint.getResponseType()).hasToString("org.springframework.http.ResponseEntity<com.webharmony.core.service.appresources.utils.CrudResourceInfoDto>");

        final Map<String, Class<?>> pathVariableTypes = appresourcesEndpoint.getPathVariableTypes();
        assertThat(pathVariableTypes.size()).isOne();
        assertThat(pathVariableTypes).containsEntry("resourceName", String.class);

        assertThat(appresourcesEndpoint.getQueryParameterTypes()).isEmpty();

        final ApiEndpointInfo i18nEntityAttributeExportEndpoint = allApiEndpoints.stream()
                .filter(apiEndpointInfo -> apiEndpointInfo.getId().equals("GET_/api/i18nEntityAttribute/export"))
                .findAny()
                .orElseThrow();

        assertThat(i18nEntityAttributeExportEndpoint.getRequestBodyType()).isNull();
        final Map<String, Class<?>> queryParameterTypes = i18nEntityAttributeExportEndpoint.getQueryParameterTypes();
        assertThat(queryParameterTypes)
                .hasSize(2)
                .containsEntry("includeSubprojectEntries", Boolean.TYPE)
                .containsEntry("includeCoreEntries", Boolean.TYPE);

    }
}
package com.webharmony.core.context;

import com.webharmony.core.utils.UrlBuilder;
import com.webharmony.core.utils.tuple.Tuple2;
import lombok.Getter;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Getter
@Component
public class FrontendInfo {

    private String frontendUrl;

    private ServerInfo serverInfo;

    private boolean isInitialized = false;

    public void init(Environment environment, ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
        this.frontendUrl = createFrontendUrl(environment);
        this.isInitialized = true;
    }

    @SafeVarargs
    public final String buildPathToFrontend(String subPath, Tuple2<String, Object>... queryParameters) {
        final UrlBuilder builder = UrlBuilder.ofDomain(this.frontendUrl);
        if(subPath != null)
            builder.withPath(subPath);

        for (Tuple2<String, Object> parameter : queryParameters) {
            builder.withQuery(parameter.getType1(), parameter.getType2());
        }

        return builder.build();
    }

    private String createFrontendUrl(Environment environment) {
        return getDefinedCustomFrontendUrl(environment);
    }

    private String getDefinedCustomFrontendUrl(Environment environment) {
        return environment.getProperty("frontend.url");
    }

}

package com.webharmony.core.context;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@Slf4j
public class InstanceInfoPresenter {

    enum InfoElement {

        FRONTEND_URL("Frontend URL", InfoElement::getFrontendURL),
        BACKEND_URL("Backend URL", info -> getBackendUrl(info.getServerInfo())),
        SWAGGER_URL("SWAGGER URL", info -> getSwaggerUrl(info.getServerInfo())),
        PID("PID", info -> getPid())
        ;

        @Getter
        private final String label;
        private final Function<FrontendInfo, String> valueResolver;

        private String cachedResult;

        InfoElement(String label, Function<FrontendInfo, String> valueResolver) {
            this.label = label;
            this.valueResolver = valueResolver;
        }

        public String getValue(FrontendInfo info) {
            if(cachedResult == null)
                cachedResult = this.valueResolver.apply(info);

            return cachedResult;
        }

        private static String getPid() {
            return ProcessHandle.current().pid() + "";
        }

        private static String getSwaggerUrl(ServerInfo serverInfo) {
            return serverInfo.getSwaggerUrl();
        }

        private static String getBackendUrl(ServerInfo serverInfo) {
            return serverInfo.getBackendUrl();
        }

        private static String getFrontendURL(FrontendInfo frontendInfo) {
            return frontendInfo.getFrontendUrl();
        }
    }

    public void printInfo(FrontendInfo frontendInfo) {

        final String leftAlignFormat = "| %-15s | %-60s |%n";
        final String separatorFormat = "+--------------------------------------------------------------------------------+%n";

        StringBuilder builder = new StringBuilder()
                .append(String.format("%n"))
                .append(String.format(separatorFormat))
                .append(String.format("| INSTANCE INFO                                                                  |%n"))
                .append(String.format(separatorFormat));

        for(InfoElement infoElement : InfoElement.values()) {
            String label = infoElement.getLabel();
            String value = infoElement.getValue(frontendInfo);
            builder.append(String.format(leftAlignFormat, label, value));
        }

        builder.append(String.format(separatorFormat));

        log.info(builder.toString());
    }
}

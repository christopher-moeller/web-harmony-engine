package com.webharmony.core.utils.dev.fepages.model;

import lombok.Getter;

@Getter
public class FrontendComponent {

    private static final String GENERIC_NAVIGATION_COMPONENT = "/components/view/GenericNavigationPage.vue";

    private final String componentPath;

    private final Boolean isCoreComponent;

    private FrontendComponent(String componentPath, Boolean isCoreComponent) {
        this.componentPath = componentPath;
        this.isCoreComponent = isCoreComponent;
    }

    public static FrontendComponent definedInFrontend() {
        return new FrontendComponent(null, null);
    }

    public static FrontendComponent genericNavigationComponent() {
        return new FrontendComponent(GENERIC_NAVIGATION_COMPONENT, true);
    }

    public static FrontendComponent ofCorePath(String path) {
        return new FrontendComponent(path, true);
    }

    public static FrontendComponent ofProjectPath(String path) {
        return new FrontendComponent(path, false);
    }
}

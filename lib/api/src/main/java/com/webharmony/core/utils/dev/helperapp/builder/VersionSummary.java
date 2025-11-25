package com.webharmony.core.utils.dev.helperapp.builder;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VersionSummary {

    private VersionDetail apiCoreVersion;
    private VersionDetail apiProjectVersion;

    private VersionDetail uiCoreVersion;
    private VersionDetail uiProjectVersion;

    public void validate(VersionSummary expectedVersionSummary) {

        try {
            apiCoreVersion.validate(expectedVersionSummary.getApiCoreVersion());
        } catch (Exception e) {
            throw new IllegalStateException("Error in version details of API Core Version: "+e.getMessage(), e);
        }

        try {
            apiProjectVersion.validate(expectedVersionSummary.getApiProjectVersion());
        } catch (Exception e) {
            throw new IllegalStateException("Error in version details of API Project Version: "+e.getMessage(), e);
        }

        try {
            uiCoreVersion.validate(expectedVersionSummary.getUiCoreVersion());
        } catch (Exception e) {
            throw new IllegalStateException("Error in version details of UI Core Version: "+e.getMessage(), e);
        }

        try {
            uiProjectVersion.validate(expectedVersionSummary.getUiProjectVersion());
        } catch (Exception e) {
            throw new IllegalStateException("Error in version details of UI Project Version: "+e.getMessage(), e);
        }
    }
}

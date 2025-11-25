package com.webharmony.core.utils.dev.helperapp.builder;

import com.webharmony.core.utils.exceptions.InternalServerException;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VersionDetail {

    private int build;
    private String version;

    public void validate(VersionDetail expectedVersion) {

        if(!this.version.equals(expectedVersion.getVersion()))
            throw new InternalServerException(String.format("Version mismatch: Expected: %s actual: %s", expectedVersion.getVersion(), this.version));

        if(this.build != expectedVersion.getBuild())
            throw new InternalServerException(String.format("Build mismatch: Expected: %s actual: %s", expectedVersion.getBuild(), this.build));

    }
}

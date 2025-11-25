package com.webharmony.core.utils.dev;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocalDevProperties {

    private SecureKeysProperties secureKeys;
    private String defaultAdminPassword;
    private String frontendCoreDevPath;
    private String frontendProjectDevPath;

}

package com.webharmony.core.utils.dev;

import com.webharmony.core.api.rest.model.SecureKeyDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SecureKeysProperties {

    private boolean importEntries;
    private List<SecureKeyDto> entries;
}

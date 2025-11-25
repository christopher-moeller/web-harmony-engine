package com.webharmony.core.data.jpa.model.userregistration;

import com.webharmony.core.data.jpa.utils.SqlXml;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NestedUserRegistrationStateDataField extends SqlXml<AppUserRegistrationStateData> {

    public NestedUserRegistrationStateDataField(AppUserRegistrationStateData data) {
        this.setValue(data);
    }
}

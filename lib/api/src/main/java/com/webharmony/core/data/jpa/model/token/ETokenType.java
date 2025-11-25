package com.webharmony.core.data.jpa.model.token;

import lombok.Getter;

@Getter
public enum ETokenType {

    REGISTRATION_INVITATION(true),
    REGISTRATION_EMAIL_CONFIRMATION(true),
    USER_ACCESS_TOKEN(false),
    USER_RESET_PASSWORD_TOKEN(true);

    private final boolean isOneTimeToken;

    ETokenType(boolean isOneTimeToken) {
        this.isOneTimeToken = isOneTimeToken;
    }
}

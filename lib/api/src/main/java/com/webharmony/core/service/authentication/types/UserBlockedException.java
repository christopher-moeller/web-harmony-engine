package com.webharmony.core.service.authentication.types;

import com.webharmony.core.utils.exceptions.InternalServerException;
import lombok.Getter;

@Getter
@SuppressWarnings("java:S110")
public class UserBlockedException extends InternalServerException {

    private final transient UserBlockedEntry userBlockedEntry;

    public UserBlockedException(UserBlockedEntry userBlockedEntry) {
        super("User is blocked");
        this.userBlockedEntry = userBlockedEntry;
    }

}

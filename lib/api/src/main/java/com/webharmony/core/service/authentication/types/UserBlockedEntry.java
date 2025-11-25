package com.webharmony.core.service.authentication.types;

import com.webharmony.core.utils.DateUtils;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
public class UserBlockedEntry {

    @Setter
    private LocalDateTime blockedFrom;
    private int loginErrorAttempts = 0;

    private LocalDateTime blockedUntil;

    public void addLoginErrorAttempt() {
        this.loginErrorAttempts++;
        int secondsToWaitFromBegin = (int) Math.pow(2, loginErrorAttempts);
        this.blockedUntil = DateUtils.getDateTimeNow().plusSeconds(secondsToWaitFromBegin);
    }

    public boolean isStillBlockedByTime() {
        return DateUtils.getDateTimeNow().isBefore(blockedUntil);
    }

}

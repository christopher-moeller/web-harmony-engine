package com.webharmony.core.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateUtils {

    private DateUtils() {

    }

    public static ZoneId getZoneId() {
        return ZoneId.systemDefault();
    }

    public static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(getZoneId()).toInstant());
    }

    public static LocalDateTime getDateTimeNow() {
        return LocalDateTime.now();
    }

    public static boolean dateAIsNotAfterDateB(LocalDate dateA, LocalDate dateB) {
        if(dateA == null || dateB == null) {
            return true;
        }

        return !dateA.isAfter(dateB);
    }
}

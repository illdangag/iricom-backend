package com.illdangag.iricom.core.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtils {
    private DateTimeUtils() {
    }

    public static long getLong(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static long getLong(Date date) {
        return date.getTime();
    }

    public static long getLong(Calendar calendar) {
        return getLong(calendar.getTime());
    }
}

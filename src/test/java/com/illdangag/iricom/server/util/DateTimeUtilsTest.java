package com.illdangag.iricom.server.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

@DisplayName("날짜 처리 테스트")
public class DateTimeUtilsTest {
    @Test
    @Order(0)
    @DisplayName("LocalDateTime")
    public void testCase00() throws Exception {
        LocalDateTime nowLocalDateTime = LocalDateTime.now();
        DateTimeUtils.getLong(nowLocalDateTime);
    }

    @Test
    @Order(1)
    @DisplayName("Date")
    public void testCase01() throws Exception {
        Date nowDate = new Date();
        DateTimeUtils.getLong(nowDate);
    }

    @Test
    @Order(1)
    @DisplayName("Calendar")
    public void testCase02() throws Exception {
        Calendar nowCalendar = Calendar.getInstance();
        DateTimeUtils.getLong(nowCalendar);
    }
}

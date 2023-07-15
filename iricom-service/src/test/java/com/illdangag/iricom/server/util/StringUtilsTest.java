package com.illdangag.iricom.server.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

@Slf4j
@DisplayName("문자열 처리 테스트")
public class StringUtilsTest {

    @Test
    @Order(0)
    @DisplayName("기본")
    public void escapeTest00() {
        String result = StringUtils.escape("TEST");
        Assertions.assertEquals(result, "TEST");
    }

    @Test
    @Order(1)
    @DisplayName("단일 탈출 문자")
    public void escapeTest01() {
        String result = StringUtils.escape("TEST.TEST");
        Assertions.assertEquals(result, "TEST\\.TEST");
    }

    @Test
    @Order(2)
    @DisplayName("모든 문자가 탈출 문자")
    public void escapeTest02() {
        String result = StringUtils.escape("\\.?![]{}()<>*+-=^$|%");
        Assertions.assertEquals(result, "\\\\\\.\\?\\!\\[\\]\\{\\}\\(\\)\\<\\>\\*\\+\\-\\=\\^\\$\\|\\%");
    }
}

package com.illdangag.iricom.server.util;

public class StringUtils {
    private StringUtils() {
    }

    public static String escape(String text) {
        if (text != null && text.length() > 0) {
            return text.replaceAll("[\\W]", "\\\\$0");
        }
        return "";
    }
}

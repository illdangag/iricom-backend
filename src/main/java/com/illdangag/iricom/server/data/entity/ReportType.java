package com.illdangag.iricom.server.data.entity;

import lombok.Getter;

@Getter
public enum ReportType {
    HATE("hate"), // 혐오
    PORNOGRAPHY("pornography"), // 음란물
    POLITICAL("political"), // 정치
    ETC("etc"); // 기타

    private String text;

    ReportType(String text) {
        this.text = text;
    }
}

package com.illdangag.iricom.server.data.entity.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.illdangag.iricom.server.exception.IricomErrorCode;
import com.illdangag.iricom.server.exception.IricomException;
import lombok.Getter;

import java.util.Arrays;

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

    @JsonCreator
    public static ReportType setValue(String key) {
        return Arrays.stream(ReportType.values())
                .filter(value -> value.getText().equalsIgnoreCase(key))
                .findAny()
                .orElseThrow(() -> new IricomException(IricomErrorCode.INVALID_REQUEST, "Type is invalid. (hate, pornography, political, etc)"));
    }
}

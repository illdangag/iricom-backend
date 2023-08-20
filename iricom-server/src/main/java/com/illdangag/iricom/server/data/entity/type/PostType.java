package com.illdangag.iricom.server.data.entity.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.illdangag.iricom.server.exception.IricomErrorCode;
import com.illdangag.iricom.server.exception.IricomException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PostType {
    POST("post"),
    NOTIFICATION("notification");

    private String text;

    PostType(String text) {
        this.text = text;
    }

    @JsonCreator
    public static PostType setValue(String key) {
        return Arrays.stream(PostType.values())
                .filter(value -> value.getText().equalsIgnoreCase(key))
                .findAny()
                .orElseThrow(() -> new IricomException(IricomErrorCode.INVALID_REQUEST, "Type is invalid. (POST, NOTIFICATION)"));
    }
}

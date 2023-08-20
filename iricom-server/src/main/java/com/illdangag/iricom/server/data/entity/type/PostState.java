package com.illdangag.iricom.server.data.entity.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.illdangag.iricom.server.exception.IricomErrorCode;
import com.illdangag.iricom.server.exception.IricomException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PostState {
    TEMPORARY("temporary"),
    PUBLISH("publish");

    private String text;

    PostState(String text) {
        this.text = text;
    }

    @JsonCreator
    public static PostState setValue(String key) {
        return Arrays.stream(PostState.values())
                .filter(value -> value.getText().equalsIgnoreCase(key))
                .findAny()
                .orElseThrow(() -> new IricomException(IricomErrorCode.INVALID_REQUEST, "State is invalid. (publish, temporary)"));
    }
}

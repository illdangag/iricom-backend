package com.illdangag.iricom.storage.data.entity.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.illdangag.iricom.server.exception.IricomErrorCode;
import com.illdangag.iricom.server.exception.IricomException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum FileType {
    IMAGE("image");

    private String text;

    FileType(String text) {
        this.text = text;
    }

    @JsonCreator
    public static FileType setValue(String key) {
        return Arrays.stream(FileType.values())
                .filter(value -> value.getText().equalsIgnoreCase(key))
                .findAny()
                .orElseThrow(() -> new IricomException(IricomErrorCode.INVALID_REQUEST, "Type is invalid. (IMAGE)"));
    }
}

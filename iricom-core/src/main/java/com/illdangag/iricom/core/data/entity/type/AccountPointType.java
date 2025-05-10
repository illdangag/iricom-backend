package com.illdangag.iricom.core.data.entity.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.illdangag.iricom.core.exception.IricomErrorCode;
import com.illdangag.iricom.core.exception.IricomException;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum AccountPointType {
    PUBLISH_POST("publishPost", 1),
    CREATE_COMMENT("createComment", 1),
    EXTERNAL_POINT("externalPoint", 1);

    private String text;
    private long defaultPoint;

    AccountPointType(String text, long defaultPoint) {
        this.text = text;
        this.defaultPoint = defaultPoint;
    }

    @JsonCreator
    public static AccountPointType setValue(String key) {
        AccountPointType[] types = AccountPointType.values();

        return Arrays.stream(types)
                .filter(value -> value.getText().equalsIgnoreCase(key))
                .findAny()
                .orElseThrow(() -> {
                    List<String> textlist = Arrays.stream(types)
                            .map(AccountPointType::getText)
                            .collect(Collectors.toList());
                    return new IricomException(IricomErrorCode.INVALID_REQUEST, "Account point type is invalid. (" + String.join(",", textlist) + ")");
                });
    }
}

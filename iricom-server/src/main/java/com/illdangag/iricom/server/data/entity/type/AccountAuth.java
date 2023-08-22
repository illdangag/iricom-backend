package com.illdangag.iricom.server.data.entity.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.illdangag.iricom.server.exception.IricomErrorCode;
import com.illdangag.iricom.server.exception.IricomException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum AccountAuth {
    SYSTEM_ADMIN("systemAdmin"),
    BOARD_ADMIN("boardAdmin"),
    ACCOUNT("account"),
    UNREGISTERED_ACCOUNT("unregisteredAccount");

    private String text;

    AccountAuth(String text) {
        this.text = text;
    }

    @JsonCreator
    public static AccountAuth setValue(String key) {
        return Arrays.stream(AccountAuth.values())
                .filter(value -> value.getText().equalsIgnoreCase(key))
                .findAny()
                .orElseThrow(() -> new IricomException(IricomErrorCode.INVALID_REQUEST, "AccountAuth is invalid. (systemAdmin, boardAdmin, account, unregisteredAccount"));
    }
}

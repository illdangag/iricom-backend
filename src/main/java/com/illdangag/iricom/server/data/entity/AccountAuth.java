package com.illdangag.iricom.server.data.entity;

import lombok.Getter;

@Getter
public enum AccountAuth {
    SYSTEM_ADMIN("systemAdmin"),
    BOARD_ADMIN("boardAdmin"),
    ACCOUNT("account");

    private String text;

    AccountAuth(String text) {
        this.text = text;
    }
}

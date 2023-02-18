package com.illdangag.iricom.server.data.entity;

import lombok.Getter;

@Getter
public enum AccountType {
    SYSTEM_ADMIN("systemAdmin"),
    BOARD_ADMIN("boardAdmin"),
    ACCOUNT("account");

    private String text;

    AccountType(String text) {
        this.text = text;
    }
}

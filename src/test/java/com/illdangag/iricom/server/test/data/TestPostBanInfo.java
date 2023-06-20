package com.illdangag.iricom.server.test.data;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TestPostBanInfo {
    private TestAccountInfo banAccount;

    private TestPostInfo post;

    private String reason;
}

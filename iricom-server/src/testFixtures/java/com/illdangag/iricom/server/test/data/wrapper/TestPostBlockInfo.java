package com.illdangag.iricom.server.test.data.wrapper;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TestPostBlockInfo {
    private TestAccountInfo account;

    private TestPostInfo post;

    private String reason;
}

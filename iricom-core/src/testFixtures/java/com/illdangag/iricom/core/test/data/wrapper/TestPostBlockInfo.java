package com.illdangag.iricom.core.test.data.wrapper;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TestPostBlockInfo {
    private TestAccountInfo account;

    private TestPostInfo post;

    private String reason;
}

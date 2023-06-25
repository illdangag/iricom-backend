package com.illdangag.iricom.server.test.data;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TestUndisclosedInfo {
    private TestAccountInfo account;

    private TestBoardInfo board;
}

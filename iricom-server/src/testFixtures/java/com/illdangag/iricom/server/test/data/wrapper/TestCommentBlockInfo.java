package com.illdangag.iricom.server.test.data.wrapper;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TestCommentBlockInfo {
    private TestAccountInfo account;

    private TestCommentInfo comment;

    private String reason;
}

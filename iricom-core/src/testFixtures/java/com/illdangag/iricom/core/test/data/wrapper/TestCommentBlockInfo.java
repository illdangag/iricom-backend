package com.illdangag.iricom.core.test.data.wrapper;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TestCommentBlockInfo {
    private TestAccountInfo account;

    private TestCommentInfo comment;

    private String reason;
}

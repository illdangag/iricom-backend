package com.illdangag.iricom.server.test.data.wrapper;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TestCommentBanInfo {
    private TestAccountInfo banAccount;

    private TestCommentInfo comment;

    private String reason;
}

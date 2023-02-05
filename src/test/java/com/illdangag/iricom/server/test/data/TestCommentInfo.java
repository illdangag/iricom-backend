package com.illdangag.iricom.server.test.data;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TestCommentInfo {
    private String content;

    private TestAccountInfo creator;

    private TestPostInfo post;

    private TestCommentInfo referenceComment;

    @Builder.Default
    private boolean deleted = false;
}

package com.illdangag.iricom.server.test.data.wrapper;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class TestCommentInfo {
    @Setter
    private String id;

    private String content;

    private TestAccountInfo creator;

    private TestPostInfo post;

    private TestCommentInfo referenceComment;

    @Builder.Default
    private boolean deleted = false;
}

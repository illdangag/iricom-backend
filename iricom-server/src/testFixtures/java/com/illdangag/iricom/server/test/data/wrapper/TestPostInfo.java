package com.illdangag.iricom.server.test.data.wrapper;

import com.illdangag.iricom.server.data.entity.type.PostState;
import com.illdangag.iricom.server.data.entity.type.PostType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class TestPostInfo {
    @Setter
    private String id;

    private String title;

    private String content;

    private boolean isAllowComment;

    private PostType postType;

    private PostState postState;

    private TestAccountInfo creator;

    private TestBoardInfo board;
}

package com.illdangag.iricom.server.service.post;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.PostType;
import com.illdangag.iricom.server.data.request.PostInfoCreate;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.service.PostService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestAccountGroupInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Collections;

@DisplayName("service: 게시물 - 생성")
@Slf4j
public class PostServiceCreateTest extends IricomTestSuite {
    @Autowired
    private PostService postService;

    // 공개 게시판
    private final TestBoardInfo boardInfo00 = TestBoardInfo.builder()
            .title("boardInfo00").isEnabled(true).build();
    // 비공개 게시판
    private final TestBoardInfo undisclosedBoardInfo00 = TestBoardInfo.builder()
            .title("undisclosedBoardInfo00").isEnabled(true).undisclosed(true).build();

    // 계정 그룹
    private final TestAccountGroupInfo accountGroupInfo00 = TestAccountGroupInfo.builder()
            .title("accountGroupInfo00").description("description")
            .accountList(Collections.singletonList(common00)).boardList(Collections.singletonList(undisclosedBoardInfo00))
            .build();

    @Autowired
    public PostServiceCreateTest(ApplicationContext context) {
        super(context);

        addTestBoardInfo(boardInfo00, undisclosedBoardInfo00);
        addTestAccountGroupInfo(accountGroupInfo00);

        init();
    }

    @Test
    @DisplayName("닉네임이 등록되지 않은 사용자")
    public void postUnregisteredAccount() throws Exception {
        Account account = getAccount(unknown00);
        String boardId = getBoardId(boardInfo00);

        PostInfoCreate postInfoCreate = PostInfoCreate.builder()
                .title("Unregistered account post title")
                .content("contents...")
                .type(PostType.POST)
                .build();

        Assertions.assertThrows(IricomException.class, () -> {
            postService.createPostInfo(account, boardId, postInfoCreate);
        });
    }

    @Test
    @DisplayName("계정 그룹에 포함되지 않은 비공개 게시판")
    public void postUndisclosedBoard() throws Exception {
        Account account = getAccount(common01);
        String boardId = getBoardId(undisclosedBoardInfo00);

        PostInfoCreate postInfoCreate = PostInfoCreate.builder()
                .title("Unregistered account post title")
                .content("contents...")
                .type(PostType.POST)
                .build();

        Assertions.assertThrows(IricomException.class, () -> {
            postService.createPostInfo(account, boardId, postInfoCreate);
        });
    }

    @Test
    @DisplayName("계정 그룹에 포함된 비공개 게시판")
    public void postUndisclosedBoardInAccountGroup() throws Exception {
        Account account = getAccount(common00);
        String boardId = getBoardId(undisclosedBoardInfo00);

        PostInfoCreate postInfoCreate = PostInfoCreate.builder()
                .title("Unregistered account post title")
                .content("contents...")
                .type(PostType.POST)
                .build();

        postService.createPostInfo(account, boardId, postInfoCreate);
    }
}

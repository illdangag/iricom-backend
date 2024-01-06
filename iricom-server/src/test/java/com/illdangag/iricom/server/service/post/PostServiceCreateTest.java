package com.illdangag.iricom.server.service.post;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.type.PostType;
import com.illdangag.iricom.server.data.request.PostInfoCreate;
import com.illdangag.iricom.server.data.response.PostInfo;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.service.AccountService;
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
    @Autowired
    private AccountService accountService;

    @Autowired
    public PostServiceCreateTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("닉네임이 등록되지 않은 사용자")
    public void postUnregisteredAccount() {
        TestBoardInfo testBoardInfo = TestBoardInfo.builder()
                .title("testBoardInfo").isEnabled(true).build();

        addTestBoardInfo(testBoardInfo);
        init();

        Account account = getAccount(unknown00);
        String boardId = getBoardId(testBoardInfo);

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
    public void postUndisclosedBoard() {
        TestBoardInfo testBoardInfo = TestBoardInfo.builder()
                .title("testBoardInfo").isEnabled(true).undisclosed(true).build();
        addTestBoardInfo(testBoardInfo);
        init();

        Account account = getAccount(common01);
        String boardId = getBoardId(testBoardInfo);

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
    public void postUndisclosedBoardInAccountGroup() {
        TestBoardInfo testBoardInfo = TestBoardInfo.builder()
                .title("testBoardInfo").isEnabled(true).undisclosed(true).build();

        TestAccountGroupInfo testAccountGroupInfo = TestAccountGroupInfo.builder()
                .title("testAccountGroupInfo").description("description")
                .accountList(Collections.singletonList(common00)).boardList(Collections.singletonList(testBoardInfo))
                .build();

        addTestBoardInfo(testBoardInfo);
        addTestAccountGroupInfo(testAccountGroupInfo);
        init();

        Account account = getAccount(common00);
        String boardId = getBoardId(testBoardInfo);

        PostInfoCreate postInfoCreate = PostInfoCreate.builder()
                .title("Unregistered account post title")
                .content("contents...")
                .type(PostType.POST)
                .build();

        postService.createPostInfo(account, boardId, postInfoCreate);
    }

    @Test
    @DisplayName("게시물 발행 후 포인트 추가")
    public void addPostPoint() {
        TestBoardInfo testBoardInfo = TestBoardInfo.builder()
                .title("testBoardInfo").isEnabled(true).undisclosed(false).build();

        addTestBoardInfo(testBoardInfo);
        init();

        Account account = getAccount(common00);
        String boardId = getBoardId(testBoardInfo);
        long beforePoint = this.accountService.getAccountInfo(String.valueOf(account.getId())).getPoint();

        PostInfoCreate postInfoCreate = PostInfoCreate.builder()
                .title("add point")
                .content("contents...")
                .type(PostType.POST)
                .build();

        PostInfo postInfo = postService.createPostInfo(account, boardId, postInfoCreate);
        postService.publishPostInfo(account, postInfo.getBoardId(), postInfo.getId());

        long afterPoint = this.accountService.getAccountInfo(String.valueOf(account.getId())).getPoint();
        Assertions.assertTrue(beforePoint < afterPoint);
    }
}

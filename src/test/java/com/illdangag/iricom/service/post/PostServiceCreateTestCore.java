package com.illdangag.iricom.service.post;

import com.illdangag.iricom.core.data.entity.type.PostState;
import com.illdangag.iricom.core.data.entity.type.PostType;
import com.illdangag.iricom.core.data.request.PostInfoCreate;
import com.illdangag.iricom.core.data.response.PostInfo;
import com.illdangag.iricom.core.exception.IricomException;
import com.illdangag.iricom.core.service.AccountService;
import com.illdangag.iricom.core.service.PostService;
import com.illdangag.iricom.core.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.core.test.data.wrapper.TestBoardInfo;
import com.illdangag.iricom.core.test.data.wrapper.TestPostInfo;
import com.illdangag.iricom.IricomTestServiceSuite;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.transaction.Transactional;
import java.util.Collections;

@DisplayName("service: 게시물 - 생성")
@Slf4j
@Transactional
public class PostServiceCreateTestCore extends IricomTestServiceSuite {
    @Autowired
    private PostService postService;
    @Autowired
    private AccountService accountService;

    @Autowired
    public PostServiceCreateTestCore(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("닉네임이 등록되지 않은 사용자")
    public void postUnregisteredAccount() {
        // 계정 생성
        TestAccountInfo account = setRandomAccount(true);
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();

        PostInfoCreate postInfoCreate = PostInfoCreate.builder()
                .title("Unregistered account post title")
                .content("contents...")
                .type(PostType.POST)
                .build();

        Assertions.assertThrows(IricomException.class, () -> {
            postService.createPostInfo(account.getId(), board.getId(), postInfoCreate);
        });
    }

    @Test
    @DisplayName("발행되지 않은 공지사항 게시물을 일반 사용자가 발행")
    void publishPostNotBoardAdmin() {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard(Collections.singletonList(account));
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account, PostType.NOTIFICATION, PostState.TEMPORARY);
        // 게시판 관리자 삭제
        deleteBoardAdmin(board, Collections.singletonList(account));

        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            postService.publishPostInfo(account.getId(), board.getId(), post.getId());
        });
        Assertions.assertEquals("04000001", exception.getErrorCode());
    }

    @Test
    @DisplayName("계정 그룹에 포함되지 않은 비공개 게시판")
    public void postUndisclosedBoard() {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        TestAccountInfo other = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard(true, true);
        // 계정 그룹 생성
        setRandomAccountGroup(Collections.singletonList(account), Collections.singletonList(board));

        PostInfoCreate postInfoCreate = PostInfoCreate.builder()
                .title("Unregistered account post title")
                .content("contents...")
                .type(PostType.POST)
                .build();

        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            postService.createPostInfo(other.getId(), board.getId(), postInfoCreate);
        });
        Assertions.assertNotNull(exception);
        Assertions.assertEquals("03000000", exception.getErrorCode());
    }

    @Test
    @DisplayName("계정 그룹에 포함된 비공개 게시판")
    public void postUndisclosedBoardInAccountGroup() {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard(true, true);
        // 계정 그룹 생성
        setRandomAccountGroup(Collections.singletonList(account), Collections.singletonList(board));

        PostInfoCreate postInfoCreate = PostInfoCreate.builder()
                .title("Unregistered account post title")
                .content("contents...")
                .type(PostType.POST)
                .build();

        Assertions.assertDoesNotThrow(() -> {
            postService.createPostInfo(account.getId(), board.getId(), postInfoCreate);
        });
    }

    @Test
    @DisplayName("게시물 발행 후 포인트 추가")
    public void addPostPoint() {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();

        long beforePoint = this.accountService.getAccountInfo(account.getId()).getPoint();

        PostInfoCreate postInfoCreate = PostInfoCreate.builder()
                .title("add point")
                .content("contents...")
                .type(PostType.POST)
                .build();

        PostInfo postInfo = postService.createPostInfo(account.getId(), board.getId(), postInfoCreate);
        postService.publishPostInfo(account.getId(), postInfo.getBoardId(), postInfo.getId());

        long afterPoint = this.accountService.getAccountInfo(account.getId()).getPoint();
        Assertions.assertTrue(beforePoint < afterPoint);
    }
}

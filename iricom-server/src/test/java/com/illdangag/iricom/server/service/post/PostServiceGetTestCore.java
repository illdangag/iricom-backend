package com.illdangag.iricom.server.service.post;

import com.illdangag.iricom.core.data.entity.type.PostState;
import com.illdangag.iricom.core.data.entity.type.PostType;
import com.illdangag.iricom.core.data.response.PostInfo;
import com.illdangag.iricom.core.exception.IricomException;
import com.illdangag.iricom.core.service.PostService;
import com.illdangag.iricom.core.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.core.test.data.wrapper.TestBoardInfo;
import com.illdangag.iricom.core.test.data.wrapper.TestPostInfo;
import com.illdangag.iricom.server.IricomTestServiceSuite;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.transaction.Transactional;
import java.util.Collections;

@DisplayName("service: 게시물 - 조회")
@Slf4j
@Transactional
public class PostServiceGetTestCore extends IricomTestServiceSuite {
    @Autowired
    private PostService postService;

    @Autowired
    public PostServiceGetTestCore(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("공개된 게시판의 게시물을 권한 없이 조회")
    public void getDisclosedBoardPost() {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);

        PostInfo postInfo = postService.getPostInfo(board.getId(), post.getId(), PostState.PUBLISH, true);

        Assertions.assertNotNull(postInfo);
    }

    @Test
    @DisplayName("비공개 게시판의 게시물 조회")
    public void getUndisclosedBoardPost() {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard(true, true);
        // 계정 그룹 생성
        setRandomAccountGroup(Collections.singletonList(account), Collections.singletonList(board));
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);

        Assertions.assertThrows(IricomException.class, () -> {
            postService.getPostInfo(board.getId(), post.getId(), PostState.PUBLISH, true);
        });
    }

    @Test
    @DisplayName("차단된 게시물 조회")
    public void getBlockedPost() {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);
        // 게시물 차단
        setRandomPostBlock(post);

        PostInfo postInfo = postService.getPostInfo(board.getId(), post.getId(), PostState.PUBLISH, true);
        Assertions.assertNotNull(postInfo);
        Assertions.assertTrue(postInfo.getBlocked());
        Assertions.assertNull(postInfo.getContent());
    }

    @Test
    @DisplayName("권한 없이 게시물 조회")
    public void getPostNotAuth() {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);

        PostInfo postInfo = postService.getPostInfo(board.getId(), post.getId(), PostState.PUBLISH);
        Assertions.assertNotNull(postInfo);
    }

    @Test
    @DisplayName("권한 없이 임시 저장 게시물 조회")
    void getTemporaryPostNotAuth() {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account, PostType.POST, PostState.TEMPORARY);

        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            postService.getPostInfo(board.getId(), post.getId(), PostState.TEMPORARY);
        });
        Assertions.assertEquals("04000007", exception.getErrorCode());
        Assertions.assertEquals("Invalid authorization.", exception.getMessage());
    }
}

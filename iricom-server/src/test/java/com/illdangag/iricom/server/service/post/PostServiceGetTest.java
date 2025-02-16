package com.illdangag.iricom.server.service.post;

import com.illdangag.iricom.server.data.entity.type.PostState;
import com.illdangag.iricom.server.data.response.PostInfo;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.service.PostService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestPostInfo;
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
public class PostServiceGetTest extends IricomTestSuite {
    @Autowired
    private PostService postService;

    @Autowired
    public PostServiceGetTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("공개된 게시판의 게시물을 권한 없이 조회")
    public void getDisclosedBoardPost() {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        TestPostInfo post = this.setRandomPost(board, account);

        PostInfo postInfo = postService.getPostInfo(board.getId(), post.getId(), PostState.PUBLISH, true);

        Assertions.assertNotNull(postInfo);
    }

    @Test
    @DisplayName("비공개 게시판의 게시물 조회")
    public void getUndisclosedBoardPost() {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard(true, true);
        // 계정 그룹 생성
        this.setRandomAccountGroup(Collections.singletonList(account), Collections.singletonList(board));
        // 게시물 생성
        TestPostInfo post = this.setRandomPost(board, account);

        Assertions.assertThrows(IricomException.class, () -> {
            postService.getPostInfo(board.getId(), post.getId(), PostState.PUBLISH, true);
        });
    }

    @Test
    @DisplayName("차단된 게시물 조회")
    public void getBlockedPost() {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        TestPostInfo post = this.setRandomPost(board, account);
        // 게시물 차단
        this.setRandomPostBlock(post);

        PostInfo postInfo = postService.getPostInfo(board.getId(), post.getId(), PostState.PUBLISH, true);
        Assertions.assertNotNull(postInfo);
        Assertions.assertTrue(postInfo.getBlocked());
        Assertions.assertNull(postInfo.getContent());
    }
}

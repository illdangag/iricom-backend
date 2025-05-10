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

@DisplayName("service: 게시물 - 삭제")
@Slf4j
@Transactional
public class PostServiceDeleteTestCore extends IricomTestServiceSuite {
    @Autowired
    private PostService postService;

    @Autowired
    public PostServiceDeleteTestCore(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("게시물 삭제")
    public void deletePost() {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);

        PostInfo postInfo = this.postService.deletePostInfo(account.getId(), board.getId(), post.getId());

        Assertions.assertEquals(post.getId(), postInfo.getId());
        Assertions.assertTrue(postInfo.getDeleted());
        Assertions.assertNull(postInfo.getContent());
    }

    @Test
    @DisplayName("자신이 작성하지 않은 게시물 삭제")
    public void notCreator() {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        TestAccountInfo other = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.postService.deletePostInfo(other.getId(), board.getId(), post.getId());
        });

        Assertions.assertNotEquals(account.getId(), other.getId());
        Assertions.assertEquals("04000002", iricomException.getErrorCode());
        Assertions.assertEquals("Invalid authorization.", iricomException.getMessage());
    }

    @Test
    @DisplayName("비활성화 게시판의 게시물 삭제")
    public void disabledBoard() {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);
        // 게시판 비활성화
        this.setDisabledBoard(Collections.singletonList(board));

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.postService.deletePostInfo(account.getId(), board.getId(), post.getId());
        });

        Assertions.assertEquals("03000001", iricomException.getErrorCode());
        Assertions.assertEquals("Board is disabled.", iricomException.getMessage());
    }

    @Test
    @DisplayName("권한 없는 비공개 게시판의 게시물 삭제")
    public void noAuthUndisclosedBoard() {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        TestAccountInfo other = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard(true, true);
        // 계정 그룹 생성
        setRandomAccountGroup(Collections.singletonList(account), Collections.singletonList(board));
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.postService.deletePostInfo(other.getId(), board.getId(), post.getId());
        });

        Assertions.assertEquals("03000000", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist board.", iricomException.getMessage());
    }

    @Test
    @DisplayName("권한이 있는 비공게 게시판의 게시물 삭제")
    public void undisclosedBoard() {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard(true, true);
        // 계정 그룹 생성
        setRandomAccountGroup(Collections.singletonList(account), Collections.singletonList(board));
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);

        PostInfo postInfo = this.postService.deletePostInfo(account.getId(), board.getId(), post.getId());

        Assertions.assertEquals(post.getId(), postInfo.getId());
        Assertions.assertNull(postInfo.getContent());
    }

    @Test
    @DisplayName("공지 사항 삭제")
    public void deleteNotification() {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard(Collections.singletonList(account));
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account, PostType.NOTIFICATION, PostState.PUBLISH);

        PostInfo postInfo = this.postService.deletePostInfo(account.getId(), board.getId(), post.getId());

        Assertions.assertEquals(post.getId(), postInfo.getId());
        Assertions.assertNull(postInfo.getContent());
    }

    @Test
    @DisplayName("게시판 관리자에서 삭제된 계정이 공지 사항 게시물 삭제")
    public void deleteNoAuthNotification() {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard(Collections.singletonList(account));
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account, PostType.NOTIFICATION, PostState.PUBLISH);
        // 게시판 관리자 삭제
        this.deleteBoardAdmin(board, Collections.singletonList(account));

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.postService.deletePostInfo(account.getId(), board.getId(), post.getId());
        });

        Assertions.assertEquals("04000001", iricomException.getErrorCode());
        Assertions.assertEquals("Invalid authorization.", iricomException.getMessage());
    }
}

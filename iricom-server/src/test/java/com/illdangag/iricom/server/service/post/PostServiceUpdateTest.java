package com.illdangag.iricom.server.service.post;

import com.illdangag.iricom.server.data.entity.type.PostState;
import com.illdangag.iricom.server.data.entity.type.PostType;
import com.illdangag.iricom.server.data.request.PostInfoUpdate;
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
import javax.validation.ConstraintViolationException;
import java.util.Collections;

@DisplayName("service: 게시물 - 수정")
@Slf4j
@Transactional
public class PostServiceUpdateTest extends IricomTestSuite {
    @Autowired
    private PostService postService;

    @Autowired
    public PostServiceUpdateTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("발행하지 않은 게시물")
    public void updateTemporaryPost() {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        TestPostInfo post = this.setRandomPost(board, account, PostType.POST, PostState.TEMPORARY);

        PostInfoUpdate postInfoUpdate = PostInfoUpdate.builder()
                .title("update title")
                .content("update content")
                .build();

        PostInfo postInfo = this.postService.updatePostInfo(account.getId(), board.getId(), post.getId(), postInfoUpdate);

        Assertions.assertEquals("update title", postInfo.getTitle());
        Assertions.assertEquals("update content", postInfo.getContent());
        Assertions.assertTrue(postInfo.getHasTemporary());
    }

    @Test
    @DisplayName("발행한 게시물")
    public void updatePublishPost() {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        TestPostInfo post = this.setRandomPost(board, account, PostType.POST, PostState.PUBLISH);

        PostInfoUpdate postInfoUpdate = PostInfoUpdate.builder()
                .title("update title")
                .content("update content")
                .build();

        PostInfo postInfo = this.postService.updatePostInfo(account.getId(), board.getId(), post.getId(), postInfoUpdate);

        Assertions.assertEquals("update title", postInfo.getTitle());
        Assertions.assertEquals("update content", postInfo.getContent());
        Assertions.assertTrue(postInfo.getHasTemporary());
    }

    @Test
    @DisplayName("발행하지 않은 공지사항")
    public void updateTemporaryNotification() {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard(Collections.singletonList(account));
        // 게시물 생성
        TestPostInfo post = this.setRandomPost(board, account, PostType.NOTIFICATION, PostState.TEMPORARY);

        PostInfoUpdate postInfoUpdate = PostInfoUpdate.builder()
                .title("update title")
                .content("update content")
                .build();

        PostInfo postInfo = this.postService.updatePostInfo(account.getId(), board.getId(), post.getId(), postInfoUpdate);

        Assertions.assertEquals("update title", postInfo.getTitle());
        Assertions.assertEquals("update content", postInfo.getContent());
        Assertions.assertTrue(postInfo.getHasTemporary());
    }

    @Test
    @DisplayName("발행한 공지사항")
    public void updatePublishNotification() {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard(Collections.singletonList(account));
        // 게시물 생성
        TestPostInfo post = this.setRandomPost(board, account, PostType.NOTIFICATION, PostState.PUBLISH);

        PostInfoUpdate postInfoUpdate = PostInfoUpdate.builder()
                .title("update title")
                .content("update content")
                .build();

        PostInfo postInfo = this.postService.updatePostInfo(account.getId(), board.getId(), post.getId(), postInfoUpdate);

        Assertions.assertEquals("update title", postInfo.getTitle());
        Assertions.assertEquals("update content", postInfo.getContent());
        Assertions.assertTrue(postInfo.getHasTemporary());
    }

    @Test
    @DisplayName("작성자가 다른 게시물")
    public void updateOtherCreator() {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        TestAccountInfo other = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        TestPostInfo post = this.setRandomPost(board, account);

        PostInfoUpdate postInfoUpdate = PostInfoUpdate.builder()
                .title("update title")
                .content("update content")
                .build();

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.postService.updatePostInfo(other.getId(), board.getId(), post.getId(), postInfoUpdate);
        });

        Assertions.assertEquals("04000002", iricomException.getErrorCode());
        Assertions.assertEquals("Invalid authorization.", iricomException.getMessage());
    }

    @Test
    @DisplayName("제목이 빈 문자열")
    public void emptyTitle() {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        TestPostInfo post = this.setRandomPost(board, account);

        PostInfoUpdate postInfoUpdate = PostInfoUpdate.builder()
                .title("")
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            this.postService.updatePostInfo(account.getId(), board.getId(), post.getId(), postInfoUpdate);
        });
    }

    @Test
    @DisplayName("제목이 긴 문자열")
    public void overflowTitle() {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        TestPostInfo post = this.setRandomPost(board, account);

        PostInfoUpdate postInfoUpdate = PostInfoUpdate.builder()
                .title(TEXT_10 + TEXT_10 + TEXT_10 + TEXT_10 + "0")
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            this.postService.updatePostInfo(account.getId(), board.getId(), post.getId(), postInfoUpdate);
        });
    }

    @Test
    @DisplayName("존재하지 않는 게시판")
    public void notExistBoard() {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        TestPostInfo post = this.setRandomPost(board, account);

        String boardId = "NOT_EXIST_BOARD";

        PostInfoUpdate postInfoUpdate = PostInfoUpdate.builder()
                .title("not exist board")
                .content("update content")
                .build();

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.postService.updatePostInfo(account.getId(), boardId, post.getId(), postInfoUpdate);
        });

        Assertions.assertEquals("03000000", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist board.", iricomException.getMessage());
    }

    @Test
    @DisplayName("일치하지 않는 게시판")
    public void notMatchBoard() {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        TestBoardInfo otherBoard = this.setRandomBoard();
        // 게시물 생성
        TestPostInfo post = this.setRandomPost(board, account);

        PostInfoUpdate postInfoUpdate = PostInfoUpdate.builder()
                .title("not matched board")
                .content("update content")
                .build();

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.postService.updatePostInfo(account.getId(), otherBoard.getId(), post.getId(), postInfoUpdate);
        });

        Assertions.assertEquals("04000000", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist post.", iricomException.getMessage());
    }

    @Test
    @DisplayName("비활성화 게시판")
    public void disabledBoard() {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        TestPostInfo post = this.setRandomPost(board, account);
        // 게시판 비활성화
        this.setDisabledBoard(Collections.singletonList(board));

        PostInfoUpdate postInfoUpdate = PostInfoUpdate.builder()
                .title("disabled board post")
                .content("update content")
                .build();

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.postService.updatePostInfo(account.getId(), board.getId(), post.getId(), postInfoUpdate);
        });

        Assertions.assertEquals("03000001", iricomException.getErrorCode());
        Assertions.assertEquals("Board is disabled.", iricomException.getMessage());
    }

    @Test
    @DisplayName("공지사항 전용 게시판의 일반 게시물")
    public void updatePostOnlyNotification() {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성 후 공지사항 전용 게시판으로 변경
        TestPostInfo post = this.setRandomPost(board, account);
        this.setNotificationOnlyBoard(Collections.singletonList(board));

        PostInfoUpdate postInfoUpdate = PostInfoUpdate.builder()
                .title("notification only board post")
                .content("update content")
                .build();

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.postService.updatePostInfo(account.getId(), board.getId(), post.getId(), postInfoUpdate);
        });

        Assertions.assertEquals("04000013", iricomException.getErrorCode());
        Assertions.assertEquals("Board is for notification only.", iricomException.getMessage());
    }

    @Test
    @DisplayName("공지 사항 전용 게시판에 공지 사항 게시물")
    public void updateNotificationOnlyNotification() {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard(Collections.singletonList(account));
        // 게시물 생성
        TestPostInfo post = this.setRandomPost(board, account, PostType.NOTIFICATION, PostState.PUBLISH);
        // 공지사항 전용 게시판으로 변경
        this.setNotificationOnlyBoard(Collections.singletonList(board));

        PostInfoUpdate postInfoUpdate = PostInfoUpdate.builder()
                .title("notification only board notification")
                .content("update content")
                .build();

        PostInfo postInfo = this.postService.updatePostInfo(account.getId(), board.getId(), post.getId(), postInfoUpdate);

        Assertions.assertEquals("notification only board notification", postInfo.getTitle());
        Assertions.assertEquals("update content", postInfo.getContent());
    }

    @Test
    @DisplayName("일반 계정이 일반 게시판의 일반 게시물을 공지 사항으로 변경")
    public void switchPostToNotificationByAccount() {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        TestPostInfo post = this.setRandomPost(board, account, PostType.POST, PostState.PUBLISH);

        PostInfoUpdate postInfoUpdate = PostInfoUpdate.builder()
                .title("switch post to notification")
                .content("update content")
                .type(PostType.NOTIFICATION)
                .build();

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.postService.updatePostInfo(account.getId(), board.getId(), post.getId(), postInfoUpdate);
        });

        Assertions.assertEquals("04000001", iricomException.getErrorCode());
        Assertions.assertEquals("Invalid authorization.", iricomException.getMessage());
    }

    @Test
    @DisplayName("게시판 관리자가 일반 게시판의 일반 게시물을 공지 사항으로 변경")
    public void switchPostToNotificationByBoardAdmin() {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard(Collections.singletonList(account));
        // 게시물 생성
        TestPostInfo post = this.setRandomPost(board, account, PostType.POST, PostState.PUBLISH);

        PostInfoUpdate postInfoUpdate = PostInfoUpdate.builder()
                .title("switch post to notification")
                .content("switch post to notification content")
                .type(PostType.NOTIFICATION)
                .build();

        PostInfo postInfo = this.postService.updatePostInfo(account.getId(), board.getId(), post.getId(), postInfoUpdate);

        Assertions.assertEquals("switch post to notification", postInfo.getTitle());
        Assertions.assertEquals("switch post to notification content", postInfo.getContent());
        Assertions.assertEquals("notification", postInfo.getType());
        Assertions.assertEquals(board.getId(), postInfo.getBoardId());
    }

    @Test
    @DisplayName("공지 사항 전용 게시판의 일반 게시물을 공지 사항으로 변경")
    public void switchPostToNotificationNotificationOnlyBoard() {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard(Collections.singletonList(account));
        // 게시물 생성
        TestPostInfo post = this.setRandomPost(board, account, PostType.POST, PostState.PUBLISH);
        this.setNotificationOnlyBoard(Collections.singletonList(board));

        PostInfoUpdate postInfoUpdate = PostInfoUpdate.builder()
                .title("switch post to notification")
                .content("switch post to notification content")
                .type(PostType.NOTIFICATION)
                .build();

        PostInfo postInfo = this.postService.updatePostInfo(account.getId(), board.getId(), post.getId(), postInfoUpdate);

        Assertions.assertEquals("switch post to notification", postInfo.getTitle());
        Assertions.assertEquals("switch post to notification content", postInfo.getContent());
        Assertions.assertEquals("notification", postInfo.getType());
        Assertions.assertEquals(board.getId(), postInfo.getBoardId());
    }
}

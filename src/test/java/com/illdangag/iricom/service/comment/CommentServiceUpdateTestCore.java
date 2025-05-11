package com.illdangag.iricom.service.comment;

import com.illdangag.iricom.core.data.request.CommentInfoUpdate;
import com.illdangag.iricom.core.data.response.CommentInfo;
import com.illdangag.iricom.core.exception.IricomException;
import com.illdangag.iricom.core.service.CommentService;
import com.illdangag.iricom.core.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.core.test.data.wrapper.TestBoardInfo;
import com.illdangag.iricom.core.test.data.wrapper.TestCommentInfo;
import com.illdangag.iricom.core.test.data.wrapper.TestPostInfo;
import com.illdangag.iricom.IricomTestServiceSuite;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

@DisplayName("service: 댓글 - 갱신")
@Slf4j
@Transactional
public class CommentServiceUpdateTestCore extends IricomTestServiceSuite {
    @Autowired
    private CommentService commentService;

    @Autowired
    public CommentServiceUpdateTestCore(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("갱신")
    public void updateComment() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);
        // 댓글 생성
        TestCommentInfo comment = setRandomComment(post, account);

        CommentInfoUpdate commentInfoUpdate = CommentInfoUpdate.builder()
                .content("update comment")
                .build();

        CommentInfo commentInfo = this.commentService.updateComment(account.getId(), board.getId(), post.getId(), comment.getId(), commentInfoUpdate);

        Assertions.assertEquals(comment.getId(), commentInfo.getId());
        Assertions.assertEquals("update comment", commentInfo.getContent());
    }

    @Test
    @DisplayName("대댓글 갱신")
    public void updateReferenceComment() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);
        // 댓글 생성
        TestCommentInfo comment00 = setRandomComment(post, account);
        // 대댓글 생성
        TestCommentInfo comment01 = setRandomComment(post, comment00, account);

        CommentInfoUpdate commentInfoUpdate = CommentInfoUpdate.builder()
                .content("update comment")
                .build();

        CommentInfo commentInfo = this.commentService.updateComment(account.getId(), board.getId(), post.getId(), comment01.getId(), commentInfoUpdate);

        Assertions.assertEquals(comment01.getId(), commentInfo.getId());
        Assertions.assertEquals("update comment", commentInfo.getContent());
    }

    @Test
    @DisplayName("내용이 빈 문자열")
    public void emptyContent() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);
        // 댓글 생성
        TestCommentInfo comment = setRandomComment(post, account);

        CommentInfoUpdate commentInfoUpdate = CommentInfoUpdate.builder()
                .content("")
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            this.commentService.updateComment(account.getId(), board.getId(), post.getId(), comment.getId(), commentInfoUpdate);
        });
    }

    @Test
    @DisplayName("내용이 긴 문자열")
    public void overflowContent() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);
        // 댓글 생성
        TestCommentInfo comment = setRandomComment(post, account);

        CommentInfoUpdate commentInfoUpdate = CommentInfoUpdate.builder()
                .content(TEXT_200 + "0")
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            this.commentService.updateComment(account.getId(), board.getId(), post.getId(), comment.getId(), commentInfoUpdate);
        });
    }

    @Test
    @DisplayName("존재하지 않는 댓글")
    public void notExistComment() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);
        // 댓글 생성
        setRandomComment(post, account);

        String commentId = "NOT_EXIST_COMMENT";

        CommentInfoUpdate commentInfoUpdate = CommentInfoUpdate.builder()
                .content("update comment")
                .build();

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.commentService.updateComment(account.getId(), board.getId(), post.getId(), commentId, commentInfoUpdate);
        });

        Assertions.assertEquals("05000000", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist comment.", iricomException.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 게시물")
    public void notExistPost() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);
        // 댓글 생성
        TestCommentInfo comment = setRandomComment(post, account);

        String postId = "NOT_EXIST_POST";

        CommentInfoUpdate commentInfoUpdate = CommentInfoUpdate.builder()
                .content("update comment")
                .build();

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.commentService.updateComment(account.getId(), board.getId(), postId, comment.getId(), commentInfoUpdate);
        });

        Assertions.assertEquals("04000000", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist post.", iricomException.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 게시판")
    public void notExistBoard() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);
        // 댓글 생성
        TestCommentInfo comment = setRandomComment(post, account);

        String boardId = "NOT_EXIST_BOARD";

        CommentInfoUpdate commentInfoUpdate = CommentInfoUpdate.builder()
                .content("update comment")
                .build();

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.commentService.updateComment(account.getId(), boardId, post.getId(), comment.getId(), commentInfoUpdate);
        });

        Assertions.assertEquals("03000000", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist board.", iricomException.getMessage());
    }

    @Test
    @DisplayName("다른 게시판에 있는 게시물")
    public void invalidBoard() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        TestBoardInfo otherBoard = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);
        setRandomPost(otherBoard, account);
        // 댓글 생성
        TestCommentInfo comment = setRandomComment(post, account);

        CommentInfoUpdate commentInfoUpdate = CommentInfoUpdate.builder()
                .content("update comment")
                .build();

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.commentService.updateComment(account.getId(), otherBoard.getId(), post.getId(), comment.getId(), commentInfoUpdate);
        });

        Assertions.assertEquals("04000000", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist post.", iricomException.getMessage());
    }

    @Test
    @DisplayName("다른 게시물에 있는 댓글")
    public void invalidPost() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        TestBoardInfo otherBoard = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);
        TestPostInfo otherPost = setRandomPost(otherBoard, account);
        // 댓글 생성
        TestCommentInfo comment = setRandomComment(post, account);
        TestCommentInfo otherComment = setRandomComment(otherPost, account);

        CommentInfoUpdate commentInfoUpdate = CommentInfoUpdate.builder()
                .content("update comment")
                .build();

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.commentService.updateComment(account.getId(), board.getId(), post.getId(), otherComment.getId(), commentInfoUpdate);
        });

        Assertions.assertEquals("05000000", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist comment.", iricomException.getMessage());
    }
}

package com.illdangag.iricom.server.service.comment;

import com.illdangag.iricom.server.data.request.CommentInfoCreate;
import com.illdangag.iricom.server.data.response.AccountInfo;
import com.illdangag.iricom.server.data.response.CommentInfo;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.service.AccountService;
import com.illdangag.iricom.server.service.CommentService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestCommentInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestPostInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

@DisplayName("service: 댓글 - 생성")
@Slf4j
@Transactional
public class CommentServiceCreateTest extends IricomTestSuite {
    @Autowired
    private CommentService commentService;
    @Autowired
    private AccountService accountService;

    public CommentServiceCreateTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("댓글 생성")
    public void createComment() throws Exception {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        TestPostInfo post = this.setRandomPost(board, account);

        CommentInfoCreate commentInfoCreate = CommentInfoCreate.builder()
                .content("댓글 생성")
                .build();
        CommentInfo commentInfo = commentService.createCommentInfo(account.getId(), board.getId(), post.getId(), commentInfoCreate);

        Assertions.assertEquals("댓글 생성", commentInfo.getContent());
        Assertions.assertNull(commentInfo.getReferenceCommentId());
        Assertions.assertFalse(commentInfo.getDeleted());
        Assertions.assertFalse(commentInfo.getReport());
    }

    @Test
    @DisplayName("내용이 빈 문자열")
    public void emptyContent() throws Exception {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        TestPostInfo post = this.setRandomPost(board, account);

        CommentInfoCreate commentInfoCreate = CommentInfoCreate.builder()
                .content("")
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            this.commentService.createCommentInfo(account.getId(), board.getId(), post.getId(), commentInfoCreate);
        });
    }

    @Test
    @DisplayName("내용이 긴 문자열")
    public void overflowContent() throws Exception {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        TestPostInfo post = this.setRandomPost(board, account);

        CommentInfoCreate commentInfoCreate = CommentInfoCreate.builder()
                .content(TEXT_200 + "0")
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            this.commentService.createCommentInfo(account.getId(), board.getId(), post.getId(), commentInfoCreate);
        });
    }

    @Test
    @DisplayName("대댓글 생성")
    public void createNestedComment() throws Exception {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        TestPostInfo post = this.setRandomPost(board, account);
        // 댓글 생성
        TestCommentInfo comment = this.setRandomComment(post, account);

        CommentInfoCreate commentInfoCreate = CommentInfoCreate.builder()
                .content("대댓글 생성").referenceCommentId(comment.getId())
                .build();

        CommentInfo commentInfo = commentService.createCommentInfo(account.getId(), board.getId(), post.getId(), commentInfoCreate);
        Assertions.assertEquals("대댓글 생성", commentInfo.getContent());
        Assertions.assertEquals(comment.getId(), commentInfo.getReferenceCommentId());
        Assertions.assertFalse(commentInfo.getDeleted());
        Assertions.assertFalse(commentInfo.getReport());
    }

    @Test
    @DisplayName("존재하지 않는 댓글에 대댓글")
    public void invalidReferenceComment() throws Exception {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        TestPostInfo post = this.setRandomPost(board, account);

        CommentInfoCreate commentInfoCreate = CommentInfoCreate.builder()
                .content("대댓글 생성").referenceCommentId("NOT_EXIST_COMMENT")
                .build();

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.commentService.createCommentInfo(account.getId(), board.getId(), post.getId(), commentInfoCreate);
        });

        Assertions.assertEquals("05000001", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist reference comment.", iricomException.getMessage());
    }

    @Test
    @DisplayName("댓글 작성 후 포인트 추가")
    public void addPointComment() throws Exception {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        TestPostInfo post = this.setRandomPost(board, account);

        AccountInfo beforeAccountInfo = this.accountService.getAccountInfo(account.getId());

        CommentInfoCreate commentInfoCreate = CommentInfoCreate.builder()
                .content("댓글 생성")
                .build();
        commentService.createCommentInfo(account.getId(), board.getId(), post.getId(), commentInfoCreate);

        AccountInfo afterAccountInfo = this.accountService.getAccountInfo(account.getId());
        Assertions.assertTrue(beforeAccountInfo.getPoint() < afterAccountInfo.getPoint());
    }
}

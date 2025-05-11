package com.illdangag.iricom.service.comment;

import com.illdangag.iricom.core.data.request.CommentInfoCreate;
import com.illdangag.iricom.core.data.response.AccountInfo;
import com.illdangag.iricom.core.data.response.CommentInfo;
import com.illdangag.iricom.core.exception.IricomException;
import com.illdangag.iricom.core.service.AccountService;
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

@DisplayName("service: 댓글 - 생성")
@Slf4j
@Transactional
public class CommentServiceCreateTestCore extends IricomTestServiceSuite {
    @Autowired
    private CommentService commentService;
    @Autowired
    private AccountService accountService;

    public CommentServiceCreateTestCore(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("댓글 생성")
    public void createComment() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);

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
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);

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
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);

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
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);
        // 댓글 생성
        TestCommentInfo comment = setRandomComment(post, account);

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
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);

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
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);

        AccountInfo beforeAccountInfo = this.accountService.getAccountInfo(account.getId());

        CommentInfoCreate commentInfoCreate = CommentInfoCreate.builder()
                .content("댓글 생성")
                .build();
        commentService.createCommentInfo(account.getId(), board.getId(), post.getId(), commentInfoCreate);

        AccountInfo afterAccountInfo = this.accountService.getAccountInfo(account.getId());
        Assertions.assertTrue(beforeAccountInfo.getPoint() < afterAccountInfo.getPoint());
    }
}

package com.illdangag.iricom.server.service.comment;

import com.illdangag.iricom.core.data.response.CommentInfo;
import com.illdangag.iricom.core.exception.IricomException;
import com.illdangag.iricom.core.service.CommentService;
import com.illdangag.iricom.core.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.core.test.data.wrapper.TestBoardInfo;
import com.illdangag.iricom.core.test.data.wrapper.TestCommentInfo;
import com.illdangag.iricom.core.test.data.wrapper.TestPostInfo;
import com.illdangag.iricom.server.IricomTestServiceSuite;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.transaction.Transactional;

@DisplayName("service: 댓글 - 삭제")
@Slf4j
@Transactional
public class CommentServiceDeleteTestCore extends IricomTestServiceSuite {
    @Autowired
    private CommentService commentService;

    @Autowired
    public CommentServiceDeleteTestCore(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("삭제")
    public void deleteComment() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);
        // 댓글 생성
        TestCommentInfo comment = setRandomComment(post, account);

        CommentInfo commentInfo = this.commentService.deleteComment(account.getId(), board.getId(), post.getId(), comment.getId());

        Assertions.assertEquals(comment.getId(), commentInfo.getId());
        Assertions.assertEquals(true, commentInfo.getDeleted());
        Assertions.assertNull(commentInfo.getContent());
    }

    @Test
    @DisplayName("대댓글이 달린 댓글 삭제")
    public void deleteHasNestedComment() throws Exception {
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

        // 댓글 삭제
        CommentInfo commentInfo00 = this.commentService.deleteComment(account.getId(), board.getId(), post.getId(), comment00.getId());
        Assertions.assertEquals(comment00.getId(), commentInfo00.getId());
        Assertions.assertEquals(true, commentInfo00.getDeleted());
        Assertions.assertNull(commentInfo00.getContent());

        // 대댓글은 삭제 되지 않은 것을 확인
        CommentInfo commentInfo01 = this.commentService.getComment(board.getId(), post.getId(), comment01.getId());
        Assertions.assertEquals(comment01.getId(), commentInfo01.getId());
        Assertions.assertEquals(false, commentInfo01.getDeleted());
        Assertions.assertNotNull(commentInfo01.getContent());
    }

    @Test
    @DisplayName("대댓글 삭제")
    public void deleteNestedComment() throws Exception {
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

        CommentInfo commentInfo = this.commentService.deleteComment(account.getId(), board.getId(), post.getId(), comment01.getId());

        Assertions.assertEquals(comment01.getId(), commentInfo.getId());
        Assertions.assertEquals(true, commentInfo.getDeleted());
        Assertions.assertNull(commentInfo.getContent());
    }

    @Test
    @DisplayName("존재하지 않는 댓글 삭제")
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

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.commentService.deleteComment(account.getId(), board.getId(), post.getId(), commentId);
        });

        Assertions.assertEquals("05000000", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist comment.", iricomException.getMessage());
    }

    @Test
    @DisplayName("자신이 작성하지 않은 댓글 삭제")
    public void notCreator() throws Exception {
        // 계정 생성
        TestAccountInfo account00 = setRandomAccount();
        TestAccountInfo account01 = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account00);
        // 댓글 생성
        TestCommentInfo comment00 = setRandomComment(post, account00);

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.commentService.deleteComment(account01.getId(), board.getId(), post.getId(), comment00.getId());
        });

        Assertions.assertNotEquals(account00.getId(), account01.getId());
        Assertions.assertEquals("05000004", iricomException.getErrorCode());
        Assertions.assertEquals("Invalid authorization.", iricomException.getMessage());
    }
}

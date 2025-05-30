package com.illdangag.iricom.service.comment;

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

@DisplayName("service: 댓글 - 조회")
@Slf4j
@Transactional
public class CommentServiceGetTestCore extends IricomTestServiceSuite {
    @Autowired
    private CommentService commentService;

    @Autowired
    public CommentServiceGetTestCore(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("조회")
    public void getComment() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);
        // 댓글 생성
        TestCommentInfo comment00 = setRandomComment(post, account);
        // 대댓글 생성
        setRandomComment(post, comment00, account);

        CommentInfo commentInfo = this.commentService.getComment(board.getId(), post.getId(), comment00.getId());

        Assertions.assertEquals(comment00.getId(), commentInfo.getId());
        Assertions.assertEquals(0, commentInfo.getUpvote());
        Assertions.assertEquals(0, commentInfo.getDownvote());
        Assertions.assertEquals(true, commentInfo.getHasNestedComment());
        Assertions.assertEquals(false, commentInfo.getDeleted());
        Assertions.assertEquals(false, commentInfo.getReport());
    }

    @Test
    @DisplayName("대댓글 조회")
    public void getReferenceComment() throws Exception {
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

        CommentInfo commentInfo = this.commentService.getComment(board.getId(), post.getId(), comment01.getId());

        Assertions.assertEquals(comment01.getId(), commentInfo.getId());
        Assertions.assertEquals(comment00.getId(), commentInfo.getReferenceCommentId());
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

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.commentService.getComment(board.getId(), post.getId(), commentId);
        });

        Assertions.assertEquals("05000000", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist comment.", iricomException.getMessage());
    }
}

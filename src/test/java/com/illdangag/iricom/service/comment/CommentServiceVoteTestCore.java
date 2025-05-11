package com.illdangag.iricom.service.comment;

import com.illdangag.iricom.core.data.entity.type.VoteType;
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

@DisplayName("service: 댓글 - 좋아요, 싫어요")
@Slf4j
@Transactional
public class CommentServiceVoteTestCore extends IricomTestServiceSuite {
    @Autowired
    private CommentService commentService;

    @Autowired
    public CommentServiceVoteTestCore(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("좋아요")
    public void upvote() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);
        // 댓글 생성
        TestCommentInfo comment = setRandomComment(post, account);

        CommentInfo commentInfo = commentService.voteComment(account.getId(), board.getId(), post.getId(), comment.getId(), VoteType.UPVOTE);

        Assertions.assertEquals(1, commentInfo.getUpvote());
        Assertions.assertEquals(0, commentInfo.getDownvote());
    }

    @Test
    @DisplayName("싫어요")
    public void downvote() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);
        // 댓글 생성
        TestCommentInfo comment = setRandomComment(post, account);

        CommentInfo commentInfo = commentService.voteComment(account.getId(), board.getId(), post.getId(), comment.getId(), VoteType.DOWNVOTE);

        Assertions.assertEquals(1, commentInfo.getDownvote());
        Assertions.assertEquals(0, commentInfo.getUpvote());
    }

    @Test
    @DisplayName("중복 좋아요")
    public void duplicateUpvote() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);
        // 댓글 생성
        TestCommentInfo comment = setRandomComment(post, account);

        CommentInfo commentInfo = commentService.voteComment(account.getId(), board.getId(), post.getId(), comment.getId(), VoteType.UPVOTE);
        Assertions.assertEquals(1, commentInfo.getUpvote());
        Assertions.assertEquals(0, commentInfo.getDownvote());

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            commentService.voteComment(account.getId(), board.getId(), post.getId(), comment.getId(), VoteType.UPVOTE);
        });
        Assertions.assertEquals("05000005", iricomException.getErrorCode());
        Assertions.assertEquals("Already vote comment.", iricomException.getMessage());
    }

    @Test
    @DisplayName("중복 싫어요")
    public void duplicateDownvote() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);
        // 댓글 생성
        TestCommentInfo comment = setRandomComment(post, account);

        CommentInfo commentInfo = commentService.voteComment(account.getId(), board.getId(), post.getId(), comment.getId(), VoteType.DOWNVOTE);
        Assertions.assertEquals(0, commentInfo.getUpvote());
        Assertions.assertEquals(1, commentInfo.getDownvote());

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            commentService.voteComment(account.getId(), board.getId(), post.getId(), comment.getId(), VoteType.DOWNVOTE);
        });
        Assertions.assertEquals("05000005", iricomException.getErrorCode());
        Assertions.assertEquals("Already vote comment.", iricomException.getMessage());
    }

    @Test
    @DisplayName("삭제된 댓글 좋아요")
    public void upvoteDeletedComment() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);
        // 댓글 생성
        TestCommentInfo comment = setRandomComment(post, account, true);

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            commentService.voteComment(account.getId(), board.getId(), post.getId(), comment.getId(), VoteType.UPVOTE);
        });
        Assertions.assertEquals("05000000", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist comment.", iricomException.getMessage());
    }

    @Test
    @DisplayName("삭제된 댓글 싫어요")
    public void downvoteDeletedComment() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);
        // 댓글 생성
        TestCommentInfo comment = setRandomComment(post, account, true);

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            commentService.voteComment(account.getId(), board.getId(), post.getId(), comment.getId(), VoteType.DOWNVOTE);
        });
        Assertions.assertEquals("05000000", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist comment.", iricomException.getMessage());
    }
}

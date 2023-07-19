package com.illdangag.iricom.server.service.comment;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.data.response.CommentInfo;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.service.CommentService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestCommentInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestCommentReportInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestPostInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Nested
@DisplayName("service: 댓글 - 좋아요, 싫어요")
@Slf4j
public class CommentVoteServiceTest extends IricomTestSuite {
    @Autowired
    private CommentService commentService;

    private static final TestBoardInfo commentTestBoardInfo = TestBoardInfo.builder()
            .title("commentTestBoardInfo").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();

    private static final TestPostInfo votePostInfo00 = TestPostInfo.builder()
            .title("votePostInfo00").content("votePostInfo00").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(commentTestBoardInfo)
            .build();

    private static final TestCommentInfo voteCommentInfo00 = TestCommentInfo.builder()
            .content("voteCommentInfo00").creator(common00).post(votePostInfo00)
            .build();
    private static final TestCommentInfo voteCommentInfo01 = TestCommentInfo.builder()
            .content("voteCommentInfo01").creator(common00).post(votePostInfo00)
            .build();
    private static final TestCommentInfo voteCommentInfo02 = TestCommentInfo.builder()
            .content("voteCommentInfo02").creator(common00).post(votePostInfo00).deleted(true)
            .build();

    @Autowired
    public CommentVoteServiceTest(ApplicationContext context) {
        super(context);

        List<TestBoardInfo> testBoardInfoList = Arrays.asList(commentTestBoardInfo);
        List<TestPostInfo> testPostInfoList = Arrays.asList(votePostInfo00);
        List<TestCommentInfo> testCommentInfoList = Arrays.asList(voteCommentInfo00, voteCommentInfo01, voteCommentInfo02);

        super.setBoard(testBoardInfoList);
        super.setPost(testPostInfoList);
        super.setComment(testCommentInfoList);
        super.setDeletedComment(testCommentInfoList);
    }

    @Test
    @DisplayName("좋아요")
    public void upvote() throws Exception {
        Account account = getAccount(common00);
        Comment comment = getComment(voteCommentInfo00);
        Post post = comment.getPost();
        Board board = post.getBoard();

        CommentInfo commentInfo = commentService.voteComment(account, board, post, comment, VoteType.UPVOTE);
        Assertions.assertEquals(1, commentInfo.getUpvote());
        Assertions.assertEquals(0, commentInfo.getDownvote());
    }

    @Test
    @DisplayName("싫어요")
    public void downvote() throws Exception {
        Account account = getAccount(common00);
        Comment comment = getComment(voteCommentInfo00);
        Post post = comment.getPost();
        Board board = post.getBoard();

        CommentInfo commentInfo = commentService.voteComment(account, board, post, comment, VoteType.DOWNVOTE);
        Assertions.assertEquals(1, commentInfo.getDownvote());
        Assertions.assertEquals(0, commentInfo.getUpvote());
    }

    @Test
    @DisplayName("중복 좋아요")
    public void duplicateUpvote() throws Exception {
        Account account = getAccount(common00);
        Comment comment = getComment(voteCommentInfo01);
        Post post = comment.getPost();
        Board board = post.getBoard();

        String boardId = String.valueOf(board.getId());
        String postId = String.valueOf(post.getId());
        String commentId = String.valueOf(comment.getId());

        CommentInfo commentInfo = commentService.voteComment(account, boardId, postId, commentId, VoteType.UPVOTE);
        Assertions.assertEquals(1, commentInfo.getUpvote());
        Assertions.assertEquals(0, commentInfo.getDownvote());

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            commentService.voteComment(account, boardId, postId, commentId, VoteType.UPVOTE);
        });
        Assertions.assertEquals("05000005", iricomException.getErrorCode());
        Assertions.assertEquals("Already vote comment.", iricomException.getMessage());
    }

    @Test
    @DisplayName("중복 싫어요")
    public void duplicateDownvote() throws Exception {
        Account account = getAccount(common00);
        Comment comment = getComment(voteCommentInfo01);
        Post post = comment.getPost();
        Board board = post.getBoard();

        String boardId = String.valueOf(board.getId());
        String postId = String.valueOf(post.getId());
        String commentId = String.valueOf(comment.getId());

        CommentInfo commentInfo = commentService.voteComment(account, boardId, postId, commentId, VoteType.DOWNVOTE);
        Assertions.assertEquals(0, commentInfo.getUpvote());
        Assertions.assertEquals(1, commentInfo.getDownvote());

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            commentService.voteComment(account, boardId, postId, commentId, VoteType.DOWNVOTE);
        });
        Assertions.assertEquals("05000005", iricomException.getErrorCode());
        Assertions.assertEquals("Already vote comment.", iricomException.getMessage());
    }

    @Test
    @DisplayName("삭제된 댓글 좋아요")
    public void upvoteDeletedComment() throws Exception {
        Account account = getAccount(common00);
        Comment comment = getComment(voteCommentInfo02);
        Post post = comment.getPost();
        Board board = post.getBoard();

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            commentService.voteComment(account, board, post, comment, VoteType.UPVOTE);
        });
        Assertions.assertEquals("05000000", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist comment.", iricomException.getMessage());
    }

    @Test
    @DisplayName("삭제된 댓글 싫어요")
    public void downvoteDeletedComment() throws Exception {
        Account account = getAccount(common00);
        Comment comment = getComment(voteCommentInfo02);
        Post post = comment.getPost();
        Board board = post.getBoard();

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            commentService.voteComment(account, board, post, comment, VoteType.DOWNVOTE);
        });
        Assertions.assertEquals("05000000", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist comment.", iricomException.getMessage());
    }
}

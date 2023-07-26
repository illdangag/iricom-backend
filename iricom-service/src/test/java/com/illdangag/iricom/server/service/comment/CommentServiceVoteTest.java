package com.illdangag.iricom.server.service.comment;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.data.response.CommentInfo;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.service.CommentService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestCommentInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestPostInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Collections;

@DisplayName("service: 댓글 - 좋아요, 싫어요")
@Slf4j
public class CommentServiceVoteTest extends IricomTestSuite {
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
    public CommentServiceVoteTest(ApplicationContext context) {
        super(context);

        addTestBoardInfo(commentTestBoardInfo);
        addTestPostInfo(votePostInfo00);
        addTestCommentInfo(voteCommentInfo00, voteCommentInfo01, voteCommentInfo02);
        init();
    }

    @Test
    @DisplayName("좋아요")
    public void upvote() throws Exception {
        Account account = getAccount(common00);
        String commentId = getCommentId(voteCommentInfo00);
        String postId = getPostId(voteCommentInfo00.getPost());
        String boardId = getBoardId(voteCommentInfo00.getPost().getBoard());

        CommentInfo commentInfo = commentService.voteComment(account, boardId, postId, commentId, VoteType.UPVOTE);

        Assertions.assertEquals(1, commentInfo.getUpvote());
        Assertions.assertEquals(0, commentInfo.getDownvote());
    }

    @Test
    @DisplayName("싫어요")
    public void downvote() throws Exception {
        Account account = getAccount(common00);
        String commentId = getCommentId(voteCommentInfo00);
        String postId = getPostId(voteCommentInfo00.getPost());
        String boardId = getBoardId(voteCommentInfo00.getPost().getBoard());

        CommentInfo commentInfo = commentService.voteComment(account, boardId, postId, commentId, VoteType.DOWNVOTE);

        Assertions.assertEquals(1, commentInfo.getDownvote());
        Assertions.assertEquals(0, commentInfo.getUpvote());
    }

    @Test
    @DisplayName("중복 좋아요")
    public void duplicateUpvote() throws Exception {
        Account account = getAccount(common00);
        String commentId = getCommentId(voteCommentInfo01);
        String postId = getPostId(voteCommentInfo01.getPost());
        String boardId = getBoardId(voteCommentInfo01.getPost().getBoard());

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
        String commentId = getCommentId(voteCommentInfo01);
        String postId = getPostId(voteCommentInfo01.getPost());
        String boardId = getBoardId(voteCommentInfo01.getPost().getBoard());

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
        String commentId = getCommentId(voteCommentInfo02);
        String postId = getPostId(voteCommentInfo02.getPost());
        String boardId = getBoardId(voteCommentInfo02.getPost().getBoard());

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            commentService.voteComment(account, boardId, postId, commentId, VoteType.UPVOTE);
        });
        Assertions.assertEquals("05000000", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist comment.", iricomException.getMessage());
    }

    @Test
    @DisplayName("삭제된 댓글 싫어요")
    public void downvoteDeletedComment() throws Exception {
        Account account = getAccount(common00);
        String commentId = getCommentId(voteCommentInfo02);
        String postId = getPostId(voteCommentInfo02.getPost());
        String boardId = getBoardId(voteCommentInfo02.getPost().getBoard());

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            commentService.voteComment(account, boardId, postId, commentId, VoteType.DOWNVOTE);
        });
        Assertions.assertEquals("05000000", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist comment.", iricomException.getMessage());
    }
}

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

@DisplayName("service: 댓글 - 조회")
@Slf4j
public class CommentServiceGetTest extends IricomTestSuite {
    @Autowired
    private CommentService commentService;

    // 게시판
    private final TestBoardInfo testBoardInfo00 = TestBoardInfo.builder()
            .title("testBoardInfo00").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();
    // 게시물
    private final TestPostInfo testPostInfo00 = TestPostInfo.builder()
            .title("testPostInfo00").content("testPostInfo00").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(testBoardInfo00)
            .build();
    // 댓글
    private final TestCommentInfo testCommentInfo00 = TestCommentInfo.builder()
            .content("testCommentInfo00").creator(allBoardAdmin).post(testPostInfo00)
            .build();
    private final TestCommentInfo testCommentInfo01 = TestCommentInfo.builder()
            .content("testCommentInfo01").creator(allBoardAdmin).post(testPostInfo00)
            .referenceComment(testCommentInfo00)
            .build();

    @Autowired
    public CommentServiceGetTest(ApplicationContext context) {
        super(context);

        addTestBoardInfo(testBoardInfo00);
        addTestPostInfo(testPostInfo00);
        addTestCommentInfo(testCommentInfo00, testCommentInfo01);

        init();
    }

    @Test
    @DisplayName("조회")
    public void getComment() throws Exception {
        Comment comment = getComment(testCommentInfo00);
        Post post = comment.getPost();
        Board board = post.getBoard();

        String boardId = String.valueOf(board.getId());
        String postId = String.valueOf(post.getId());
        String commentId = String.valueOf(comment.getId());

        CommentInfo commentInfo = this.commentService.getComment(boardId, postId, commentId);

        Assertions.assertEquals(commentId, commentInfo.getId());
        Assertions.assertEquals(0, commentInfo.getUpvote());
        Assertions.assertEquals(0, commentInfo.getDownvote());
        Assertions.assertEquals(true, commentInfo.getHasNestedComment());
        Assertions.assertEquals(false, commentInfo.getDeleted());
        Assertions.assertEquals(false, commentInfo.getReport());
    }

    @Test
    @DisplayName("대댓글 조회")
    public void getReferenceComment() throws Exception {
        Comment comment = getComment(testCommentInfo01);
        Comment referenceComment = comment.getReferenceComment();
        Post post = comment.getPost();
        Board board = post.getBoard();

        String boardId = String.valueOf(board.getId());
        String postId = String.valueOf(post.getId());
        String commentId = String.valueOf(comment.getId());
        String referenceCommentId = String.valueOf(referenceComment.getId());

        CommentInfo commentInfo = this.commentService.getComment(boardId, postId, commentId);

        Assertions.assertEquals(commentId, commentInfo.getId());
        Assertions.assertEquals(referenceCommentId, commentInfo.getReferenceCommentId());
    }

    @Test
    @DisplayName("존재하지 않는 댓글")
    public void notExistComment() throws Exception {
        Post post = getPost(testPostInfo00);
        Board board = post.getBoard();

        String boardId = String.valueOf(board.getId());
        String postId = String.valueOf(post.getId());
        String commentId = "NOT_EXIST_COMMENT";

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.commentService.getComment(boardId, postId, commentId);
        });

        Assertions.assertEquals("05000000", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist comment.", iricomException.getMessage());
    }
}

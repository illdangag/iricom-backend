package com.illdangag.iricom.server.service.comment;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.PostState;
import com.illdangag.iricom.server.data.entity.PostType;
import com.illdangag.iricom.server.data.request.CommentInfoUpdate;
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

import javax.validation.ConstraintViolationException;
import java.util.Collections;

@Slf4j
@DisplayName("service: 댓글 - 갱신")
public class CommentServiceUpdateTest extends IricomTestSuite {
    @Autowired
    private CommentService commentService;

    // 게시판
    private final TestBoardInfo testBoardInfo00 = TestBoardInfo.builder()
            .title("testBoardInfo00").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();
    private final TestBoardInfo testBoardInfo01 = TestBoardInfo.builder()
            .title("testBoardInfo01").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();
    // 게시물
    private final TestPostInfo testPostInfo00 = TestPostInfo.builder()
            .title("testPostInfo00").content("testPostInfo00").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(testBoardInfo00)
            .build();
    private final TestPostInfo testPostInfo01 = TestPostInfo.builder()
            .title("testPostInfo01").content("testPostInfo01").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(testBoardInfo01)
            .build();
    // 댓글
    private final TestCommentInfo testCommentInfo00 = TestCommentInfo.builder()
            .content("testCommentInfo00").creator(allBoardAdmin).post(testPostInfo00)
            .build();
    private final TestCommentInfo testCommentInfo01 = TestCommentInfo.builder()
            .content("testCommentInfo01").creator(allBoardAdmin).post(testPostInfo00)
            .referenceComment(testCommentInfo00).build();
    private final TestCommentInfo testCommentInfo02 = TestCommentInfo.builder()
            .content("testCommentInfo02").creator(allBoardAdmin).post(testPostInfo01)
            .build();

    @Autowired
    public CommentServiceUpdateTest(ApplicationContext context) {
        super(context);

        addTestBoardInfo(testBoardInfo00, testBoardInfo01);
        addTestPostInfo(testPostInfo00, testPostInfo01);
        addTestCommentInfo(testCommentInfo00, testCommentInfo01, testCommentInfo02);

        init();
    }

    @Test
    @DisplayName("갱신")
    public void updateComment() throws Exception {
        TestCommentInfo targetCommentInfo = testCommentInfo00;

        Account account = getAccount(targetCommentInfo.getCreator());
        String boardId = getBoardId(targetCommentInfo.getPost().getBoard());
        String postId = getPostId(targetCommentInfo.getPost());
        String commentId = getCommentId(targetCommentInfo);

        CommentInfoUpdate commentInfoUpdate = CommentInfoUpdate.builder()
                .content("update comment")
                .build();

        CommentInfo commentInfo = this.commentService.updateComment(account, boardId, postId, commentId, commentInfoUpdate);

        Assertions.assertEquals(commentId, commentInfo.getId());
        Assertions.assertEquals("update comment", commentInfo.getContent());
    }

    @Test
    @DisplayName("대댓글 갱신")
    public void updateReferenceComment() throws Exception {
        TestCommentInfo targetCommentInfo = testCommentInfo01;

        Account account = getAccount(targetCommentInfo.getCreator());
        String boardId = getBoardId(targetCommentInfo.getPost().getBoard());
        String postId = getPostId(targetCommentInfo.getPost());
        String commentId = getCommentId(targetCommentInfo);

        CommentInfoUpdate commentInfoUpdate = CommentInfoUpdate.builder()
                .content("update comment")
                .build();

        CommentInfo commentInfo = this.commentService.updateComment(account, boardId, postId, commentId, commentInfoUpdate);

        Assertions.assertEquals(commentId, commentInfo.getId());
        Assertions.assertEquals("update comment", commentInfo.getContent());
    }

    @Test
    @DisplayName("내용이 빈 문자열")
    public void emptyContent() throws Exception {
        TestCommentInfo targetCommentInfo = testCommentInfo00;

        Account account = getAccount(targetCommentInfo.getCreator());
        String boardId = getBoardId(targetCommentInfo.getPost().getBoard());
        String postId = getPostId(targetCommentInfo.getPost());
        String commentId = getCommentId(targetCommentInfo);

        CommentInfoUpdate commentInfoUpdate = CommentInfoUpdate.builder()
                .content("")
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            this.commentService.updateComment(account, boardId, postId, commentId, commentInfoUpdate);
        });
    }

    @Test
    @DisplayName("내용이 긴 문자열")
    public void overflowContent() throws Exception {
        TestCommentInfo targetCommentInfo = testCommentInfo00;

        Account account = getAccount(targetCommentInfo.getCreator());
        String boardId = getBoardId(targetCommentInfo.getPost().getBoard());
        String postId = getPostId(targetCommentInfo.getPost());
        String commentId = getCommentId(targetCommentInfo);

        CommentInfoUpdate commentInfoUpdate = CommentInfoUpdate.builder()
                .content("0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                        "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                        "0")
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            this.commentService.updateComment(account, boardId, postId, commentId, commentInfoUpdate);
        });
    }

    @Test
    @DisplayName("존재하지 않는 댓글")
    public void notExistComment() throws Exception {
        TestCommentInfo targetCommentInfo = testCommentInfo00;

        Account account = getAccount(targetCommentInfo.getCreator());
        String boardId = getBoardId(targetCommentInfo.getPost().getBoard());
        String postId = getPostId(targetCommentInfo.getPost());
        String commentId = "NOT_EXIST_COMMENT";

        CommentInfoUpdate commentInfoUpdate = CommentInfoUpdate.builder()
                .content("update comment")
                .build();

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.commentService.updateComment(account, boardId, postId, commentId, commentInfoUpdate);
        });

        Assertions.assertEquals("05000000", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist comment.", iricomException.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 게시물")
    public void notExistPost() throws Exception {
        TestCommentInfo targetCommentInfo = testCommentInfo00;

        Account account = getAccount(targetCommentInfo.getCreator());
        String boardId = getBoardId(targetCommentInfo.getPost().getBoard());
        String postId = "NOT_EXIST_POST";
        String commentId = getCommentId(targetCommentInfo);

        CommentInfoUpdate commentInfoUpdate = CommentInfoUpdate.builder()
                .content("update comment")
                .build();

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.commentService.updateComment(account, boardId, postId, commentId, commentInfoUpdate);
        });

        Assertions.assertEquals("04000000", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist post.", iricomException.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 게시판")
    public void notExistBoard() throws Exception {
        TestCommentInfo targetCommentInfo = testCommentInfo00;

        Account account = getAccount(targetCommentInfo.getCreator());
        String boardId = "NOT_EXIST_BOARD";
        String postId = getPostId(targetCommentInfo.getPost());
        String commentId = getCommentId(targetCommentInfo);

        CommentInfoUpdate commentInfoUpdate = CommentInfoUpdate.builder()
                .content("update comment")
                .build();

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.commentService.updateComment(account, boardId, postId, commentId, commentInfoUpdate);
        });

        Assertions.assertEquals("03000000", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist board.", iricomException.getMessage());
    }

    @Test
    @DisplayName("다른 게시판에 있는 게시물")
    public void invalidBoard() throws Exception {
        TestCommentInfo targetCommentInfo = testCommentInfo00;

        Account account = getAccount(targetCommentInfo.getCreator());
        String boardId = getBoardId(testBoardInfo01);
        String postId = getPostId(targetCommentInfo.getPost());
        String commentId = getCommentId(targetCommentInfo);

        CommentInfoUpdate commentInfoUpdate = CommentInfoUpdate.builder()
                .content("update comment")
                .build();

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.commentService.updateComment(account, boardId, postId, commentId, commentInfoUpdate);
        });

        Assertions.assertEquals("04000000", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist post.", iricomException.getMessage());
    }

    @Test
    @DisplayName("다른 게시물에 있는 댓글")
    public void invalidPost() throws Exception {
        TestCommentInfo targetCommentInfo = testCommentInfo00;

        Account account = getAccount(targetCommentInfo.getCreator());
        String boardId = getBoardId(targetCommentInfo.getPost().getBoard());
        String postId = getPostId(targetCommentInfo.getPost());
        String commentId = getCommentId(testCommentInfo02);

        CommentInfoUpdate commentInfoUpdate = CommentInfoUpdate.builder()
                .content("update comment")
                .build();

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.commentService.updateComment(account, boardId, postId, commentId, commentInfoUpdate);
        });

        Assertions.assertEquals("05000000", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist comment.", iricomException.getMessage());
    }
}

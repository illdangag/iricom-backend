package com.illdangag.iricom.server.service.comment;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.PostState;
import com.illdangag.iricom.server.data.entity.PostType;
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

@Slf4j
@DisplayName("service: 댓글 - 삭제")
public class CommentServiceDeleteTest extends IricomTestSuite {
    @Autowired
    private CommentService commentService;

    // 게시판
    private final TestBoardInfo testBoardInfo00 = TestBoardInfo.builder()
            .title("testBoardInfo00").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();
    // 게시물
    private final TestPostInfo testPostInfo00 = TestPostInfo.builder()
            .title("testPostInfo00").content("testPostInfo00").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(testBoardInfo00)
            .build();
    // 댓글
    private final TestCommentInfo testCommentInfo00 = TestCommentInfo.builder()
            .content("testCommentInfo00").creator(common00).post(testPostInfo00)
            .build();
    private final TestCommentInfo testCommentInfo01 = TestCommentInfo.builder()
            .content("testCommentInfo01").creator(common00).post(testPostInfo00)
            .build();
    private final TestCommentInfo testCommentInfo02 = TestCommentInfo.builder()
            .content("testCommentInfo02").creator(common00).post(testPostInfo00)
            .referenceComment(testCommentInfo01)
            .build();
    private final TestCommentInfo testCommentInfo03 = TestCommentInfo.builder()
            .content("testCommentInfo03").creator(common00).post(testPostInfo00)
            .build();
    private final TestCommentInfo testCommentInfo04 = TestCommentInfo.builder()
            .content("testCommentInfo04").creator(common00).post(testPostInfo00)
            .referenceComment(testCommentInfo03)
            .build();
    private final TestCommentInfo testCommentInfo05 = TestCommentInfo.builder()
            .content("testCommentInfo05").creator(common00).post(testPostInfo00)
            .build();

    @Autowired
    public CommentServiceDeleteTest(ApplicationContext context) {
        super(context);

        addTestBoardInfo(testBoardInfo00);
        addTestPostInfo(testPostInfo00);
        addTestCommentInfo(testCommentInfo00, testCommentInfo01, testCommentInfo02, testCommentInfo03,
                testCommentInfo04, testCommentInfo05);

        init();
    }

    @Test
    @DisplayName("삭제")
    public void deleteComment() throws Exception {
        TestCommentInfo targetCommentInfo = testCommentInfo00;

        Account account = getAccount(targetCommentInfo.getCreator());
        String boardId = getBoardId(targetCommentInfo.getPost().getBoard());
        String postId = getPostId(targetCommentInfo.getPost());
        String commentId = getCommentId(targetCommentInfo);

        CommentInfo commentInfo = this.commentService.deleteComment(account, boardId, postId, commentId);

        Assertions.assertEquals(commentId, commentInfo.getId());
        Assertions.assertEquals(true, commentInfo.getDeleted());
        Assertions.assertNull(commentInfo.getContent());
    }

    @Test
    @DisplayName("대댓글이 달린 댓글 삭제")
    public void deleteHasNestedComment() throws Exception {
        TestCommentInfo targetCommentInfo = testCommentInfo01;

        Account account = getAccount(targetCommentInfo.getCreator());
        String boardId = getBoardId(targetCommentInfo.getPost().getBoard());
        String postId = getPostId(targetCommentInfo.getPost());
        String commentId = getCommentId(targetCommentInfo);

        CommentInfo commentInfo = this.commentService.deleteComment(account, boardId, postId, commentId);

        Assertions.assertEquals(commentId, commentInfo.getId());
        Assertions.assertEquals(true, commentInfo.getDeleted());
        Assertions.assertNull(commentInfo.getContent());
    }

    @Test
    @DisplayName("대댓글 삭제")
    public void deleteNestedComment() throws Exception {
        TestCommentInfo targetCommentInfo = testCommentInfo04;

        Account account = getAccount(targetCommentInfo.getCreator());
        String boardId = getBoardId(targetCommentInfo.getPost().getBoard());
        String postId = getPostId(targetCommentInfo.getPost());
        String commentId = getCommentId(targetCommentInfo);

        CommentInfo commentInfo = this.commentService.deleteComment(account, boardId, postId, commentId);

        Assertions.assertEquals(commentId, commentInfo.getId());
        Assertions.assertEquals(true, commentInfo.getDeleted());
        Assertions.assertNull(commentInfo.getContent());
    }

    @Test
    @DisplayName("존재하지 않는 댓글 삭제")
    public void notExistComment() throws Exception {
        TestCommentInfo targetCommentInfo = testCommentInfo00;

        Account account = getAccount(targetCommentInfo.getCreator());
        String boardId = getBoardId(targetCommentInfo.getPost().getBoard());
        String postId = getPostId(targetCommentInfo.getPost());
        String commentId = "NOT_EXIST_COMMENT";

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.commentService.deleteComment(account, boardId, postId, commentId);
        });

        Assertions.assertEquals("05000000", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist comment.", iricomException.getMessage());
    }

    @Test
    @DisplayName("자신이 작성하지 않은 댓글 삭제")
    public void notCreator() throws Exception {
        TestCommentInfo targetCommentInfo = testCommentInfo05;

        Account creator = getAccount(targetCommentInfo.getCreator());
        Account account = getAccount(common01);
        String boardId = getBoardId(targetCommentInfo.getPost().getBoard());
        String postId = getPostId(targetCommentInfo.getPost());
        String commentId = getCommentId(targetCommentInfo);

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.commentService.deleteComment(account, boardId, postId, commentId);
        });

        Assertions.assertNotEquals(creator.getId(), account.getId());
        Assertions.assertEquals("05000004", iricomException.getErrorCode());
        Assertions.assertEquals("Invalid authorization.", iricomException.getMessage());
    }
}

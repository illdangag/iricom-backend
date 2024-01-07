package com.illdangag.iricom.server.service.comment;

import com.illdangag.iricom.server.data.entity.type.PostState;
import com.illdangag.iricom.server.data.entity.type.PostType;
import com.illdangag.iricom.server.data.request.CommentInfoCreate;
import com.illdangag.iricom.server.data.response.CommentInfo;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.service.AccountService;
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

@DisplayName("service: 댓글 - 생성")
@Slf4j
public class CommentServiceCreateTest extends IricomTestSuite {
    @Autowired
    private CommentService commentService;
    @Autowired
    private AccountService accountService;

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
            .content("commentInfo00").creator(allBoardAdmin).post(testPostInfo00)
            .build();

    public CommentServiceCreateTest(ApplicationContext context) {
        super(context);

        addTestBoardInfo(testBoardInfo00);
        addTestPostInfo(testPostInfo00);
        addTestCommentInfo(testCommentInfo00);

        init();
    }

    @Test
    @DisplayName("댓글 생성")
    public void createComment() throws Exception {
        String accountId = getAccountId(common00);
        String boardId = getBoardId(testPostInfo00.getBoard());
        String postId = getPostId(testPostInfo00);

        CommentInfoCreate commentInfoCreate = CommentInfoCreate.builder()
                .content("댓글 생성")
                .build();
        CommentInfo commentInfo = commentService.createCommentInfo(accountId, boardId, postId, commentInfoCreate);

        Assertions.assertEquals("댓글 생성", commentInfo.getContent());
        Assertions.assertNull(commentInfo.getReferenceCommentId());
        Assertions.assertFalse(commentInfo.getDeleted());
        Assertions.assertFalse(commentInfo.getReport());
    }

    @Test
    @DisplayName("내용이 빈 문자열")
    public void emptyContent() throws Exception {
        String accountId = getAccountId(common00);
        String boardId = getBoardId(testPostInfo00.getBoard());
        String postId = getPostId(testPostInfo00);

        CommentInfoCreate commentInfoCreate = CommentInfoCreate.builder()
                .content("")
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            this.commentService.createCommentInfo(accountId, boardId, postId, commentInfoCreate);
        });
    }

    @Test
    @DisplayName("내용이 긴 문자열")
    public void overflowContent() throws Exception {
        String accountId = getAccountId(common00);
        String boardId = getBoardId(testPostInfo00.getBoard());
        String postId = getPostId(testPostInfo00);

        CommentInfoCreate commentInfoCreate = CommentInfoCreate.builder()
                .content("0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                        "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                        "0")
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            this.commentService.createCommentInfo(accountId, boardId, postId, commentInfoCreate);
        });
    }

    @Test
    @DisplayName("대댓글 생성")
    public void createNestedComment() throws Exception {
        String accountId = getAccountId(common00);
        String boardId = getBoardId(testPostInfo00.getBoard());
        String postId = getPostId(testPostInfo00);

        String referenceCommentId = getCommentId(testCommentInfo00);

        CommentInfoCreate commentInfoCreate = CommentInfoCreate.builder()
                .content("대댓글 생성").referenceCommentId(referenceCommentId)
                .build();

        CommentInfo commentInfo = commentService.createCommentInfo(accountId, boardId, postId, commentInfoCreate);
        Assertions.assertEquals("대댓글 생성", commentInfo.getContent());
        Assertions.assertEquals(referenceCommentId, commentInfo.getReferenceCommentId());
        Assertions.assertFalse(commentInfo.getDeleted());
        Assertions.assertFalse(commentInfo.getReport());
    }

    @Test
    @DisplayName("존재하지 않는 댓글에 대댓글")
    public void invalidReferenceComment() throws Exception {
        String accountId = getAccountId(common00);
        String boardId = getBoardId(testPostInfo00.getBoard());
        String postId = getPostId(testPostInfo00);

        CommentInfoCreate commentInfoCreate = CommentInfoCreate.builder()
                .content("대댓글 생성").referenceCommentId("NOT_EXIST_COMMENT")
                .build();

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.commentService.createCommentInfo(accountId, boardId, postId, commentInfoCreate);
        });

        Assertions.assertEquals("05000001", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist reference comment.", iricomException.getMessage());
    }

    @Test
    @DisplayName("댓글 작성 후 포인트 추가")
    public void addPointComment() throws Exception {
        String accountId = getAccountId(common00);
        String boardId = getBoardId(testPostInfo00.getBoard());
        String postId = getPostId(testPostInfo00);

        long beforePoint = this.accountService.getAccountInfo(accountId).getPoint();

        CommentInfoCreate commentInfoCreate = CommentInfoCreate.builder()
                .content("댓글 생성")
                .build();
        commentService.createCommentInfo(accountId, boardId, postId, commentInfoCreate);

        long afterPoint = this.accountService.getAccountInfo(accountId).getPoint();
        Assertions.assertTrue(beforePoint < afterPoint);
    }
}

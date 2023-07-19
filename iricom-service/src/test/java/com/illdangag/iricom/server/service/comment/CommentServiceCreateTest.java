package com.illdangag.iricom.server.service.comment;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.data.request.CommentInfoCreate;
import com.illdangag.iricom.server.data.response.CommentInfo;
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

@DisplayName("service: 댓글 - 생성")
@Slf4j
public class CommentServiceCreateTest extends IricomTestSuite {
    @Autowired
    private CommentService commentService;

    private static final TestBoardInfo commentTestBoardInfo = TestBoardInfo.builder()
            .title("commentTestBoardInfo").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();

    private static final TestPostInfo commentTestPostInfo00 = TestPostInfo.builder()
            .title("commentTestPostInfo").content("commentTestPostInfo").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(commentTestBoardInfo)
            .build();

    private static final TestCommentInfo commentInfo00 = TestCommentInfo.builder()
            .content("commentInfo00").creator(allBoardAdmin).post(commentTestPostInfo00)
            .build();

    public CommentServiceCreateTest(ApplicationContext context) {
        super(context);

        addTestBoardInfo(commentTestBoardInfo);
        addTestPostInfo(commentTestPostInfo00);
        addTestCommentInfo(commentInfo00);
        init();
    }

    @Test
    @DisplayName("댓글 생성")
    public void createComment() throws Exception {
        Account account = getAccount(common00);
        Post post = getPost(commentTestPostInfo00);
        Board board = post.getBoard();

        CommentInfoCreate commentInfoCreate = CommentInfoCreate.builder()
                .content("댓글 생성")
                .build();
        CommentInfo commentInfo = commentService.createCommentInfo(account, board, post, commentInfoCreate);

        Assertions.assertEquals("댓글 생성", commentInfo.getContent());
        Assertions.assertNull(commentInfo.getReferenceCommentId());
        Assertions.assertFalse(commentInfo.getIsDeleted());
        Assertions.assertFalse(commentInfo.getIsReport());
    }

    @Test
    @DisplayName("대댓글 생성")
    public void createNestedComment() throws Exception {
        Account account = getAccount(common00);
        Post post = getPost(commentTestPostInfo00);
        Board board = post.getBoard();
        Comment referenceComment = getComment(commentInfo00);
        String referenceCommentId = String.valueOf(referenceComment.getId());

        CommentInfoCreate commentInfoCreate = CommentInfoCreate.builder()
                .content("대댓글 생성").referenceCommentId(referenceCommentId)
                .build();

        CommentInfo commentInfo = commentService.createCommentInfo(account, board, post, commentInfoCreate);
        Assertions.assertEquals("대댓글 생성", commentInfo.getContent());
        Assertions.assertEquals(referenceCommentId, commentInfo.getReferenceCommentId());
        Assertions.assertFalse(commentInfo.getIsDeleted());
        Assertions.assertFalse(commentInfo.getIsReport());
    }
}

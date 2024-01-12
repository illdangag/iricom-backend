package com.illdangag.iricom.server.service.block.comment;

import com.illdangag.iricom.server.data.entity.type.PostState;
import com.illdangag.iricom.server.data.entity.type.PostType;
import com.illdangag.iricom.server.data.request.CommentBlockInfoCreate;
import com.illdangag.iricom.server.data.response.CommentBlockInfo;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.service.BlockService;
import com.illdangag.iricom.server.service.CommentService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Collections;

@DisplayName("service: 차단 - 댓글 차단")
@Slf4j
@Transactional
public class BlockServiceCommentBlockTest extends IricomTestSuite {
    @Autowired
    private BlockService blockService;
    @Autowired
    private CommentService commentService;

    public BlockServiceCommentBlockTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("시스템 관리자")
    void blockSystemAdmin() {
        TestBoardInfo testBoardInfo = TestBoardInfo.builder()
                .title("board").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();
        TestPostInfo testPostInfo = TestPostInfo.builder()
                .title("post title").content("content").isAllowComment(true)
                .postType(PostType.POST).postState(PostState.PUBLISH)
                .creator(allBoardAdmin).board(testBoardInfo)
                .build();
        TestCommentInfo testCommentInfo = TestCommentInfo.builder()
                .content("comment").creator(allBoardAdmin).post(testPostInfo)
                .build();

        addTestBoardInfo(testBoardInfo);
        addTestPostInfo(testPostInfo);
        addTestCommentInfo(testCommentInfo);
        init();

        String accountId = getAccountId(systemAdmin);
        String boardId = getBoardId(testCommentInfo.getPost().getBoard());
        String postId = getPostId(testCommentInfo.getPost());
        String commentId = getCommentId(testCommentInfo);

        CommentBlockInfoCreate commentBlockInfoCreate = CommentBlockInfoCreate.builder()
                .reason("block reason")
                .build();

        CommentBlockInfo commentBlockInfo = this.blockService.blockComment(accountId, boardId, postId, commentId, commentBlockInfoCreate);

        Assertions.assertEquals("block reason", commentBlockInfo.getReason());
        Assertions.assertNotNull(commentBlockInfo.getCommentInfo());
        Assertions.assertEquals(commentId, commentBlockInfo.getCommentInfo().getId());
        Assertions.assertEquals("comment", commentBlockInfo.getCommentInfo().getContent());
    }

    @Test
    @DisplayName("게시판 관리자")
    void blockBoardAdmin() {
        TestBoardInfo testBoardInfo = TestBoardInfo.builder()
                .title("board").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();
        TestPostInfo testPostInfo = TestPostInfo.builder()
                .title("post title").content("content").isAllowComment(true)
                .postType(PostType.POST).postState(PostState.PUBLISH)
                .creator(allBoardAdmin).board(testBoardInfo)
                .build();
        TestCommentInfo testCommentInfo = TestCommentInfo.builder()
                .content("comment").creator(allBoardAdmin).post(testPostInfo)
                .build();

        addTestBoardInfo(testBoardInfo);
        addTestPostInfo(testPostInfo);
        addTestCommentInfo(testCommentInfo);
        init();

        String accountId = getAccountId(allBoardAdmin);
        String boardId = getBoardId(testCommentInfo.getPost().getBoard());
        String postId = getPostId(testCommentInfo.getPost());
        String commentId = getCommentId(testCommentInfo);

        CommentBlockInfoCreate commentBlockInfoCreate = CommentBlockInfoCreate.builder()
                .reason("block reason")
                .build();

        CommentBlockInfo commentBlockInfo = this.blockService.blockComment(accountId, boardId, postId, commentId, commentBlockInfoCreate);

        Assertions.assertEquals("block reason", commentBlockInfo.getReason());
        Assertions.assertNotNull(commentBlockInfo.getCommentInfo());
        Assertions.assertEquals(commentId, commentBlockInfo.getCommentInfo().getId());
        Assertions.assertEquals("comment", commentBlockInfo.getCommentInfo().getContent());
    }

    @Test
    @DisplayName("다른 게시판 관리자")
    void blockOtherBoardAdmin() {
        TestBoardInfo testBoardInfo00 = TestBoardInfo.builder()
                .title("board").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();
        TestBoardInfo testBoardInfo01 = TestBoardInfo.builder()
                .title("board").isEnabled(true).adminList(Arrays.asList(allBoardAdmin, common00)).build();
        TestPostInfo testPostInfo = TestPostInfo.builder()
                .title("post title").content("content").isAllowComment(true)
                .postType(PostType.POST).postState(PostState.PUBLISH)
                .creator(allBoardAdmin).board(testBoardInfo00)
                .build();
        TestCommentInfo testCommentInfo = TestCommentInfo.builder()
                .content("comment").creator(allBoardAdmin).post(testPostInfo)
                .build();

        addTestBoardInfo(testBoardInfo00, testBoardInfo01);
        addTestPostInfo(testPostInfo);
        addTestCommentInfo(testCommentInfo);
        init();

        String accountId = getAccountId(common00);
        String boardId = getBoardId(testCommentInfo.getPost().getBoard());
        String postId = getPostId(testCommentInfo.getPost());
        String commentId = getCommentId(testCommentInfo);

        CommentBlockInfoCreate commentBlockInfoCreate = CommentBlockInfoCreate.builder()
                .reason("block reason")
                .build();

        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            this.blockService.blockComment(accountId, boardId, postId, commentId, commentBlockInfoCreate);
        });

        Assertions.assertEquals("05000009", exception.getErrorCode());
    }

    @Test
    @DisplayName("일반 사용자")
    void blockAccount() {
        TestBoardInfo testBoardInfo00 = TestBoardInfo.builder()
                .title("board").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();
        TestPostInfo testPostInfo = TestPostInfo.builder()
                .title("post title").content("content").isAllowComment(true)
                .postType(PostType.POST).postState(PostState.PUBLISH)
                .creator(allBoardAdmin).board(testBoardInfo00)
                .build();
        TestCommentInfo testCommentInfo = TestCommentInfo.builder()
                .content("comment").creator(allBoardAdmin).post(testPostInfo)
                .build();

        addTestBoardInfo(testBoardInfo00);
        addTestPostInfo(testPostInfo);
        addTestCommentInfo(testCommentInfo);
        init();

        String accountId = getAccountId(common09);
        String boardId = getBoardId(testCommentInfo.getPost().getBoard());
        String postId = getPostId(testCommentInfo.getPost());
        String commentId = getCommentId(testCommentInfo);

        CommentBlockInfoCreate commentBlockInfoCreate = CommentBlockInfoCreate.builder()
                .reason("block reason")
                .build();

        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            this.blockService.blockComment(accountId, boardId, postId, commentId, commentBlockInfoCreate);
        });

        Assertions.assertEquals("05000009", exception.getErrorCode());
    }

    @Test
    @DisplayName("등록되지 않은 사용자")
    void blockUnknown() {
        TestBoardInfo testBoardInfo00 = TestBoardInfo.builder()
                .title("board").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();
        TestPostInfo testPostInfo = TestPostInfo.builder()
                .title("post title").content("content").isAllowComment(true)
                .postType(PostType.POST).postState(PostState.PUBLISH)
                .creator(allBoardAdmin).board(testBoardInfo00)
                .build();
        TestCommentInfo testCommentInfo = TestCommentInfo.builder()
                .content("comment").creator(allBoardAdmin).post(testPostInfo)
                .build();

        addTestBoardInfo(testBoardInfo00);
        addTestPostInfo(testPostInfo);
        addTestCommentInfo(testCommentInfo);
        init();

        String accountId = getAccountId(unknown00);
        String boardId = getBoardId(testCommentInfo.getPost().getBoard());
        String postId = getPostId(testCommentInfo.getPost());
        String commentId = getCommentId(testCommentInfo);

        CommentBlockInfoCreate commentBlockInfoCreate = CommentBlockInfoCreate.builder()
                .reason("block reason")
                .build();

        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            this.blockService.blockComment(accountId, boardId, postId, commentId, commentBlockInfoCreate);
        });

        Assertions.assertEquals("05000009", exception.getErrorCode());
    }

    @Test
    @DisplayName("이미 차단된 댓글")
    void alreadyBlockPost() {
        TestBoardInfo testBoardInfo00 = TestBoardInfo.builder()
                .title("board").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();
        TestPostInfo testPostInfo = TestPostInfo.builder()
                .title("post title").content("content").isAllowComment(true)
                .postType(PostType.POST).postState(PostState.PUBLISH)
                .creator(allBoardAdmin).board(testBoardInfo00)
                .build();
        TestCommentInfo testCommentInfo = TestCommentInfo.builder()
                .content("comment").creator(allBoardAdmin).post(testPostInfo)
                .build();
        TestPostBlockInfo testPostBlockInfo = TestPostBlockInfo.builder()
                        .post(testPostInfo).account(systemAdmin)
                        .reason("reason").build();

        addTestBoardInfo(testBoardInfo00);
        addTestPostInfo(testPostInfo);
        addTestCommentInfo(testCommentInfo);
        addTestPostBlockInfo(testPostBlockInfo);
        init();

        String accountId = getAccountId(systemAdmin);
        String boardId = getBoardId(testCommentInfo.getPost().getBoard());
        String postId = getPostId(testCommentInfo.getPost());
        String commentId = getCommentId(testCommentInfo);

        CommentBlockInfoCreate commentBlockInfoCreate = CommentBlockInfoCreate.builder()
                .reason("block reason")
                .build();

        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            this.blockService.blockComment(accountId, boardId, postId, commentId, commentBlockInfoCreate);
        });

        Assertions.assertEquals("04000010", exception.getErrorCode());
    }

    @Test
    @DisplayName("이미 차단된 댓글")
    void alreadyBlockComment() {
        TestBoardInfo testBoardInfo00 = TestBoardInfo.builder()
                .title("board").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();
        TestPostInfo testPostInfo = TestPostInfo.builder()
                .title("post title").content("content").isAllowComment(true)
                .postType(PostType.POST).postState(PostState.PUBLISH)
                .creator(allBoardAdmin).board(testBoardInfo00)
                .build();
        TestCommentInfo testCommentInfo = TestCommentInfo.builder()
                .content("comment").creator(allBoardAdmin).post(testPostInfo)
                .build();
        TestCommentBlockInfo testCommentBlockInfo = TestCommentBlockInfo.builder()
                .comment(testCommentInfo).account(systemAdmin)
                .reason("block reason")
                .build();

        addTestBoardInfo(testBoardInfo00);
        addTestPostInfo(testPostInfo);
        addTestCommentInfo(testCommentInfo);
        addTestCommentBlockInfo(testCommentBlockInfo);
        init();

        String accountInfo = getAccountId(systemAdmin);
        String boardId = getBoardId(testCommentInfo.getPost().getBoard());
        String postId = getPostId(testCommentInfo.getPost());
        String commentId = getCommentId(testCommentInfo);

        CommentBlockInfoCreate commentBlockInfoCreate = CommentBlockInfoCreate.builder()
                .reason("block reason")
                .build();

        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            this.blockService.blockComment(accountInfo, boardId, postId, commentId, commentBlockInfoCreate);
        });

        Assertions.assertEquals("05000008", exception.getErrorCode());
    }

    @Test
    @DisplayName("차단된 댓글 조회")
    void getBlockComment() {
        TestBoardInfo testBoardInfo00 = TestBoardInfo.builder()
                .title("board").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();
        TestPostInfo testPostInfo = TestPostInfo.builder()
                .title("post title").content("content").isAllowComment(true)
                .postType(PostType.POST).postState(PostState.PUBLISH)
                .creator(allBoardAdmin).board(testBoardInfo00)
                .build();
        TestCommentInfo testCommentInfo = TestCommentInfo.builder()
                .content("comment").creator(allBoardAdmin).post(testPostInfo)
                .build();
        TestCommentBlockInfo testCommentBlockInfo = TestCommentBlockInfo.builder()
                .comment(testCommentInfo).account(systemAdmin)
                .reason("block reason")
                .build();

        addTestBoardInfo(testBoardInfo00);
        addTestPostInfo(testPostInfo);
        addTestCommentInfo(testCommentInfo);
        addTestCommentBlockInfo(testCommentBlockInfo);
        init();

        String accountInfo = getAccountId(systemAdmin);
        String boardId = getBoardId(testCommentInfo.getPost().getBoard());
        String postId = getPostId(testCommentInfo.getPost());
        String commentId = getCommentId(testCommentInfo);

        this.commentService.getComment(boardId, postId, commentId);
    }
}

package com.illdangag.iricom.server.service.block.comment;

import com.illdangag.iricom.server.data.entity.type.PostState;
import com.illdangag.iricom.server.data.entity.type.PostType;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.service.BlockService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestCommentBlockInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestCommentInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestPostInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.Collections;

@DisplayName("service: 차단 - 댓글 차단 해제")
public class BlockServiceCommentUnblockTest extends IricomTestSuite {
    @Autowired
    private BlockService blockService;

    public BlockServiceCommentUnblockTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("시스템 관리자")
    void unblockSystemAdmin() {
        TestBoardInfo testBoardInfo = TestBoardInfo.builder()
                .title("board").isEnabled(true)
                .adminList(Collections.singletonList(allBoardAdmin))
                .build();
        TestPostInfo testPostInfo = TestPostInfo.builder()
                .board(testBoardInfo).creator(allBoardAdmin)
                .title("post").content("content").isAllowComment(true)
                .postType(PostType.POST).postState(PostState.PUBLISH)
                .build();
        TestCommentInfo testCommentInfo = TestCommentInfo.builder()
                .post(testPostInfo).creator(allBoardAdmin)
                .content("comment")
                .build();
        TestCommentBlockInfo testCommentBlockInfo = TestCommentBlockInfo.builder()
                .account(systemAdmin).comment(testCommentInfo)
                .reason("test")
                .build();

        addTestBoardInfo(testBoardInfo);
        addTestPostInfo(testPostInfo);
        addTestCommentInfo(testCommentInfo);
        addTestCommentBlockInfo(testCommentBlockInfo);
        init();

        String accountId = getAccountId(systemAdmin);
        String boardId = getBoardId(testCommentInfo.getPost().getBoard());
        String postId = getPostId(testCommentInfo.getPost());
        String commentId = getCommentId(testCommentInfo);

        this.blockService.unblockComment(accountId, boardId, postId, commentId);
    }

    @Test
    @DisplayName("게시판 관리자")
    void unblockBoardAdmin() {
        TestBoardInfo testBoardInfo = TestBoardInfo.builder()
                .title("board").isEnabled(true)
                .adminList(Collections.singletonList(allBoardAdmin))
                .build();
        TestPostInfo testPostInfo = TestPostInfo.builder()
                .board(testBoardInfo).creator(allBoardAdmin)
                .title("post").content("content").isAllowComment(true)
                .postType(PostType.POST).postState(PostState.PUBLISH)
                .build();
        TestCommentInfo testCommentInfo = TestCommentInfo.builder()
                .post(testPostInfo).creator(allBoardAdmin)
                .content("comment")
                .build();
        TestCommentBlockInfo testCommentBlockInfo = TestCommentBlockInfo.builder()
                .account(systemAdmin).comment(testCommentInfo)
                .reason("test")
                .build();

        addTestBoardInfo(testBoardInfo);
        addTestPostInfo(testPostInfo);
        addTestCommentInfo(testCommentInfo);
        addTestCommentBlockInfo(testCommentBlockInfo);
        init();

        String accountId = getAccountId(allBoardAdmin);
        String boardId = getBoardId(testCommentInfo.getPost().getBoard());
        String postId = getPostId(testCommentInfo.getPost());
        String commentId = getCommentId(testCommentInfo);

        this.blockService.unblockComment(accountId, boardId, postId, commentId);
    }

    @Test
    @DisplayName("다른 게시판 관리자")
    void unblockOtherBoardAdmin() {
        TestBoardInfo testBoardInfo00 = TestBoardInfo.builder()
                .title("board").isEnabled(true)
                .adminList(Collections.singletonList(allBoardAdmin))
                .build();
        TestBoardInfo testBoardInfo01 = TestBoardInfo.builder()
                .title("board").isEnabled(true)
                .adminList(Arrays.asList(allBoardAdmin, common00))
                .build();
        TestPostInfo testPostInfo = TestPostInfo.builder()
                .board(testBoardInfo00).creator(allBoardAdmin)
                .title("post").content("content").isAllowComment(true)
                .postType(PostType.POST).postState(PostState.PUBLISH)
                .build();
        TestCommentInfo testCommentInfo = TestCommentInfo.builder()
                .post(testPostInfo).creator(allBoardAdmin)
                .content("comment")
                .build();
        TestCommentBlockInfo testCommentBlockInfo = TestCommentBlockInfo.builder()
                .account(systemAdmin).comment(testCommentInfo)
                .reason("test")
                .build();

        addTestBoardInfo(testBoardInfo00, testBoardInfo01);
        addTestPostInfo(testPostInfo);
        addTestCommentInfo(testCommentInfo);
        addTestCommentBlockInfo(testCommentBlockInfo);
        init();

        String accountId = getAccountId(common00);
        String boardId = getBoardId(testCommentInfo.getPost().getBoard());
        String postId = getPostId(testCommentInfo.getPost());
        String commentId = getCommentId(testCommentInfo);

        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            this.blockService.unblockComment(accountId, boardId, postId, commentId);
        });

        Assertions.assertEquals("05000009", exception.getErrorCode());
    }

    @Test
    @DisplayName("일반 사용자")
    void unblockAccount() {
        TestBoardInfo testBoardInfo = TestBoardInfo.builder()
                .title("board").isEnabled(true)
                .adminList(Collections.singletonList(allBoardAdmin))
                .build();
        TestPostInfo testPostInfo = TestPostInfo.builder()
                .board(testBoardInfo).creator(allBoardAdmin)
                .title("post").content("content").isAllowComment(true)
                .postType(PostType.POST).postState(PostState.PUBLISH)
                .build();
        TestCommentInfo testCommentInfo = TestCommentInfo.builder()
                .post(testPostInfo).creator(allBoardAdmin)
                .content("comment")
                .build();
        TestCommentBlockInfo testCommentBlockInfo = TestCommentBlockInfo.builder()
                .account(systemAdmin).comment(testCommentInfo)
                .reason("test")
                .build();

        addTestBoardInfo(testBoardInfo);
        addTestPostInfo(testPostInfo);
        addTestCommentInfo(testCommentInfo);
        addTestCommentBlockInfo(testCommentBlockInfo);
        init();

        String accountId = getAccountId(common00);
        String boardId = getBoardId(testCommentInfo.getPost().getBoard());
        String postId = getPostId(testCommentInfo.getPost());
        String commentId = getCommentId(testCommentInfo);

        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            this.blockService.unblockComment(accountId, boardId, postId, commentId);
        });

        Assertions.assertEquals("05000009", exception.getErrorCode());
    }

    @Test
    @DisplayName("등록되지 않은 사용자")
    void unblockUnknown() {
        TestBoardInfo testBoardInfo = TestBoardInfo.builder()
                .title("board").isEnabled(true)
                .adminList(Collections.singletonList(allBoardAdmin))
                .build();
        TestPostInfo testPostInfo = TestPostInfo.builder()
                .board(testBoardInfo).creator(allBoardAdmin)
                .title("post").content("content").isAllowComment(true)
                .postType(PostType.POST).postState(PostState.PUBLISH)
                .build();
        TestCommentInfo testCommentInfo = TestCommentInfo.builder()
                .post(testPostInfo).creator(allBoardAdmin)
                .content("comment")
                .build();
        TestCommentBlockInfo testCommentBlockInfo = TestCommentBlockInfo.builder()
                .account(systemAdmin).comment(testCommentInfo)
                .reason("test")
                .build();

        addTestBoardInfo(testBoardInfo);
        addTestPostInfo(testPostInfo);
        addTestCommentInfo(testCommentInfo);
        addTestCommentBlockInfo(testCommentBlockInfo);
        init();

        String accountId = getAccountId(unknown00);
        String boardId = getBoardId(testCommentInfo.getPost().getBoard());
        String postId = getPostId(testCommentInfo.getPost());
        String commentId = getCommentId(testCommentInfo);

        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            this.blockService.unblockComment(accountId, boardId, postId, commentId);
        });

        Assertions.assertEquals("05000009", exception.getErrorCode());
    }

    @Test
    @DisplayName("차단되지 않은 댓글")
    void unblockCommentNotBlockComment() {
        TestBoardInfo testBoardInfo = TestBoardInfo.builder()
                .title("board").isEnabled(true)
                .adminList(Collections.singletonList(allBoardAdmin))
                .build();
        TestPostInfo testPostInfo = TestPostInfo.builder()
                .board(testBoardInfo).creator(allBoardAdmin)
                .title("post").content("content").isAllowComment(true)
                .postType(PostType.POST).postState(PostState.PUBLISH)
                .build();
        TestCommentInfo testCommentInfo = TestCommentInfo.builder()
                .post(testPostInfo).creator(allBoardAdmin)
                .content("comment")
                .build();

        addTestBoardInfo(testBoardInfo);
        addTestPostInfo(testPostInfo);
        addTestCommentInfo(testCommentInfo);
        init();

        String accountId = getAccountId(systemAdmin);
        String boardId = getBoardId(testCommentInfo.getPost().getBoard());
        String postId = getPostId(testCommentInfo.getPost());
        String commentId = getCommentId(testCommentInfo);

        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            this.blockService.unblockComment(accountId, boardId, postId, commentId);
        });

        Assertions.assertEquals("050000010", exception.getErrorCode());
    }
}

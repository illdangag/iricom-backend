package com.illdangag.iricom.server.service.ban.comment;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.type.PostState;
import com.illdangag.iricom.server.data.entity.type.PostType;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.service.BanService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestCommentBanInfo;
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
public class BanServiceCommentUnbanTest extends IricomTestSuite {
    @Autowired
    private BanService banService;

    public BanServiceCommentUnbanTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("시스템 관리자")
    void unbanSystemAdmin() {
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
        TestCommentBanInfo testCommentBanInfo = TestCommentBanInfo.builder()
                .banAccount(systemAdmin).comment(testCommentInfo)
                .reason("test")
                .build();

        addTestBoardInfo(testBoardInfo);
        addTestPostInfo(testPostInfo);
        addTestCommentInfo(testCommentInfo);
        addTestCommentBanInfo(testCommentBanInfo);
        init();

        Account account = getAccount(systemAdmin);
        String boardId = getBoardId(testCommentInfo.getPost().getBoard());
        String postId = getPostId(testCommentInfo.getPost());
        String commentId = getCommentId(testCommentInfo);

        this.banService.unbanComment(account, boardId, postId, commentId);
    }

    @Test
    @DisplayName("게시판 관리자")
    void unbanBoardAdmin() {
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
        TestCommentBanInfo testCommentBanInfo = TestCommentBanInfo.builder()
                .banAccount(systemAdmin).comment(testCommentInfo)
                .reason("test")
                .build();

        addTestBoardInfo(testBoardInfo);
        addTestPostInfo(testPostInfo);
        addTestCommentInfo(testCommentInfo);
        addTestCommentBanInfo(testCommentBanInfo);
        init();

        Account account = getAccount(allBoardAdmin);
        String boardId = getBoardId(testCommentInfo.getPost().getBoard());
        String postId = getPostId(testCommentInfo.getPost());
        String commentId = getCommentId(testCommentInfo);

        this.banService.unbanComment(account, boardId, postId, commentId);
    }

    @Test
    @DisplayName("다른 게시판 관리자")
    void unbanOtherBoardAdmin() {
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
        TestCommentBanInfo testCommentBanInfo = TestCommentBanInfo.builder()
                .banAccount(systemAdmin).comment(testCommentInfo)
                .reason("test")
                .build();

        addTestBoardInfo(testBoardInfo00, testBoardInfo01);
        addTestPostInfo(testPostInfo);
        addTestCommentInfo(testCommentInfo);
        addTestCommentBanInfo(testCommentBanInfo);
        init();

        Account account = getAccount(common00);
        String boardId = getBoardId(testCommentInfo.getPost().getBoard());
        String postId = getPostId(testCommentInfo.getPost());
        String commentId = getCommentId(testCommentInfo);

        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            this.banService.unbanComment(account, boardId, postId, commentId);
        });

        Assertions.assertEquals("05000009", exception.getErrorCode());
    }

    @Test
    @DisplayName("일반 사용자")
    void unbanAccount() {
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
        TestCommentBanInfo testCommentBanInfo = TestCommentBanInfo.builder()
                .banAccount(systemAdmin).comment(testCommentInfo)
                .reason("test")
                .build();

        addTestBoardInfo(testBoardInfo);
        addTestPostInfo(testPostInfo);
        addTestCommentInfo(testCommentInfo);
        addTestCommentBanInfo(testCommentBanInfo);
        init();

        Account account = getAccount(common00);
        String boardId = getBoardId(testCommentInfo.getPost().getBoard());
        String postId = getPostId(testCommentInfo.getPost());
        String commentId = getCommentId(testCommentInfo);

        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            this.banService.unbanComment(account, boardId, postId, commentId);
        });

        Assertions.assertEquals("05000009", exception.getErrorCode());
    }

    @Test
    @DisplayName("등록되지 않은 사용자")
    void unbanUnknown() {
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
        TestCommentBanInfo testCommentBanInfo = TestCommentBanInfo.builder()
                .banAccount(systemAdmin).comment(testCommentInfo)
                .reason("test")
                .build();

        addTestBoardInfo(testBoardInfo);
        addTestPostInfo(testPostInfo);
        addTestCommentInfo(testCommentInfo);
        addTestCommentBanInfo(testCommentBanInfo);
        init();

        Account account = getAccount(unknown00);
        String boardId = getBoardId(testCommentInfo.getPost().getBoard());
        String postId = getPostId(testCommentInfo.getPost());
        String commentId = getCommentId(testCommentInfo);

        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            this.banService.unbanComment(account, boardId, postId, commentId);
        });

        Assertions.assertEquals("05000009", exception.getErrorCode());
    }

    @Test
    @DisplayName("차단되지 않은 댓글")
    void unbanCommentNotBanComment() {
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

        Account account = getAccount(systemAdmin);
        String boardId = getBoardId(testCommentInfo.getPost().getBoard());
        String postId = getPostId(testCommentInfo.getPost());
        String commentId = getCommentId(testCommentInfo);

        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            this.banService.unbanComment(account, boardId, postId, commentId);
        });

        Assertions.assertEquals("050000010", exception.getErrorCode());
    }
}

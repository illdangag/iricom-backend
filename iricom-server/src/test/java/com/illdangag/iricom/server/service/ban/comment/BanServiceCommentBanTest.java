package com.illdangag.iricom.server.service.ban.comment;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.type.PostState;
import com.illdangag.iricom.server.data.entity.type.PostType;
import com.illdangag.iricom.server.data.request.CommentBanInfoCreate;
import com.illdangag.iricom.server.data.response.CommentBanInfo;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.service.BanService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.Collections;

@DisplayName("service: 차단 - 댓글 차단")
@Slf4j
public class BanServiceCommentBanTest extends IricomTestSuite {
    @Autowired
    private BanService banService;

    public BanServiceCommentBanTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("시스템 관리자")
    void banSystemAdmin() {
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

        Account account = getAccount(systemAdmin);
        String boardId = getBoardId(testBoardInfo);
        String postId = getPostId(testPostInfo);
        String commentId = getCommentId(testCommentInfo);

        CommentBanInfoCreate commentBanInfoCreate = CommentBanInfoCreate.builder()
                .reason("ban reason")
                .build();

        CommentBanInfo commentBanInfo = this.banService.banComment(account, boardId, postId, commentId, commentBanInfoCreate);

        Assertions.assertEquals("ban reason", commentBanInfo.getReason());
        Assertions.assertNotNull(commentBanInfo.getCommentInfo());
        Assertions.assertEquals(commentId, commentBanInfo.getCommentInfo().getId());
        Assertions.assertEquals("comment", commentBanInfo.getCommentInfo().getContent());
    }

    @Test
    @DisplayName("게시판 관리자")
    void banBoardAdmin() {
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

        Account account = getAccount(allBoardAdmin);
        String boardId = getBoardId(testBoardInfo);
        String postId = getPostId(testPostInfo);
        String commentId = getCommentId(testCommentInfo);

        CommentBanInfoCreate commentBanInfoCreate = CommentBanInfoCreate.builder()
                .reason("ban reason")
                .build();

        CommentBanInfo commentBanInfo = this.banService.banComment(account, boardId, postId, commentId, commentBanInfoCreate);

        Assertions.assertEquals("ban reason", commentBanInfo.getReason());
        Assertions.assertNotNull(commentBanInfo.getCommentInfo());
        Assertions.assertEquals(commentId, commentBanInfo.getCommentInfo().getId());
        Assertions.assertEquals("comment", commentBanInfo.getCommentInfo().getContent());
    }

    @Test
    @DisplayName("다른 게시판 관리자")
    void banOtherBoardAdmin() {
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

        Account account = getAccount(common00);
        String boardId = getBoardId(testBoardInfo00);
        String postId = getPostId(testPostInfo);
        String commentId = getCommentId(testCommentInfo);

        CommentBanInfoCreate commentBanInfoCreate = CommentBanInfoCreate.builder()
                .reason("ban reason")
                .build();

        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            this.banService.banComment(account, boardId, postId, commentId, commentBanInfoCreate);
        });

        Assertions.assertEquals("05000009", exception.getErrorCode());
    }

    @Test
    @DisplayName("일반 사용자")
    void banAccount() {
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

        Account account = getAccount(common09);
        String boardId = getBoardId(testBoardInfo00);
        String postId = getPostId(testPostInfo);
        String commentId = getCommentId(testCommentInfo);

        CommentBanInfoCreate commentBanInfoCreate = CommentBanInfoCreate.builder()
                .reason("ban reason")
                .build();

        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            this.banService.banComment(account, boardId, postId, commentId, commentBanInfoCreate);
        });

        Assertions.assertEquals("05000009", exception.getErrorCode());
    }

    @Test
    @DisplayName("등록되지 않은 사용자")
    void banUnknown() {
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

        Account account = getAccount(unknown00);
        String boardId = getBoardId(testBoardInfo00);
        String postId = getPostId(testPostInfo);
        String commentId = getCommentId(testCommentInfo);

        CommentBanInfoCreate commentBanInfoCreate = CommentBanInfoCreate.builder()
                .reason("ban reason")
                .build();

        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            this.banService.banComment(account, boardId, postId, commentId, commentBanInfoCreate);
        });

        Assertions.assertEquals("05000009", exception.getErrorCode());
    }

    @Test
    @DisplayName("이미 차단된 게시물")
    void alreadyBanPost() {
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
        TestPostBanInfo testPostBanInfo = TestPostBanInfo.builder()
                        .post(testPostInfo).banAccount(systemAdmin)
                        .reason("reason").build();

        addTestBoardInfo(testBoardInfo00);
        addTestPostInfo(testPostInfo);
        addTestCommentInfo(testCommentInfo);
        addTestPostBanInfo(testPostBanInfo);
        init();

        Account account = getAccount(systemAdmin);
        String boardId = getBoardId(testBoardInfo00);
        String postId = getPostId(testPostInfo);
        String commentId = getCommentId(testCommentInfo);

        CommentBanInfoCreate commentBanInfoCreate = CommentBanInfoCreate.builder()
                .reason("ban reason")
                .build();

        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            this.banService.banComment(account, boardId, postId, commentId, commentBanInfoCreate);
        });

        Assertions.assertEquals("04000010", exception.getErrorCode());
    }

    @Test
    @DisplayName("이미 차단된 게시물")
    void alreadyBanComment() {
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
        TestCommentBanInfo testCommentBanInfo = TestCommentBanInfo.builder()
                .comment(testCommentInfo).banAccount(systemAdmin)
                .reason("ban reason")
                .build();

        addTestBoardInfo(testBoardInfo00);
        addTestPostInfo(testPostInfo);
        addTestCommentInfo(testCommentInfo);
        addTestCommentBanInfo(testCommentBanInfo);
        init();

        Account account = getAccount(systemAdmin);
        String boardId = getBoardId(testBoardInfo00);
        String postId = getPostId(testPostInfo);
        String commentId = getCommentId(testCommentInfo);

        CommentBanInfoCreate commentBanInfoCreate = CommentBanInfoCreate.builder()
                .reason("ban reason")
                .build();

        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            this.banService.banComment(account, boardId, postId, commentId, commentBanInfoCreate);
        });

        Assertions.assertEquals("05000008", exception.getErrorCode());
    }
}

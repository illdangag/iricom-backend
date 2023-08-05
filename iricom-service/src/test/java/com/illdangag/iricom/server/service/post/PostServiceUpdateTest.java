package com.illdangag.iricom.server.service.post;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.data.request.PostInfoUpdate;
import com.illdangag.iricom.server.data.response.PostInfo;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.service.PostService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestPostInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.validation.ConstraintViolationException;
import java.util.Collections;

@DisplayName("service: 게시물 - 수정")
@Slf4j
public class PostServiceUpdateTest extends IricomTestSuite {
    @Autowired
    private PostService postService;

    // 공개 게시판
    private final TestBoardInfo testBoardInfo00 = TestBoardInfo.builder()
            .title("testBoardInfo00").adminList(Collections.singletonList(allBoardAdmin))
            .isEnabled(true).build();
    private final TestBoardInfo testBoardInfo01 = TestBoardInfo.builder()
            .title("testBoardInfo01").adminList(Collections.singletonList(allBoardAdmin))
            .isEnabled(true).build();
    // 비활성화 게시판
    private final TestBoardInfo disabledTestBoardInfo00 = TestBoardInfo.builder()
            .title("disabledTestBoardInfo00").adminList(Collections.singletonList(allBoardAdmin))
            .isEnabled(false).build();
    // 비공개 게시판
    private final TestBoardInfo undisclosedBoardInfo00 = TestBoardInfo.builder()
            .title("undisclosedBoardInfo00").adminList(Collections.singletonList(allBoardAdmin))
            .isEnabled(true).undisclosed(true).build();
    // 공지 사항 전용 게시판
    private final TestBoardInfo notificationTestBoardInfo00 = TestBoardInfo.builder()
            .title("notificationTestBoardInfo00").adminList(Collections.singletonList(allBoardAdmin))
            .isEnabled(true).undisclosed(false).notificationOnly(true).build();

    // 게시물
    private final TestPostInfo temporaryTestPostInfo00 = TestPostInfo.builder()
            .title("temporaryTestPostInfo00").content("temporaryTestPostInfo00").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.TEMPORARY)
            .creator(common00).board(testBoardInfo00).build();
    private final TestPostInfo publishTestPostInfo00 = TestPostInfo.builder()
            .title("publishTestPostInfo00").content("publishTestPostInfo00").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(testBoardInfo00).build();
    private final TestPostInfo publishTestPostInfo01 = TestPostInfo.builder()
            .title("publishTestPostInfo00").content("publishTestPostInfo00").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(testBoardInfo00).build();
    private final TestPostInfo temporaryTestNotificationInfo00 = TestPostInfo.builder()
            .title("temporaryTestNotificationInfo00").content("temporaryTestNotificationInfo00").isAllowComment(true)
            .postType(PostType.NOTIFICATION).postState(PostState.TEMPORARY)
            .creator(allBoardAdmin).board(testBoardInfo00).build();
    private final TestPostInfo publishTestNotificationInfo00 = TestPostInfo.builder()
            .title("temporaryTestNotificationInfo00").content("temporaryTestNotificationInfo00").isAllowComment(true)
            .postType(PostType.NOTIFICATION).postState(PostState.TEMPORARY)
            .creator(allBoardAdmin).board(testBoardInfo00).build();
    private final TestPostInfo disabledBoardTestPostInfo00 = TestPostInfo.builder()
            .title("disabledBoardTestPostInfo00").content("disabledBoardTestPostInfo00").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(disabledTestBoardInfo00).build();
    private final TestPostInfo publishTestPostInfo02 = TestPostInfo.builder()
            .title("publishTestPostInfo02").content("publishTestPostInfo02").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(notificationTestBoardInfo00).build();
    private final TestPostInfo temporaryTestNotificationInfo01 = TestPostInfo.builder()
            .title("temporaryTestNotificationInfo01").content("temporaryTestNotificationInfo01").isAllowComment(true)
            .postType(PostType.NOTIFICATION).postState(PostState.TEMPORARY)
            .creator(allBoardAdmin).board(notificationTestBoardInfo00).build();
    private final TestPostInfo temporaryTestNotificationInfo02 = TestPostInfo.builder()
            .title("temporaryTestNotificationInfo02").content("temporaryTestNotificationInfo00").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.TEMPORARY)
            .creator(common00).board(testBoardInfo00).build();
    private final TestPostInfo temporaryTestNotificationInfo03 = TestPostInfo.builder()
            .title("temporaryTestNotificationInfo02").content("temporaryTestNotificationInfo00").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.TEMPORARY)
            .creator(allBoardAdmin).board(testBoardInfo00).build();
    private final TestPostInfo temporaryTestNotificationInfo04 = TestPostInfo.builder()
            .title("temporaryTestNotificationInfo02").content("temporaryTestNotificationInfo00").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.TEMPORARY)
            .creator(allBoardAdmin).board(notificationTestBoardInfo00).build();

    @Autowired
    public PostServiceUpdateTest(ApplicationContext context) {
        super(context);

        addTestBoardInfo(testBoardInfo00, testBoardInfo01, disabledTestBoardInfo00, undisclosedBoardInfo00, notificationTestBoardInfo00);
        addTestPostInfo(temporaryTestPostInfo00,
                publishTestPostInfo00, publishTestPostInfo01, publishTestPostInfo02,
                temporaryTestNotificationInfo00,
                publishTestNotificationInfo00, disabledBoardTestPostInfo00, temporaryTestNotificationInfo01, temporaryTestNotificationInfo02,
                temporaryTestNotificationInfo03, temporaryTestNotificationInfo04);

        init();
    }

    @Test
    @DisplayName("발행하지 않은 게시물")
    public void updateTemporaryPost() {
        String boardId = getBoardId(temporaryTestPostInfo00.getBoard());
        String postId = getPostId(temporaryTestPostInfo00);
        Account account = getAccount(temporaryTestPostInfo00.getCreator());

        PostInfoUpdate postInfoUpdate = PostInfoUpdate.builder()
                .title("update title")
                .content("update content")
                .build();

        PostInfo postInfo = this.postService.updatePostInfo(account, boardId, postId, postInfoUpdate);

        Assertions.assertEquals("update title", postInfo.getTitle());
        Assertions.assertEquals("update content", postInfo.getContent());
        Assertions.assertTrue(postInfo.getHasTemporary());
    }

    @Test
    @DisplayName("발행한 게시물")
    public void updatePublishPost() {
        String boardId = getBoardId(publishTestPostInfo00.getBoard());
        String postId = getPostId(publishTestPostInfo00);
        Account account = getAccount(publishTestPostInfo00.getCreator());

        PostInfoUpdate postInfoUpdate = PostInfoUpdate.builder()
                .title("update title")
                .content("update content")
                .build();

        PostInfo postInfo = this.postService.updatePostInfo(account, boardId, postId, postInfoUpdate);

        Assertions.assertEquals("update title", postInfo.getTitle());
        Assertions.assertEquals("update content", postInfo.getContent());
        Assertions.assertTrue(postInfo.getHasTemporary());
    }

    @Test
    @DisplayName("발행하지 않은 공지사항")
    public void updateTemporaryNotification() {
        String boardId = getBoardId(temporaryTestNotificationInfo00.getBoard());
        String postId = getPostId(temporaryTestNotificationInfo00);
        Account account = getAccount(temporaryTestNotificationInfo00.getCreator());

        PostInfoUpdate postInfoUpdate = PostInfoUpdate.builder()
                .title("update title")
                .content("update content")
                .build();

        PostInfo postInfo = this.postService.updatePostInfo(account, boardId, postId, postInfoUpdate);

        Assertions.assertEquals("update title", postInfo.getTitle());
        Assertions.assertEquals("update content", postInfo.getContent());
        Assertions.assertTrue(postInfo.getHasTemporary());
    }

    @Test
    @DisplayName("발행한 공지사항")
    public void updatePublishNotification() {
        String boardId = getBoardId(publishTestNotificationInfo00.getBoard());
        String postId = getPostId(publishTestNotificationInfo00);
        Account account = getAccount(publishTestNotificationInfo00.getCreator());

        PostInfoUpdate postInfoUpdate = PostInfoUpdate.builder()
                .title("update title")
                .content("update content")
                .build();

        PostInfo postInfo = this.postService.updatePostInfo(account, boardId, postId, postInfoUpdate);

        Assertions.assertEquals("update title", postInfo.getTitle());
        Assertions.assertEquals("update content", postInfo.getContent());
        Assertions.assertTrue(postInfo.getHasTemporary());
    }

    @Test
    @DisplayName("작성자가 다른 게시물")
    public void updateOtherCreator() {
        Account account = getAccount(common01);
        String boardId = getBoardId(publishTestPostInfo01.getBoard());
        String postId = getPostId(publishTestPostInfo01);

        PostInfoUpdate postInfoUpdate = PostInfoUpdate.builder()
                .title("update title")
                .content("update content")
                .build();

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.postService.updatePostInfo(account, boardId, postId, postInfoUpdate);
        });

        Assertions.assertEquals("04000002", iricomException.getErrorCode());
        Assertions.assertEquals("Invalid authorization.", iricomException.getMessage());
    }

    @Test
    @DisplayName("제목이 빈 문자열")
    public void emptyTitle() {
        String boardId = getBoardId(publishTestPostInfo00.getBoard());
        String postId = getPostId(publishTestPostInfo00);
        Account account = getAccount(publishTestPostInfo00.getCreator());

        PostInfoUpdate postInfoUpdate = PostInfoUpdate.builder()
                .title("")
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            this.postService.updatePostInfo(account, boardId, postId, postInfoUpdate);
        });
    }

    @Test
    @DisplayName("제목이 긴 문자열")
    public void overflowTitle() {
        String boardId = getBoardId(publishTestPostInfo00.getBoard());
        String postId = getPostId(publishTestPostInfo00);
        Account account = getAccount(publishTestPostInfo00.getCreator());

        PostInfoUpdate postInfoUpdate = PostInfoUpdate.builder()
                .title("01234567890123456789012345678901234567890")
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            this.postService.updatePostInfo(account, boardId, postId, postInfoUpdate);
        });
    }

    @Test
    @DisplayName("존재하지 않는 게시판")
    public void notExistBoard() {
        String postId = getPostId(publishTestPostInfo00);
        Account account = getAccount(publishTestPostInfo00.getCreator());

        String boardId = "NOT_EXIST_BOARD";

        PostInfoUpdate postInfoUpdate = PostInfoUpdate.builder()
                .title("not exist board")
                .content("update content")
                .build();

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.postService.updatePostInfo(account, boardId, postId, postInfoUpdate);
        });

        Assertions.assertEquals("03000000", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist board.", iricomException.getMessage());
    }

    @Test
    @DisplayName("일치하지 않는 게시판")
    public void notMatchBoard() {
        String boardId = getBoardId(testBoardInfo01);
        String postId = getPostId(publishTestPostInfo00);
        Account account = getAccount(publishTestPostInfo00.getCreator());

        PostInfoUpdate postInfoUpdate = PostInfoUpdate.builder()
                .title("not matched board")
                .content("update content")
                .build();

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.postService.updatePostInfo(account, boardId, postId, postInfoUpdate);
        });

        Assertions.assertEquals("04000000", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist post.", iricomException.getMessage());
    }

    @Test
    @DisplayName("비활성화 게시판")
    public void disabledBoard() {
        String boardId = getBoardId(disabledBoardTestPostInfo00.getBoard());
        String postId = getPostId(disabledBoardTestPostInfo00);
        Account account = getAccount(disabledBoardTestPostInfo00.getCreator());

        PostInfoUpdate postInfoUpdate = PostInfoUpdate.builder()
                .title("disabled board post")
                .content("update content")
                .build();

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.postService.updatePostInfo(account, boardId, postId, postInfoUpdate);
        });

        Assertions.assertEquals("03000001", iricomException.getErrorCode());
        Assertions.assertEquals("Board is disabled.", iricomException.getMessage());
    }

    @Test
    @DisplayName("공지사항 전용 게시판의 일반 게시물")
    public void updatePostOnlyNotification() {
        String boardId = getBoardId(publishTestPostInfo02.getBoard());
        String postId = getPostId(publishTestPostInfo02);
        Account account = getAccount(publishTestPostInfo02.getCreator());

        PostInfoUpdate postInfoUpdate = PostInfoUpdate.builder()
                .title("notification only board post")
                .content("update content")
                .build();

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.postService.updatePostInfo(account, boardId, postId, postInfoUpdate);
        });

        Assertions.assertEquals("04000013", iricomException.getErrorCode());
        Assertions.assertEquals("Board is for notification only.", iricomException.getMessage());
    }

    @Test
    @DisplayName("공지 사항 전용 게시판에 공지 사항 게시물")
    public void updateNotificationOnlyNotification() {
        String boardId = getBoardId(temporaryTestNotificationInfo01.getBoard());
        String postId = getPostId(temporaryTestNotificationInfo01);
        Account account = getAccount(temporaryTestNotificationInfo01.getCreator());

        PostInfoUpdate postInfoUpdate = PostInfoUpdate.builder()
                .title("notification only board notification")
                .content("update content")
                .build();

        PostInfo postInfo = this.postService.updatePostInfo(account, boardId, postId, postInfoUpdate);

        Assertions.assertEquals("notification only board notification", postInfo.getTitle());
        Assertions.assertEquals("update content", postInfo.getContent());
    }

    @Test
    @DisplayName("일반 계정이 일반 게시판의 일반 게시물을 공지 사항으로 변경")
    public void switchPostToNotificationByAccount() {
        String boardId = getBoardId(temporaryTestNotificationInfo02.getBoard());
        String postId = getPostId(temporaryTestNotificationInfo02);
        Account account = getAccount(temporaryTestNotificationInfo02.getCreator());

        PostInfoUpdate postInfoUpdate = PostInfoUpdate.builder()
                .title("switch post to notification")
                .content("update content")
                .type(PostType.NOTIFICATION)
                .build();

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.postService.updatePostInfo(account, boardId, postId, postInfoUpdate);
        });

        Assertions.assertEquals("04000001", iricomException.getErrorCode());
        Assertions.assertEquals("Invalid authorization.", iricomException.getMessage());
    }

    @Test
    @DisplayName("게시판 관리자가 일반 게시판의 일반 게시물을 공지 사항으로 변경")
    public void switchPostToNotificationByBoardAdmin() {
        String boardId = getBoardId(temporaryTestNotificationInfo03.getBoard());
        String postId = getPostId(temporaryTestNotificationInfo03);
        Account account = getAccount(temporaryTestNotificationInfo03.getCreator());

        PostInfoUpdate postInfoUpdate = PostInfoUpdate.builder()
                .title("switch post to notification")
                .content("switch post to notification content")
                .type(PostType.NOTIFICATION)
                .build();

        PostInfo postInfo = this.postService.updatePostInfo(account, boardId, postId, postInfoUpdate);

        Assertions.assertEquals("switch post to notification", postInfo.getTitle());
        Assertions.assertEquals("switch post to notification content", postInfo.getContent());
        Assertions.assertEquals("notification", postInfo.getType());
        Assertions.assertEquals(boardId, postInfo.getBoardId());
    }

    @Test
    @DisplayName("공지 사항 전용 게시판의 일반 게시물을 공지 사항으로 변경")
    public void switchPostToNotificationNotificationOnlyBoard() {
        String boardId = getBoardId(temporaryTestNotificationInfo04.getBoard());
        String postId = getPostId(temporaryTestNotificationInfo04);
        Account account = getAccount(temporaryTestNotificationInfo04.getCreator());

        PostInfoUpdate postInfoUpdate = PostInfoUpdate.builder()
                .title("switch post to notification")
                .content("switch post to notification content")
                .type(PostType.NOTIFICATION)
                .build();

        PostInfo postInfo = this.postService.updatePostInfo(account, boardId, postId, postInfoUpdate);

        Assertions.assertEquals("switch post to notification", postInfo.getTitle());
        Assertions.assertEquals("switch post to notification content", postInfo.getContent());
        Assertions.assertEquals("notification", postInfo.getType());
        Assertions.assertEquals(boardId, postInfo.getBoardId());
    }
}

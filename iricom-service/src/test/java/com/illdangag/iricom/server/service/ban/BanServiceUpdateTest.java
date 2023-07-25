package com.illdangag.iricom.server.service.ban;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.data.request.PostBanInfoUpdate;
import com.illdangag.iricom.server.data.response.PostBanInfo;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.service.BanService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestPostBanInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestPostInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Collections;

@DisplayName("service: 차단 - 수정")
@Slf4j
public class BanServiceUpdateTest extends IricomTestSuite {
    @Autowired
    private BanService banService;

    private final TestBoardInfo boardInfo00 = TestBoardInfo.builder()
            .title("boardInfo00").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();

    private final TestPostInfo alreadyBanPostInfo00 = TestPostInfo.builder()
            .title("alreadyBanPostInfo01").content("alreadyBanPostInfo01").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(boardInfo00)
            .build();

    private final TestPostBanInfo postBanInfo00 = TestPostBanInfo.builder()
            .banAccount(systemAdmin).post(alreadyBanPostInfo00).reason("BAN")
            .build();

    public BanServiceUpdateTest(ApplicationContext context) {
        super(context);

        addTestBoardInfo(boardInfo00);
        addTestPostInfo(alreadyBanPostInfo00);
        addTestPostBanInfo(postBanInfo00);

        init();
    }

    @Test
    @DisplayName("시스템 관리자")
    public void updateSystemAdmin() throws Exception {
        Account account = getAccount(systemAdmin);
        String boardId = getBoardId(alreadyBanPostInfo00.getBoard());
        String postId = getPostId(alreadyBanPostInfo00);

        PostBanInfoUpdate postBanInfoUpdate = PostBanInfoUpdate.builder()
                .reason("UPDATE_SYSTEM_ADMIN")
                .build();

        PostBanInfo postBanInfo = banService.updatePostBanInfo(account, boardId, postId, postBanInfoUpdate);

        Assertions.assertEquals("UPDATE_SYSTEM_ADMIN", postBanInfo.getReason());
    }

    @Test
    @DisplayName("게시판 관리자")
    public void updateBoardAdmin() throws Exception {
        Account account = getAccount(allBoardAdmin);
        String boardId = getBoardId(alreadyBanPostInfo00.getBoard());
        String postId = getPostId(alreadyBanPostInfo00);

        PostBanInfoUpdate postBanInfoUpdate = PostBanInfoUpdate.builder()
                .reason("UPDATE_BOARD_ADMIN")
                .build();

        PostBanInfo postBanInfo = banService.updatePostBanInfo(account, boardId, postId, postBanInfoUpdate);

        Assertions.assertEquals("UPDATE_BOARD_ADMIN", postBanInfo.getReason());
    }

    @Test
    @DisplayName("다른 게시판 관리자")
    public void updateOtherBoardAdmin() throws Exception {
        Account account = getAccount(enableBoardAdmin);
        String boardId = getBoardId(alreadyBanPostInfo00.getBoard());
        String postId = getPostId(alreadyBanPostInfo00);

        PostBanInfoUpdate postBanInfoUpdate = PostBanInfoUpdate.builder()
                .reason("UPDATE_OTHER_BOARD_ADMIN")
                .build();

        Assertions.assertThrows(IricomException.class, () -> {
            banService.updatePostBanInfo(account, boardId, postId, postBanInfoUpdate);
        });
    }

    @Test
    @DisplayName("일반 사용자")
    public void updateAccount() throws Exception {
        Account account = getAccount(common00);
        String boardId = getBoardId(alreadyBanPostInfo00.getBoard());
        String postId = getPostId(alreadyBanPostInfo00);

        PostBanInfoUpdate postBanInfoUpdate = PostBanInfoUpdate.builder()
                .reason("UPDATE_ACCOUNT")
                .build();

        Assertions.assertThrows(IricomException.class, () -> {
            banService.updatePostBanInfo(account, boardId, postId, postBanInfoUpdate);
        });
    }

    @Test
    @DisplayName("등록되지 않은 사용자")
    public void updateUnknown() throws Exception {
        Account account = getAccount(unknown00);
        String boardId = getBoardId(alreadyBanPostInfo00.getBoard());
        String postId = getPostId(alreadyBanPostInfo00);

        PostBanInfoUpdate postBanInfoUpdate = PostBanInfoUpdate.builder()
                .reason("UPDATE_UNKNOWN")
                .build();

        Assertions.assertThrows(IricomException.class, () -> {
            banService.updatePostBanInfo(account, boardId, postId, postBanInfoUpdate);
        });
    }
}

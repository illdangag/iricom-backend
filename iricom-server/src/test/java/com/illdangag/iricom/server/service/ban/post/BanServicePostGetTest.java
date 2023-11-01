package com.illdangag.iricom.server.service.ban.post;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.type.PostState;
import com.illdangag.iricom.server.data.entity.type.PostType;
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

@DisplayName("service: 차단 - 조회")
@Slf4j
public class BanServicePostGetTest extends IricomTestSuite {
    @Autowired
    private BanService banService;

    private final TestBoardInfo boardInfo00 = TestBoardInfo.builder()
            .title("boardInfo00").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();
    private final TestBoardInfo boardInfo01 = TestBoardInfo.builder()
            .title("boardInfo01").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();

    private final TestPostInfo alreadyBanPostInfo00 = TestPostInfo.builder()
            .title("alreadyBanPostInfo00").content("alreadyBanPostInfo00").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(boardInfo00)
            .build();

    private final TestPostBanInfo postBanInfo00 = TestPostBanInfo.builder()
            .banAccount(systemAdmin).post(alreadyBanPostInfo00).reason("BAN")
            .build();

    public BanServicePostGetTest(ApplicationContext context) {
        super(context);

        addTestBoardInfo(boardInfo00, boardInfo01);
        addTestPostInfo(alreadyBanPostInfo00);
        addTestPostBanInfo(postBanInfo00);

        init();
    }

    @Test
    @DisplayName("시스템 관리자")
    public void getPostBanInfoSystemAdmin() throws Exception {
        Account account = getAccount(systemAdmin);
        String boardId = getBoardId(alreadyBanPostInfo00.getBoard());
        String postId = getPostId(alreadyBanPostInfo00);

        PostBanInfo postBanInfo = banService.getPostBanInfo(account, boardId, postId);
        Assertions.assertTrue(postBanInfo.getEnabled());
    }

    @Test
    @DisplayName("게시판 관리자")
    public void getPostBanInfoBoardAdmin() throws Exception {
        Account account = getAccount(allBoardAdmin);
        String boardId = getBoardId(alreadyBanPostInfo00.getBoard());
        String postId = getPostId(alreadyBanPostInfo00);

        PostBanInfo postBanInfo = banService.getPostBanInfo(account, boardId, postId);
        Assertions.assertTrue(postBanInfo.getEnabled());
    }

    @Test
    @DisplayName("다른 게시판 관리자")
    public void getPostBanInfoOtherBoardAdmin() throws Exception {
        Account account = getAccount(enableBoardAdmin);
        String boardId = getBoardId(alreadyBanPostInfo00.getBoard());
        String postId = getPostId(alreadyBanPostInfo00);

        Assertions.assertThrows(IricomException.class, () -> {
            banService.getPostBanInfo(account, boardId, postId);
        });
    }

    @Test
    @DisplayName("일반 사용자")
    public void getPostBanInfoAccount() throws Exception {
        Account account = getAccount(common00);
        String boardId = getBoardId(alreadyBanPostInfo00.getBoard());
        String postId = getPostId(alreadyBanPostInfo00);

        Assertions.assertThrows(IricomException.class, () -> {
            banService.getPostBanInfo(account, boardId, postId);
        });
    }

    @Test
    @DisplayName("등록되지 않은 사용자")
    public void getPostBanInfoUnknown() throws Exception {
        Account account = getAccount(unknown00);
        String boardId = getBoardId(alreadyBanPostInfo00.getBoard());
        String postId = getPostId(alreadyBanPostInfo00);

        Assertions.assertThrows(IricomException.class, () -> {
            banService.getPostBanInfo(account, boardId, postId);
        });
    }

    @Test
    @DisplayName("시스템 관리자")
    public void getPostInOtherBoard() throws Exception {
        Account account = getAccount(systemAdmin);
        String boardId = getBoardId(boardInfo01);
        String postId = getPostId(alreadyBanPostInfo00);

        Assertions.assertThrows(IricomException.class, () -> {
            banService.getPostBanInfo(account, boardId, postId);
        });
    }
}

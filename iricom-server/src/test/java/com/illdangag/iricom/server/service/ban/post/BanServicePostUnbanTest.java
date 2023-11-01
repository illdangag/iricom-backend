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

@DisplayName("service: 차단 - 차단 해제")
@Slf4j
public class BanServicePostUnbanTest extends IricomTestSuite {
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
    private final TestPostInfo alreadyBanPostInfo01 = TestPostInfo.builder()
            .title("alreadyBanPostInfo01").content("alreadyBanPostInfo01").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(boardInfo00)
            .build();
    private final TestPostInfo alreadyBanPostInfo02 = TestPostInfo.builder()
            .title("alreadyBanPostInfo02").content("alreadyBanPostInfo02").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(boardInfo00)
            .build();
    private final TestPostInfo alreadyBanPostInfo03 = TestPostInfo.builder()
            .title("alreadyBanPostInfo03").content("alreadyBanPostInfo03").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(boardInfo00)
            .build();

    private final TestPostBanInfo postBanInfo00 = TestPostBanInfo.builder()
            .banAccount(systemAdmin).post(alreadyBanPostInfo00).reason("BAN")
            .build();
    private final TestPostBanInfo postBanInfo01 = TestPostBanInfo.builder()
            .banAccount(systemAdmin).post(alreadyBanPostInfo01).reason("BAN")
            .build();
    private final TestPostBanInfo postBanInfo02 = TestPostBanInfo.builder()
            .banAccount(systemAdmin).post(alreadyBanPostInfo02).reason("BAN")
            .build();
    private final TestPostBanInfo postBanInfo03 = TestPostBanInfo.builder()
            .banAccount(systemAdmin).post(alreadyBanPostInfo03).reason("BAN")
            .build();

    public BanServicePostUnbanTest(ApplicationContext context) {
        super(context);

        addTestBoardInfo(boardInfo00, boardInfo01);
        addTestPostInfo(alreadyBanPostInfo00, alreadyBanPostInfo01, alreadyBanPostInfo02, alreadyBanPostInfo03);
        addTestPostBanInfo(postBanInfo00, postBanInfo01, postBanInfo02, postBanInfo03);

        init();
    }

    @Test
    @DisplayName("시스템 관리자")
    public void unbanSystemAdmin() throws Exception {
        Account account = getAccount(systemAdmin);
        String boardId = getBoardId(alreadyBanPostInfo02.getBoard());
        String postId = getPostId(alreadyBanPostInfo02);

        PostBanInfo postBanInfo = banService.unbanPost(account, boardId, postId);

        Assertions.assertFalse(postBanInfo.getEnabled());
    }

    @Test
    @DisplayName("게시판 관리자")
    public void unbanBoardAdmin() throws Exception {
        Account account = getAccount(allBoardAdmin);
        String boardId = getBoardId(alreadyBanPostInfo03.getBoard());
        String postId = getPostId(alreadyBanPostInfo03);

        PostBanInfo postBanInfo = banService.unbanPost(account, boardId, postId);

        Assertions.assertFalse(postBanInfo.getEnabled());
    }

    @Test
    @DisplayName("다른 게시판 관리자")
    public void unbanOtherBoardAdmin() throws Exception {
        Account account = getAccount(enableBoardAdmin);
        String boardId = getBoardId(alreadyBanPostInfo01.getBoard());
        String postId = getPostId(alreadyBanPostInfo01);

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            banService.unbanPost(account, boardId, postId);
        });

        Assertions.assertEquals("04000009", iricomException.getErrorCode());
        Assertions.assertEquals("Invalid authorization.", iricomException.getMessage());
    }

    @Test
    @DisplayName("일반 사용자")
    public void unbanAccount() throws Exception {
        Account account = getAccount(common00);
        String boardId = getBoardId(alreadyBanPostInfo01.getBoard());
        String postId = getPostId(alreadyBanPostInfo01);

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            banService.unbanPost(account, boardId, postId);
        });

        Assertions.assertEquals("04000009", iricomException.getErrorCode());
        Assertions.assertEquals("Invalid authorization.", iricomException.getMessage());
    }

    @Test
    @DisplayName("등록되지 않은 사용자")
    public void unbanUnknown() throws Exception {
        Account account = getAccount(unknown00);
        String boardId = getBoardId(alreadyBanPostInfo01.getBoard());
        String postId = getPostId(alreadyBanPostInfo01);

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            banService.unbanPost(account, boardId, postId);
        });

        Assertions.assertEquals("04000009", iricomException.getErrorCode());
        Assertions.assertEquals("Invalid authorization.", iricomException.getMessage());
    }

    @Test
    @DisplayName("게시물이 포함된 게시판이 아닌 다른 게시판")
    public void unbanPostInOtherBoard() throws Exception {
        Account account = getAccount(systemAdmin);
        String boardId = getBoardId(boardInfo01);
        String postId = getPostId(alreadyBanPostInfo01);

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            banService.unbanPost(account, boardId, postId);
        });

        Assertions.assertEquals("04000000", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist post.", iricomException.getMessage());
    }
}

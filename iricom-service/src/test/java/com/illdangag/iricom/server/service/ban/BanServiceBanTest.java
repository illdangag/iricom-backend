package com.illdangag.iricom.server.service.ban;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.data.request.PostBanInfoCreate;
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

@DisplayName("service: 차단 - 차단")
@Slf4j
public class BanServiceBanTest extends IricomTestSuite {
    @Autowired
    private BanService banService;

    private final TestBoardInfo boardInfo00 = TestBoardInfo.builder()
            .title("boardInfo00").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();
    private final TestBoardInfo boardInfo01 = TestBoardInfo.builder()
            .title("boardInfo01").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();

    private final TestPostInfo toBanPostInfo00 = TestPostInfo.builder()
            .title("toBanPostInfo00").content("toBanPostInfo00").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(boardInfo00)
            .build();
    private final TestPostInfo toBanPostInfo01 = TestPostInfo.builder()
            .title("toBanPostInfo01").content("toBanPostInfo01").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(boardInfo00)
            .build();
    private final TestPostInfo toBanPostInfo02 = TestPostInfo.builder()
            .title("toBanPostInfo02").content("toBanPostInfo02").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(boardInfo00)
            .build();
    private final TestPostInfo toBanPostInfo04 = TestPostInfo.builder()
            .title("toBanPostInfo04").content("toBanPostInfo04").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(boardInfo01)
            .build();
    private final TestPostInfo alreadyBanPostInfo00 = TestPostInfo.builder()
            .title("alreadyBanPostInfo00").content("alreadyBanPostInfo00").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(boardInfo01)
            .build();
    private final TestPostInfo alreadyBanPostInfo01 = TestPostInfo.builder()
            .title("alreadyBanPostInfo01").content("alreadyBanPostInfo01").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(boardInfo01)
            .build();

    private final TestPostBanInfo postBanInfo00 = TestPostBanInfo.builder()
            .banAccount(systemAdmin).post(alreadyBanPostInfo00).reason("BAN")
            .build();
    private final TestPostBanInfo postBanInfo01 = TestPostBanInfo.builder()
            .banAccount(systemAdmin).post(alreadyBanPostInfo01).reason("BAN")
            .build();

    public BanServiceBanTest(ApplicationContext context) {
        super(context);

        addTestBoardInfo(boardInfo00, boardInfo01);
        addTestPostInfo(toBanPostInfo00, toBanPostInfo01, toBanPostInfo02, toBanPostInfo04, alreadyBanPostInfo00, alreadyBanPostInfo01);
        addTestPostBanInfo(postBanInfo00, postBanInfo01);
        init();
    }

    @Test
    @DisplayName("시스템 관리자")
    public void banSystemAdmin() throws Exception {
        Account account = getAccount(systemAdmin);
        String boardId = getBoardId(toBanPostInfo00.getBoard());
        String postId = getPostId(toBanPostInfo00);

        PostBanInfoCreate postBanInfoCreate = PostBanInfoCreate.builder()
                .reason("BAN")
                .build();
        PostBanInfo postBanInfo = banService.banPost(account, boardId, postId, postBanInfoCreate);

        Assertions.assertNotNull(postBanInfo);
        Assertions.assertEquals("BAN", postBanInfo.getReason());
        Assertions.assertEquals(String.valueOf(postId), postBanInfo.getPostInfo().getId());
        Assertions.assertTrue(postBanInfo.getPostInfo().getBan());
        Assertions.assertTrue(postBanInfo.getEnabled());
    }

    @Test
    @DisplayName("게시판 관리자")
    public void banBoardAdmin() throws Exception {
        Account account = getAccount(allBoardAdmin);
        String boardId = getBoardId(toBanPostInfo01.getBoard());
        String postId = getPostId(toBanPostInfo01);

        PostBanInfoCreate postBanInfoCreate = PostBanInfoCreate.builder()
                .reason("BAN")
                .build();
        PostBanInfo postBanInfo = banService.banPost(account, boardId, postId, postBanInfoCreate);

        Assertions.assertNotNull(postBanInfo);
        Assertions.assertEquals("BAN", postBanInfo.getReason());
        Assertions.assertEquals(String.valueOf(postId), postBanInfo.getPostInfo().getId());
        Assertions.assertTrue(postBanInfo.getPostInfo().getBan());
        Assertions.assertTrue(postBanInfo.getEnabled());
    }

    @Test
    @DisplayName("다른 게시판 관리자")
    public void banOtherBoardAdmin() throws Exception {
        Account account = getAccount(enableBoardAdmin);
        String boardId = getBoardId(toBanPostInfo01.getBoard());
        String postId = getPostId(toBanPostInfo01);

        PostBanInfoCreate postBanInfoCreate = PostBanInfoCreate.builder()
                .reason("BAN")
                .build();

        Assertions.assertThrows(IricomException.class, () -> {
            banService.banPost(account, boardId, postId, postBanInfoCreate);
        });
    }

    @Test
    @DisplayName("일반 사용자")
    public void banAccount() throws Exception {
        Account account = getAccount(common00);
        String boardId = getBoardId(toBanPostInfo02.getBoard());
        String postId = getPostId(toBanPostInfo02);

        PostBanInfoCreate postBanInfoCreate = PostBanInfoCreate.builder()
                .reason("BAN")
                .build();

        Assertions.assertThrows(IricomException.class, () -> {
            banService.banPost(account, boardId, postId, postBanInfoCreate);
        });
    }

    @Test
    @DisplayName("등록되지 않은 사용자")
    public void banUnknown() throws Exception {
        Account account = getAccount(unknown00);
        String boardId = getBoardId(toBanPostInfo02.getBoard());
        String postId = getPostId(toBanPostInfo02);

        PostBanInfoCreate postBanInfoCreate = PostBanInfoCreate.builder()
                .reason("BAN")
                .build();

        Assertions.assertThrows(IricomException.class, () -> {
            banService.banPost(account, boardId, postId, postBanInfoCreate);
        });
    }

    @Test
    @DisplayName("이미 차단된 게시물")
    public void alreadyBanPost() throws Exception {
        Account account = getAccount(systemAdmin);
        String boardId = getBoardId(alreadyBanPostInfo00.getBoard());
        String postId = getPostId(alreadyBanPostInfo00);

        PostBanInfoCreate postBanInfoCreate = PostBanInfoCreate.builder()
                .reason("BAN")
                .build();

        Assertions.assertThrows(IricomException.class, () -> {
            banService.banPost(account, boardId, postId, postBanInfoCreate);
        });
    }

    @Test
    @DisplayName("이미 차단한 게시물")
    public void banAlreadyBanPost() {
        Account account = getAccount(systemAdmin);
        String boardId = getBoardId(alreadyBanPostInfo01.getBoard());
        String postId = getPostId(alreadyBanPostInfo01);

        PostBanInfoCreate postBanInfoCreate = PostBanInfoCreate.builder()
                .reason("Already ban")
                .build();
        Assertions.assertThrows(IricomException.class, () -> {
            banService.banPost(account, boardId, postId, postBanInfoCreate);
        });
    }

    @Test
    @DisplayName("다른 게시판에 존재하는 게시물")
    public void banPostInOtherBoard() throws Exception {
        Account account = getAccount(systemAdmin);
        String boardId = getBoardId(boardInfo00);
        String postId = getPostId(toBanPostInfo04);

        PostBanInfoCreate postBanInfoCreate = PostBanInfoCreate.builder()
                .reason("not exist post")
                .build();
        Assertions.assertThrows(IricomException.class, () -> {
            banService.banPost(account, boardId, postId, postBanInfoCreate);
        });
    }
}

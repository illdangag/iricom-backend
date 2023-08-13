package com.illdangag.iricom.server.service.ban;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.PostState;
import com.illdangag.iricom.server.data.entity.PostType;
import com.illdangag.iricom.server.data.request.PostBanInfoSearch;
import com.illdangag.iricom.server.data.response.PostBanInfoList;
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

@DisplayName("service: 차단 - 검색")
@Slf4j
public class BanServiceSearchTest extends IricomTestSuite {
    @Autowired
    private BanService banService;

    // 게시판
    private final TestBoardInfo boardInfo00 = TestBoardInfo.builder()
            .title("boardInfo00").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();
    // 게시물
    private final TestPostInfo alreadyBanPostInfo00 = TestPostInfo.builder()
            .title("alreadyBanPostInfo00").content("alreadyBanPostInfo00").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(boardInfo00)
            .build();
    private final TestPostInfo alreadyBanPostInfo01 = TestPostInfo.builder()
            .title("alreadyBanPostInfo00").content("alreadyBanPostInfo00").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(boardInfo00)
            .build();
    // 게시물 신고
    private final TestPostBanInfo postBanInfo00 = TestPostBanInfo.builder()
            .banAccount(systemAdmin).post(alreadyBanPostInfo00).reason("BAN")
            .build();
    private final TestPostBanInfo postBanInfo01 = TestPostBanInfo.builder()
            .banAccount(systemAdmin).post(alreadyBanPostInfo01).reason("BAN")
            .build();

    public BanServiceSearchTest(ApplicationContext context) {
        super(context);

        addTestBoardInfo(boardInfo00);
        addTestPostInfo(alreadyBanPostInfo00, alreadyBanPostInfo01);
        addTestPostBanInfo(postBanInfo00, postBanInfo01);

        init();
    }

    @Test
    @DisplayName("시스템 관리자")
    public void searchSystemAdmin() throws Exception {
        Account account = getAccount(systemAdmin);
        String boardId = getBoardId(boardInfo00);

        PostBanInfoSearch postBanInfoSearch = PostBanInfoSearch.builder()
                .reason("")
                .skip(0)
                .limit(10)
                .build();

        PostBanInfoList postBanInfoList = banService.getPostBanInfoList(account, boardId, postBanInfoSearch);

        Assertions.assertEquals(0, postBanInfoList.getSkip());
        Assertions.assertEquals(10, postBanInfoList.getLimit());
        Assertions.assertEquals(2, postBanInfoList.getPostBanInfoList().size());
    }

    @Test
    @DisplayName("게시판 관리자")
    public void searchBoardAdmin() throws Exception {
        Account account = getAccount(allBoardAdmin);
        String boardId = getBoardId(boardInfo00);

        PostBanInfoSearch postBanInfoSearch = PostBanInfoSearch.builder()
                .reason("")
                .skip(0)
                .limit(10)
                .build();

        PostBanInfoList postBanInfoList = banService.getPostBanInfoList(account, boardId, postBanInfoSearch);

        Assertions.assertEquals(0, postBanInfoList.getSkip());
        Assertions.assertEquals(10, postBanInfoList.getLimit());
        Assertions.assertEquals(2, postBanInfoList.getPostBanInfoList().size());
    }

    @Test
    @DisplayName("다른 게시판 관리자")
    public void searchOtherBoardAdmin() throws Exception {
        Account account = getAccount(enableBoardAdmin);
        String boardId = getBoardId(boardInfo00);

        PostBanInfoSearch postBanInfoSearch = PostBanInfoSearch.builder()
                .reason("")
                .skip(0)
                .limit(10)
                .build();

        Assertions.assertThrows(IricomException.class, () -> {
            banService.getPostBanInfoList(account, boardId, postBanInfoSearch);
        });
    }

    @Test
    @DisplayName("일반 사용자")
    public void searchAccount() throws Exception {
        Account account = getAccount(common00);
        String boardId = getBoardId(boardInfo00);

        PostBanInfoSearch postBanInfoSearch = PostBanInfoSearch.builder()
                .reason("")
                .skip(0)
                .limit(10)
                .build();

        Assertions.assertThrows(IricomException.class, () -> {
            banService.getPostBanInfoList(account, boardId, postBanInfoSearch);
        });
    }

    @Test
    @DisplayName("등록되지 않은 사용자")
    public void searchUnknown() throws Exception {
        Account account = getAccount(unknown00);
        String boardId = getBoardId(boardInfo00);

        PostBanInfoSearch postBanInfoSearch = PostBanInfoSearch.builder()
                .reason("")
                .skip(0)
                .limit(10)
                .build();

        Assertions.assertThrows(IricomException.class, () -> {
            banService.getPostBanInfoList(account, boardId, postBanInfoSearch);
        });
    }
}

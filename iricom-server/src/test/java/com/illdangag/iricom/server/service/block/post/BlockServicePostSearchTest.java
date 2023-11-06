package com.illdangag.iricom.server.service.block.post;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.type.PostState;
import com.illdangag.iricom.server.data.entity.type.PostType;
import com.illdangag.iricom.server.data.request.PostBlockInfoSearch;
import com.illdangag.iricom.server.data.response.PostBlockInfoList;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.service.BlockService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestPostBlockInfo;
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
public class BlockServicePostSearchTest extends IricomTestSuite {
    @Autowired
    private BlockService blockService;

    // 게시판
    private final TestBoardInfo boardInfo00 = TestBoardInfo.builder()
            .title("boardInfo00").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();
    // 게시물
    private final TestPostInfo alreadyBlockPostInfo00 = TestPostInfo.builder()
            .title("alreadyBlockPostInfo00").content("alreadyBlockPostInfo00").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(boardInfo00)
            .build();
    private final TestPostInfo alreadyBlockPostInfo01 = TestPostInfo.builder()
            .title("alreadyBlockPostInfo01").content("alreadyBlockPostInfo01").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(boardInfo00)
            .build();
    // 게시물 신고
    private final TestPostBlockInfo postBlockInfo00 = TestPostBlockInfo.builder()
            .account(systemAdmin).post(alreadyBlockPostInfo00).reason("Block")
            .build();
    private final TestPostBlockInfo postBlockInfo01 = TestPostBlockInfo.builder()
            .account(systemAdmin).post(alreadyBlockPostInfo01).reason("Block")
            .build();

    public BlockServicePostSearchTest(ApplicationContext context) {
        super(context);

        addTestBoardInfo(boardInfo00);
        addTestPostInfo(alreadyBlockPostInfo00, alreadyBlockPostInfo01);
        addTestPostBlockInfo(postBlockInfo00, postBlockInfo01);

        init();
    }

    @Test
    @DisplayName("시스템 관리자")
    public void searchSystemAdmin() throws Exception {
        Account account = getAccount(systemAdmin);
        String boardId = getBoardId(boardInfo00);

        PostBlockInfoSearch postBlockInfoSearch = PostBlockInfoSearch.builder()
                .reason("")
                .skip(0)
                .limit(10)
                .build();

        PostBlockInfoList postBlockInfoList = blockService.getPostBlockInfoList(account, boardId, postBlockInfoSearch);

        Assertions.assertEquals(0, postBlockInfoList.getSkip());
        Assertions.assertEquals(10, postBlockInfoList.getLimit());
        Assertions.assertEquals(2, postBlockInfoList.getPostBlockInfoList().size());
    }

    @Test
    @DisplayName("게시판 관리자")
    public void searchBoardAdmin() throws Exception {
        Account account = getAccount(allBoardAdmin);
        String boardId = getBoardId(boardInfo00);

        PostBlockInfoSearch postBlockInfoSearch = PostBlockInfoSearch.builder()
                .reason("")
                .skip(0)
                .limit(10)
                .build();

        PostBlockInfoList postBlockInfoList = blockService.getPostBlockInfoList(account, boardId, postBlockInfoSearch);

        Assertions.assertEquals(0, postBlockInfoList.getSkip());
        Assertions.assertEquals(10, postBlockInfoList.getLimit());
        Assertions.assertEquals(2, postBlockInfoList.getPostBlockInfoList().size());
    }

    @Test
    @DisplayName("다른 게시판 관리자")
    public void searchOtherBoardAdmin() throws Exception {
        Account account = getAccount(enableBoardAdmin);
        String boardId = getBoardId(boardInfo00);

        PostBlockInfoSearch postBlockInfoSearch = PostBlockInfoSearch.builder()
                .reason("")
                .skip(0)
                .limit(10)
                .build();

        Assertions.assertThrows(IricomException.class, () -> {
            blockService.getPostBlockInfoList(account, boardId, postBlockInfoSearch);
        });
    }

    @Test
    @DisplayName("일반 사용자")
    public void searchAccount() throws Exception {
        Account account = getAccount(common00);
        String boardId = getBoardId(boardInfo00);

        PostBlockInfoSearch postBlockInfoSearch = PostBlockInfoSearch.builder()
                .reason("")
                .skip(0)
                .limit(10)
                .build();

        Assertions.assertThrows(IricomException.class, () -> {
            blockService.getPostBlockInfoList(account, boardId, postBlockInfoSearch);
        });
    }

    @Test
    @DisplayName("등록되지 않은 사용자")
    public void searchUnknown() throws Exception {
        Account account = getAccount(unknown00);
        String boardId = getBoardId(boardInfo00);

        PostBlockInfoSearch postBlockInfoSearch = PostBlockInfoSearch.builder()
                .reason("")
                .skip(0)
                .limit(10)
                .build();

        Assertions.assertThrows(IricomException.class, () -> {
            blockService.getPostBlockInfoList(account, boardId, postBlockInfoSearch);
        });
    }
}

package com.illdangag.iricom.server.service.block.post;

import com.illdangag.iricom.server.data.request.PostBlockInfoCreate;
import com.illdangag.iricom.server.data.request.PostBlockInfoSearch;
import com.illdangag.iricom.server.data.response.PostBlockInfoList;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.service.BlockService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestPostInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

@DisplayName("service: 차단 - 검색")
@Slf4j
@Transactional
public class BlockServicePostSearchTest extends IricomTestSuite {
    @Autowired
    private BlockService blockService;

    public BlockServicePostSearchTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("시스템 관리자")
    public void searchSystemAdmin() throws Exception {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성 - 15개 게시물 생성, 그 중 5개 게시물 차단
        this.setRandomPost(board, account, 10);
        List<TestPostInfo> blockPostList = this.setRandomPost(board, account, 5);
        // 게시물 차단
        PostBlockInfoCreate postBlockInfoCreate = PostBlockInfoCreate.builder()
                .reason("block")
                .build();
        for (TestPostInfo post : blockPostList) {
            blockService.blockPost(systemAdmin.getId(), board.getId(), post.getId(), postBlockInfoCreate);
        }

        // 차단된 게시물 조회
        PostBlockInfoSearch postBlockInfoSearch = PostBlockInfoSearch.builder()
                .reason("")
                .skip(0)
                .limit(10)
                .build();
        PostBlockInfoList postBlockInfoList = blockService.getPostBlockInfoList(systemAdmin.getId(), board.getId(), postBlockInfoSearch);

        Assertions.assertEquals(0, postBlockInfoList.getSkip());
        Assertions.assertEquals(10, postBlockInfoList.getLimit());
        Assertions.assertEquals(5, postBlockInfoList.getPostBlockInfoList().size());
    }

    @Test
    @DisplayName("게시판 관리자")
    public void searchBoardAdmin() throws Exception {
        // 계정 생성
        TestAccountInfo boardAdmin = this.setRandomAccount();
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard(Arrays.asList(boardAdmin));
        // 게시물 생성
        List<TestPostInfo> postList = this.setRandomPost(board, account, 10);
        // 게시물 차단
        PostBlockInfoCreate postBlockInfoCreate = PostBlockInfoCreate.builder()
                .reason("block")
                .build();
        for (TestPostInfo post : postList) {
            blockService.blockPost(systemAdmin.getId(), board.getId(), post.getId(), postBlockInfoCreate);
        }

        // 차단된 게시물 검색
        PostBlockInfoSearch postBlockInfoSearch = PostBlockInfoSearch.builder()
                .reason("")
                .skip(0)
                .limit(10)
                .build();
        PostBlockInfoList postBlockInfoList = blockService.getPostBlockInfoList(boardAdmin.getId(), board.getId(), postBlockInfoSearch);
        Assertions.assertEquals(0, postBlockInfoList.getSkip());
        Assertions.assertEquals(10, postBlockInfoList.getLimit());
        Assertions.assertEquals(10, postBlockInfoList.getPostBlockInfoList().size());
    }

    @Test
    @DisplayName("다른 게시판 관리자")
    public void searchOtherBoardAdmin() throws Exception {
        // 계정 생성
        TestAccountInfo boardAdmin = this.setRandomAccount();
        TestAccountInfo otherBoardAdmin = this.setRandomAccount();
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard(Arrays.asList(boardAdmin));
        this.setRandomBoard(Arrays.asList(otherBoardAdmin));
        // 게시물 생성
        List<TestPostInfo> postList = this.setRandomPost(board, account, 10);
        // 게시물 차단
        PostBlockInfoCreate postBlockInfoCreate = PostBlockInfoCreate.builder()
                .reason("block")
                .build();
        for (TestPostInfo post : postList) {
            blockService.blockPost(systemAdmin.getId(), board.getId(), post.getId(), postBlockInfoCreate);
        }

        // 차단된 게시물 검색
        PostBlockInfoSearch postBlockInfoSearch = PostBlockInfoSearch.builder()
                .reason("")
                .skip(0)
                .limit(10)
                .build();
        PostBlockInfoList postBlockInfoList = blockService.getPostBlockInfoList(boardAdmin.getId(), board.getId(), postBlockInfoSearch);
        Assertions.assertEquals(0, postBlockInfoList.getSkip());
        Assertions.assertEquals(10, postBlockInfoList.getLimit());
        Assertions.assertEquals(10, postBlockInfoList.getPostBlockInfoList().size());

        // 다른 게시판 관리자가 차단된 게시물 검색
        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            blockService.getPostBlockInfoList(otherBoardAdmin.getId(), board.getId(), postBlockInfoSearch);
        });
        Assertions.assertNotNull(exception);
        Assertions.assertEquals("04000009", exception.getErrorCode());
    }

    @Test
    @DisplayName("일반 사용자")
    public void searchAccount() throws Exception {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        List<TestPostInfo> postList = this.setRandomPost(board, account, 10);
        // 게시물 차단
        PostBlockInfoCreate postBlockInfoCreate = PostBlockInfoCreate.builder()
                .reason("block")
                .build();
        for (TestPostInfo post : postList) {
            blockService.blockPost(systemAdmin.getId(), board.getId(), post.getId(), postBlockInfoCreate);
        }

        PostBlockInfoSearch postBlockInfoSearch = PostBlockInfoSearch.builder()
                .reason("")
                .skip(0)
                .limit(10)
                .build();

        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            blockService.getPostBlockInfoList(account.getId(), board.getId(), postBlockInfoSearch);
        });
        Assertions.assertNotNull(exception);
        Assertions.assertEquals("04000009", exception.getErrorCode());
    }

    @Test
    @DisplayName("등록되지 않은 사용자")
    public void searchUnknown() throws Exception {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        TestAccountInfo unregisteredAccount = this.setRandomAccount(true);
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        List<TestPostInfo> postList = this.setRandomPost(board, account, 10);
        // 게시물 차단
        PostBlockInfoCreate postBlockInfoCreate = PostBlockInfoCreate.builder()
                .reason("block")
                .build();
        for (TestPostInfo post : postList) {
            blockService.blockPost(systemAdmin.getId(), board.getId(), post.getId(), postBlockInfoCreate);
        }

        PostBlockInfoSearch postBlockInfoSearch = PostBlockInfoSearch.builder()
                .reason("")
                .skip(0)
                .limit(10)
                .build();

        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            blockService.getPostBlockInfoList(unregisteredAccount.getId(), board.getId(), postBlockInfoSearch);
        });
        Assertions.assertNotNull(exception);
        Assertions.assertEquals("04000009", exception.getErrorCode());
    }
}

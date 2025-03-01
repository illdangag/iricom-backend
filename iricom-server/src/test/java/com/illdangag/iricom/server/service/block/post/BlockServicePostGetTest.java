package com.illdangag.iricom.server.service.block.post;

import com.illdangag.iricom.server.data.request.PostBlockInfoCreate;
import com.illdangag.iricom.server.data.response.PostBlockInfo;
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
import java.util.Collections;

@DisplayName("service: 차단 - 조회")
@Slf4j
@Transactional
public class BlockServicePostGetTest extends IricomTestSuite {
    @Autowired
    private BlockService blockService;

    public BlockServicePostGetTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("시스템 관리자")
    public void getPostBlockInfoSystemAdmin() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);

        // 게시물 차단
        PostBlockInfoCreate postBlockInfoCreate = PostBlockInfoCreate.builder()
                .reason("block")
                .build();
        blockService.blockPost(systemAdmin.getId(), board.getId(), post.getId(), postBlockInfoCreate);

        // 게시물 차단 정보 조회
        PostBlockInfo postBlockInfo = blockService.getPostBlockInfo(systemAdmin.getId(), board.getId(), post.getId());
        Assertions.assertNotNull(postBlockInfo);
        Assertions.assertEquals(board.getId(), postBlockInfo.getPostInfo().getBoardId());
        Assertions.assertEquals(post.getId(), postBlockInfo.getPostInfo().getId());
    }

    @Test
    @DisplayName("게시판 관리자")
    public void getPostBlockInfoBoardAdmin() throws Exception {
        // 계정 생성
        TestAccountInfo boardAdmin = setRandomAccount();
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard(Arrays.asList(boardAdmin));
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);

        // 게시물 차단
        PostBlockInfoCreate postBlockInfoCreate = PostBlockInfoCreate.builder()
                .reason("block")
                .build();
        blockService.blockPost(boardAdmin.getId(), board.getId(), post.getId(), postBlockInfoCreate);

        PostBlockInfo postBlockInfo = blockService.getPostBlockInfo(boardAdmin.getId(), board.getId(), post.getId());
        Assertions.assertNotNull(postBlockInfo);
        Assertions.assertEquals(board.getId(), postBlockInfo.getPostInfo().getBoardId());
        Assertions.assertEquals(post.getId(), postBlockInfo.getPostInfo().getId());
    }

    @Test
    @DisplayName("다른 게시판 관리자")
    public void getPostBlockInfoOtherBoardAdmin() throws Exception {
        // 계정 생성
        TestAccountInfo boardAdmin = setRandomAccount();
        TestAccountInfo otherBoardAdmin = setRandomAccount();
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard(Arrays.asList(boardAdmin));
        setRandomBoard(Arrays.asList(otherBoardAdmin));
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);

        // 게시물 차단
        PostBlockInfoCreate postBlockInfoCreate = PostBlockInfoCreate.builder()
                .reason("block")
                .build();
        blockService.blockPost(boardAdmin.getId(), board.getId(), post.getId(), postBlockInfoCreate);

        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            blockService.getPostBlockInfo(otherBoardAdmin.getId(), board.getId(), post.getId());
        });
        Assertions.assertEquals("04000009", exception.getErrorCode());
    }

    @Test
    @DisplayName("일반 사용자")
    public void getPostBlockInfoAccount() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);

        // 게시물 차단
        PostBlockInfoCreate postBlockInfoCreate = PostBlockInfoCreate.builder()
                .reason("block")
                .build();
        blockService.blockPost(systemAdmin.getId(), board.getId(), post.getId(), postBlockInfoCreate);

        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            blockService.getPostBlockInfo(account.getId(), board.getId(), post.getId());
        });
        Assertions.assertEquals("04000009", exception.getErrorCode());
    }

    @Test
    @DisplayName("등록되지 않은 사용자")
    public void getPostBlockInfoUnknown() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        TestAccountInfo unregisteredAccount = setRandomAccount(true);
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);

        // 게시물 차단
        PostBlockInfoCreate postBlockInfoCreate = PostBlockInfoCreate.builder()
                .reason("block")
                .build();
        blockService.blockPost(systemAdmin.getId(), board.getId(), post.getId(), postBlockInfoCreate);

        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            blockService.getPostBlockInfo(unregisteredAccount.getId(), board.getId(), post.getId());
        });
        Assertions.assertEquals("04000009", exception.getErrorCode());
    }

    @Test
    @DisplayName("존재하지 않는 게시물")
    public void getNotExistPost() {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();

        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            blockService.getPostBlockInfo(account.getId(), board.getId(), "UNKNOWN");
        });
        Assertions.assertEquals("04000000", exception.getErrorCode());
    }

    @Test
    @DisplayName("차단하지 않은 게시물")
    public void getNotBlockPost() {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard(Collections.singletonList(account));
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);

        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            blockService.getPostBlockInfo(account.getId(), board.getId(), post.getId());
        });
        Assertions.assertEquals("07000000", exception.getErrorCode());
    }
}

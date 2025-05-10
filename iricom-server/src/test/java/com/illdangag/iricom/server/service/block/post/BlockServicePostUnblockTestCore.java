package com.illdangag.iricom.server.service.block.post;

import com.illdangag.iricom.core.data.request.PostBlockInfoCreate;
import com.illdangag.iricom.core.data.response.PostBlockInfo;
import com.illdangag.iricom.core.exception.IricomException;
import com.illdangag.iricom.core.service.BlockService;
import com.illdangag.iricom.core.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.core.test.data.wrapper.TestBoardInfo;
import com.illdangag.iricom.core.test.data.wrapper.TestPostInfo;
import com.illdangag.iricom.server.IricomTestServiceSuite;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.transaction.Transactional;
import java.util.Arrays;

@DisplayName("service: 차단 - 차단 해제")
@Slf4j
@Transactional
public class BlockServicePostUnblockTestCore extends IricomTestServiceSuite {
    @Autowired
    private BlockService blockService;

    public BlockServicePostUnblockTestCore(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("시스템 관리자")
    public void unblockSystemAdmin() throws Exception {
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

        // 게시물 차단 해제
        PostBlockInfo postBlockInfo = blockService.unblockPost(systemAdmin.getId(), board.getId(), post.getId());
        Assertions.assertNotNull(postBlockInfo);
    }

    @Test
    @DisplayName("게시판 관리자")
    public void unblockBoardAdmin() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        TestAccountInfo boardAdmin = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard(Arrays.asList(boardAdmin));
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);

        // 게시물 차단
        PostBlockInfoCreate postBlockInfoCreate = PostBlockInfoCreate.builder()
                .reason("block")
                .build();
        blockService.blockPost(systemAdmin.getId(), board.getId(), post.getId(), postBlockInfoCreate);

        PostBlockInfo postBlockInfo = blockService.unblockPost(boardAdmin.getId(), board.getId(), post.getId());
        Assertions.assertNotNull(postBlockInfo);
    }

    @Test
    @DisplayName("다른 게시판 관리자")
    public void unblockOtherBoardAdmin() throws Exception {
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

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            blockService.unblockPost(otherBoardAdmin.getId(), board.getId(), post.getId());
        });

        Assertions.assertEquals("04000009", iricomException.getErrorCode());
    }

    @Test
    @DisplayName("일반 사용자")
    public void unblockAccount() throws Exception {
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

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            blockService.unblockPost(account.getId(), board.getId(), post.getId());
        });

        Assertions.assertEquals("04000009", iricomException.getErrorCode());
    }

    @Test
    @DisplayName("등록되지 않은 사용자")
    public void unblockUnknown() throws Exception {
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

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            blockService.unblockPost(unregisteredAccount.getId(), board.getId(), post.getId());
        });

        Assertions.assertEquals("04000009", iricomException.getErrorCode());
    }

    @Test
    @DisplayName("게시물이 포함된 게시판이 아닌 다른 게시판")
    public void unblockPostInOtherBoard() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        TestBoardInfo otherBoard = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            blockService.unblockPost(systemAdmin.getId(), otherBoard.getId(), post.getId());
        });

        Assertions.assertEquals("04000000", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist post.", iricomException.getMessage());
    }

    @Test
    @DisplayName("차단 되지 않은 게시물 차단 해제")
    public void unblockNotBlockPost() {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            blockService.unblockPost(systemAdmin.getId(), board.getId(), post.getId());
        });

        Assertions.assertEquals("04000015", iricomException.getErrorCode());
        Assertions.assertEquals("This post has not been blocked.", iricomException.getMessage());
    }
}

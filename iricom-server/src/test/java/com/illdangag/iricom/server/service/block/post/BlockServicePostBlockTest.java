package com.illdangag.iricom.server.service.block.post;

import com.illdangag.iricom.server.data.entity.type.PostState;
import com.illdangag.iricom.server.data.entity.type.PostType;
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
import java.util.Collections;

@DisplayName("service: 차단 - 게시물 차단")
@Slf4j
@Transactional
public class BlockServicePostBlockTest extends IricomTestSuite {
    @Autowired
    private BlockService blockService;

    public BlockServicePostBlockTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("시스템 관리자")
    public void blockSystemAdmin() throws Exception {
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
        PostBlockInfo postBlockInfo = blockService.blockPost(systemAdmin.getId(), board.getId(), post.getId(), postBlockInfoCreate);

        Assertions.assertNotNull(postBlockInfo);
        Assertions.assertEquals("block", postBlockInfo.getReason());
        Assertions.assertEquals(post.getId(), postBlockInfo.getPostInfo().getId());
        Assertions.assertTrue(postBlockInfo.getPostInfo().getBlocked());
    }

    @Test
    @DisplayName("게시판 관리자")
    public void blockBoardAdmin() throws Exception {
        // 계정 생성
        TestAccountInfo boardAdmin = setRandomAccount();
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard(Collections.singletonList(boardAdmin));

        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);

        // 게시물 차단
        PostBlockInfoCreate postBlockInfoCreate = PostBlockInfoCreate.builder()
                .reason("block")
                .build();
        PostBlockInfo postBlockInfo = blockService.blockPost(boardAdmin.getId(), board.getId(), post.getId(), postBlockInfoCreate);

        Assertions.assertNotNull(postBlockInfo);
        Assertions.assertEquals("block", postBlockInfo.getReason());
        Assertions.assertEquals(post.getId(), postBlockInfo.getPostInfo().getId());
        Assertions.assertTrue(postBlockInfo.getPostInfo().getBlocked());
    }

    @Test
    @DisplayName("다른 게시판 관리자")
    public void blockOtherBoardAdmin() throws Exception {
        // 계정 생성
        TestAccountInfo boardAdmin = setRandomAccount();
        TestAccountInfo otherBoardAdmin = setRandomAccount();
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard(Collections.singletonList(boardAdmin));
        setRandomBoard(Collections.singletonList(otherBoardAdmin));
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);

        PostBlockInfoCreate postBlockInfoCreate = PostBlockInfoCreate.builder()
                .reason("block")
                .build();

        Assertions.assertThrows(IricomException.class, () -> {
            blockService.blockPost(otherBoardAdmin.getId(), board.getId(), post.getId(), postBlockInfoCreate);
        });
    }

    @Test
    @DisplayName("일반 사용자")
    public void blockAccount() throws Exception {
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

        Assertions.assertThrows(IricomException.class, () -> {
            blockService.blockPost(account.getId(), board.getId(), post.getId(), postBlockInfoCreate);
        });
    }

    @Test
    @DisplayName("등록되지 않은 사용자")
    public void blockUnknown() throws Exception {
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

        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            blockService.blockPost(unregisteredAccount.getId(), board.getId(), post.getId(), postBlockInfoCreate);
        });

        Assertions.assertEquals("04000009", exception.getErrorCode());
    }

    @Test
    @DisplayName("이미 차단된 게시물")
    public void alreadyBlockPost() throws Exception {
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

        // 차단된 게시물 차단 시도
        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            blockService.blockPost(systemAdmin.getId(), board.getId(), post.getId(), postBlockInfoCreate);
        });

        Assertions.assertEquals("04000010", exception.getErrorCode());
    }

    @Test
    @DisplayName("다른 게시판에 존재하는 게시물")
    public void blockPostInOtherBoard() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        TestBoardInfo otherBoard = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);

        PostBlockInfoCreate postBlockInfoCreate = PostBlockInfoCreate.builder()
                .reason("not exist post")
                .build();
        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            blockService.blockPost(systemAdmin.getId(), otherBoard.getId(), post.getId(), postBlockInfoCreate);
        });

        Assertions.assertEquals("04000000", exception.getErrorCode());
    }

    @Test
    @DisplayName("발행되지 않은 게시물")
    public void blockNotPublishPost() {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account, PostType.POST, PostState.TEMPORARY);

        PostBlockInfoCreate postBlockInfoCreate = PostBlockInfoCreate.builder()
                .reason("not exist post")
                .build();
        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            blockService.blockPost(systemAdmin.getId(), board.getId(), post.getId(), postBlockInfoCreate);
        });
        Assertions.assertEquals("04000005", exception.getErrorCode());
        Assertions.assertEquals("Not exist publish content.", exception.getMessage());
    }
}

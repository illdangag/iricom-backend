package com.illdangag.iricom.service.block.post;

import com.illdangag.iricom.core.data.request.PostBlockInfoCreate;
import com.illdangag.iricom.core.data.request.PostBlockInfoUpdate;
import com.illdangag.iricom.core.data.response.PostBlockInfo;
import com.illdangag.iricom.core.exception.IricomException;
import com.illdangag.iricom.core.service.BlockService;
import com.illdangag.iricom.core.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.core.test.data.wrapper.TestBoardInfo;
import com.illdangag.iricom.core.test.data.wrapper.TestPostInfo;
import com.illdangag.iricom.IricomTestServiceSuite;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Collections;

@DisplayName("service: 차단 - 수정")
@Slf4j
@Transactional
public class BlockServicePostUpdateTestCore extends IricomTestServiceSuite {
    @Autowired
    private BlockService blockService;

    public BlockServicePostUpdateTestCore(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("시스템 관리자")
    public void updateSystemAdmin() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);

        // 게시물 차단
        PostBlockInfoCreate postBlockInfoCreate = PostBlockInfoCreate.builder()
                .reason("BLOCK")
                .build();
        PostBlockInfo postBlockInfo = blockService.blockPost(systemAdmin.getId(), board.getId(), post.getId(), postBlockInfoCreate);
        Assertions.assertNotNull(postBlockInfo);
        Assertions.assertEquals(post.getId(), postBlockInfo.getPostInfo().getId());
        Assertions.assertEquals("BLOCK", postBlockInfo.getReason());

        // 게시물 차단 사유 갱신
        PostBlockInfoUpdate postBlockInfoUpdate = PostBlockInfoUpdate.builder()
                .reason("UPDATE_SYSTEM_ADMIN")
                .build();
        PostBlockInfo updatedPostBlockInfo = blockService.updatePostBlockInfo(systemAdmin.getId(), board.getId(), post.getId(), postBlockInfoUpdate);
        Assertions.assertNotNull(updatedPostBlockInfo);
        Assertions.assertEquals(post.getId(), updatedPostBlockInfo.getPostInfo().getId());
        Assertions.assertEquals("UPDATE_SYSTEM_ADMIN", updatedPostBlockInfo.getReason());
    }

    @Test
    @DisplayName("게시판 관리자")
    public void updateBoardAdmin() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        TestAccountInfo boardAdmin = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard(Arrays.asList(boardAdmin));
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);

        // 게시물 차단
        PostBlockInfoCreate postBlockInfoCreate = PostBlockInfoCreate.builder()
                .reason("BLOCK")
                .build();
        blockService.blockPost(boardAdmin.getId(), board.getId(), post.getId(), postBlockInfoCreate);

        // 게시물 차단 사유 갱신
        PostBlockInfoUpdate postBlockInfoUpdate = PostBlockInfoUpdate.builder()
                .reason("UPDATE_SYSTEM_ADMIN")
                .build();
        PostBlockInfo updatedPostBlockInfo = blockService.updatePostBlockInfo(boardAdmin.getId(), board.getId(), post.getId(), postBlockInfoUpdate);
        Assertions.assertNotNull(updatedPostBlockInfo);
        Assertions.assertEquals(post.getId(), updatedPostBlockInfo.getPostInfo().getId());
        Assertions.assertEquals("UPDATE_SYSTEM_ADMIN", updatedPostBlockInfo.getReason());
    }

    @Test
    @DisplayName("다른 게시판 관리자")
    public void updateOtherBoardAdmin() throws Exception {
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

        // 게시물 차단 사유 수정
        PostBlockInfoUpdate postBlockInfoUpdate = PostBlockInfoUpdate.builder()
                .reason("UPDATE_OTHER_BOARD_ADMIN")
                .build();
        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            blockService.updatePostBlockInfo(otherBoardAdmin.getId(), board.getId(), post.getId(), postBlockInfoUpdate);
        });
        Assertions.assertEquals("04000009", exception.getErrorCode());
    }

    @Test
    @DisplayName("일반 사용자")
    public void updateAccount() throws Exception {
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

        // 게시물 차단 사유 수정
        PostBlockInfoUpdate postBlockInfoUpdate = PostBlockInfoUpdate.builder()
                .reason("UPDATE_ACCOUNT")
                .build();
        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            blockService.updatePostBlockInfo(account.getId(), board.getId(), post.getId(), postBlockInfoUpdate);
        });
        Assertions.assertEquals("04000009", exception.getErrorCode());
    }

    @Test
    @DisplayName("등록되지 않은 사용자")
    public void updateUnknown() throws Exception {
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

        // 게시물 차단 사유 수정
        PostBlockInfoUpdate postBlockInfoUpdate = PostBlockInfoUpdate.builder()
                .reason("UPDATE_UNKNOWN")
                .build();
        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            blockService.updatePostBlockInfo(unregisteredAccount.getId(), board.getId(), post.getId(), postBlockInfoUpdate);
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

        PostBlockInfoUpdate postBlockInfoUpdate = PostBlockInfoUpdate.builder()
                .reason("UPDATE_UNKNOWN")
                .build();

        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            blockService.updatePostBlockInfo(account.getId(), board.getId(), "UNKNOWN", postBlockInfoUpdate);
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

        PostBlockInfoUpdate postBlockInfoUpdate = PostBlockInfoUpdate.builder()
                .reason("UPDATE_UNKNOWN")
                .build();

        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            blockService.updatePostBlockInfo(account.getId(), board.getId(), post.getId(), postBlockInfoUpdate);
        });
        Assertions.assertEquals("07000000", exception.getErrorCode());
    }
}

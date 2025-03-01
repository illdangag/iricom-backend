package com.illdangag.iricom.server.service.block.post;

import com.illdangag.iricom.server.data.request.PostBlockInfoCreate;
import com.illdangag.iricom.server.data.request.PostBlockInfoUpdate;
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

@DisplayName("service: 차단 - 수정")
@Slf4j
@Transactional
public class BlockServicePostUpdateTest extends IricomTestSuite {
    @Autowired
    private BlockService blockService;

    public BlockServicePostUpdateTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("시스템 관리자")
    public void updateSystemAdmin() throws Exception {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        TestPostInfo post = this.setRandomPost(board, account);

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
        TestAccountInfo account = this.setRandomAccount();
        TestAccountInfo boardAdmin = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard(Arrays.asList(boardAdmin));
        // 게시물 생성
        TestPostInfo post = this.setRandomPost(board, account);

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
        TestAccountInfo boardAdmin = this.setRandomAccount();
        TestAccountInfo otherBoardAdmin = this.setRandomAccount();
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard(Arrays.asList(boardAdmin));
        this.setRandomBoard(Arrays.asList(otherBoardAdmin));
        // 게시물 생성
        TestPostInfo post = this.setRandomPost(board, account);

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
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        TestPostInfo post = this.setRandomPost(board, account);

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
        TestAccountInfo account = this.setRandomAccount();
        TestAccountInfo unregisteredAccount = this.setRandomAccount(true);
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        TestPostInfo post = this.setRandomPost(board, account);

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
}

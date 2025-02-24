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
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        TestPostInfo post = TestPostInfo.builder()
                .title("post title").content("content").isAllowComment(true)
                .postType(PostType.POST).postState(PostState.PUBLISH)
                .creator(account).board(board)
                .build();
        this.setPost(post);

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
        TestAccountInfo boardAdmin = this.setRandomAccount();
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = TestBoardInfo.builder()
                .title("board").isEnabled(true).adminList(Collections.singletonList(boardAdmin)).build();
        this.setBoard(board);
        // 게시물 생성
        TestPostInfo post = TestPostInfo.builder()
                .title("post title").content("content").isAllowComment(true)
                .postType(PostType.POST).postState(PostState.PUBLISH)
                .creator(account).board(board)
                .build();
        this.setPost(post);

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
        TestAccountInfo boardAdmin = this.setRandomAccount();
        TestAccountInfo otherBoardAdmin = this.setRandomAccount();
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = TestBoardInfo.builder()
                .title("board").isEnabled(true).adminList(Collections.singletonList(boardAdmin)).build();
        this.setBoard(board);
        TestBoardInfo otherBoard = TestBoardInfo.builder()
                .title("board").isEnabled(true).adminList(Collections.singletonList(otherBoardAdmin)).build();
        this.setBoard(otherBoard);
        // 게시물 생성
        TestPostInfo post = TestPostInfo.builder()
                .title("post title").content("content").isAllowComment(true)
                .postType(PostType.POST).postState(PostState.PUBLISH)
                .creator(account).board(board)
                .build();
        this.setPost(post);

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
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        TestPostInfo post = TestPostInfo.builder()
                .title("post title").content("content").isAllowComment(true)
                .postType(PostType.POST).postState(PostState.PUBLISH)
                .creator(account).board(board)
                .build();
        this.setPost(post);

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
        TestAccountInfo account = this.setRandomAccount();
        TestAccountInfo unregisteredAccount = this.setRandomUnregisteredAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        TestPostInfo post = this.setRandomPost(board, account);

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
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        TestBoardInfo otherBoard = this.setRandomBoard();
        // 게시물 생성
        TestPostInfo post = this.setRandomPost(board, account);

        PostBlockInfoCreate postBlockInfoCreate = PostBlockInfoCreate.builder()
                .reason("not exist post")
                .build();
        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            blockService.blockPost(systemAdmin.getId(), otherBoard.getId(), post.getId(), postBlockInfoCreate);
        });

        Assertions.assertEquals("04000000", exception.getErrorCode());
    }
}

package com.illdangag.iricom.server.service.board;

import com.illdangag.iricom.server.data.response.BoardInfo;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.service.AccountGroupService;
import com.illdangag.iricom.server.service.BoardService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestAccountGroupInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Collections;

@DisplayName("service: 게시판 - 조회")
@Transactional
public class BoardServiceGetTest extends IricomTestSuite {
    @Autowired
    private BoardService boardService;
    @Autowired
    private AccountGroupService accountGroupService;

    @Autowired
    public BoardServiceGetTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("공개 게시판 조회")
    public void getDisclosed() throws Exception {
        // 게시판 생성
        TestBoardInfo board = setRandomBoard(Collections.emptyList(), true, false);

        // 게시판 조회
        BoardInfo boardInfo = boardService.getBoardInfo(board.getId());
        Assertions.assertNotNull(boardInfo);
        Assertions.assertEquals(board.getId(), boardInfo.getId());
    }

    @Test
    @DisplayName("권한 없이 비공개 게시판 조회")
    public void getUndisclosed() throws Exception {
        // 게시판 생성
        TestBoardInfo board = setRandomBoard(Collections.emptyList(), true, true);

        // 권한 없이 게시판 조회
        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            boardService.getBoardInfo(board.getId());
        });
        Assertions.assertEquals("03000000", exception.getErrorCode());
    }

    @Test
    @DisplayName("시스템 관리자 계정으로 비공개 게시판 조회")
    public void getUndisclosedBySystemAdmin() throws Exception {
        // 게시판 생성
        TestBoardInfo board = setRandomBoard(Collections.emptyList(), true, true);

        BoardInfo boardInfo = boardService.getBoardInfo(systemAdmin.getId(), board.getId());
        Assertions.assertNotNull(boardInfo);
        Assertions.assertEquals(board.getId(), boardInfo.getId());
    }

    @Test
    @DisplayName("계정 그룹에 포함된 비공개 게시판")
    public void getUndisclosedInAccountGroup() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        TestAccountInfo otherAccount = setRandomAccount();
        // 게시판 생성
        TestBoardInfo disClosedBoard = setRandomBoard();
        TestBoardInfo undisclosedBoard = setRandomBoard(Collections.emptyList(), true, true);
        // 계정 그룹 생성
        TestAccountGroupInfo accountGroup = TestAccountGroupInfo.builder()
                .title("title").description("description")
                .accountList(Arrays.asList(account)).boardList(Arrays.asList(disClosedBoard, undisclosedBoard))
                .build();
        this.setAccountGroup(accountGroup);

        // 계정 그룹에 포함된 계정으로 게시판 조회
        Assertions.assertDoesNotThrow(() -> {
            // 공개 게시판이라 조회 가능
            boardService.getBoardInfo(account.getId(), disClosedBoard.getId());
            // 비공개 게시판이지만 계정 그룹에 포함되어 있어 조회 가능
            boardService.getBoardInfo(account.getId(), undisclosedBoard.getId());
        });

        // 계정 그룹에 포함되지 않은 계정으로 게시판 조회
        Assertions.assertDoesNotThrow(() -> {
            // 공개 게시판이라 조회 가능
            boardService.getBoardInfo(otherAccount.getId(), disClosedBoard.getId());
        });
        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            // 비공개 게시판이라 조회 불가능
            boardService.getBoardInfo(otherAccount.getId(), undisclosedBoard.getId());
        });
        Assertions.assertEquals("03000000", exception.getErrorCode());
    }

    @Test
    @DisplayName("계정 그룹에 포함되지 않은 비공개 게시판")
    public void getUndisclosedNotInAccountGroup() throws Exception {
        // 계정 생성
        TestAccountInfo group0Account = setRandomAccount();
        TestAccountInfo group1Account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo group0Board = setRandomBoard(Collections.emptyList(), true, true);
        TestBoardInfo group1Board = setRandomBoard(Collections.emptyList(), true, true);
        // 계정 그룹 생성
        TestAccountGroupInfo accountGroup0 = TestAccountGroupInfo.builder()
                .title("title").description("description")
                .accountList(Arrays.asList(group0Account)).boardList(Arrays.asList(group0Board))
                .build();
        TestAccountGroupInfo accountGroup1 = TestAccountGroupInfo.builder()
                .title("title").description("description")
                .accountList(Arrays.asList(group1Account)).boardList(Arrays.asList(group1Board))
                .build();
        this.setAccountGroup(accountGroup0, accountGroup1);

        // 1번 그룹 계정의 게시판 조회
        Assertions.assertDoesNotThrow(() -> {
            // 1번 그룹에 포함된 게시판은 조회 가능
            boardService.getBoardInfo(group0Account.getId(), group0Board.getId());
        });
        IricomException exception0 = Assertions.assertThrows(IricomException.class, () -> {
            // 2번 그룹에 포함된 게시판은 조회 불가능
            boardService.getBoardInfo(group0Account.getId(), group1Board.getId());
        });
        Assertions.assertEquals("03000000", exception0.getErrorCode());

        // 2번 그룹 계정의 게시판 조회
        IricomException exception1 = Assertions.assertThrows(IricomException.class, () -> {
            // 1번 그룹에 포함된 게시판은 조회 불가능
            boardService.getBoardInfo(group1Account.getId(), group0Board.getId());
        });
        Assertions.assertEquals("03000000", exception1.getErrorCode());
        Assertions.assertDoesNotThrow(() -> {
            // 2번 그룹에 포함된 게시판은 조회 가능
            boardService.getBoardInfo(group1Account.getId(), group1Board.getId());
        });
    }

    @Test
    @DisplayName("삭제된 계정 그룹에 포함된 비공개 게시판 조회")
    public void getUndisclosedBoardAndDeletedAccountGroup() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard(Collections.emptyList(), true, true);
        // 계정 그룹 생성
        TestAccountGroupInfo accountGroup = TestAccountGroupInfo.builder()
                .title("title").description("description")
                .accountList(Arrays.asList(account)).boardList(Arrays.asList(board))
                .build();
        this.setAccountGroup(accountGroup);

        // 게시판 조회
        Assertions.assertDoesNotThrow(() -> {
            // 그룹에 포함된 게시판은 조회 가능
            boardService.getBoardInfo(account.getId(), board.getId());
        });

        // 계정 그룹 삭제
        accountGroupService.deleteAccountGroupInfo(accountGroup.getId());
        IricomException exception = Assertions.assertThrows(IricomException.class, () -> {
            // 계정 그룹이 삭제된 후에는 게시판 조회 불가능
            boardService.getBoardInfo(account.getId(), board.getId());
        });
        Assertions.assertEquals("03000000", exception.getErrorCode());
    }

    @Test
    @DisplayName("시스템 관리자가 비공개 게시판 조회")
    public void getUndisclosedBoardBySystemAdmin() throws Exception {
        // 게시판 생성
        TestBoardInfo board = setRandomBoard(Collections.emptyList(), true, true);

        // 게시판 조회
        Assertions.assertDoesNotThrow(() -> {
            boardService.getBoardInfo(systemAdmin.getId(), board.getId());
        });
    }

    @Test
    @DisplayName("게시판 관리자가 비공개 게시판 조회")
    public void getUndisclosedBoardByBoardAdmin() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard(Arrays.asList(account), true, true);

        // 게시판 조회
        Assertions.assertDoesNotThrow(() -> {
            boardService.getBoardInfo(account.getId(), board.getId());
        });
    }
}

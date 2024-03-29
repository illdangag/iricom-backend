package com.illdangag.iricom.server.service.board;

import com.illdangag.iricom.server.data.response.BoardInfo;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.service.BoardService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestAccountGroupInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Collections;

@DisplayName("service: 게시판 - 조회")
@Slf4j
@Transactional
public class BoardServiceGetTest extends IricomTestSuite {
    @Autowired
    private BoardService boardService;

    // 게시판
    private final TestBoardInfo disclosedBoard00 = TestBoardInfo.builder()
            .title("disclosedBoard00").isEnabled(true).undisclosed(false)
            .adminList(Collections.singletonList(allBoardAdmin)).build();
    private final TestBoardInfo undisclosedBoard00 = TestBoardInfo.builder()
            .title("undisclosedBoard00").isEnabled(true).undisclosed(true)
            .adminList(Collections.singletonList(allBoardAdmin)).build();
    private final TestBoardInfo undisclosedBoard01 = TestBoardInfo.builder()
            .title("undisclosedBoard01").isEnabled(true).undisclosed(true)
            .adminList(Collections.singletonList(allBoardAdmin)).build();
    private final TestBoardInfo undisclosedBoard02 = TestBoardInfo.builder()
            .title("undisclosedBoard03").isEnabled(true).undisclosed(true)
            .adminList(Collections.singletonList(allBoardAdmin)).build();

    // 게정 그룹
    private final TestAccountGroupInfo testAccountGroupInfo00 = TestAccountGroupInfo.builder()
            .title("testAccountGroupInfo00").description("description")
            .accountList(Arrays.asList(common00)).boardList(Arrays.asList(undisclosedBoard00))
            .build();

    @Autowired
    public BoardServiceGetTest(ApplicationContext context) {
        super(context);

        addTestBoardInfo(disclosedBoard00, undisclosedBoard00, undisclosedBoard01, undisclosedBoard02);
        addTestAccountGroupInfo(testAccountGroupInfo00);
        init();
    }

    @Test
    @DisplayName("공개 게시판 조회")
    public void getDisclosed() throws Exception {
        String boardId = getBoardId(disclosedBoard00);
        BoardInfo boardInfo = boardService.getBoardInfo(boardId);

        Assertions.assertNotNull(boardInfo);
    }

    @Test
    @DisplayName("비공개 게시판 조회")
    public void getUndisclosed() throws Exception {
        String boardId = getBoardId(undisclosedBoard00);

        Assertions.assertThrows(IricomException.class, () -> {
            boardService.getBoardInfo(boardId);
        });
    }

    @Test
    @DisplayName("계정 그룹에 포함된 비공개 게시판")
    public void getUndisclosedInAccountGroup() throws Exception {
        String accountId = getAccountId(testAccountGroupInfo00.getAccountList().get(0));
        String boardId = getBoardId(undisclosedBoard00);

        BoardInfo boardInfo = boardService.getBoardInfo(accountId, boardId);

        Assertions.assertNotNull(boardInfo);
    }

    @Test
    @DisplayName("계정 그룹에 포함되지 않은 비공개 게시판")
    public void getUndisclosedNotInAccountGroup() throws Exception {
        String accountId = getAccountId(common00);
        String boardId = getBoardId(undisclosedBoard01);

        Assertions.assertThrows(IricomException.class, () -> {
            boardService.getBoardInfo(accountId, boardId);
        });
    }

    @Test
    @DisplayName("삭제된 계정 그룹에 포함된 비공개 게시판 조회")
    public void getUndisclosedBoardAndDeletedAccountGroup() throws Exception {
        String accountId = getAccountId(common00);
        String boardId = getBoardId(undisclosedBoard02);

        Assertions.assertThrows(IricomException.class, () -> {
            boardService.getBoardInfo(accountId, boardId);
        });
    }

    @Test
    @DisplayName("시스템 관리자가 비공개 게시판 조회")
    public void getUndisclosedBoardBySystemAdmin() throws Exception {
        String accountId = getAccountId(systemAdmin);
        String boardId = getBoardId(undisclosedBoard00);

        BoardInfo boardInfo = boardService.getBoardInfo(accountId, boardId);

        Assertions.assertNotNull(boardInfo);
    }

    @Test
    @DisplayName("게시판 관리자가 비공개 게시판 조회")
    public void getUndisclosedBoardByBoardAdmin() throws Exception {
        String accountId = getAccountId(allBoardAdmin);
        String boardId = getBoardId(undisclosedBoard01);

        BoardInfo boardInfo = boardService.getBoardInfo(accountId, boardId);

        Assertions.assertNotNull(boardInfo);
    }
}

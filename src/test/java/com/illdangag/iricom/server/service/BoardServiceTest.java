package com.illdangag.iricom.server.service;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.Board;
import com.illdangag.iricom.server.data.request.BoardInfoSearch;
import com.illdangag.iricom.server.data.response.BoardInfo;
import com.illdangag.iricom.server.data.response.BoardInfoList;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.TestAccountGroupInfo;
import com.illdangag.iricom.server.test.data.TestBoardInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class BoardServiceTest extends IricomTestSuite {
    @Autowired
    private BoardService boardService;

    private TestBoardInfo disclosedBoard00 = TestBoardInfo.builder()
            .title("testBoardInfo00").isEnabled(true).undisclosed(false)
            .adminList(Collections.singletonList(allBoardAdmin)).build();

    private TestBoardInfo undisclosedBoard00 = TestBoardInfo.builder()
            .title("undisclosedBoard00").isEnabled(true).undisclosed(true)
            .adminList(Collections.singletonList(allBoardAdmin)).build();
    private TestBoardInfo undisclosedBoard01 = TestBoardInfo.builder()
            .title("undisclosedBoard01").isEnabled(true).undisclosed(true)
            .adminList(Collections.singletonList(allBoardAdmin)).build();
    private TestBoardInfo undisclosedBoard02 = TestBoardInfo.builder()
            .title("undisclosedBoard02").isEnabled(true).undisclosed(true)
            .adminList(Collections.singletonList(allBoardAdmin)).build();

    private TestAccountGroupInfo testAccountGroupInfo00 = TestAccountGroupInfo.builder()
            .title("testAccountGroupInfo00").description("description")
            .accountList(Arrays.asList(common00)).boardList(Arrays.asList(undisclosedBoard00))
            .build();

    private TestAccountGroupInfo testAccountGroupInfo01 = TestAccountGroupInfo.builder()
            .title("testAccountGroupInfo01").description("description")
            .accountList(Arrays.asList(common01)).boardList(Arrays.asList(undisclosedBoard01))
            .build();

    private TestAccountGroupInfo testAccountGroupInfo02 = TestAccountGroupInfo.builder()
            .title("testAccountGroupInfo02").description("description")
            .accountList(Arrays.asList(common02)).boardList(Arrays.asList(undisclosedBoard02))
            .build();


    @Autowired
    public BoardServiceTest(ApplicationContext context) {
        super(context);

        super.setBoard(Arrays.asList(disclosedBoard00, undisclosedBoard00, undisclosedBoard01, undisclosedBoard02));
        super.setAccountGroup(Arrays.asList(testAccountGroupInfo00, testAccountGroupInfo01, testAccountGroupInfo02));
    }

    @Nested
    @DisplayName("조회")
    class Get {

        @Test
        @DisplayName("공개 게시판 조회")
        public void getDisclosed() throws Exception {
            Board board = getBoard(disclosedBoard00);
            String boardId = String.valueOf(board.getId());
            BoardInfo boardInfo = boardService.getBoardInfo(boardId);

            Assertions.assertNotNull(boardInfo);
            Assertions.assertEquals(board.getTitle(), boardInfo.getTitle());
        }

        @Test
        @DisplayName("비공개 게시판 조회")
        public void getUndisclosed() throws Exception {
            Board board = getBoard(undisclosedBoard00);
            String boardId = String.valueOf(board.getId());

            Assertions.assertThrows(IricomException.class, () -> {
                boardService.getBoardInfo(boardId);
            });
        }

        @Test
        @DisplayName("계정 그룹에 포함된 비공개 게시판")
        public void getUndisclosedInAccountGroup() throws Exception {
            Account account = getAccount(testAccountGroupInfo00.getAccountList().get(0));
            Board board = getBoard(undisclosedBoard00);
            String boardId = String.valueOf(board.getId());

            BoardInfo boardInfo = boardService.getBoardInfo(account, boardId);

            Assertions.assertNotNull(boardInfo);
            Assertions.assertEquals(board.getTitle(), boardInfo.getTitle());
        }

        @Test
        @DisplayName("계정 그룹에 포함되지 않은 비공개 게시판")
        public void getUndisclosedNotInAccountGroup() throws Exception {
            Account account = getAccount(common00);
            Board board = getBoard(undisclosedBoard01);
            String boardId = String.valueOf(board.getId());

            Assertions.assertThrows(IricomException.class, () -> {
                boardService.getBoardInfo(account, boardId);
            });
        }
    }

    @Nested
    @DisplayName("목록 조회")
    class GetList {

        @Test
        @DisplayName("공개 개시판 목록 조회")
        public void searchDisclosedBoardList() throws Exception {
            String undisclosedBoardId00 = String.valueOf(getBoard(undisclosedBoard00).getId());
            String undisclosedBoardId01 = String.valueOf(getBoard(undisclosedBoard01).getId());

            BoardInfoSearch boardInfoSearch = BoardInfoSearch.builder()
                    .skip(0).limit(100)
                    .build();

            BoardInfoList boardInfoList = boardService.getBoardInfoList(boardInfoSearch);

            List<String> boardInfoIdList = boardInfoList.getBoardInfoList().stream()
                    .map(BoardInfo::getId)
                    .collect(Collectors.toList());

            // 비공개 게시판은 조회되지 않아야 함
            Assertions.assertFalse(boardInfoIdList.contains(undisclosedBoardId00));
            Assertions.assertFalse(boardInfoIdList.contains(undisclosedBoardId01));
        }

        @Test
        @DisplayName("공개 게시판 및 계정 그룹에 포함된 게시물 목록 조회")
        public void searchDisclosedBoardAndAccountGroupBoardList() throws Exception {
            Account account = getAccount(common02);

            String accessibleBoardId = String.valueOf(getBoard(undisclosedBoard02).getId());
            String inaccessibleBoardId = String.valueOf(getBoard(undisclosedBoard01).getId());

            BoardInfoSearch boardInfoSearch = BoardInfoSearch.builder()
                    .skip(0).limit(100)
                    .build();

            BoardInfoList boardInfoList = boardService.getBoardInfoList(account, boardInfoSearch);

            List<String> boardInfoIdList = boardInfoList.getBoardInfoList().stream()
                    .map(BoardInfo::getId)
                    .collect(Collectors.toList());

            Assertions.assertTrue(boardInfoIdList.contains(accessibleBoardId));
            Assertions.assertFalse(boardInfoIdList.contains(inaccessibleBoardId));
        }
    }
}

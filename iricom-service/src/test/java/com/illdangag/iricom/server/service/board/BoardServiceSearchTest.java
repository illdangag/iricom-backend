package com.illdangag.iricom.server.service.board;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.request.BoardInfoSearch;
import com.illdangag.iricom.server.data.response.BoardInfo;
import com.illdangag.iricom.server.data.response.BoardInfoList;
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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@DisplayName("service: 게시판 - 목록 조회")
@Slf4j
public class BoardServiceSearchTest extends IricomTestSuite {
    @Autowired
    private BoardService boardService;

    // 공개 게시판 목록
    private final TestBoardInfo disclosedBoard00 = TestBoardInfo.builder()
            .title("disclosedBoard00").isEnabled(true).undisclosed(false).adminList(Collections.singletonList(allBoardAdmin)).build();
    // 비공게 게시판 목록
    private final TestBoardInfo undisclosedBoard00 = TestBoardInfo.builder()
            .title("undisclosedBoard00").isEnabled(true).undisclosed(true).adminList(Collections.singletonList(allBoardAdmin)).build();
    private final TestBoardInfo undisclosedBoard01 = TestBoardInfo.builder()
            .title("undisclosedBoard01").isEnabled(true).undisclosed(true).adminList(Collections.singletonList(allBoardAdmin)).build();
    private final TestBoardInfo undisclosedBoard02 = TestBoardInfo.builder()
            .title("undisclosedBoard02").isEnabled(true).undisclosed(true).adminList(Collections.singletonList(allBoardAdmin)).build();
    // 계정 그룹
    private final TestAccountGroupInfo testAccountGroupInfo00 = TestAccountGroupInfo.builder()
            .title("testAccountGroupInfo00").description("description").deleted(false)
            .accountList(Collections.singletonList(common00)).boardList(Collections.singletonList(undisclosedBoard01))
            .build();
    private final TestAccountGroupInfo testAccountGroupInfo01 = TestAccountGroupInfo.builder()
            .title("testAccountGroupInfo01").description("description").deleted(true)
            .accountList(Collections.singletonList(common00)).boardList(Collections.singletonList(undisclosedBoard02))
            .build();

    public BoardServiceSearchTest(ApplicationContext context) {
        super(context);

        addTestBoardInfo(disclosedBoard00, undisclosedBoard00, undisclosedBoard01, undisclosedBoard02);
        addTestAccountGroupInfo(testAccountGroupInfo00, testAccountGroupInfo01);

        init();
    }

    @Test
    @DisplayName("공개 개시판 목록 조회")
    public void searchDisclosedBoardList() throws Exception {
        String disclosedBoardId = String.valueOf(getBoard(disclosedBoard00).getId());
        String undisclosedBoardId = String.valueOf(getBoard(undisclosedBoard00).getId());

        List<String> list = getAllList(BoardInfoSearch.builder().build(), (searchRequest -> {
            BoardInfoSearch boardInfoSearch = (BoardInfoSearch) searchRequest;
            BoardInfoList boardInfoList = boardService.getBoardInfoList(boardInfoSearch);
            return boardInfoList.getBoardInfoList().stream()
                    .map(BoardInfo::getId)
                    .collect(Collectors.toList());
        }));

        Assertions.assertTrue(list.contains(disclosedBoardId));
        Assertions.assertFalse(list.contains(undisclosedBoardId));
    }

    @Test
    @DisplayName("공개 게시판 및 계정 그룹에 포함된 게시물 목록 조회")
    public void searchDisclosedBoardAndAccountGroupBoardList() throws Exception {
        Account account = getAccount(common00);
        String inaccessibleBoardId = String.valueOf(getBoard(undisclosedBoard00).getId());
        String accessibleBoardId = String.valueOf(getBoard(undisclosedBoard01).getId());

        List<String> list = getAllList(BoardInfoSearch.builder().build(), searchRequest -> {
            BoardInfoSearch boardInfoSearch = (BoardInfoSearch) searchRequest;
            BoardInfoList boardInfoList = boardService.getBoardInfoList(account, boardInfoSearch);
            return boardInfoList.getBoardInfoList().stream()
                    .map(BoardInfo::getId)
                    .collect(Collectors.toList());
        });

        Assertions.assertTrue(list.contains(accessibleBoardId));
        Assertions.assertFalse(list.contains(inaccessibleBoardId));
    }

    @Test
    @DisplayName("삭제된 계정 그룹에 포함된 게시판 목록 조회")
    public void searchDisclosedBoardAndDeletedAccountGroupBoardList() throws Exception {
        Account account = getAccount(common01);
        String undisclosedBoardId = String.valueOf(getBoard(undisclosedBoard02).getId());

        List<String> list = getAllList(BoardInfoSearch.builder().build(), searchRequest -> {
            BoardInfoSearch boardInfoSearch = (BoardInfoSearch) searchRequest;
            BoardInfoList boardInfoList = boardService.getBoardInfoList(account, boardInfoSearch);
            return boardInfoList.getBoardInfoList().stream()
                    .map(BoardInfo::getId)
                    .collect(Collectors.toList());
        });

        Assertions.assertFalse(list.contains(undisclosedBoardId));
    }
}

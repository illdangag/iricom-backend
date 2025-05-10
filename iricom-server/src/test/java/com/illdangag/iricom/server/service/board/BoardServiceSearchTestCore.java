package com.illdangag.iricom.server.service.board;

import com.illdangag.iricom.core.data.request.BoardInfoSearch;
import com.illdangag.iricom.core.data.response.BoardInfo;
import com.illdangag.iricom.core.data.response.BoardInfoList;
import com.illdangag.iricom.core.service.AccountGroupService;
import com.illdangag.iricom.core.service.BoardService;
import com.illdangag.iricom.core.test.data.wrapper.TestAccountGroupInfo;
import com.illdangag.iricom.core.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.core.test.data.wrapper.TestBoardInfo;
import com.illdangag.iricom.server.IricomTestServiceSuite;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@DisplayName("service: 게시판 - 목록 조회")
@Transactional
public class BoardServiceSearchTestCore extends IricomTestServiceSuite {
    @Autowired
    private BoardService boardService;
    @Autowired
    private AccountGroupService accountGroupService;

    public BoardServiceSearchTestCore(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("공개 개시판 목록 조회")
    public void searchDisclosedBoardList() throws Exception {
        // 게시판 생성
        List<TestBoardInfo> boardList = setRandomBoard(5);
        List<TestBoardInfo> undisclosedBoardList = setRandomBoard(Collections.emptyList(), true, true, 5);

        // 게시판 조회
        List<String> boardIdList = getAllList(BoardInfoSearch.builder().build(), (searchRequest -> {
            BoardInfoSearch boardInfoSearch = (BoardInfoSearch) searchRequest;
            BoardInfoList boardInfoList = boardService.getBoardInfoList(boardInfoSearch);
            return boardInfoList.getBoardInfoList().stream()
                    .map(BoardInfo::getId)
                    .collect(Collectors.toList());
        }));

        boardList.forEach(board -> {
            // 공개 게시판은은 모두 존재
            Assertions.assertTrue(boardIdList.contains(board.getId()));
        });
        undisclosedBoardList.forEach(board -> {
            // 비공개 게시판은 모두 존재 하지 않음
            Assertions.assertFalse(boardIdList.contains(board.getId()));
        });
    }

    @Test
    @DisplayName("삭제된 계정 그룹에 포함된 게시판 목록 조회")
    public void searchDisclosedBoardAndDeletedAccountGroupBoardList() throws Exception {
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

        // 계정 그룹 삭제
        accountGroupService.deleteAccountGroupInfo(accountGroup.getId());

        List<String> list = getAllList(BoardInfoSearch.builder().build(), searchRequest -> {
            BoardInfoSearch boardInfoSearch = (BoardInfoSearch) searchRequest;
            BoardInfoList boardInfoList = boardService.getBoardInfoList(account.getId(), boardInfoSearch);
            return boardInfoList.getBoardInfoList().stream()
                    .map(BoardInfo::getId)
                    .collect(Collectors.toList());
        });

        Assertions.assertFalse(list.contains(board.getId()));
    }

    @Test
    @DisplayName("비공개 게시판 관리자가 비공개 게시판 목록 조회")
    public void searchDisclosedBoardListByBoardAdmin() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard(Arrays.asList(account), true, true);

        List<String> list = getAllList(BoardInfoSearch.builder().build(), searchRequest -> {
            BoardInfoSearch boardInfoSearch = (BoardInfoSearch) searchRequest;
            BoardInfoList boardInfoList = boardService.getBoardInfoList(account.getId(), boardInfoSearch);
            return boardInfoList.getBoardInfoList().stream()
                    .map(BoardInfo::getId)
                    .collect(Collectors.toList());
        });

        Assertions.assertTrue(list.contains(board.getId()));
    }
}

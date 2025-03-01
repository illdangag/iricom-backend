package com.illdangag.iricom.server.service.board.authorization;

import com.illdangag.iricom.server.data.request.BoardAdminInfoSearch;
import com.illdangag.iricom.server.data.response.AccountInfo;
import com.illdangag.iricom.server.data.response.BoardAdminInfo;
import com.illdangag.iricom.server.data.response.BoardAdminInfoList;
import com.illdangag.iricom.server.service.BoardAuthorizationService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

@DisplayName("service: 게시판 관리자 - 검색")
@Transactional
public class BoardAuthorizationServiceSearchTest extends IricomTestSuite {
    @Autowired
    private BoardAuthorizationService boardAuthorizationService;

    @Autowired
    public BoardAuthorizationServiceSearchTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("검색")
    public void searchBoardAdmin() {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard(Arrays.asList(account));

        BoardAdminInfoSearch boardAdminInfoSearch = BoardAdminInfoSearch.builder()
                .keyword(board.getTitle())
                .build();

        BoardAdminInfoList boardAdminInfoList = this.boardAuthorizationService.getBoardAdminInfoList(systemAdmin.getId(), boardAdminInfoSearch);
        Assertions.assertEquals(1, boardAdminInfoList.getTotal());
        BoardAdminInfo boardAdmin = boardAdminInfoList.getBoardAdminInfoList().get(0);
        List<AccountInfo> boardAdminAccountList = boardAdmin.getAccountInfoList();
        Assertions.assertEquals(1, boardAdminAccountList.size());
        AccountInfo boardAdminAccount = boardAdminAccountList.get(0);
        Assertions.assertEquals(account.getId(), boardAdminAccount.getId());
    }

    @Test
    @DisplayName("keyword 빈 문자열")
    public void emptyKeyword() {
        // 게시판 생성
        setRandomBoard();

        BoardAdminInfoSearch boardAdminInfoSearch = BoardAdminInfoSearch.builder()
                .keyword("")
                .build();

        BoardAdminInfoList boardAdminInfoList = this.boardAuthorizationService.getBoardAdminInfoList(systemAdmin.getId(), boardAdminInfoSearch);
        Assertions.assertNotEquals(0, boardAdminInfoList.getTotal());
    }
}

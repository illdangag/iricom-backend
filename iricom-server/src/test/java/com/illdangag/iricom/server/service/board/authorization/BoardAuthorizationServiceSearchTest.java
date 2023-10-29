package com.illdangag.iricom.server.service.board.authorization;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.request.BoardAdminInfoSearch;
import com.illdangag.iricom.server.data.response.BoardAdminInfoList;
import com.illdangag.iricom.server.service.BoardAuthorizationService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;

@Slf4j
@DisplayName("service: 게시판 관리자 - 검색")
public class BoardAuthorizationServiceSearchTest extends IricomTestSuite {
    @Autowired
    private BoardAuthorizationService boardAuthorizationService;
    // 게시판
    private final TestBoardInfo testBoardInfo00 = TestBoardInfo.builder()
            .title("search test 00 keyword").isEnabled(true).undisclosed(false)
            .adminList(Arrays.asList(common00, common01, common02))
            .build();
    private final TestBoardInfo testBoardInfo01 = TestBoardInfo.builder()
            .title("search test 01 keyword").isEnabled(true).undisclosed(false)
            .adminList(Arrays.asList(common00, common01))
            .build();
    private final TestBoardInfo testBoardInfo02 = TestBoardInfo.builder()
            .title("search test 02 keyword").isEnabled(true).undisclosed(false)
            .adminList(Arrays.asList(common00))
            .build();
    private final TestBoardInfo testBoardInfo03 = TestBoardInfo.builder()
            .title("search test 03 keyword").isEnabled(true).undisclosed(false)
            .build();

    @Autowired
    public BoardAuthorizationServiceSearchTest(ApplicationContext context) {
        super(context);

        addTestBoardInfo(testBoardInfo00, testBoardInfo01, testBoardInfo02, testBoardInfo03);
        init();
    }

    @Test
    @DisplayName("검색")
    public void searchBoardAdmin() {
        Account account = getAccount(systemAdmin);
        BoardAdminInfoSearch boardAdminInfoSearch = BoardAdminInfoSearch.builder()
                .keyword("search test 00 keyword")
                .build();

        BoardAdminInfoList boardAdminInfoList = this.boardAuthorizationService.getBoardAdminInfoList(account, boardAdminInfoSearch);

        Assertions.assertNotEquals(0, boardAdminInfoList.getTotal());
    }

    @Test
    @DisplayName("keyword 빈 문자열")
    public void emptyKeyword() {
        Account account = getAccount(systemAdmin);
        BoardAdminInfoSearch boardAdminInfoSearch = BoardAdminInfoSearch.builder()
                .keyword("")
                .build();

        BoardAdminInfoList boardAdminInfoList = this.boardAuthorizationService.getBoardAdminInfoList(account, boardAdminInfoSearch);

        Assertions.assertNotEquals(0, boardAdminInfoList.getTotal());
    }
}

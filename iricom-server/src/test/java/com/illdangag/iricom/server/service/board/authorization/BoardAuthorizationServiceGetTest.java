package com.illdangag.iricom.server.service.board.authorization;

import com.illdangag.iricom.server.data.response.AccountInfo;
import com.illdangag.iricom.server.data.response.BoardAdminInfo;
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

@DisplayName("service: 게시판 관리자 - 조회")
@Transactional
public class BoardAuthorizationServiceGetTest extends IricomTestSuite {
    @Autowired
    private BoardAuthorizationService boardAuthorizationService;

    @Autowired
    public BoardAuthorizationServiceGetTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("게시판 관리자 조회")
    public void getBoardAdmin() {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard(Arrays.asList(account));

        // 게시판 관리자 조회
        BoardAdminInfo boardAdminInfo = this.boardAuthorizationService.getBoardAdminInfo(board.getId());
        List<AccountInfo> accountInfoList = boardAdminInfo.getAccountInfoList();
        Assertions.assertNotNull(accountInfoList);
        Assertions.assertEquals(1, accountInfoList.size());
        AccountInfo boardAdminAccount = accountInfoList.get(0);
        Assertions.assertEquals(account.getId(), boardAdminAccount.getId());
    }
}

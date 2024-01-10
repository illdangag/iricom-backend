package com.illdangag.iricom.server.service.board.authorization;

import com.illdangag.iricom.server.data.response.AccountInfo;
import com.illdangag.iricom.server.data.response.BoardAdminInfo;
import com.illdangag.iricom.server.service.BoardAuthorizationService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@DisplayName("service: 게시판 관리자 - 조회")
@Slf4j
@Transactional
public class BoardAuthorizationServiceGetTest extends IricomTestSuite {
    @Autowired
    private BoardAuthorizationService boardAuthorizationService;
    // 게시판
    private final TestBoardInfo testBoardInfo00 = TestBoardInfo.builder()
            .title("testBoardInfo00").isEnabled(true).undisclosed(false)
            .adminList(Collections.singletonList(common00))
            .build();

    @Autowired
    public BoardAuthorizationServiceGetTest(ApplicationContext context) {
        super(context);

        addTestBoardInfo(testBoardInfo00);

        init();
    }

    @Test
    @DisplayName("게시판 관리자 조회")
    public void getBoardAdmin() {
        String accountId = getAccountId(common00);
        String boardId = getBoardId(testBoardInfo00);

        BoardAdminInfo boardAdminInfo = this.boardAuthorizationService.getBoardAdminInfo(boardId);

        List<AccountInfo> accountInfoList = boardAdminInfo.getAccountInfoList();
        List<String> accountInfoIdList = accountInfoList.stream()
                .map(AccountInfo::getId)
                .collect(Collectors.toList());

        Assertions.assertTrue(accountInfoIdList.contains(accountId));
    }
}

package com.illdangag.iricom.server.repository;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.BoardAdmin;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Collections;
import java.util.List;

@DisplayName("repository: BoardAdminRepository")
@Slf4j
public class BoardAdminRepositoryTest extends IricomTestSuite {
    @Autowired
    private BoardAdminRepository boardAdminRepository;

    public BoardAdminRepositoryTest(ApplicationContext context) {
        super(context);
    }

    @DisplayName("getLastBoardAdminList")
    @Test
    public void getLastBoardAdminList() {
        // 게시판
        TestBoardInfo testBoardInfo00 = TestBoardInfo.builder()
                .title("testBoardInfo00").isEnabled(true).undisclosed(false)
                .adminList(Collections.singletonList(common00))
                .build();
        TestBoardInfo testBoardInfo01 = TestBoardInfo.builder()
                .title("testBoardInfo01").isEnabled(true).undisclosed(false)
                .adminList(Collections.singletonList(common00))
                .build();
        TestBoardInfo testBoardInfo02 = TestBoardInfo.builder()
                .title("testBoardInfo02").isEnabled(true).undisclosed(false)
                .adminList(Collections.singletonList(common00))
                .build();
        addTestBoardInfo(testBoardInfo00, testBoardInfo01, testBoardInfo02);
        init();

        Account account = getAccount(common00);

        List<BoardAdmin> boardAdminList = boardAdminRepository.getBoardAdminList(account, 0, 20);
        long boardAdminCount = this.boardAdminRepository.getBoardAdminCount(account);

        Assertions.assertNotNull(boardAdminList);
        Assertions.assertEquals(boardAdminCount, boardAdminList.size());
    }
}

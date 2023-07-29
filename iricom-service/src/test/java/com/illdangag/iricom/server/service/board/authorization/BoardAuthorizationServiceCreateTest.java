package com.illdangag.iricom.server.service.board.authorization;

import com.illdangag.iricom.server.data.request.BoardAdminInfoCreate;
import com.illdangag.iricom.server.data.response.AccountInfo;
import com.illdangag.iricom.server.data.response.BoardAdminInfo;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.service.BoardAuthorizationService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@DisplayName("service: 게시판 관리자 - 생성")
public class BoardAuthorizationServiceCreateTest extends IricomTestSuite {
    @Autowired
    private BoardAuthorizationService boardAuthorizationService;
    // 게시판
    private final TestBoardInfo testBoardInfo00 = TestBoardInfo.builder()
            .title("testBoardInfo00").isEnabled(true).undisclosed(false)
            .build();
    private final TestBoardInfo testBoardInfo01 = TestBoardInfo.builder()
            .title("testBoardInfo01").isEnabled(true).undisclosed(false)
            .adminList(Collections.singletonList(common00))
            .build();

    @Autowired
    public BoardAuthorizationServiceCreateTest(ApplicationContext context) {
        super(context);

        addTestBoardInfo(testBoardInfo00, testBoardInfo01);

        init();
    }

    @Test
    @DisplayName("게시판 관리자 생성")
    public void createBoardAdmin() throws Exception {
        String accountId = getAccountId(common00);
        String boardId = getBoardId(testBoardInfo00);

        BoardAdminInfoCreate boardAdminInfoCreate = BoardAdminInfoCreate.builder()
                .boardId(boardId)
                .accountId(accountId)
                .build();

        BoardAdminInfo boardAdminInfo = this.boardAuthorizationService.createBoardAdminAuth(boardAdminInfoCreate);

        List<AccountInfo> accountInfoList = boardAdminInfo.getAccountInfoList();
        Assertions.assertNotNull(accountInfoList);

        List<String> accountInfoIdList = accountInfoList.stream()
                .map(AccountInfo::getId)
                .collect(Collectors.toList());
        Assertions.assertTrue(accountInfoIdList.contains(accountId));
    }

    @Test
    @DisplayName("이미 관리자로 추가된 게시판에 관리자로 추가")
    public void duplicateCreateBoardAdmin() throws Exception {
        String accountId = getAccountId(common00);
        String boardId = getBoardId(testBoardInfo01);

        BoardAdminInfoCreate boardAdminInfoCreate = BoardAdminInfoCreate.builder()
                .boardId(boardId)
                .accountId(accountId)
                .build();

        BoardAdminInfo boardAdminInfo = this.boardAuthorizationService.createBoardAdminAuth(boardAdminInfoCreate);

        List<AccountInfo> accountInfoList = boardAdminInfo.getAccountInfoList();
        Assertions.assertNotNull(accountInfoList);
        Assertions.assertEquals(1, accountInfoList.size());
    }

    @Test
    @DisplayName("계정을 설정하지 않음")
    public void notExistAccountId() throws Exception {
        String boardId = getBoardId(testBoardInfo01);

        BoardAdminInfoCreate boardAdminInfoCreate = BoardAdminInfoCreate.builder()
                .boardId(boardId)
                .accountId(null)
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            this.boardAuthorizationService.createBoardAdminAuth(boardAdminInfoCreate);
        });
    }

    @Test
    @DisplayName("계정에 빈 문자열")
    public void emptyAccountId() throws Exception {
        String boardId = getBoardId(testBoardInfo01);

        BoardAdminInfoCreate boardAdminInfoCreate = BoardAdminInfoCreate.builder()
                .boardId(boardId)
                .accountId("")
                .build();

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.boardAuthorizationService.createBoardAdminAuth(boardAdminInfoCreate);
        });

        Assertions.assertEquals("02000000", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist account.", iricomException.getMessage());
    }

    @Test
    @DisplayName("게시판을 설정하지 않음")
    public void notExistBoardId() {
        String accountId = getAccountId(common00);

        BoardAdminInfoCreate boardAdminInfoCreate = BoardAdminInfoCreate.builder()
                .boardId(null)
                .accountId(accountId)
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            this.boardAuthorizationService.createBoardAdminAuth(boardAdminInfoCreate);
        });
    }

    @Test
    @DisplayName("게시판에 빈 문자열")
    public void emptyBoardId() {
        String accountId = getAccountId(common00);

        BoardAdminInfoCreate boardAdminInfoCreate = BoardAdminInfoCreate.builder()
                .boardId("")
                .accountId(accountId)
                .build();

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.boardAuthorizationService.createBoardAdminAuth(boardAdminInfoCreate);
        });

        Assertions.assertEquals("03000000", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist board.", iricomException.getMessage());
    }
}

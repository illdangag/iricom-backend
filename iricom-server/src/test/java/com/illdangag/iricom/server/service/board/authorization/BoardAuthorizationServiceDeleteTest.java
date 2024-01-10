package com.illdangag.iricom.server.service.board.authorization;

import com.illdangag.iricom.server.data.request.BoardAdminInfoDelete;
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

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@DisplayName("service: 게시판 관리자 - 삭제")
@Slf4j
@Transactional
public class BoardAuthorizationServiceDeleteTest extends IricomTestSuite {
    @Autowired
    private BoardAuthorizationService boardAuthorizationService;
    // 게시판
    private final TestBoardInfo testBoardInfo00 = TestBoardInfo.builder()
            .title("testBoardInfo00").isEnabled(true).undisclosed(false)
            .adminList(Collections.singletonList(common00))
            .build();
    // 게시판
    private final TestBoardInfo testBoardInfo01 = TestBoardInfo.builder()
            .title("testBoardInfo01").isEnabled(true).undisclosed(false)
            .adminList(Collections.singletonList(common00))
            .build();

    @Autowired
    public BoardAuthorizationServiceDeleteTest(ApplicationContext context) {
        super(context);

        addTestBoardInfo(testBoardInfo00, testBoardInfo01);

        init();
    }

    @Test
    @DisplayName("게시판 관리자 삭제")
    public void deleteBoardAdmin() throws Exception {
        String accountId = getAccountId(common00);
        String boardId = getBoardId(testBoardInfo00);

        BoardAdminInfoDelete boardAdminInfoDelete = BoardAdminInfoDelete.builder()
                .boardId(boardId)
                .accountId(accountId)
                .build();

        BoardAdminInfo boardAdminInfo = this.boardAuthorizationService.deleteBoardAdminAuth(boardAdminInfoDelete);

        List<AccountInfo> accountInfoList = boardAdminInfo.getAccountInfoList();
        Assertions.assertNotNull(accountInfoList);

        List<String> accountInfoIdList = accountInfoList.stream()
                .map(AccountInfo::getId)
                .collect(Collectors.toList());
        Assertions.assertFalse(accountInfoIdList.contains(accountId));
    }

    @Test
    @DisplayName("게시판 관리자로 등록되지 않은 계정")
    public void notExistBoardAdmin() throws Exception {
        String accountId = getAccountId(common01);
        String boardId = getBoardId(testBoardInfo00);

        BoardAdminInfoDelete boardAdminInfoDelete = BoardAdminInfoDelete.builder()
                .boardId(boardId)
                .accountId(accountId)
                .build();

        BoardAdminInfo boardAdminInfo = this.boardAuthorizationService.deleteBoardAdminAuth(boardAdminInfoDelete);

        List<AccountInfo> accountInfoList = boardAdminInfo.getAccountInfoList();
        Assertions.assertNotNull(accountInfoList);

        List<String> accountInfoIdList = accountInfoList.stream()
                .map(AccountInfo::getId)
                .collect(Collectors.toList());
        Assertions.assertFalse(accountInfoIdList.contains(accountId));
    }

    @Test
    @DisplayName("계정을 설정하지 않음")
    public void notExistAccountId() throws Exception {
        String boardId = getBoardId(testBoardInfo01);

        BoardAdminInfoDelete boardAdminInfoDelete = BoardAdminInfoDelete.builder()
                .boardId(boardId)
                .accountId(null)
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            this.boardAuthorizationService.deleteBoardAdminAuth(boardAdminInfoDelete);
        });
    }

    @Test
    @DisplayName("계정에 빈 문자열")
    public void emptyAccountId() throws Exception {
        String boardId = getBoardId(testBoardInfo01);

        BoardAdminInfoDelete boardAdminInfoDelete = BoardAdminInfoDelete.builder()
                .boardId(boardId)
                .accountId("")
                .build();

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.boardAuthorizationService.deleteBoardAdminAuth(boardAdminInfoDelete);
        });

        Assertions.assertEquals("02000000", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist account.", iricomException.getMessage());
    }

    @Test
    @DisplayName("게시판을 설정하지 않음")
    public void notExistBoardId() {
        String accountId = getAccountId(common01);

        BoardAdminInfoDelete boardAdminInfoDelete = BoardAdminInfoDelete.builder()
                .boardId(null)
                .accountId(accountId)
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            this.boardAuthorizationService.deleteBoardAdminAuth(boardAdminInfoDelete);
        });
    }

    @Test
    @DisplayName("게시판에 빈 문자열")
    public void emptyBoardId() {
        String accountId = getAccountId(common01);

        BoardAdminInfoDelete boardAdminInfoDelete = BoardAdminInfoDelete.builder()
                .boardId("")
                .accountId(accountId)
                .build();

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            this.boardAuthorizationService.deleteBoardAdminAuth(boardAdminInfoDelete);
        });

        Assertions.assertEquals("03000000", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist board.", iricomException.getMessage());
    }
}

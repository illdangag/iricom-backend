package com.illdangag.iricom.server.service.board.authorization;

import com.illdangag.iricom.server.data.request.BoardAdminInfoDelete;
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
import javax.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.List;

@DisplayName("service: 게시판 관리자 - 삭제")
@Transactional
public class BoardAuthorizationServiceDeleteTest extends IricomTestSuite {
    @Autowired
    private BoardAuthorizationService boardAuthorizationService;

    @Autowired
    public BoardAuthorizationServiceDeleteTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("게시판 관리자 삭제")
    public void deleteBoardAdmin() throws Exception {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard(Arrays.asList(account));

        // 게시판 관리자 삭제
        BoardAdminInfoDelete boardAdminInfoDelete = BoardAdminInfoDelete.builder()
                .boardId(board.getId())
                .accountId(account.getId())
                .build();
        BoardAdminInfo boardAdminInfo = this.boardAuthorizationService.deleteBoardAdminAuth(boardAdminInfoDelete);

        List<AccountInfo> accountInfoList = boardAdminInfo.getAccountInfoList();
        Assertions.assertNotNull(accountInfoList);
        Assertions.assertTrue(accountInfoList.isEmpty());
    }

    @Test
    @DisplayName("게시판 관리자로 등록되지 않은 계정")
    public void notExistBoardAdmin() throws Exception {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        TestAccountInfo otherAccount = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard(Arrays.asList(account));

        // 게시판 관리자 삭제
        BoardAdminInfoDelete boardAdminInfoDelete = BoardAdminInfoDelete.builder()
                .boardId(board.getId())
                .accountId(otherAccount.getId())
                .build();

        BoardAdminInfo boardAdminInfo = this.boardAuthorizationService.deleteBoardAdminAuth(boardAdminInfoDelete);

        // 원래 등록 되어 있던 관리자만 존재하는지 확인
        List<AccountInfo> accountInfoList = boardAdminInfo.getAccountInfoList();
        Assertions.assertNotNull(accountInfoList);
        Assertions.assertEquals(1, accountInfoList.size());
        AccountInfo boardAdminAccount = accountInfoList.get(0);
        Assertions.assertEquals(account.getId(), boardAdminAccount.getId());
    }

    @Test
    @DisplayName("계정을 설정하지 않음")
    public void notExistAccountId() throws Exception {
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();

        BoardAdminInfoDelete boardAdminInfoDelete = BoardAdminInfoDelete.builder()
                .boardId(board.getId())
                .accountId(null)
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            this.boardAuthorizationService.deleteBoardAdminAuth(boardAdminInfoDelete);
        });
    }

    @Test
    @DisplayName("계정에 빈 문자열")
    public void emptyAccountId() throws Exception {
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();

        BoardAdminInfoDelete boardAdminInfoDelete = BoardAdminInfoDelete.builder()
                .boardId(board.getId())
                .accountId("")
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            this.boardAuthorizationService.deleteBoardAdminAuth(boardAdminInfoDelete);
        });
    }

    @Test
    @DisplayName("게시판을 설정하지 않음")
    public void notExistBoardId() {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();

        BoardAdminInfoDelete boardAdminInfoDelete = BoardAdminInfoDelete.builder()
                .boardId(null)
                .accountId(account.getId())
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            this.boardAuthorizationService.deleteBoardAdminAuth(boardAdminInfoDelete);
        });
    }

    @Test
    @DisplayName("게시판에 빈 문자열")
    public void emptyBoardId() {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();

        BoardAdminInfoDelete boardAdminInfoDelete = BoardAdminInfoDelete.builder()
                .boardId("")
                .accountId(account.getId())
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            this.boardAuthorizationService.deleteBoardAdminAuth(boardAdminInfoDelete);
        });
    }
}

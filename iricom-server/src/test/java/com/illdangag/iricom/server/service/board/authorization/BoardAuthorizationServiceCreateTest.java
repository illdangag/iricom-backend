package com.illdangag.iricom.server.service.board.authorization;

import com.illdangag.iricom.server.data.request.BoardAdminInfoCreate;
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
import java.util.List;

@DisplayName("service: 게시판 관리자 - 생성")
@Transactional
public class BoardAuthorizationServiceCreateTest extends IricomTestSuite {
    @Autowired
    private BoardAuthorizationService boardAuthorizationService;

    @Autowired
    public BoardAuthorizationServiceCreateTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("게시판 관리자 생성")
    public void createBoardAdmin() throws Exception {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();

        // 게시판 관리자 생성
        BoardAdminInfoCreate boardAdminInfoCreate = BoardAdminInfoCreate.builder()
                .boardId(board.getId())
                .accountId(account.getId())
                .build();
        BoardAdminInfo boardAdminInfo = this.boardAuthorizationService.createBoardAdminAuth(boardAdminInfoCreate);
        List<AccountInfo> boardAdminInfoList = boardAdminInfo.getAccountInfoList();
        Assertions.assertNotNull(boardAdminInfoList);
        Assertions.assertEquals(1, boardAdminInfoList.size());
    }

    @Test
    @DisplayName("이미 관리자로 추가된 게시판에 관리자로 추가")
    public void duplicateCreateBoardAdmin() throws Exception {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();

        // 게시판 관리자 생성
        BoardAdminInfoCreate boardAdminInfoCreate = BoardAdminInfoCreate.builder()
                .boardId(board.getId())
                .accountId(account.getId())
                .build();
        this.boardAuthorizationService.createBoardAdminAuth(boardAdminInfoCreate);

        // 다시 게시판 관리자로 추가
        BoardAdminInfo boardAdminInfo = this.boardAuthorizationService.createBoardAdminAuth(boardAdminInfoCreate);
        List<AccountInfo> boardAdminInfoList = boardAdminInfo.getAccountInfoList();
        Assertions.assertNotNull(boardAdminInfoList);
        Assertions.assertEquals(1, boardAdminInfoList.size());
    }

    @Test
    @DisplayName("계정을 설정하지 않음")
    public void notExistAccountId() throws Exception {
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();

        // 게시판 관리자 생성
        BoardAdminInfoCreate boardAdminInfoCreate = BoardAdminInfoCreate.builder()
                .boardId(board.getId())
                .accountId(null)
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            this.boardAuthorizationService.createBoardAdminAuth(boardAdminInfoCreate);
        });
    }

    @Test
    @DisplayName("계정에 빈 문자열")
    public void emptyAccountId() throws Exception {
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();

        BoardAdminInfoCreate boardAdminInfoCreate = BoardAdminInfoCreate.builder()
                .boardId(board.getId())
                .accountId("")
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            this.boardAuthorizationService.createBoardAdminAuth(boardAdminInfoCreate);
        });
    }

    @Test
    @DisplayName("게시판을 설정하지 않음")
    public void notExistBoardId() {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();

        BoardAdminInfoCreate boardAdminInfoCreate = BoardAdminInfoCreate.builder()
                .boardId(null)
                .accountId(account.getId())
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            this.boardAuthorizationService.createBoardAdminAuth(boardAdminInfoCreate);
        });
    }

    @Test
    @DisplayName("게시판에 빈 문자열")
    public void emptyBoardId() {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();

        BoardAdminInfoCreate boardAdminInfoCreate = BoardAdminInfoCreate.builder()
                .boardId("")
                .accountId(account.getId())
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            this.boardAuthorizationService.createBoardAdminAuth(boardAdminInfoCreate);
        });
    }
}

package com.illdangag.iricom.server.service.account.group;

import com.illdangag.iricom.server.data.request.AccountGroupInfoCreate;
import com.illdangag.iricom.server.data.response.AccountGroupInfo;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.service.AccountGroupService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.Collections;

@DisplayName("service: 계정 그룹 - 생성")
@Slf4j
@Transactional
public class AccountGroupServiceCreateTest extends IricomTestSuite {
    @Autowired
    private AccountGroupService accountGroupService;

    public AccountGroupServiceCreateTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("기본 생성")
    public void createEmptyAccountGroup() throws Exception {
        String title = "New empty account group";
        String description = "description";

        AccountGroupInfoCreate accountGroupInfoCreate = AccountGroupInfoCreate.builder()
                .title(title)
                .description(description)
                .build();

        AccountGroupInfo accountGroupInfo = accountGroupService.createAccountGroupInfo(accountGroupInfoCreate);

        Assertions.assertNotNull(accountGroupInfo);
        Assertions.assertEquals(title, accountGroupInfo.getTitle());
        Assertions.assertEquals(description, accountGroupInfo.getDescription());
        Assertions.assertTrue(accountGroupInfo.getAccountInfoList().isEmpty());
        Assertions.assertTrue(accountGroupInfo.getBoardInfoList().isEmpty());
    }

    @Test
    @DisplayName("계정 추가")
    public void createAccountGroupWithAccount() throws Exception {
        // 계정 생성
        TestAccountInfo testAccount = this.setRandomAccount(1).get(0);

        String title = "New account group with account";
        String description = "description";
        String accountId = testAccount.getId();

        AccountGroupInfoCreate accountGroupInfoCreate = AccountGroupInfoCreate.builder()
                .title(title)
                .description(description)
                .accountIdList(Collections.singletonList(accountId))
                .build();

        AccountGroupInfo accountGroupInfo = accountGroupService.createAccountGroupInfo(accountGroupInfoCreate);

        Assertions.assertNotNull(accountGroupInfo);
        Assertions.assertEquals(title, accountGroupInfo.getTitle());
        Assertions.assertEquals(description, accountGroupInfo.getDescription());
        Assertions.assertEquals(1, accountGroupInfo.getAccountInfoList().size());
        Assertions.assertTrue(accountGroupInfo.getBoardInfoList().isEmpty());
    }

    @Test
    @DisplayName("게시판 추가")
    public void createAccountGroupWithBoard() throws Exception {
        // 게시판 관리자 생성
        TestAccountInfo boardAdminAccount = TestAccountInfo
                .builder()
                .email("accountGroup01@iricom.com")
                .nickname("accountGroup01")
                .description("this is accountGroup01.")
                .build();
        this.setAccount(boardAdminAccount);

        // 게시판 생성
        TestBoardInfo testBoard = TestBoardInfo.builder()
                .title("testBoardInfo00")
                .isEnabled(true)
                .adminList(Collections.singletonList(boardAdminAccount))
                .build();
        this.setBoard(testBoard);

        String title = "New account group with board";
        String description = "description";
        String boardId = testBoard.getId();

        AccountGroupInfoCreate accountGroupInfoCreate = AccountGroupInfoCreate.builder()
                .title(title)
                .description(description)
                .boardIdList(Arrays.asList(boardId))
                .build();

        AccountGroupInfo accountGroupInfo = accountGroupService.createAccountGroupInfo(accountGroupInfoCreate);

        Assertions.assertNotNull(accountGroupInfo);
        Assertions.assertEquals(title, accountGroupInfo.getTitle());
        Assertions.assertEquals(description, accountGroupInfo.getDescription());
        Assertions.assertTrue(accountGroupInfo.getAccountInfoList().isEmpty());
        Assertions.assertEquals(1, accountGroupInfo.getBoardInfoList().size());
    }

    @Test
    @DisplayName("계정과 게시판 추가")
    public void createAccountGroupWithAccountBoard() throws Exception {
        // 게시판 관리자 생성
        TestAccountInfo account = TestAccountInfo
                .builder()
                .email("accountGroup02@iricom.com")
                .nickname("accountGroup02")
                .description("this is accountGroup02.")
                .build();
        this.setAccount(account);

        // 게시판 생성
        TestBoardInfo board = TestBoardInfo.builder()
                .title("testBoardInfo00")
                .isEnabled(true)
                .build();
        this.setBoard(board);

        String title = "New account group";
        String description = "description";
        String accountId = account.getId();
        String boardId = board.getId();

        AccountGroupInfoCreate accountGroupInfoCreate = AccountGroupInfoCreate.builder()
                .title(title)
                .description(description)
                .boardIdList(Arrays.asList(boardId))
                .accountIdList(Arrays.asList(accountId))
                .build();

        AccountGroupInfo accountGroupInfo = accountGroupService.createAccountGroupInfo(accountGroupInfoCreate);

        Assertions.assertNotNull(accountGroupInfo);
        Assertions.assertEquals(title, accountGroupInfo.getTitle());
        Assertions.assertEquals(description, accountGroupInfo.getDescription());
        Assertions.assertEquals(1, accountGroupInfo.getAccountInfoList().size());
        Assertions.assertEquals(1, accountGroupInfo.getBoardInfoList().size());
    }

    @Test
    @DisplayName("제목을 입력하지 않음")
    public void createNullTitle() throws Exception {
        AccountGroupInfoCreate accountGroupInfoCreate = AccountGroupInfoCreate.builder()
                .title(null)
                .description("description")
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            accountGroupService.createAccountGroupInfo(accountGroupInfoCreate);
        });
    }

    @Test
    @DisplayName("제목에 빈 문자열을 입력")
    public void createEmptyTitle() throws Exception {
        AccountGroupInfoCreate accountGroupInfoCreate = AccountGroupInfoCreate.builder()
                .title("")
                .description("description")
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            accountGroupService.createAccountGroupInfo(accountGroupInfoCreate);
        });
    }

    @Test
    @DisplayName("존재하지 않는 계정을 추가")
    public void createNotExistAccount() throws Exception {
        AccountGroupInfoCreate accountGroupInfoCreate = AccountGroupInfoCreate.builder()
                .title("not exist account")
                .description("description")
                .accountIdList(Arrays.asList("NOT_EXIST_ACCOUNT"))
                .build();

        Assertions.assertThrows(IricomException.class, () -> {
            accountGroupService.createAccountGroupInfo(accountGroupInfoCreate);
        });
    }

    @Test
    @DisplayName("존재하지 않는 게시판을 추가")
    public void createNotExistBoard() throws Exception {
        AccountGroupInfoCreate accountGroupInfoCreate = AccountGroupInfoCreate.builder()
                .title("not exist account")
                .description("description")
                .boardIdList(Arrays.asList("NOT_EXIST_BOARD"))
                .build();

        Assertions.assertThrows(IricomException.class, () -> {
            accountGroupService.createAccountGroupInfo(accountGroupInfoCreate);
        });
    }
}

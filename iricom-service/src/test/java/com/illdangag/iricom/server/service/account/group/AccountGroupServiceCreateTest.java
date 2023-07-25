package com.illdangag.iricom.server.service.account.group;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.request.AccountGroupInfoCreate;
import com.illdangag.iricom.server.data.response.AccountGroupInfo;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.service.AccountGroupService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.Collections;

@DisplayName("service: 계정 그룹 - 생성")
@Slf4j
public class AccountGroupServiceCreateTest extends IricomTestSuite {
    @Autowired
    private AccountGroupService accountGroupService;

    private final TestBoardInfo testBoardInfo00 = TestBoardInfo.builder()
            .title("testBoardInfo00").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();

    public AccountGroupServiceCreateTest(ApplicationContext context) {
        super(context);

        addTestBoardInfo(testBoardInfo00);
        init();
    }

    @Test
    @DisplayName("기본")
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
        String title = "New account group with account";
        String description = "description";

        Account account = getAccount(common00);

        AccountGroupInfoCreate accountGroupInfoCreate = AccountGroupInfoCreate.builder()
                .title(title)
                .description(description)
                .accountIdList(Arrays.asList(String.valueOf(account.getId())))
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
        String title = "New account group with board";
        String description = "description";

        String boardId = getBoardId(testBoardInfo00);

        AccountGroupInfoCreate accountGroupInfoCreate = AccountGroupInfoCreate.builder()
                .title(title)
                .description(description)
                .boardIdList(Arrays.asList(String.valueOf(boardId)))
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
        String title = "New account group";
        String description = "description";

        Account account = getAccount(common00);
        String boardId = getBoardId(testBoardInfo00);

        AccountGroupInfoCreate accountGroupInfoCreate = AccountGroupInfoCreate.builder()
                .title(title)
                .description(description)
                .boardIdList(Arrays.asList(String.valueOf(boardId)))
                .accountIdList(Arrays.asList(String.valueOf(account.getId())))
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
    @DisplayName("제목을 빈 문자열을 입력")
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

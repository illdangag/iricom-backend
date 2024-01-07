package com.illdangag.iricom.server.service.account.group;

import com.illdangag.iricom.server.data.response.AccountGroupInfo;
import com.illdangag.iricom.server.data.response.AccountInfo;
import com.illdangag.iricom.server.data.response.BoardInfo;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.service.AccountGroupService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestAccountGroupInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.Collections;

@DisplayName("service: 계정 그룹 - 조회")
@Slf4j
public class AccountGroupServiceGetTest extends IricomTestSuite {
    @Autowired
    private AccountGroupService accountGroupService;

    private final TestBoardInfo testBoardInfo00 = TestBoardInfo.builder()
            .title("testBoardInfo00").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();
    private final TestBoardInfo testBoardInfo01 = TestBoardInfo.builder()
            .title("testBoardInfo01").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();

    private final TestAccountGroupInfo testAccountGroupInfo00 = TestAccountGroupInfo.builder()
            .title("testAccountGroupInfo00").description("description")
            .accountList(Arrays.asList(common00, common01)).boardList(Arrays.asList(testBoardInfo00, testBoardInfo01)).build();

    public AccountGroupServiceGetTest(ApplicationContext context) {
        super(context);

        addTestBoardInfo(testBoardInfo00, testBoardInfo01);
        addTestAccountGroupInfo(testAccountGroupInfo00);

        init();
    }

    @Test
    @DisplayName("그룹 정보 조회")
    public void get() {
        AccountGroupInfo testAccountGroup = getAccountGroup(testAccountGroupInfo00);
        String accountGroupId = testAccountGroup.getId();

        String[] accountIds = testAccountGroupInfo00.getAccountList().stream()
                .map(AccountGroupServiceGetTest.this::getAccount)
                .map(item -> String.valueOf(item.getId()))
                .toArray(String[]::new);
        String[] boardIds = testAccountGroupInfo00.getBoardList().stream()
                .map(AccountGroupServiceGetTest.this::getBoardId)
                .toArray(String[]::new);

        AccountGroupInfo accountGroupInfo = accountGroupService.getAccountGroupInfo(accountGroupId);

        Assertions.assertNotNull(accountGroupInfo);
        Assertions.assertEquals(testAccountGroup.getTitle(), accountGroupInfo.getTitle());
        Assertions.assertEquals(testAccountGroup.getDescription(), accountGroupInfo.getDescription());

        String[] accountInfoIds = accountGroupInfo.getAccountInfoList().stream()
                .map(AccountInfo::getId)
                .toArray(String[]::new);
        String[] boardInfoIds = accountGroupInfo.getBoardInfoList().stream()
                .map(BoardInfo::getId)
                .toArray(String[]::new);

        Assertions.assertArrayEquals(accountIds, accountInfoIds);
        Assertions.assertArrayEquals(boardIds, boardInfoIds);
    }

    @Test
    @DisplayName("존재하지 않는 그룹 조회")
    public void getNotExistAccountGroup() throws Exception {
        String accountGroupId = "NOT_EXIST_ACCOUNT_GROUP";

        Assertions.assertThrows(IricomException.class, () -> {
            accountGroupService.getAccountGroupInfo(accountGroupId);
        });
    }
}

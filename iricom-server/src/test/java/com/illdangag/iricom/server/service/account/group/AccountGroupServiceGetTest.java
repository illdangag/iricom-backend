package com.illdangag.iricom.server.service.account.group;

import com.illdangag.iricom.server.data.response.AccountGroupInfo;
import com.illdangag.iricom.server.data.response.AccountInfo;
import com.illdangag.iricom.server.data.response.BoardInfo;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.service.AccountGroupService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestAccountGroupInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.transaction.Transactional;
import java.util.List;

@DisplayName("service: 계정 그룹 - 조회")
@Transactional
public class AccountGroupServiceGetTest extends IricomTestSuite {
    @Autowired
    private AccountGroupService accountGroupService;

    public AccountGroupServiceGetTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("그룹 정보 조회")
    void getAccountGroup() {
        // 계정 그룹 생성
        TestAccountGroupInfo accountGroup = TestAccountGroupInfo.builder()
                .title("title")
                .description("description")
                .build();
        this.setAccountGroup(accountGroup);

        AccountGroupInfo accountGroupInfo = accountGroupService.getAccountGroupInfo(accountGroup.getId());

        Assertions.assertNotNull(accountGroupInfo);
        Assertions.assertEquals(accountGroup.getId(), accountGroupInfo.getId());
    }

    @Test
    @DisplayName("그룹에 추가된 게시판 및 멤버 목록 조회")
    void getAddedAccountAndBoardAccountGroup() {
        // 계정 생성
        List<TestAccountInfo> accountList = setRandomAccount(5);
        String[] accountIds = accountList.stream()
                .map(TestAccountInfo::getId)
                .sorted()
                .toArray(String[]::new);

        // 게시판 생성
        List<TestBoardInfo> boardList = setRandomBoard(5);
        String[] boardIds = boardList.stream()
                .map(TestBoardInfo::getId)
                .sorted()
                .toArray(String[]::new);

        // 계정 그룹 생성
        TestAccountGroupInfo accountGroup = TestAccountGroupInfo.builder()
                .title("title")
                .description("description")
                .accountList(accountList)
                .boardList(boardList)
                .build();
        this.setAccountGroup(accountGroup);

        AccountGroupInfo accountGroupInfo = accountGroupService.getAccountGroupInfo(accountGroup.getId());
        String[] groupAccountIds = accountGroupInfo.getAccountInfoList().stream()
                .map(AccountInfo::getId)
                .sorted()
                .toArray(String[]::new);
        String[] groupBoardIds = accountGroupInfo.getBoardInfoList().stream()
                .map(BoardInfo::getId)
                .sorted()
                .toArray(String[]::new);

        Assertions.assertNotNull(accountGroupInfo);
        Assertions.assertArrayEquals(accountIds, groupAccountIds);
        Assertions.assertArrayEquals(boardIds, groupBoardIds);
    }

    @Test
    @DisplayName("존재하지 않는 그룹 조회")
    void getNotExistAccountGroup() throws Exception {
        List<TestAccountGroupInfo> accountGroupInfoList = setRandomAccountGroup(5);

        // 생성한 그룹은 조회 가능
        Assertions.assertDoesNotThrow(() -> {
            accountGroupService.getAccountGroupInfo(accountGroupInfoList.get(0).getId());
        });

        // 존재하지 않는 ID로는 조회 불가능
        String accountGroupId = "NOT_EXIST_ACCOUNT_GROUP";
        Assertions.assertThrows(IricomException.class, () -> {
            accountGroupService.getAccountGroupInfo(accountGroupId);
        });
    }
}

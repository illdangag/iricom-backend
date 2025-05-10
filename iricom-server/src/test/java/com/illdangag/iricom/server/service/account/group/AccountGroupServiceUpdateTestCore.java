package com.illdangag.iricom.server.service.account.group;

import com.illdangag.iricom.core.data.request.AccountGroupInfoUpdate;
import com.illdangag.iricom.core.data.response.AccountGroupInfo;
import com.illdangag.iricom.core.data.response.AccountInfo;
import com.illdangag.iricom.core.data.response.BoardInfo;
import com.illdangag.iricom.core.exception.IricomException;
import com.illdangag.iricom.core.service.AccountGroupService;
import com.illdangag.iricom.core.test.data.wrapper.TestAccountGroupInfo;
import com.illdangag.iricom.core.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.core.test.data.wrapper.TestBoardInfo;
import com.illdangag.iricom.server.IricomTestServiceSuite;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@DisplayName("service: 계정 그룹 - 수정")
@Transactional
public class AccountGroupServiceUpdateTestCore extends IricomTestServiceSuite {
    @Autowired
    private AccountGroupService accountGroupService;

    public AccountGroupServiceUpdateTestCore(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("제목, 설명")
    public void updateTitleDescription() throws Exception {
        // 계정 그룹 생성
        TestAccountGroupInfo testAccountGroupInfo = setRandomAccountGroup(1).get(0);
        String accountGroupId = testAccountGroupInfo.getId();

        String title = "Update account group";
        String description = "update description";

        AccountGroupInfoUpdate accountGroupInfoUpdate = AccountGroupInfoUpdate.builder()
                .title(title)
                .description(description)
                .build();

        AccountGroupInfo accountGroupInfo = accountGroupService.updateAccountGroupInfo(accountGroupId, accountGroupInfoUpdate);

        Assertions.assertNotNull(accountGroupInfo);
        Assertions.assertEquals(title, accountGroupInfo.getTitle());
        Assertions.assertEquals(description, accountGroupInfo.getDescription());
    }

    @Test
    @DisplayName("계정이 등록되지 않은 그룹에 계정 추가")
    public void updateAccountEmptyAccountGroup() throws Exception {
        // 계정 그룹 생성
        TestAccountGroupInfo accountGroup = setRandomAccountGroup(1).get(0);
        String accountGroupId = accountGroup.getId();

        // 계정 생성
        TestAccountInfo account = setRandomAccount(1).get(0);
        String accountId = account.getId();

        AccountGroupInfoUpdate accountGroupInfoUpdate = AccountGroupInfoUpdate.builder()
                .accountIdList(Collections.singletonList(accountId))
                .build();

        AccountGroupInfo accountGroupInfo = accountGroupService.updateAccountGroupInfo(accountGroupId, accountGroupInfoUpdate);
        List<AccountInfo> accountInfoList = accountGroupInfo.getAccountInfoList();

        Assertions.assertEquals(1, accountInfoList.size());
        Assertions.assertEquals(accountId, accountInfoList.get(0).getId());
    }

    @Test
    @DisplayName("게시판이 등록되지 않은 그룹에 게시판 추가")
    public void updateBoardEmptyAccountGroup() throws Exception {
        // 계정 그룹 생성
        TestAccountGroupInfo accountGroup = setRandomAccountGroup(1).get(0);
        String accountGroupId = accountGroup.getId();

        // 게시판 생성
        TestBoardInfo board = setRandomBoard(1).get(0);
        String boardId = board.getId();

        AccountGroupInfoUpdate accountGroupInfoUpdate = AccountGroupInfoUpdate.builder()
                .boardIdList(Arrays.asList(String.valueOf(boardId)))
                .build();

        AccountGroupInfo accountGroupInfo = accountGroupService.updateAccountGroupInfo(accountGroupId, accountGroupInfoUpdate);
        List<BoardInfo> boardInfoList = accountGroupInfo.getBoardInfoList();

        Assertions.assertEquals(1, boardInfoList.size());
        Assertions.assertEquals(String.valueOf(boardId), boardInfoList.get(0).getId());
    }

    @Test
    @DisplayName("계정이 등록된 그룹에 다른 계정 목록으로 수정")
    public void updateAccountAlreadyAccountGroup() throws Exception {
        // 계정 생성
        List<TestAccountInfo> preAccountList = setRandomAccount(2);
        List<String> preAccountIdList = preAccountList.stream()
                .map(TestAccountInfo::getId)
                .sorted()
                .collect(Collectors.toList());
        List<TestAccountInfo> postAccountList = setRandomAccount(3);
        List<String> postAccountIdList = postAccountList.stream()
                .map(TestAccountInfo::getId)
                .sorted()
                .collect(Collectors.toList());

        // 계정 그룹 생성
        TestAccountGroupInfo accountGroup = TestAccountGroupInfo.builder()
                .title("title")
                .description("description")
                .accountList(preAccountList)
                .build();
        this.setAccountGroup(accountGroup);
        String accountGroupId = accountGroup.getId();

        // 수정 전 조회
        AccountGroupInfo accountGroupInfo = this.accountGroupService.getAccountGroupInfo(accountGroupId);
        List<String> accountIdList = accountGroupInfo.getAccountInfoList().stream()
                .map(AccountInfo::getId)
                .sorted()
                .collect(Collectors.toList());
        Assertions.assertArrayEquals(preAccountIdList.toArray(), accountIdList.toArray());

        // 수정
        AccountGroupInfoUpdate update = AccountGroupInfoUpdate.builder()
                .accountIdList(postAccountIdList)
                .build();
        AccountGroupInfo updatedAccountGroupInfo = this.accountGroupService.updateAccountGroupInfo(accountGroupId, update);
        List<String> updatedAccountIdList = updatedAccountGroupInfo.getAccountInfoList().stream()
                .map(AccountInfo::getId)
                .sorted()
                        .collect(Collectors.toList());
        Assertions.assertArrayEquals(postAccountIdList.toArray(), updatedAccountIdList.toArray());

        // 다시 조회
        AccountGroupInfo getAccountGroupInfo = this.accountGroupService.getAccountGroupInfo(accountGroupId);
        List<String> getAccountIdList = getAccountGroupInfo.getAccountInfoList().stream()
                .map(AccountInfo::getId)
                .sorted()
                .collect(Collectors.toList());
        Assertions.assertArrayEquals(postAccountIdList.toArray(), getAccountIdList.toArray());
    }

    @Test
    @DisplayName("게시판이 등록된 그룹에 다른 게시판 목록으로 수정")
    public void updateBoardAlreadyAccountGroup() throws Exception {
        // 게시판 생성
        List<TestBoardInfo> preBoardList = setRandomBoard(2);
        List<String> preBoardIdList = preBoardList.stream()
                .map(TestBoardInfo::getId)
                .sorted()
                .collect(Collectors.toList());
        List<TestBoardInfo> postBoardList = setRandomBoard(3);
        List<String> postBoardIdList = postBoardList.stream()
                .map(TestBoardInfo::getId)
                .sorted()
                .collect(Collectors.toList());

        // 계정 그룹 생성
        TestAccountGroupInfo accountGroup = TestAccountGroupInfo.builder()
                .title("title")
                .description("description")
                .boardList(preBoardList)
                .build();
        this.setAccountGroup(accountGroup);
        String accountGroupId = accountGroup.getId();

        // 수정 전 조회
        AccountGroupInfo accountGroupInfo = this.accountGroupService.getAccountGroupInfo(accountGroupId);
        List<String> boardIdList = accountGroupInfo.getBoardInfoList().stream()
                .map(BoardInfo::getId)
                .sorted()
                .collect(Collectors.toList());
        Assertions.assertArrayEquals(preBoardIdList.toArray(), boardIdList.toArray());

        AccountGroupInfoUpdate update = AccountGroupInfoUpdate.builder()
                .boardIdList(postBoardIdList)
                .build();

        // 수정
        AccountGroupInfo updatedAccountGroupInfo = this.accountGroupService.updateAccountGroupInfo(accountGroupId, update);
        List<String> updatedBoardIdList = updatedAccountGroupInfo.getBoardInfoList().stream()
                .map(BoardInfo::getId)
                .sorted()
                .collect(Collectors.toList());
        Assertions.assertArrayEquals(postBoardIdList.toArray(), updatedBoardIdList.toArray());

        // 수정 후 조회
        AccountGroupInfo getAccountGroupInfo = this.accountGroupService.getAccountGroupInfo(accountGroupId);
        List<String> getBoardIdList = getAccountGroupInfo.getBoardInfoList().stream()
                .map(BoardInfo::getId)
                .sorted()
                .collect(Collectors.toList());
        Assertions.assertArrayEquals(postBoardIdList.toArray(), getBoardIdList.toArray());
    }

    @Test
    @DisplayName("존재하지 않는 계정 그룹")
    public void updateNotExistAccountGroup() throws Exception {
        setRandomAccountGroup(5);

        String accountGroupId = "NOT_EXIST_ACCOUNT_GROUP";

        AccountGroupInfoUpdate accountGroupInfoUpdate = AccountGroupInfoUpdate.builder()
                .title("update")
                .description("description")
                .build();

        Assertions.assertThrows(IricomException.class, () -> {
            accountGroupService.updateAccountGroupInfo(accountGroupId, accountGroupInfoUpdate);
        });
    }

    @Test
    @DisplayName("존재하지 않는 계정을 추가")
    public void updateNotExistAccount() throws Exception {
        TestAccountGroupInfo accountGroup = setRandomAccountGroup(1).get(0);
        String accountGroupId = accountGroup.getId();

        AccountGroupInfoUpdate accountGroupInfoUpdate = AccountGroupInfoUpdate.builder()
                .accountIdList(Arrays.asList("NOT_EXIST_ACCOUNT"))
                .build();

        Assertions.assertThrows(IricomException.class, () -> {
            accountGroupService.updateAccountGroupInfo(accountGroupId, accountGroupInfoUpdate);
        });
    }

    @Test
    @DisplayName("존재하지 않는 게시판을 추가")
    public void updateNotExistBoard() throws Exception {
        TestAccountGroupInfo accountGroup = setRandomAccountGroup(1).get(0);
        String accountGroupId = accountGroup.getId();

        AccountGroupInfoUpdate accountGroupInfoUpdate = AccountGroupInfoUpdate.builder()
                .boardIdList(Arrays.asList("NOT_EXIST_BOARD"))
                .build();

        Assertions.assertThrows(IricomException.class, () -> {
            accountGroupService.updateAccountGroupInfo(accountGroupId, accountGroupInfoUpdate);
        });
    }
}

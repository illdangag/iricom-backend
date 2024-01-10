package com.illdangag.iricom.server.service.account.group;

import com.illdangag.iricom.server.data.request.AccountGroupInfoUpdate;
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

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@DisplayName("service: 계정 그룹 - 수정")
@Slf4j
@Transactional
public class AccountGroupServiceUpdateTest extends IricomTestSuite {
    @Autowired
    private AccountGroupService accountGroupService;

    private final TestBoardInfo testBoardInfo00 = TestBoardInfo.builder()
            .title("testBoardInfo00").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();
    private final TestBoardInfo testBoardInfo01 = TestBoardInfo.builder()
            .title("testBoardInfo01").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();
    private final TestBoardInfo testBoardInfo02 = TestBoardInfo.builder()
            .title("testBoardInfo02").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();

    private final TestAccountGroupInfo testAccountGroupInfo00 = TestAccountGroupInfo.builder()
            .title("testAccountGroupInfo00").description("description").build();
    private final TestAccountGroupInfo testAccountGroupInfo01 = TestAccountGroupInfo.builder()
            .title("testAccountGroupInfo01").description("description").build();
    private final TestAccountGroupInfo testAccountGroupInfo02 = TestAccountGroupInfo.builder()
            .title("testAccountGroupInfo02").description("description")
            .accountList(Arrays.asList(common00, common01)).boardList(Arrays.asList(testBoardInfo00, testBoardInfo01)).build();
    private final TestAccountGroupInfo testAccountGroupInfo03 = TestAccountGroupInfo.builder()
            .title("testAccountGroupInfo03").description("description").build();

    public AccountGroupServiceUpdateTest(ApplicationContext context) {
        super(context);

        addTestBoardInfo(testBoardInfo00, testBoardInfo01, testBoardInfo02);
        addTestAccountGroupInfo(testAccountGroupInfo00, testAccountGroupInfo01, testAccountGroupInfo02, testAccountGroupInfo03);

        init();
    }

    @Test
    @DisplayName("제목, 설명")
    public void updateTitleDescription() throws Exception {
        AccountGroupInfo testAccountGroup = getAccountGroup(testAccountGroupInfo00);
        String accountGroupId = testAccountGroup.getId();

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
        AccountGroupInfo testAccountGroup = getAccountGroup(testAccountGroupInfo01);
        String accountGroupId = testAccountGroup.getId();

        String accountId = getAccountId(common00);

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
        AccountGroupInfo testAccountGroup = getAccountGroup(testAccountGroupInfo01);
        String accountGroupId = testAccountGroup.getId();

        String boardId = getBoardId(testBoardInfo00);

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
        AccountGroupInfo testAccountGroup = getAccountGroup(testAccountGroupInfo02);
        String accountGroupId = testAccountGroup.getId();

        String accountId00 = getAccountId(testAccountGroupInfo02.getAccountList().get(0));
        String accountId01 = getAccountId(common02);

        String boardId00 = getBoardId(testAccountGroupInfo02.getBoardList().get(0));
        String boardId01 = getBoardId(testAccountGroupInfo02.getBoardList().get(1));

        AccountGroupInfoUpdate accountGroupInfoUpdate = AccountGroupInfoUpdate.builder()
                .accountIdList(Arrays.asList(accountId00, accountId01))
                .build();

        AccountGroupInfo accountGroupInfo = accountGroupService.updateAccountGroupInfo(accountGroupId, accountGroupInfoUpdate);

        List<AccountInfo> accountInfoList = accountGroupInfo.getAccountInfoList();
        Assertions.assertEquals(2, accountInfoList.size());
        AccountInfo accountInfo00 = accountInfoList.get(0);
        AccountInfo accountInfo01 = accountInfoList.get(1);
        Assertions.assertEquals(accountId00, accountInfo00.getId());
        Assertions.assertEquals(accountId01, accountInfo01.getId());

        List<BoardInfo> boardInfoList = accountGroupInfo.getBoardInfoList();
        Assertions.assertEquals(2, boardInfoList.size());
        BoardInfo boardInfo00 = boardInfoList.get(0);
        BoardInfo boardInfo01 = boardInfoList.get(1);
        Assertions.assertEquals(boardId00, boardInfo00.getId());
        Assertions.assertEquals(boardId01, boardInfo01.getId());
    }

    @Test
    @DisplayName("게시판이 등록된 그룹에 다른 게시판 목록으로 수정")
    public void updateBoardAlreadyAccountGroup() throws Exception {
        AccountGroupInfo testAccountGroup = getAccountGroup(testAccountGroupInfo02);
        String accountGroupId = testAccountGroup.getId();

        String accountId00 = getAccountId(testAccountGroupInfo02.getAccountList().get(0));
        String accountId01 = getAccountId(testAccountGroupInfo02.getAccountList().get(1));

        String boardId00 = getBoardId(testAccountGroupInfo02.getBoardList().get(0));
        String boardId01 = getBoardId(testBoardInfo02);

        AccountGroupInfoUpdate accountGroupInfoUpdate = AccountGroupInfoUpdate.builder()
                .boardIdList(Arrays.asList(boardId00, boardId01))
                .build();

        AccountGroupInfo accountGroupInfo = accountGroupService.updateAccountGroupInfo(accountGroupId, accountGroupInfoUpdate);

        List<AccountInfo> accountInfoList = accountGroupInfo.getAccountInfoList();
        Assertions.assertEquals(2, accountInfoList.size());
        AccountInfo accountInfo00 = accountInfoList.get(0);
        AccountInfo accountInfo01 = accountInfoList.get(1);
        Assertions.assertEquals(accountId00, accountInfo00.getId());
        Assertions.assertEquals(accountId01, accountInfo01.getId());

        List<BoardInfo> boardInfoList = accountGroupInfo.getBoardInfoList();
        Assertions.assertEquals(2, boardInfoList.size());
        BoardInfo boardInfo00 = boardInfoList.get(0);
        BoardInfo boardInfo01 = boardInfoList.get(1);
        Assertions.assertEquals(boardId00, boardInfo00.getId());
        Assertions.assertEquals(boardId01, boardInfo01.getId());
    }

    @Test
    @DisplayName("존재하지 않는 계정 그룹")
    public void updateNotExistAccountGroup() throws Exception {
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
        AccountGroupInfo testAccountGroup = getAccountGroup(testAccountGroupInfo03);
        String accountGroupId = testAccountGroup.getId();

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
        AccountGroupInfo testAccountGroup = getAccountGroup(testAccountGroupInfo03);
        String accountGroupId = testAccountGroup.getId();

        AccountGroupInfoUpdate accountGroupInfoUpdate = AccountGroupInfoUpdate.builder()
                .boardIdList(Arrays.asList("NOT_EXIST_BOARD"))
                .build();

        Assertions.assertThrows(IricomException.class, () -> {
            accountGroupService.updateAccountGroupInfo(accountGroupId, accountGroupInfoUpdate);
        });
    }
}

package com.illdangag.iricom.server.service;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.AccountGroup;
import com.illdangag.iricom.server.data.entity.Board;
import com.illdangag.iricom.server.data.request.AccountGroupInfoCreate;
import com.illdangag.iricom.server.data.request.AccountGroupInfoUpdate;
import com.illdangag.iricom.server.data.response.AccountGroupInfo;
import com.illdangag.iricom.server.data.response.AccountInfo;
import com.illdangag.iricom.server.data.response.BoardInfo;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.TestAccountGroupInfo;
import com.illdangag.iricom.server.test.data.TestBoardInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@DisplayName("계정 그룹")
public class AccountGroupServiceTest extends IricomTestSuite {
    @Autowired
    private AccountGroupService accountGroupService;

    private TestBoardInfo testBoardInfo00 = TestBoardInfo.builder()
            .title("testBoardInfo00").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();
    private TestBoardInfo testBoardInfo01 = TestBoardInfo.builder()
            .title("testBoardInfo01").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();
    private TestBoardInfo testBoardInfo02 = TestBoardInfo.builder()
            .title("testBoardInfo02").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();

    private TestAccountGroupInfo testAccountGroupInfo00 = TestAccountGroupInfo.builder()
            .title("testAccountGroupInfo00").description("description").build();
    private TestAccountGroupInfo testAccountGroupInfo01 = TestAccountGroupInfo.builder()
            .title("testAccountGroupInfo01").description("description").build();
    private TestAccountGroupInfo testAccountGroupInfo02 = TestAccountGroupInfo.builder()
            .title("testAccountGroupInfo02").description("description")
            .accountList(Arrays.asList(common00, common01)).boardList(Arrays.asList(testBoardInfo00, testBoardInfo01)).build();
    private TestAccountGroupInfo testAccountGroupInfo03 = TestAccountGroupInfo.builder()
            .title("testAccountGroupInfo03").description("description")
            .accountList(Arrays.asList(common00, common01)).boardList(Arrays.asList(testBoardInfo00, testBoardInfo01)).build();

    @Autowired
    public AccountGroupServiceTest(ApplicationContext context) {
        super(context);

        super.setBoard(Arrays.asList(testBoardInfo00, testBoardInfo01, testBoardInfo02));
        super.setAccountGroup(Arrays.asList(testAccountGroupInfo00, testAccountGroupInfo01, testAccountGroupInfo02,
                testAccountGroupInfo03));
    }

    @Nested
    @DisplayName("생성")
    class Create {

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

            Board board = getBoard(testBoardInfo00);

            AccountGroupInfoCreate accountGroupInfoCreate = AccountGroupInfoCreate.builder()
                    .title(title)
                    .description(description)
                    .boardIdList(Arrays.asList(String.valueOf(board.getId())))
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
            Board board = getBoard(testBoardInfo00);

            AccountGroupInfoCreate accountGroupInfoCreate = AccountGroupInfoCreate.builder()
                    .title(title)
                    .description(description)
                    .boardIdList(Arrays.asList(String.valueOf(board.getId())))
                    .accountIdList(Arrays.asList(String.valueOf(account.getId())))
                    .build();

            AccountGroupInfo accountGroupInfo = accountGroupService.createAccountGroupInfo(accountGroupInfoCreate);

            Assertions.assertNotNull(accountGroupInfo);
            Assertions.assertEquals(title, accountGroupInfo.getTitle());
            Assertions.assertEquals(description, accountGroupInfo.getDescription());
            Assertions.assertEquals(1, accountGroupInfo.getAccountInfoList().size());
            Assertions.assertEquals(1, accountGroupInfo.getBoardInfoList().size());
        }
    }

    @Nested
    @DisplayName("수정")
    class Update {

        @Test
        @DisplayName("제목, 설명")
        public void updateTitleDescription() throws Exception {
            AccountGroup accountGroup = getAccountGroup(testAccountGroupInfo00);
            String accountGroupId = String.valueOf(accountGroup.getId());

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
            AccountGroup accountGroup = getAccountGroup(testAccountGroupInfo01);
            String accountGroupId = String.valueOf(accountGroup.getId());

            Account account = getAccount(common00);

            AccountGroupInfoUpdate accountGroupInfoUpdate = AccountGroupInfoUpdate.builder()
                    .accountIdList(Arrays.asList(String.valueOf(account.getId())))
                    .build();

            AccountGroupInfo accountGroupInfo = accountGroupService.updateAccountGroupInfo(accountGroupId, accountGroupInfoUpdate);
            List<AccountInfo> accountInfoList = accountGroupInfo.getAccountInfoList();

            Assertions.assertEquals(1, accountInfoList.size());
            Assertions.assertEquals(String.valueOf(account.getId()), accountInfoList.get(0).getId());
        }

        @Test
        @DisplayName("게시판이 등록되지 않은 그룹에 게시판 추가")
        public void updateBoardEmptyAccountGroup() throws Exception {
            AccountGroup accountGroup = getAccountGroup(testAccountGroupInfo01);
            String accountGroupId = String.valueOf(accountGroup.getId());

            Board board = getBoard(testBoardInfo00);

            AccountGroupInfoUpdate accountGroupInfoUpdate = AccountGroupInfoUpdate.builder()
                    .boardIdList(Arrays.asList(String.valueOf(board.getId())))
                    .build();

            AccountGroupInfo accountGroupInfo = accountGroupService.updateAccountGroupInfo(accountGroupId, accountGroupInfoUpdate);
            List<BoardInfo> boardInfoList = accountGroupInfo.getBoardInfoList();

            Assertions.assertEquals(1, boardInfoList.size());
            Assertions.assertEquals(String.valueOf(board.getId()), boardInfoList.get(0).getId());
        }

        @Test
        @DisplayName("계정이 등록된 그룹에 다른 계정 목록으로 수정")
        public void updateAccountAlreadyAccountGroup() throws Exception {
            AccountGroup accountGroup = getAccountGroup(testAccountGroupInfo02);
            String accountGroupId = String.valueOf(accountGroup.getId());

            Account account00 = getAccount(testAccountGroupInfo02.getAccountList().get(0));
            Account account01 = getAccount(common02);
            String accountId00 = String.valueOf(account00.getId());
            String accountId01 = String.valueOf(account01.getId());

            Board board00 = getBoard(testAccountGroupInfo02.getBoardList().get(0));
            Board board01 = getBoard(testAccountGroupInfo02.getBoardList().get(1));
            String boardId00 = String.valueOf(board00.getId());
            String boardId01 = String.valueOf(board01.getId());

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
            AccountGroup accountGroup = getAccountGroup(testAccountGroupInfo02);
            String accountGroupId = String.valueOf(accountGroup.getId());

            Account account00 = getAccount(testAccountGroupInfo02.getAccountList().get(0));
            Account account01 = getAccount(testAccountGroupInfo02.getAccountList().get(1));
            String accountId00 = String.valueOf(account00.getId());
            String accountId01 = String.valueOf(account01.getId());

            Board board00 = getBoard(testAccountGroupInfo02.getBoardList().get(0));
            Board board01 = getBoard(testBoardInfo02);
            String boardId00 = String.valueOf(board00.getId());
            String boardId01 = String.valueOf(board01.getId());

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
    }
}

package com.illdangag.iricom.server.service.implement;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.data.request.AccountGroupInfoCreate;
import com.illdangag.iricom.server.data.request.AccountGroupInfoSearch;
import com.illdangag.iricom.server.data.request.AccountGroupInfoUpdate;
import com.illdangag.iricom.server.data.response.AccountGroupInfo;
import com.illdangag.iricom.server.data.response.AccountGroupInfoList;
import com.illdangag.iricom.server.exception.IricomErrorCode;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.repository.AccountGroupRepository;
import com.illdangag.iricom.server.repository.AccountRepository;
import com.illdangag.iricom.server.repository.BoardRepository;
import com.illdangag.iricom.server.service.AccountGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Validated
@Service
public class AccountGroupServiceImpl implements AccountGroupService {
    private final AccountRepository accountRepository;
    private final BoardRepository boardRepository;
    private final AccountGroupRepository accountGroupRepository;

    @Autowired
    public AccountGroupServiceImpl(AccountRepository accountRepository, BoardRepository boardRepository,
                                   AccountGroupRepository accountGroupRepository) {
        this.accountRepository = accountRepository;
        this.boardRepository = boardRepository;
        this.accountGroupRepository = accountGroupRepository;
    }

    /**
     * 계정 그룹 생성
     */
    @Override
    public AccountGroupInfo createAccountGroupInfo(AccountGroupInfoCreate accountGroupInfoCreate) {
        List<String> accountIdList = accountGroupInfoCreate.getAccountIdList().stream().distinct().collect(Collectors.toList());
        List<String> boardIdList = accountGroupInfoCreate.getBoardIdList().stream().distinct().collect(Collectors.toList());

        if (!accountIdList.isEmpty() && !this.validateAccount(accountIdList)) {
            // 계정 ID 목록이 존재하는 경우 포함된 ID가 모두 유효한지 확인
            throw new IricomException(IricomErrorCode.INVALID_ACCOUNT_LIST);
        }

        if (!boardIdList.isEmpty() && !this.validateBoard(boardIdList)) {
            throw new IricomException(IricomErrorCode.INVALID_BOARD_LIST);
        }

        AccountGroup accountGroup = AccountGroup.builder()
                .title(accountGroupInfoCreate.getTitle())
                .description(accountGroupInfoCreate.getDescription())
                .enabled(true)
                .deleted(false)
                .build();

        List<AccountInAccountGroup> accountInAccountGroupList = accountIdList.stream()
                .map(item -> {
                    return AccountInAccountGroup.builder()
                            .accountGroup(accountGroup)
                            .account(this.getAccount(item))
                            .build();
                })
                .collect(Collectors.toList());
        List<Account> accountList = accountInAccountGroupList.stream()
                .map(AccountInAccountGroup::getAccount)
                .collect(Collectors.toList());

        List<BoardInAccountGroup> boardInAccountGroupList = boardIdList.stream()
                .map(item -> {
                    return BoardInAccountGroup.builder()
                            .accountGroup(accountGroup)
                            .board(this.getBoard(item))
                            .build();
                })
                .collect(Collectors.toList());
        List<Board> boardList = boardInAccountGroupList.stream()
                .map(BoardInAccountGroup::getBoard)
                .collect(Collectors.toList());

        this.accountGroupRepository.saveAccountGroup(accountGroup, accountInAccountGroupList, boardInAccountGroupList);
        return new AccountGroupInfo(accountGroup, accountList, boardList);
    }

    @Override
    public AccountGroupInfoList getAccountGroupInfoList(AccountGroupInfoSearch accountGroupInfoSearch) {
        int skip = accountGroupInfoSearch.getSkip();
        int limit = accountGroupInfoSearch.getLimit();

        List<AccountGroup> accountGroupList = this.accountGroupRepository.getAccountGroupList(skip, limit);
        long total = this.accountGroupRepository.getAccountGroupCount();

        List<AccountGroupInfo> accountGroupInfoList = accountGroupList.stream()
                .map(item -> {
                    List<Account> accountList = this.accountGroupRepository.getAccountListInAccountGroup(item);
                    List<Board> boardList = this.accountGroupRepository.getBoardListInAccountGroup(item);
                    return new AccountGroupInfo(item, accountList, boardList);
                }).collect(Collectors.toList());
        return AccountGroupInfoList.builder()
                .accountGroupInfoList(accountGroupInfoList)
                .total(total)
                .skip(skip)
                .limit(limit)
                .build();
    }

    @Override
    public AccountGroupInfo getAccountGroupInfo(String accountGroupId) {
        AccountGroup accountGroup = this.getAccountGroup(accountGroupId);

        if (accountGroup.getDeleted()) { // 삭제된 계정 그룹인 경우
            throw new IricomException(IricomErrorCode.NOT_EXIST_ACCOUNT_GROUP);
        }

        List<Account> accountList = this.accountGroupRepository.getAccountListInAccountGroup(accountGroup);
        List<Board> boardList = this.accountGroupRepository.getBoardListInAccountGroup(accountGroup);
        return new AccountGroupInfo(accountGroup, accountList, boardList);
    }

    @Override
    public AccountGroupInfo updateAccountGroupInfo(String accountGroupId, AccountGroupInfoUpdate accountGroupInfoUpdate) {
        AccountGroup accountGroup = this.getAccountGroup(accountGroupId);

        if (accountGroup.getDeleted()) { // 삭제된 계정 그룹인 경우
            throw new IricomException(IricomErrorCode.NOT_EXIST_ACCOUNT_GROUP);
        }

        List<String> accountIdList = accountGroupInfoUpdate.getAccountIdList();
        if (accountIdList != null) {
            accountIdList = accountIdList.stream().distinct().collect(Collectors.toList());
        }
        List<String> boardIdList = accountGroupInfoUpdate.getBoardIdList();
        if (boardIdList != null) {
            boardIdList = boardIdList.stream().distinct().collect(Collectors.toList());
        }

        if (accountIdList != null && !accountIdList.isEmpty() && !this.validateAccount(accountIdList)) {
            throw new IricomException(IricomErrorCode.INVALID_ACCOUNT_LIST);
        }

        if (boardIdList != null && !boardIdList.isEmpty() && !this.validateBoard(boardIdList)) {
            throw new IricomException(IricomErrorCode.INVALID_BOARD_LIST);
        }

        String title = accountGroupInfoUpdate.getTitle();
        String description = accountGroupInfoUpdate.getDescription();
        Boolean enabled = accountGroupInfoUpdate.getEnabled();

        if (title != null) {
            accountGroup.setTitle(title);
        }

        if (description != null) {
            accountGroup.setDescription(description);
        }

        if (enabled != null) {
            accountGroup.setEnabled(enabled);
        }

        List<AccountInAccountGroup> accountInAccountGroupList = null;
        if (accountIdList != null) {
            accountInAccountGroupList = accountIdList.stream()
                    .map(this::getAccount)
                    .map(item -> AccountInAccountGroup.builder()
                            .accountGroup(accountGroup).account(item).build())
                    .collect(Collectors.toList());
        }

        List<BoardInAccountGroup> boardInAccountGroupList = null;
        if (boardIdList != null) {
            boardInAccountGroupList = boardIdList.stream()
                    .map(this::getBoard)
                    .map(item -> BoardInAccountGroup.builder()
                            .accountGroup(accountGroup).board(item).build())
                    .collect(Collectors.toList());
        }

        this.accountGroupRepository.updateAccountGroup(accountGroup, accountInAccountGroupList, boardInAccountGroupList);

        if (accountInAccountGroupList == null) {
            List<Account> accountList = this.accountGroupRepository.getAccountListInAccountGroup(accountGroup);
            accountInAccountGroupList = accountList.stream()
                    .map(item -> AccountInAccountGroup.builder().accountGroup(accountGroup).account(item).build())
                    .collect(Collectors.toList());
        }
        List<Account> accountList = accountInAccountGroupList.stream()
                .map(AccountInAccountGroup::getAccount)
                .collect(Collectors.toList());

        if (boardInAccountGroupList == null) {
            List<Board> boardList = this.accountGroupRepository.getBoardListInAccountGroup(accountGroup);
            boardInAccountGroupList = boardList.stream()
                    .map(item -> BoardInAccountGroup.builder().accountGroup(accountGroup).board(item).build())
                    .collect(Collectors.toList());
        }
        List<Board> boardList = boardInAccountGroupList.stream()
                .map(BoardInAccountGroup::getBoard)
                .collect(Collectors.toList());

        return new AccountGroupInfo(accountGroup, accountList, boardList);
    }

    @Override
    public AccountGroupInfo deleteAccountGroupInfo(String accountGroupId) {
        AccountGroup accountGroup = this.getAccountGroup(accountGroupId);
        accountGroup.setDeleted(true);
        this.accountGroupRepository.updateAccountGroup(accountGroup);

        List<Account> accountList = this.accountGroupRepository.getAccountListInAccountGroup(accountGroup);
        List<Board> boardList = this.accountGroupRepository.getBoardListInAccountGroup(accountGroup);

        return new AccountGroupInfo(accountGroup, accountList, boardList);
    }

    /**
     * 게시판이 모두 존재하는지 확인
     */
    private boolean validateBoard(List<String> idList) {
        List<Long> boardIdList;
        try {
            boardIdList = idList.stream().map(Long::parseLong).collect(Collectors.toList());
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_BOARD);
        }

        return this.boardRepository.existBoard(boardIdList);
    }

    /**
     * id 목록이 모두 존재하는지 확인
     */
    private boolean validateAccount(List<String> idList) {
        List<Long> accountIdList;
        try {
            accountIdList = idList.stream().map(Long::parseLong).collect(Collectors.toList());
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_ACCOUNT);
        }
        return this.accountRepository.existAccount(accountIdList);
    }

    private AccountGroup getAccountGroup(String id) {
        long accountGroupId = -1;
        try {
            accountGroupId = Long.parseLong(id);
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_ACCOUNT_GROUP);
        }
        Optional<AccountGroup> accountGroupOptional = this.accountGroupRepository.getAccountGroup(accountGroupId);
        return accountGroupOptional.orElseThrow(() -> new IricomException(IricomErrorCode.NOT_EXIST_ACCOUNT_GROUP));
    }

    private Account getAccount(String id) {
        long accountId = -1L;
        try {
            accountId = Long.parseLong(id);
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_ACCOUNT);
        }
        Optional<Account> accountOptional = this.accountRepository.getAccount(accountId);
        return accountOptional.orElseThrow(() -> new IricomException(IricomErrorCode.NOT_EXIST_ACCOUNT));
    }

    private Board getBoard(String id) {
        long boardId = -1;
        try {
            boardId = Long.parseLong(id);
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_BOARD);
        }
        Optional<Board> boardOptional = this.boardRepository.getBoard(boardId);
        return boardOptional.orElseThrow(() -> new IricomException(IricomErrorCode.NOT_EXIST_BOARD));
    }
}
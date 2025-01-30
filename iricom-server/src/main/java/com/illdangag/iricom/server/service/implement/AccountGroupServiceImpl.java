package com.illdangag.iricom.server.service.implement;

import com.illdangag.iricom.server.data.entity.AccountGroup;
import com.illdangag.iricom.server.data.entity.AccountGroupAccount;
import com.illdangag.iricom.server.data.entity.AccountGroupBoard;
import com.illdangag.iricom.server.data.entity.Board;
import com.illdangag.iricom.server.data.request.AccountGroupInfoCreate;
import com.illdangag.iricom.server.data.request.AccountGroupInfoSearch;
import com.illdangag.iricom.server.data.request.AccountGroupInfoUpdate;
import com.illdangag.iricom.server.data.response.AccountGroupInfo;
import com.illdangag.iricom.server.data.response.AccountGroupInfoList;
import com.illdangag.iricom.server.exception.IricomErrorCode;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.repository.*;
import com.illdangag.iricom.server.service.AccountGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Validated
@Transactional
@Service
public class AccountGroupServiceImpl extends IricomService implements AccountGroupService {
    private final BoardRepository boardRepository;
    private final AccountGroupRepository accountGroupRepository;

    @Autowired
    public AccountGroupServiceImpl(AccountRepository accountRepository, BoardRepository boardRepository,
                                   PostRepository postRepository, CommentRepository commentRepository,
                                   AccountGroupRepository accountGroupRepository) {
        super(accountRepository, boardRepository, postRepository, commentRepository);
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

        if (!accountIdList.isEmpty() && !this.isExistAccount(accountIdList)) {
            // 계정 ID 목록이 존재하는 경우 포함된 ID가 모두 유효한지 확인
            throw new IricomException(IricomErrorCode.INVALID_ACCOUNT_LIST);
        }

        if (!boardIdList.isEmpty() && !this.isExistBoard(boardIdList)) {
            throw new IricomException(IricomErrorCode.INVALID_BOARD_LIST);
        }

        AccountGroup accountGroup = AccountGroup.builder()
                .title(accountGroupInfoCreate.getTitle())
                .description(accountGroupInfoCreate.getDescription())
                .enabled(true)
                .build();

        List<AccountGroupAccount> accountGroupAccountList = accountIdList.stream()
                .map(item -> {
                    return AccountGroupAccount.builder()
                            .accountGroup(accountGroup)
                            .account(this.getAccount(item))
                            .build();
                })
                .collect(Collectors.toList());
        List<AccountGroupBoard> accountGroupBoardList = boardIdList.stream()
                .map(item -> {
                    return AccountGroupBoard.builder()
                            .accountGroup(accountGroup)
                            .board(this.getBoard(item))
                            .build();
                })
                .collect(Collectors.toList());
        accountGroup.setAccountGroupAccountList(accountGroupAccountList);
        accountGroup.setAccountGroupBoardList(accountGroupBoardList);

        this.accountGroupRepository.saveAccountGroup(accountGroup);
        return new AccountGroupInfo(accountGroup);
    }

    @Override
    public AccountGroupInfoList getAccountGroupInfoList(AccountGroupInfoSearch accountGroupInfoSearch) {
        int skip = accountGroupInfoSearch.getSkip();
        int limit = accountGroupInfoSearch.getLimit();

        List<AccountGroup> accountGroupList = this.accountGroupRepository.getAccountGroupList(skip, limit);
        long total = this.accountGroupRepository.getAccountGroupCount();

        List<AccountGroupInfo> accountGroupInfoList = accountGroupList.stream()
                .map(AccountGroupInfo::new)
                .collect(Collectors.toList());

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
        return new AccountGroupInfo(accountGroup);
    }

    @Override
    public AccountGroupInfo updateAccountGroupInfo(String accountGroupId, AccountGroupInfoUpdate accountGroupInfoUpdate) {
        AccountGroup accountGroup = this.getAccountGroup(accountGroupId);

        List<String> accountIdList = accountGroupInfoUpdate.getAccountIdList();
        if (accountIdList != null) {
            accountIdList = accountIdList.stream().distinct().collect(Collectors.toList());
        }

        List<String> boardIdList = accountGroupInfoUpdate.getBoardIdList();
        if (boardIdList != null) {
            boardIdList = boardIdList.stream().distinct().collect(Collectors.toList());
        }

        if (accountIdList != null && !accountIdList.isEmpty() && !this.isExistAccount(accountIdList)) {
            throw new IricomException(IricomErrorCode.INVALID_ACCOUNT_LIST);
        }

        if (boardIdList != null && !boardIdList.isEmpty() && !this.isExistBoard(boardIdList)) {
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

        List<AccountGroupAccount> accountGroupAccountList = null;
        if (accountIdList != null) {
            accountGroupAccountList = accountIdList.stream()
                    .map(this::getAccount)
                    .map(item -> AccountGroupAccount.builder()
                            .accountGroup(accountGroup).account(item).build())
                    .collect(Collectors.toList());
            accountGroup.setAccountGroupAccountList(accountGroupAccountList);
        }

        List<AccountGroupBoard> accountGroupBoardList = null;
        if (boardIdList != null) {
            accountGroupBoardList = boardIdList.stream()
                    .map(this::getBoard)
                    .map(item -> AccountGroupBoard.builder()
                            .accountGroup(accountGroup).board(item).build())
                    .collect(Collectors.toList());
            accountGroup.setAccountGroupBoardList(accountGroupBoardList);
        }

        this.accountGroupRepository.saveAccountGroup(accountGroup);
        return new AccountGroupInfo(accountGroup);
    }

    @Override
    public AccountGroupInfo deleteAccountGroupInfo(String accountGroupId) {
        AccountGroup accountGroup = this.getAccountGroup(accountGroupId);
        AccountGroupInfo accountGroupInfo = new AccountGroupInfo(accountGroup);
        this.accountGroupRepository.removeAccountGroup(accountGroup);
        return accountGroupInfo;
    }

    /**
     * 게시판이 모두 존재하는지 확인
     */
    private boolean isExistBoard(List<String> boardIdList) {
        List<Long> idList;
        try {
            idList = boardIdList.stream()
                    .map(Long::parseLong)
                    .distinct()
                    .collect(Collectors.toList());
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_BOARD);
        }

        List<Board> boardList = this.boardRepository.getBoardList(idList);
        return idList.size() == boardList.size();
    }

    /**
     * id 목록이 모두 존재하는지 확인
     */
    private boolean isExistAccount(List<String> accountIdList) {
        List<Long> idList;
        try {
            idList = accountIdList.stream().map(Long::parseLong).collect(Collectors.toList());
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_ACCOUNT);
        }
        return this.accountRepository.existAccount(idList);
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
}

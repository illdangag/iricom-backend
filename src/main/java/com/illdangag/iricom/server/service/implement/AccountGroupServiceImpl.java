package com.illdangag.iricom.server.service.implement;

import com.illdangag.iricom.server.data.entity.AccountGroup;
import com.illdangag.iricom.server.data.request.AccountGroupInfoCreate;
import com.illdangag.iricom.server.data.request.AccountGroupInfoSearch;
import com.illdangag.iricom.server.data.request.AccountGroupInfoUpdate;
import com.illdangag.iricom.server.data.response.AccountGroupInfo;
import com.illdangag.iricom.server.data.response.AccountGroupInfoList;
import com.illdangag.iricom.server.exception.IricomErrorCode;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.repository.AccountRepository;
import com.illdangag.iricom.server.repository.BoardRepository;
import com.illdangag.iricom.server.service.AccountGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountGroupServiceImpl implements AccountGroupService {
    private final AccountRepository accountRepository;
    private final BoardRepository boardRepository;

    @Autowired
    public AccountGroupServiceImpl(AccountRepository accountRepository, BoardRepository boardRepository) {
        this.accountRepository = accountRepository;
        this.boardRepository = boardRepository;
    }

    @Override
    public AccountGroupInfo createAccountGroupInfo(AccountGroupInfoCreate accountGroupInfoCreate) {
        List<String> accountIdList = accountGroupInfoCreate.getAccountIdList().stream().distinct().collect(Collectors.toList());
        List<String> boardIdList = accountGroupInfoCreate.getBoardIdList().stream().distinct().collect(Collectors.toList());

        if (!accountIdList.isEmpty() && !this.validateAccount(accountIdList)) {
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

        // TODO 데이터 저장
        return null;
    }

    @Override
    public AccountGroupInfoList getAccountGroupInfoList(AccountGroupInfoSearch accountGroupInfoSearch) {
        return null;
    }

    @Override
    public AccountGroupInfo getAccountGroupInfo(String groupId) {
        return null;
    }

    @Override
    public AccountGroupInfo updateAccountGroupInfo(String groupId, AccountGroupInfoUpdate accountGroupInfoUpdate) {
        return null;
    }

    @Override
    public AccountGroupInfo deleteAccountGroupInfo(String groupId) {
        return null;
    }

    private boolean validateBoard(List<String> idList) {
        List<Long> boardIdList;
        try {
            boardIdList = idList.stream().map(Long::parseLong).collect(Collectors.toList());
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.INVALID_REQUEST); // TODO
        }

        return this.boardRepository.existBoard(boardIdList);
    }

    private boolean validateAccount(List<String> idList) {
        List<Long> accountIdList;
        try {
            accountIdList = idList.stream().map(Long::parseLong).collect(Collectors.toList());
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.INVALID_REQUEST); // TODO
        }
        return this.accountRepository.existAccount(accountIdList);
    }
}

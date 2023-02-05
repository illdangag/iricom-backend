package com.illdangag.iricom.server.service.implement;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.AccountDetail;
import com.illdangag.iricom.server.repository.AccountRepository;
import com.illdangag.iricom.server.data.request.AccountInfoCreate;
import com.illdangag.iricom.server.data.request.AccountInfoSearch;
import com.illdangag.iricom.server.data.request.AccountInfoUpdate;
import com.illdangag.iricom.server.data.response.AccountInfo;
import com.illdangag.iricom.server.data.response.AccountInfoList;
import com.illdangag.iricom.server.exception.IricomErrorCode;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Validated
@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public AccountInfo createAccountInfo(@Valid AccountInfoCreate accountInfoCreate) {
        Account account = Account.builder()
                .email(accountInfoCreate.getEmail())
                .admin(accountInfoCreate.getIsAdmin())
                .build();
        this.accountRepository.saveAccount(account);

        AccountDetail accountDetail = AccountDetail.builder()
                .account(account)
                .nickname(accountInfoCreate.getNickname())
                .description(accountInfoCreate.getDescription())
                .build();
        this.accountRepository.saveAccountDetail(accountDetail);

        return new AccountInfo(account, accountDetail);
    }

    @Override
    public Account getAccount(String id) {
        try {
            return this.getAccount(Long.parseLong(id));
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_ACCOUNT);
        }
    }

    @Override
    public Account getAccount(long id) {
        Optional<Account> accountOptional = this.accountRepository.getAccount(id);
        return accountOptional.orElseThrow(() -> new IricomException(IricomErrorCode.NOT_EXIST_ACCOUNT));
    }

    @Override
    public AccountInfoList getAccountInfoList(@Valid AccountInfoSearch accountInfoSearch) {
        long totalAccountCount = -1;
        List<Account> accountList;

        if (accountInfoSearch.getKeyword().isEmpty()) {
            accountList = this.accountRepository.getAccountList(accountInfoSearch.getSkip(), accountInfoSearch.getLimit());
            totalAccountCount = this.accountRepository.getAccountCount();
        } else {
            accountList = this.accountRepository.getAccountList(accountInfoSearch.getKeyword(), accountInfoSearch.getSkip(), accountInfoSearch.getLimit());
            totalAccountCount = this.accountRepository.getAccountCount(accountInfoSearch.getKeyword());
        }

        Map<Account, AccountDetail> accountDetailMap = this.accountRepository.getAccountDetailList(accountList);

        List<AccountInfo> accountInfoList = accountList.stream().map(account -> {
            AccountDetail accountDetail = accountDetailMap.getOrDefault(account, new AccountDetail());
            return new AccountInfo(account, accountDetail);
        }).collect(Collectors.toList());

        return AccountInfoList.builder()
                .skip(accountInfoSearch.getSkip())
                .limit(accountInfoSearch.getLimit())
                .total(totalAccountCount)
                .accountInfoList(accountInfoList)
                .build();
    }

    @Override
    public AccountInfo getAccountInfo(String id) {
        Account account = this.getAccount(id);
        return this.getAccountInfo(account);
    }

    @Override
    public AccountInfo getAccountInfo(Account account) {
        if (account == null) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_ACCOUNT);
        }

        Optional<AccountDetail> accountDetailOptional  = this.accountRepository.getAccountDetail(account);
        AccountDetail accountDetail = accountDetailOptional.orElseGet(() -> AccountDetail.builder().build());
        return new AccountInfo(account, accountDetail);
    }

    @Override
    public AccountInfo updateAccountDetail(String accountId, AccountInfoUpdate accountInfoUpdate) {
        Account account = this.getAccount(accountId);
        return this.updateAccountDetail(account, accountInfoUpdate);
    }

    @Override
    public AccountInfo updateAccountDetail(Account account, AccountInfoUpdate accountInfoUpdate) {
        Optional<AccountDetail> accountDetailOptional  = this.accountRepository.getAccountDetail(account);
        AccountDetail accountDetail = accountDetailOptional.orElseGet(() -> AccountDetail.builder().build());
        accountDetail.setAccount(account);

        if (accountDetail.getNickname().isEmpty() && accountInfoUpdate.getNickname() == null) {
            throw new IricomException(IricomErrorCode.MISSING_ACCOUNT_NICKNAME_FILED);
        }

        if (accountInfoUpdate.getNickname() != null) {
            accountDetail.setNickname(accountInfoUpdate.getNickname());
        }

        if (accountInfoUpdate.getDescription() != null) {
            accountDetail.setDescription(accountInfoUpdate.getDescription());
        }

        accountDetail.setUpdateDate(LocalDateTime.now());
        this.accountRepository.saveAccountDetail(accountDetail);
        return new AccountInfo(account, accountDetail);
    }

    @Override
    public Map<Account, AccountInfo> getAccountInfoMap(List<Account> accountList) {
        Map<Account, AccountDetail> accountAccountDetailMap = this.accountRepository.getAccountDetailList(accountList);
        Map<Account, AccountInfo> accountAccountInfoMap = new HashMap<>();
        accountList.forEach(account -> {
            AccountInfo accountInfo = new AccountInfo(account, accountAccountDetailMap.getOrDefault(account, AccountDetail.builder().build()));
            accountAccountInfoMap.put(account, accountInfo);
        });
        return accountAccountInfoMap;
    }
}

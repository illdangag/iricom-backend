package com.illdangag.iricom.server.service.implement;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.type.AccountAuth;
import com.illdangag.iricom.server.data.entity.AccountDetail;
import com.illdangag.iricom.server.data.request.AccountInfoSearch;
import com.illdangag.iricom.server.data.request.AccountInfoUpdate;
import com.illdangag.iricom.server.data.response.AccountInfo;
import com.illdangag.iricom.server.data.response.AccountInfoList;
import com.illdangag.iricom.server.exception.IricomErrorCode;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.repository.AccountRepository;
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
    public void saveAccount(Account account) {
        this.accountRepository.saveAccount(account);
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

        List<AccountInfo> accountInfoList = accountList.stream()
                .map(AccountInfo::new)
                .collect(Collectors.toList());

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

        return new AccountInfo(account);
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

        String nickname = accountInfoUpdate.getNickname();

        if (accountDetail.getNickname().isEmpty() && (nickname == null || nickname.isEmpty())) {
            throw new IricomException(IricomErrorCode.MISSING_ACCOUNT_NICKNAME_FILED);
        }

        if (nickname != null) {
            if (nickname.isEmpty()) {
                throw new IricomException(IricomErrorCode.MISSING_ACCOUNT_NICKNAME_FILED);
            }
            Optional<Account> accountOptional = this.accountRepository.getAccountByNickname(nickname); // 닉네임 중복 검사
            if (accountOptional.isPresent() && !accountOptional.get().equals(account)) {
                throw new IricomException(IricomErrorCode.ALREADY_ACCOUNT_NICKNAME);
            }
            accountDetail.setNickname(nickname);
        }

        if (accountInfoUpdate.getDescription() != null) {
            accountDetail.setDescription(accountInfoUpdate.getDescription());
        }

        accountDetail.setUpdateDate(LocalDateTime.now());
        this.accountRepository.saveAccountDetail(accountDetail);

        account.setAccountDetail(accountDetail);
        if (account.getAuth() == AccountAuth.UNREGISTERED_ACCOUNT) {
            account.setAuth(AccountAuth.ACCOUNT);
        }

        this.accountRepository.saveAccount(account);
        return new AccountInfo(account);
    }

    @Override
    public Map<Account, AccountInfo> getAccountInfoMap(List<Account> accountList) {
        Map<Account, AccountInfo> accountAccountInfoMap = new HashMap<>();
        accountList.forEach(account -> {
            AccountInfo accountInfo = new AccountInfo(account);
            accountAccountInfoMap.put(account, accountInfo);
        });
        return accountAccountInfoMap;
    }

    private Account getAccount(String id) {
        try {
            return this.getAccount(Long.parseLong(id));
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_ACCOUNT);
        }
    }

    private Account getAccount(long id) {
        Optional<Account> accountOptional = this.accountRepository.getAccount(id);
        return accountOptional.orElseThrow(() -> new IricomException(IricomErrorCode.NOT_EXIST_ACCOUNT));
    }
}

package com.illdangag.iricom.core.service.implement;

import com.illdangag.iricom.core.data.entity.Account;
import com.illdangag.iricom.core.data.entity.AccountDetail;
import com.illdangag.iricom.core.data.entity.type.AccountAuth;
import com.illdangag.iricom.core.data.request.AccountInfoSearch;
import com.illdangag.iricom.core.data.request.AccountInfoUpdate;
import com.illdangag.iricom.core.data.response.AccountInfo;
import com.illdangag.iricom.core.data.response.AccountInfoList;
import com.illdangag.iricom.core.exception.IricomErrorCode;
import com.illdangag.iricom.core.exception.IricomException;
import com.illdangag.iricom.core.repository.AccountRepository;
import com.illdangag.iricom.core.repository.BoardRepository;
import com.illdangag.iricom.core.repository.CommentRepository;
import com.illdangag.iricom.core.repository.PostRepository;
import com.illdangag.iricom.core.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Validated
@Transactional
@Service
public class AccountServiceImpl extends IricomService implements AccountService {

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository, BoardRepository boardRepository,
                              PostRepository postRepository, CommentRepository commentRepository) {
        super(accountRepository, boardRepository, postRepository, commentRepository);
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
}

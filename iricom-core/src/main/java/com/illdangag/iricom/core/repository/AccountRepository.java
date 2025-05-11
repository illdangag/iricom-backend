package com.illdangag.iricom.core.repository;

import com.illdangag.iricom.core.data.entity.Account;
import com.illdangag.iricom.core.data.entity.AccountDetail;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AccountRepository {
    List<Account> getAccountList(String email);

    List<Account> getAccountList(int offset, int limit);

    long getAccountCount();

    List<Account> getAccountList(String keyword, int offset, int limit);

    long getAccountCount(String keyword);

    Optional<Account> getAccount(long id);

    Optional<Account> getAccount(String id);

    Optional<Account> getAccountByNickname(String nickname);

    Optional<AccountDetail> getAccountDetail(Account account);

    Map<Account, AccountDetail> getAccountDetailList(List<Account> accountList);

    boolean existAccount(List<Long> accountIdList);

    void saveAccount(Account account);

    void saveAccountDetail(AccountDetail accountDetail);
}

package com.illdangag.iricom.server.repository;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.AccountDetail;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AccountRepository {
    List<Account> getAccountList(String email);

    List<Account> getAccountList(int offset, int limit);

    long getAccountCount();

    List<Account> getAccountList(String likeEmail, int offset, int limit);

    long getAccountCount(String containEmail);

    Optional<Account> getAccount(long id);

    Optional<AccountDetail> getAccountDetail(Account account);

    Map<Account, AccountDetail> getAccountDetailList(List<Account> accountList);

    Optional<Account> getAccount(String nickname);

    void saveAccount(Account account);

    void saveAccountDetail(AccountDetail accountDetail);
}

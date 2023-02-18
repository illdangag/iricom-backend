package com.illdangag.iricom.server.service;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.request.AccountInfoCreate;
import com.illdangag.iricom.server.data.request.AccountInfoSearch;
import com.illdangag.iricom.server.data.request.AccountInfoUpdate;
import com.illdangag.iricom.server.data.response.AccountInfo;
import com.illdangag.iricom.server.data.response.AccountInfoList;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

public interface AccountService {
    AccountInfo createAccountInfo(@Valid AccountInfoCreate accountInfoCreate);

    AccountInfo getAccountInfo(String id);

    AccountInfo getAccountInfo(Account account);

    Map<Account, AccountInfo> getAccountInfoMap(List<Account> accountList);

    AccountInfoList getAccountInfoList(@Valid AccountInfoSearch accountInfoSearch);

    AccountInfo updateAccountDetail(String accountId, AccountInfoUpdate accountInfoUpdate);

    AccountInfo updateAccountDetail(Account account, AccountInfoUpdate accountInfoUpdate);

    Account getAccount(String id);

    Account getAccount(long id);

    void saveAccount(Account account);
}

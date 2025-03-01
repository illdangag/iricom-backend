package com.illdangag.iricom.server.service;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.request.AccountInfoSearch;
import com.illdangag.iricom.server.data.request.AccountInfoUpdate;
import com.illdangag.iricom.server.data.response.AccountInfo;
import com.illdangag.iricom.server.data.response.AccountInfoList;

import javax.validation.Valid;

public interface AccountService {
    /**
     * 계정 정보 조회
     */
    AccountInfo getAccountInfo(String id);

    AccountInfo getAccountInfo(Account account);

    /**
     * 계정 목록 조회
     */
    AccountInfoList getAccountInfoList(@Valid AccountInfoSearch accountInfoSearch);

    /**
     * 계정 정보 수정
     */
    AccountInfo updateAccountDetail(String accountId, AccountInfoUpdate accountInfoUpdate);

    AccountInfo updateAccountDetail(Account account, AccountInfoUpdate accountInfoUpdate);

    /**
     * 계정 정보 저장
     */
    void saveAccount(Account account);
}

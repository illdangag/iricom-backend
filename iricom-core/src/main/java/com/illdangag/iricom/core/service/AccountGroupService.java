package com.illdangag.iricom.core.service;

import com.illdangag.iricom.core.data.request.AccountGroupInfoCreate;
import com.illdangag.iricom.core.data.request.AccountGroupInfoSearch;
import com.illdangag.iricom.core.data.request.AccountGroupInfoUpdate;
import com.illdangag.iricom.core.data.response.AccountGroupInfo;
import com.illdangag.iricom.core.data.response.AccountGroupInfoList;

import javax.validation.Valid;

public interface AccountGroupService {
    AccountGroupInfo createAccountGroupInfo(@Valid AccountGroupInfoCreate groupInfoCreate);

    AccountGroupInfoList getAccountGroupInfoList(@Valid AccountGroupInfoSearch accountGroupInfoSearch);

    AccountGroupInfo getAccountGroupInfo(String accountGroupId);

    AccountGroupInfo updateAccountGroupInfo(String accountGroupId, @Valid AccountGroupInfoUpdate accountGroupInfoUpdate);

    AccountGroupInfo deleteAccountGroupInfo(String accountGroupId);
}

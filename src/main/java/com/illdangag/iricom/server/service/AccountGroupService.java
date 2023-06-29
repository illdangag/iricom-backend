package com.illdangag.iricom.server.service;

import com.illdangag.iricom.server.data.request.GroupInfoCreate;
import com.illdangag.iricom.server.data.request.GroupInfoSearch;
import com.illdangag.iricom.server.data.request.GroupInfoUpdate;
import com.illdangag.iricom.server.data.response.AccountGroupInfo;
import com.illdangag.iricom.server.data.response.AccountGroupInfoList;

import javax.validation.Valid;

public interface AccountGroupService {
    AccountGroupInfo createAccountGroupInfo(@Valid GroupInfoCreate groupInfoCreate);

    AccountGroupInfoList getAccountGroupInfoList(@Valid GroupInfoSearch groupInfoSearch);

    AccountGroupInfo getAccountGroupInfo(String groupId);

    AccountGroupInfo updateAccountGroupInfo(String groupId, @Valid GroupInfoUpdate groupInfoUpdate);

    AccountGroupInfo deleteAccountGroupInfo(String groupId);

    AccountGroupInfo addAccountGroupBoard(String groupId, String boardId);

    AccountGroupInfo removeAccountGroupBoard(String groupId, String boardId);

    AccountGroupInfo addAccountGroupAccount(String groupId, String accountId);

    AccountGroupInfo removeAccountGroupAccount(String groupId, String accountId);
}

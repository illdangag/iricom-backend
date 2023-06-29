package com.illdangag.iricom.server.service.implement;

import com.illdangag.iricom.server.data.request.GroupInfoCreate;
import com.illdangag.iricom.server.data.request.GroupInfoSearch;
import com.illdangag.iricom.server.data.request.GroupInfoUpdate;
import com.illdangag.iricom.server.data.response.AccountGroupInfo;
import com.illdangag.iricom.server.data.response.AccountGroupInfoList;
import com.illdangag.iricom.server.service.AccountGroupService;
import org.springframework.stereotype.Service;

@Service
public class AccountGroupServiceImpl implements AccountGroupService {
    @Override
    public AccountGroupInfo createAccountGroupInfo(GroupInfoCreate groupInfoCreate) {
        return null;
    }

    @Override
    public AccountGroupInfoList getAccountGroupInfoList(GroupInfoSearch groupInfoSearch) {
        return null;
    }

    @Override
    public AccountGroupInfo getAccountGroupInfo(String groupId) {
        return null;
    }

    @Override
    public AccountGroupInfo updateAccountGroupInfo(String groupId, GroupInfoUpdate groupInfoUpdate) {
        return null;
    }

    @Override
    public AccountGroupInfo deleteAccountGroupInfo(String groupId) {
        return null;
    }

    @Override
    public AccountGroupInfo addAccountGroupBoard(String groupId, String boardId) {
        return null;
    }

    @Override
    public AccountGroupInfo removeAccountGroupBoard(String groupId, String boardId) {
        return null;
    }

    @Override
    public AccountGroupInfo addAccountGroupAccount(String groupId, String accountId) {
        return null;
    }

    @Override
    public AccountGroupInfo removeAccountGroupAccount(String groupId, String accountId) {
        return null;
    }
}

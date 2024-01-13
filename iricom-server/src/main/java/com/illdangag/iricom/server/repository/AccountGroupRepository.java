package com.illdangag.iricom.server.repository;

import com.illdangag.iricom.server.data.entity.AccountGroup;
import com.illdangag.iricom.server.data.entity.AccountGroupAccount;
import com.illdangag.iricom.server.data.entity.AccountGroupBoard;

import java.util.List;
import java.util.Optional;

public interface AccountGroupRepository {
    Optional<AccountGroup> getAccountGroup(long id);

    void saveAccountGroup(AccountGroup accountGroup);

    void updateAccountGroup(AccountGroup accountGroup, List<AccountGroupAccount> accountGroupAccountList, List<AccountGroupBoard> accountGroupBoardList);

    List<AccountGroup> getAccountGroupList(int skip, int limit);

    long getAccountGroupCount();

    void removeAccountGroup(AccountGroup accountGroup);
}

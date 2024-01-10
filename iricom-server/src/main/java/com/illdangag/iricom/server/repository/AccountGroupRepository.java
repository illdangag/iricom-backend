package com.illdangag.iricom.server.repository;

import com.illdangag.iricom.server.data.entity.*;

import java.util.List;
import java.util.Optional;

public interface AccountGroupRepository {
    Optional<AccountGroup> getAccountGroup(long id);

    void saveAccountGroup(AccountGroup accountGroup);

    void updateAccountGroup(AccountGroup accountGroup, List<AccountGroupAccount> accountGroupAccountList, List<AccountGroupBoard> accountGroupBoardList);

    void updateAccountGroup(AccountGroup accountGroup);

    List<Account> getAccountListInAccountGroup(AccountGroup accountGroup);

    List<Board> getBoardListInAccountGroup(AccountGroup accountGroup);

    List<AccountGroup> getAccountGroupList(int skip, int limit);

    long getAccountGroupCount();

    void removeAccountGroup(AccountGroup accountGroup);
}

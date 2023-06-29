package com.illdangag.iricom.server.repository;

import com.illdangag.iricom.server.data.entity.*;

import java.util.List;
import java.util.Optional;

public interface AccountGroupRepository {
    Optional<AccountGroup> getAccountGroup(long id);

    List<AccountGroup> getAccountGroupList(Account account);

    List<Account> getAccountListInAccountGroup(AccountGroup accountGroup);

    List<Board> getBoardListInAccountGroup(AccountGroup accountGroup);

    List<Board> getAccessibleBoardList(Account account);

    void save(AccountGroup accountGroup);

    void save(AccountInAccountGroup accountInAccountGroup);

    void save(BoardInAccountGroup boardInAccountGroup);
}

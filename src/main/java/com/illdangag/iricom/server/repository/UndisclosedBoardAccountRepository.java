package com.illdangag.iricom.server.repository;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.UndisclosedBoardAccount;

import java.util.List;

public interface UndisclosedBoardAccountRepository {
    List<UndisclosedBoardAccount> getUndisclosedBoardAccountList(Account account);

    void save(UndisclosedBoardAccount undisclosedBoardAccount);
}

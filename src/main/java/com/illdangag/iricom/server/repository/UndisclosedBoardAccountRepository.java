package com.illdangag.iricom.server.repository;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.UndisclosedBoardAccount;

import java.util.List;

public interface UndisclosedBoardAccountRepository {
    void save(UndisclosedBoardAccount undisclosedBoardAccount);

    List<UndisclosedBoardAccount> getUndisclosedBoardAccountList(Account account);
}

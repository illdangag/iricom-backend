package com.illdangag.iricom.core.repository;

import com.illdangag.iricom.core.data.entity.Account;
import com.illdangag.iricom.core.data.entity.AccountPoint;
import com.illdangag.iricom.core.data.entity.AccountPointTable;
import com.illdangag.iricom.core.data.entity.type.AccountPointType;

import java.util.Optional;

public interface AccountPointRepository {
    Optional<AccountPointTable> getAccountPointTable(AccountPointType type);

    long getPoint(Account account);

    void save(AccountPointTable accountPointTable);

    void save(AccountPoint accountPoint);
}

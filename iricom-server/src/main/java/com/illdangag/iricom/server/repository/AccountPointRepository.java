package com.illdangag.iricom.server.repository;

import com.illdangag.iricom.server.data.entity.AccountPoint;
import com.illdangag.iricom.server.data.entity.AccountPointTable;
import com.illdangag.iricom.server.data.entity.type.AccountPointType;

import java.util.Optional;

public interface AccountPointRepository {
    Optional<AccountPointTable> getAccountPointTable(AccountPointType type);

    void save(AccountPointTable accountPointTable);

    void save(AccountPoint accountPoint);
}

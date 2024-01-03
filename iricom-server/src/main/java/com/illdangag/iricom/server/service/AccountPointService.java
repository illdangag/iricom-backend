package com.illdangag.iricom.server.service;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.type.AccountPointType;

public interface AccountPointService {
    void addAccountPoint(Account account, AccountPointType accountPointType);
}

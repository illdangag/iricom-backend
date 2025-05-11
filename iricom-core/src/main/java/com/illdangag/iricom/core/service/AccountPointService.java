package com.illdangag.iricom.core.service;

import com.illdangag.iricom.core.data.entity.Account;
import com.illdangag.iricom.core.data.entity.type.AccountPointType;

public interface AccountPointService {
    void addAccountPoint(Account account, AccountPointType accountPointType);
}

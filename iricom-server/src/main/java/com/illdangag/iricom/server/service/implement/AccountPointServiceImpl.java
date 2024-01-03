package com.illdangag.iricom.server.service.implement;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.AccountPointTable;
import com.illdangag.iricom.server.data.entity.type.AccountPointType;
import com.illdangag.iricom.server.repository.AccountPointRepository;
import com.illdangag.iricom.server.service.AccountPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountPointServiceImpl implements AccountPointService {
    private final AccountPointRepository accountPointRepository;

    @Autowired
    public AccountPointServiceImpl(AccountPointRepository accountPointRepository) {
        this.accountPointRepository = accountPointRepository;
    }

    @Override
    public void addAccountPoint(Account account, AccountPointType accountPointType) {
        Optional<AccountPointTable> accountPointTypeOptional = this.accountPointRepository.getAccountPointTable(accountPointType);
        long point = accountPointTypeOptional.isPresent() ? accountPointTypeOptional.get().getPoint() : accountPointType.getDefaultPoint();
        // TODO
    }
}

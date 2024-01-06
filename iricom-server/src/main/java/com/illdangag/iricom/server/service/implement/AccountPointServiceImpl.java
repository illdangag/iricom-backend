package com.illdangag.iricom.server.service.implement;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.AccountPoint;
import com.illdangag.iricom.server.data.entity.AccountPointTable;
import com.illdangag.iricom.server.data.entity.type.AccountPointType;
import com.illdangag.iricom.server.repository.AccountPointRepository;
import com.illdangag.iricom.server.service.AccountPointService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class AccountPointServiceImpl implements AccountPointService {
    private final AccountPointRepository accountPointRepository;

    @Autowired
    public AccountPointServiceImpl(AccountPointRepository accountPointRepository) {
        this.accountPointRepository = accountPointRepository;
    }

    @Override
    public void addAccountPoint(Account account, AccountPointType accountPointType) {
        long point = this.getAccountPoint(accountPointType);

        AccountPoint accountPoint = AccountPoint.builder()
                .account(account)
                .type(accountPointType)
                .point(point)
                .build();

        this.accountPointRepository.save(accountPoint);
    }

    private long getAccountPoint(AccountPointType accountPointType) {
        Optional<AccountPointTable> accountPointTypeOptional = this.accountPointRepository.getAccountPointTable(accountPointType);
        return accountPointTypeOptional.isPresent() ? accountPointTypeOptional.get().getPoint() : accountPointType.getDefaultPoint();
    }
}

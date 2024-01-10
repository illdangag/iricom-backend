package com.illdangag.iricom.server.service.implement;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.AccountPoint;
import com.illdangag.iricom.server.data.entity.AccountPointTable;
import com.illdangag.iricom.server.data.entity.type.AccountPointType;
import com.illdangag.iricom.server.repository.*;
import com.illdangag.iricom.server.service.AccountPointService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Slf4j
@Transactional
@Service
public class AccountPointServiceImpl extends IricomService implements AccountPointService {
    private final AccountPointRepository accountPointRepository;

    @Autowired
    public AccountPointServiceImpl(AccountRepository accountRepository, BoardRepository boardRepository,
                                   PostRepository postRepository, CommentRepository commentRepository,
                                   AccountPointRepository accountPointRepository) {
        super(accountRepository, boardRepository, postRepository, commentRepository);
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

        account.getPointList().add(accountPoint);
        this.accountPointRepository.save(accountPoint);
        this.accountRepository.saveAccount(account);
    }

    private long getAccountPoint(AccountPointType accountPointType) {
        Optional<AccountPointTable> accountPointTypeOptional = this.accountPointRepository.getAccountPointTable(accountPointType);
        return accountPointTypeOptional.isPresent() ? accountPointTypeOptional.get().getPoint() : accountPointType.getDefaultPoint();
    }
}

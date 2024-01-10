package com.illdangag.iricom.server.repository.implement;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.AccountPoint;
import com.illdangag.iricom.server.data.entity.AccountPointTable;
import com.illdangag.iricom.server.data.entity.type.AccountPointType;
import com.illdangag.iricom.server.repository.AccountPointRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional
@Repository
public class AccountPointRepositoryImpl implements AccountPointRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<AccountPointTable> getAccountPointTable(AccountPointType type) {
        final String jpql = "SELECT t FROM AccountPointTable t"
                + " WHERE t.type = :type";

        TypedQuery<AccountPointTable> query = this.entityManager.createQuery(jpql, AccountPointTable.class)
                .setParameter("type", type);

        List<AccountPointTable> resultList = query.getResultList();

        if (resultList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(resultList.get(0));
        }
    }

    @Override
    public long getPoint(Account account) {
        final String jpql = "SELECT SUM(ap.point) FROM AccountPoint ap" +
                " WHERE ap.account = :account";

        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class)
                .setParameter("account", account);

        return query.getSingleResult();
    }

    @Override
    public void save(AccountPointTable accountPointTable) {
        Optional<AccountPointTable> accountPointTableOptional = this.getAccountPointTable(accountPointTable.getType());

        accountPointTableOptional.ifPresent(this.entityManager::remove); // 이미 해당 type의 정보가 존재 하는 경우 이전 정보는 샂게

        this.entityManager.persist(accountPointTable);
        this.entityManager.flush();
    }

    @Override
    public void save(AccountPoint accountPoint) {
        if (accountPoint.getId() == null) {
            this.entityManager.persist(accountPoint);
        } else {
            this.entityManager.merge(accountPoint);
        }
        this.entityManager.flush();
    }
}

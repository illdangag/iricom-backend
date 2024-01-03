package com.illdangag.iricom.server.repository.implement;

import com.illdangag.iricom.server.data.entity.AccountPoint;
import com.illdangag.iricom.server.data.entity.AccountPointTable;
import com.illdangag.iricom.server.data.entity.type.AccountPointType;
import com.illdangag.iricom.server.repository.AccountPointRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class AccountPointRepositoryImpl implements AccountPointRepository {
    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    public AccountPointRepositoryImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Optional<AccountPointTable> getAccountPointTable(AccountPointType type) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        return this.getAccountPointTable(entityManager, type);
    }

    private Optional<AccountPointTable> getAccountPointTable(EntityManager entityManager, AccountPointType type) {
        final String jpql = "SELECT t FROM AccountPointTable t"
                + " WHERE t.type = :type";

        TypedQuery<AccountPointTable> query = entityManager.createQuery(jpql, AccountPointTable.class)
                .setParameter("type", type);

        List<AccountPointTable> resultList = query.getResultList();
        entityManager.close();

        if (resultList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(resultList.get(0));
        }
    }

    @Override
    public void save(AccountPointTable accountPointTable) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();

        Optional<AccountPointTable> accountPointTableOptional = this.getAccountPointTable(entityManager, accountPointTable.getType());

        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        accountPointTableOptional.ifPresent(entityManager::remove); // 이미 해당 type의 정보가 존재 하는 경우 이전 정보는 샂게

        entityManager.persist(accountPointTable);
        entityTransaction.commit();
        entityManager.close();
    }

    @Override
    public void save(AccountPoint accountPoint) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();

        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        if (accountPoint.getId() == null) {
            entityManager.persist(accountPoint);
        } else {
            entityManager.merge(accountPoint);
        }

        entityTransaction.commit();
        entityManager.close();
    }
}

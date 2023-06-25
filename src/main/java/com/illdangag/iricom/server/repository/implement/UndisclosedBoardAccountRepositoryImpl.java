package com.illdangag.iricom.server.repository.implement;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.UndisclosedBoardAccount;
import com.illdangag.iricom.server.repository.UndisclosedBoardAccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.List;

@Slf4j
@Repository
public class UndisclosedBoardAccountRepositoryImpl implements UndisclosedBoardAccountRepository {
    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    public UndisclosedBoardAccountRepositoryImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public List<UndisclosedBoardAccount> getUndisclosedBoardAccountList(Account account) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT uba FROM UndisclosedBoardAccount uba" +
                " WHERE account = :account";

        TypedQuery<UndisclosedBoardAccount> query = entityManager.createQuery(jpql, UndisclosedBoardAccount.class)
                .setParameter("account", account);
        List<UndisclosedBoardAccount> resultList = query.getResultList();
        entityManager.close();
        return resultList;
    }

    @Override
    public void save(UndisclosedBoardAccount undisclosedBoardAccount) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();
        if (undisclosedBoardAccount.getId() == null) {
            entityManager.persist(undisclosedBoardAccount);
        } else {
            entityManager.merge(undisclosedBoardAccount);
        }
        entityTransaction.commit();
        entityManager.close();
    }
}

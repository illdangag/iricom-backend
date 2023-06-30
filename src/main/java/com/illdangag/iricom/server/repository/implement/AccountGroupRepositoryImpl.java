package com.illdangag.iricom.server.repository.implement;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.repository.AccountGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class AccountGroupRepositoryImpl implements AccountGroupRepository {
    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    public AccountGroupRepositoryImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Optional<AccountGroup> getAccountGroup(long id) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT ag FROM AccountGroup ag WHERE ag.id = :id";

        TypedQuery<AccountGroup> query = entityManager.createQuery(jpql, AccountGroup.class)
                .setParameter("id", id);

        List<AccountGroup> resultList = query.getResultList();

        entityManager.close();

        if (resultList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(resultList.get(0));
        }
    }

    @Override
    public List<AccountGroup> getAccountGroupList(Account account) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT aiag FROM AccountInAccountGroup aiag WHERE aiag.account = :account";

        TypedQuery<AccountGroup> query = entityManager.createQuery(jpql, AccountGroup.class)
                .setParameter("account", account);

        List<AccountGroup> resultList = query.getResultList();
        entityManager.close();
        return resultList;
    }

    @Override
    public List<Account> getAccountListInAccountGroup(AccountGroup accountGroup) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT aiag.account FROM AccountInAccountGroup aiag WHERE aiag.accountGroup = :accountGroup";

        TypedQuery<Account> query = entityManager.createQuery(jpql, Account.class)
                .setParameter("accountGroup", accountGroup);

        List<Account> resultList = query.getResultList();
        entityManager.close();
        return resultList;
    }

    @Override
    public List<Board> getBoardListInAccountGroup(AccountGroup accountGroup) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT biag.board FROM BoardInAccountGroup biag WHERE (biag.accountGroup = :accountGroup)";

        TypedQuery<Board> query = entityManager.createQuery(jpql, Board.class)
                .setParameter("accountGroup", accountGroup);

        List<Board> resultList = query.getResultList();
        entityManager.close();
        return resultList;
    }

    @Override
    public List<Board> getAccessibleBoardList(Account account) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();

        List<Long> accountGroupIdList = this.getAccountGroupId(entityManager, account);

        final String jpql = "SELECT biag.board FROM BoardInAccountGroup biag" +
                " WHERE biag.accountGroup.id IN :accountGroupId";

        TypedQuery<Board> query = entityManager.createQuery(jpql, Board.class)
                .setParameter("accountGroupId", accountGroupIdList);

        List<Board> resultList = query.getResultList();
        entityManager.close();
        return resultList;
    }

    /**
     * 계정이 포함된 계정 그룹의 ID 목록 조회
     */
    private List<Long> getAccountGroupId(EntityManager entityManager, Account account) {
        final String jpql = "SELECT ag.id FROM AccountGroup ag RIGHT JOIN AccountInAccountGroup aiag ON ag.id = aiag.accountGroup.id WHERE aiag.account = :account";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
                .setParameter("account", account);
        return query.getResultList();
    }

    @Override
    public void save(AccountGroup accountGroup) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        if (accountGroup.getId() == null) {
            entityManager.persist(accountGroup);
        } else {
            entityManager.merge(accountGroup);
        }

        entityTransaction.commit();
        entityManager.close();
    }

    @Override
    public void save(AccountInAccountGroup accountInAccountGroup) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        if (accountInAccountGroup.getId() == null) {
            entityManager.persist(accountInAccountGroup);
        } else {
            entityManager.merge(accountInAccountGroup);
        }

        entityTransaction.commit();
        entityManager.close();
    }

    @Override
    public void save(BoardInAccountGroup boardInAccountGroup) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        if (boardInAccountGroup.getId() == null) {
            entityManager.persist(boardInAccountGroup);
        } else {
            entityManager.merge(boardInAccountGroup);
        }

        entityTransaction.commit();
        entityManager.close();
    }
}

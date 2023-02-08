package com.illdangag.iricom.server.repository.implement;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.AccountDetail;
import com.illdangag.iricom.server.repository.AccountRepository;
import com.illdangag.iricom.server.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class AccountRepositoryImpl implements AccountRepository {
    private final EntityManager entityManager;

    @Autowired
    public AccountRepositoryImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManager = entityManagerFactory.createEntityManager();
    }

    @Override
    public Optional<Account> getAccount(long id) {
        String jpql = "SELECT a FROM Account a WHERE a.id = :id";
        TypedQuery<Account> query = this.entityManager.createQuery(jpql, Account.class);
        query.setParameter("id", id);
        List<Account> accountList = query.getResultList();
        if (accountList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(accountList.get(0));
        }
    }

    @Override
    public List<Account> getAccountList(String email) {
        String jpql = "SELECT a FROM Account a WHERE a.email = :email";
        TypedQuery<Account> query = this.entityManager.createQuery(jpql, Account.class);
        query.setParameter("email", email);
        return query.getResultList();
    }

    @Override
    public List<Account> getAccountList(int offset, int limit) {
        String jpql = "SELECT a FROM Account a ORDER BY a.email";
        TypedQuery<Account> query = this.entityManager.createQuery(jpql, Account.class);
        query.setFirstResult(offset)
                .setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    public long getAccountCount() {
        String jpql = "SELECT COUNT(*) FROM Account a";
        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class);
        return query.getSingleResult();
    }

    @Override
    public List<Account> getAccountList(String containEmail, int offset, int limit) {
        String jpql = "SELECT a FROM Account a WHERE a.email LIKE :email ORDER BY a.email";
        TypedQuery<Account> query = this.entityManager.createQuery(jpql, Account.class);
        query.setParameter("email", "%" + StringUtils.escape(containEmail) + "%")
                .setFirstResult(offset)
                .setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    public long getAccountCount(String containEmail) {
        String jpql = "SELECT COUNT(*) FROM Account a WHERE a.email LIKE :email";
        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class);
        query.setParameter("email", "%" + StringUtils.escape(containEmail) + "%");
        return query.getSingleResult();
    }

    @Override
    public Optional<AccountDetail> getAccountDetail(Account account) {
        String jpql = "SELECT ad FROM AccountDetail ad WHERE ad.account = :account ORDER BY ad.createDate DESC";
        TypedQuery<AccountDetail> query = this.entityManager.createQuery(jpql, AccountDetail.class);
        query.setParameter("account", account);
        List<AccountDetail> accountDetailList = query.getResultList();
        if (accountDetailList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(accountDetailList.get(0));
        }
    }

    @Override
    public Map<Account, AccountDetail> getAccountDetailList(List<Account> accountList) {
        String jpql = "SELECT ad FROM AccountDetail ad WHERE ad.account IN :accounts ORDER BY ad.createDate DESC";
        TypedQuery<AccountDetail> query = this.entityManager.createQuery(jpql, AccountDetail.class);
        query.setParameter("accounts", accountList);
        List<AccountDetail> accountDetailList = query.getResultList();
        Map<Account, List<AccountDetail>> accountAccountDetailMap = accountDetailList.stream().collect(Collectors.groupingBy(AccountDetail::getAccount));

        Map<Account, AccountDetail> resultMap = new HashMap<>();
        Set<Account> keySet = accountAccountDetailMap.keySet();
        for (Account account : keySet) {
            List<AccountDetail> list = accountAccountDetailMap.get(account);
            if (!list.isEmpty()) {
                resultMap.put(account, list.get(0));
            }
        }
        return resultMap;
    }

    @Override
    public Optional<Account> getAccount(String nickname) {
        final String jpql = "SELECT a FROM Account a " +
                "WHERE a.accountDetail IS NOT NULL " +
                "AND a.accountDetail.nickname = :nickname";
        TypedQuery<Account> query = this.entityManager.createQuery(jpql, Account.class)
                .setParameter("nickname", nickname);
        List<Account> list = query.getResultList();
        if (list.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(list.get(0));
        }
    }

    @Override
    public void saveAccount(Account account) {
        EntityTransaction transaction = this.entityManager.getTransaction();
        transaction.begin();
        if (account.getId() == null) {
            this.entityManager.persist(account);
        } else {
            this.entityManager.merge(account);
        }
        transaction.commit();
    }

    @Override
    public void saveAccountDetail(AccountDetail accountDetail) {
        EntityTransaction transaction = this.entityManager.getTransaction();
        transaction.begin();
        if (accountDetail.getId() == null) {
            this.entityManager.persist(accountDetail);
        } else {
            this.entityManager.merge(accountDetail);
        }
        transaction.commit();
    }
}

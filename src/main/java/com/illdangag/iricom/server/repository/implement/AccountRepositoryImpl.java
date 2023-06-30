package com.illdangag.iricom.server.repository.implement;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.AccountDetail;
import com.illdangag.iricom.server.exception.IricomErrorCode;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.repository.AccountRepository;
import com.illdangag.iricom.server.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class AccountRepositoryImpl implements AccountRepository {
    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    public AccountRepositoryImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Optional<Account> getAccount(long id) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT a FROM Account a" +
                " WHERE a.id = :id";

        TypedQuery<Account> query = entityManager.createQuery(jpql, Account.class)
                .setParameter("id", id);
        List<Account> accountList = query.getResultList();
        entityManager.close();
        if (accountList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(accountList.get(0));
        }
    }

    @Override
    public Optional<Account> getAccount(String id) {
        long accountId = -1;
        try {
            accountId = Long.parseLong(id);
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_ACCOUNT);
        }
        return this.getAccount(accountId);
    }

    @Override
    public List<Account> getAccountList(String email) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT a FROM Account a" +
                " WHERE a.email = :email";

        TypedQuery<Account> query = entityManager.createQuery(jpql, Account.class)
                .setParameter("email", email);
        List<Account> resultList = query.getResultList();
        entityManager.close();
        return resultList;
    }

    @Override
    public List<Account> getAccountList(int offset, int limit) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT a FROM Account a" +
                " ORDER BY a.email";

        TypedQuery<Account> query = entityManager.createQuery(jpql, Account.class)
                .setFirstResult(offset)
                .setMaxResults(limit);
        List<Account> resultList = query.getResultList();
        entityManager.close();
        return resultList;
    }

    @Override
    public long getAccountCount() {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT COUNT(*) FROM Account a";

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        long result = query.getSingleResult();
        entityManager.close();
        return result;
    }

    @Override
    public List<Account> getAccountList(String keyword, int offset, int limit) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT a FROM Account a" +
                " WHERE UPPER(a.email) LIKE UPPER(:keyword)" +
                " OR UPPER(a.accountDetail.nickname) LIKE UPPER(:keyword)" +
                " ORDER BY a.email";

        TypedQuery<Account> query = entityManager.createQuery(jpql, Account.class)
                .setParameter("keyword", "%" +  StringUtils.escape(keyword) + "%")
                .setFirstResult(offset)
                .setMaxResults(limit);
        List<Account> resultList = query.getResultList();
        entityManager.close();
        return resultList;
    }

    @Override
    public long getAccountCount(String keyword) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT COUNT(*) FROM Account a" +
                " WHERE UPPER(a.email) LIKE UPPER(:keyword)" +
                " OR UPPER(a.accountDetail.nickname) LIKE UPPER(:keyword)";

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
                .setParameter("keyword", "%" + StringUtils.escape(keyword) + "%");
        long result = query.getSingleResult();
        entityManager.close();
        return result;
    }

    @Override
    public Optional<AccountDetail> getAccountDetail(Account account) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT ad FROM AccountDetail ad" +
                " WHERE ad.account = :account" +
                " ORDER BY ad.createDate DESC";

        TypedQuery<AccountDetail> query = entityManager.createQuery(jpql, AccountDetail.class)
                .setParameter("account", account);
        List<AccountDetail> accountDetailList = query.getResultList();
        entityManager.close();
        if (accountDetailList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(accountDetailList.get(0));
        }
    }

    @Override
    public Map<Account, AccountDetail> getAccountDetailList(List<Account> accountList) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT ad FROM AccountDetail ad" +
                " WHERE ad.account IN :accounts" +
                " ORDER BY ad.createDate DESC";

        TypedQuery<AccountDetail> query = entityManager.createQuery(jpql, AccountDetail.class)
                .setParameter("accounts", accountList);
        List<AccountDetail> accountDetailList = query.getResultList();
        entityManager.close();

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
    public boolean existAccount(List<Long> accountIdList) {
        Set<Long> accountIdSet = new HashSet<>(accountIdList);
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT COUNT(*) FROM Account a WHERE a.id IN (:accountIdSet)";

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
                .setParameter("accountIdSet", accountIdSet);

        long result = query.getSingleResult();

        if (accountIdSet.size() == result) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Optional<Account> getAccountByNickname(String nickname) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT a FROM Account a" +
                " WHERE a.accountDetail IS NOT NULL" +
                " AND a.accountDetail.nickname = :nickname";

        TypedQuery<Account> query = entityManager.createQuery(jpql, Account.class)
                .setParameter("nickname", nickname);
        List<Account> list = query.getResultList();
        entityManager.close();
        if (list.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(list.get(0));
        }
    }

    @Override
    public void saveAccount(Account account) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        if (account.getId() == null) {
            entityManager.persist(account);
        } else {
            entityManager.merge(account);
        }
        transaction.commit();
        entityManager.close();
    }

    @Override
    public void saveAccountDetail(AccountDetail accountDetail) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        if (accountDetail.getId() == null) {
            entityManager.persist(accountDetail);
        } else {
            entityManager.merge(accountDetail);
        }
        transaction.commit();
        entityManager.close();
    }
}

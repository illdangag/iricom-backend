package com.illdangag.iricom.server.repository.implement;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.exception.IricomErrorCode;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.repository.AccountGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        Optional<AccountGroup> accountGroupOptional = this.getAccountGroup(entityManager, id);
        entityManager.close();
        return accountGroupOptional;
    }

    private Optional<AccountGroup> getAccountGroup(EntityManager entityManager, long id) {
        final String jpql = "SELECT ag FROM AccountGroup ag WHERE ag.id = :id";

        TypedQuery<AccountGroup> query = entityManager.createQuery(jpql, AccountGroup.class)
                .setParameter("id", id);

        List<AccountGroup> resultList = query.getResultList();

        if (resultList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(resultList.get(0));
        }
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
        final String jpql = "SELECT biag.board FROM BoardInAccountGroup biag WHERE biag.accountGroup = :accountGroup";

        TypedQuery<Board> query = entityManager.createQuery(jpql, Board.class)
                .setParameter("accountGroup", accountGroup);

        List<Board> resultList = query.getResultList();
        entityManager.close();
        return resultList;
    }

    @Override
    public void saveAccountGroup(AccountGroup accountGroup, List<AccountInAccountGroup> accountInAccountGroupList, List<BoardInAccountGroup> boardInAccountGroupList) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        // 계정 그룹 저장
        entityManager.persist(accountGroup);

        // 계정 정보 저장
        accountInAccountGroupList.forEach(entityManager::persist);

        // 게시판 정보 저장
        boardInAccountGroupList.forEach(entityManager::persist);

        entityTransaction.commit();
        entityManager.close();
    }

    @Override
    public void updateAccountGroup(AccountGroup accountGroup, List<AccountInAccountGroup> accountInAccountGroupList, List<BoardInAccountGroup> boardInAccountGroupList) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();

        if (accountGroup.getId() == null) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_ACCOUNT_GROUP);
        }

        Optional<AccountGroup> accountGroupOptional = this.getAccountGroup(entityManager, accountGroup.getId());
        if (accountGroupOptional.isEmpty()) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_ACCOUNT_GROUP);
        }

        // 계정 그룹에 추가할 계정과 제거할 계정 처리
        List<AccountInAccountGroup> addAccountInAccountGroupList;
        List<AccountInAccountGroup> removeAccountInAccountGroupList;
        if (accountInAccountGroupList != null) {
            List<AccountInAccountGroup> dbAccountInAccountGroupList = this.getAccountInAccountGroupList(entityManager, accountGroup);
            addAccountInAccountGroupList = accountInAccountGroupList.stream()
                    .filter(item -> !dbAccountInAccountGroupList.contains(item)).collect(Collectors.toList());
            removeAccountInAccountGroupList = dbAccountInAccountGroupList.stream()
                    .filter(item -> !accountInAccountGroupList.contains(item)).collect(Collectors.toList());
        } else {
            addAccountInAccountGroupList = Collections.emptyList();
            removeAccountInAccountGroupList = Collections.emptyList();
        }

        // 계정 그룹에 추가할 게시물과 제거할 게시물 처리
        List<BoardInAccountGroup> addBoardInAccountGroupList;
        List<BoardInAccountGroup> removeBoardInAccountGroupList;
        if (boardInAccountGroupList != null) {
            List<BoardInAccountGroup> dbBoardInAccountGroupList = this.getBoardInAccountGroupList(entityManager, accountGroup);
            addBoardInAccountGroupList = boardInAccountGroupList.stream()
                    .filter(item -> !dbBoardInAccountGroupList.contains(item)).collect(Collectors.toList());
            removeBoardInAccountGroupList = dbBoardInAccountGroupList.stream()
                    .filter(item -> !boardInAccountGroupList.contains(item)).collect(Collectors.toList());
        } else {
            addBoardInAccountGroupList = Collections.emptyList();
            removeBoardInAccountGroupList = Collections.emptyList();
        }

        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        entityManager.merge(accountGroup);
        addAccountInAccountGroupList.forEach(item -> entityManager.persist(item));
        removeAccountInAccountGroupList.forEach(item -> entityManager.remove(item));
        addBoardInAccountGroupList.forEach(item -> entityManager.persist(item));
        removeBoardInAccountGroupList.forEach(item -> entityManager.remove(item));

        entityTransaction.commit();
        entityManager.close();
    }

    @Override
    public void updateAccountGroup(AccountGroup accountGroup) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        entityManager.merge(accountGroup);

        entityTransaction.commit();
        entityManager.close();
    }

    private List<AccountInAccountGroup> getAccountInAccountGroupList(EntityManager entityManager, AccountGroup accountGroup) {
        final String jpql = "SELECT aiag FROM AccountInAccountGroup aiag WHERE aiag.accountGroup = :accountGroup";

        TypedQuery<AccountInAccountGroup> query = entityManager.createQuery(jpql, AccountInAccountGroup.class)
                .setParameter("accountGroup", accountGroup);
        return query.getResultList();
    }

    private List<BoardInAccountGroup> getBoardInAccountGroupList(EntityManager entityManager, AccountGroup accountGroup) {
        final String jpql = "SELECT biag FROM BoardInAccountGroup biag WHERE biag.accountGroup = :accountGroup";

        TypedQuery<BoardInAccountGroup> query = entityManager.createQuery(jpql, BoardInAccountGroup.class)
                .setParameter("accountGroup", accountGroup);
        return query.getResultList();
    }

    @Override
    public List<AccountGroup> getAccountGroupList(int skip, int limit) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT ag FROM AccountGroup ag WHERE ag.deleted = false";

        TypedQuery<AccountGroup> query = entityManager.createQuery(jpql, AccountGroup.class)
                .setFirstResult(skip)
                .setMaxResults(limit);
        List<AccountGroup> resultList = query.getResultList();
        entityManager.close();
        return resultList;
    }

    @Override
    public long getAccountGroupCount() {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT COUNT(*) FROM AccountGroup ag WHERE ag.deleted = false";

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        long result = query.getSingleResult();
        entityManager.close();
        return result;
    }
}

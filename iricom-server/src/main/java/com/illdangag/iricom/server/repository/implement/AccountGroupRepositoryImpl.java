package com.illdangag.iricom.server.repository.implement;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.exception.IricomErrorCode;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.repository.AccountGroupRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Repository
public class AccountGroupRepositoryImpl implements AccountGroupRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<AccountGroup> getAccountGroup(long id) {
        final String jpql = "SELECT ag FROM AccountGroup ag WHERE ag.id = :id";

        TypedQuery<AccountGroup> query = this.entityManager.createQuery(jpql, AccountGroup.class)
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
        final String jpql = "SELECT aiag.account FROM AccountInAccountGroup aiag WHERE aiag.accountGroup = :accountGroup";

        TypedQuery<Account> query = this.entityManager.createQuery(jpql, Account.class)
                .setParameter("accountGroup", accountGroup);

        List<Account> resultList = query.getResultList();
        return resultList;
    }

    @Override
    public List<Board> getBoardListInAccountGroup(AccountGroup accountGroup) {
        final String jpql = "SELECT biag.board FROM BoardInAccountGroup biag WHERE biag.accountGroup = :accountGroup";

        TypedQuery<Board> query = this.entityManager.createQuery(jpql, Board.class)
                .setParameter("accountGroup", accountGroup);

        List<Board> resultList = query.getResultList();
        return resultList;
    }

    @Override
    public void saveAccountGroup(AccountGroup accountGroup, List<AccountInAccountGroup> accountInAccountGroupList, List<BoardInAccountGroup> boardInAccountGroupList) {
        // 계정 그룹 저장
        this.entityManager.persist(accountGroup);

        // 계정 정보 저장
        accountInAccountGroupList.forEach(entityManager::persist);

        // 게시판 정보 저장
        boardInAccountGroupList.forEach(entityManager::persist);
    }

    @Override
    public void updateAccountGroup(AccountGroup accountGroup, List<AccountInAccountGroup> accountInAccountGroupList, List<BoardInAccountGroup> boardInAccountGroupList) {
        if (accountGroup.getId() == null) {
            throw new IricomException(IricomErrorCode.NOT_EXIST_ACCOUNT_GROUP);
        }

        Optional<AccountGroup> accountGroupOptional = this.getAccountGroup(accountGroup.getId());
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

        this.entityManager.merge(accountGroup);
        addAccountInAccountGroupList.forEach(item -> this.entityManager.persist(item));
        removeAccountInAccountGroupList.forEach(item -> this.entityManager.remove(item));
        addBoardInAccountGroupList.forEach(item -> this.entityManager.persist(item));
        removeBoardInAccountGroupList.forEach(item -> this.entityManager.remove(item));
    }

    @Override
    public void updateAccountGroup(AccountGroup accountGroup) {
        this.entityManager.merge(accountGroup);
    }

    private List<AccountInAccountGroup> getAccountInAccountGroupList(EntityManager entityManager, AccountGroup accountGroup) {
        final String jpql = "SELECT aiag FROM AccountInAccountGroup aiag WHERE aiag.accountGroup = :accountGroup";

        TypedQuery<AccountInAccountGroup> query = this.entityManager.createQuery(jpql, AccountInAccountGroup.class)
                .setParameter("accountGroup", accountGroup);
        return query.getResultList();
    }

    private List<BoardInAccountGroup> getBoardInAccountGroupList(EntityManager entityManager, AccountGroup accountGroup) {
        final String jpql = "SELECT biag FROM BoardInAccountGroup biag WHERE biag.accountGroup = :accountGroup";

        TypedQuery<BoardInAccountGroup> query = this.entityManager.createQuery(jpql, BoardInAccountGroup.class)
                .setParameter("accountGroup", accountGroup);
        return query.getResultList();
    }

    @Override
    public List<AccountGroup> getAccountGroupList(int skip, int limit) {
        final String jpql = "SELECT ag FROM AccountGroup ag";

        TypedQuery<AccountGroup> query = this.entityManager.createQuery(jpql, AccountGroup.class)
                .setFirstResult(skip)
                .setMaxResults(limit);
        List<AccountGroup> resultList = query.getResultList();
        return resultList;
    }

    @Override
    public long getAccountGroupCount() {
        final String jpql = "SELECT COUNT(*) FROM AccountGroup ag";

        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class);
        long result = query.getSingleResult();
        return result;
    }

    @Override
    public void removeAccountGroup(AccountGroup accountGroup) {
        List<AccountInAccountGroup> dbAccountInAccountGroupList = this.getAccountInAccountGroupList(entityManager, accountGroup);
        List<BoardInAccountGroup> dbBoardInAccountGroupList = this.getBoardInAccountGroupList(entityManager, accountGroup);

        dbAccountInAccountGroupList.forEach(entityManager::remove);
        dbBoardInAccountGroupList.forEach(entityManager::remove);

        AccountGroup dbAccountGroup = this.entityManager.find(AccountGroup.class, accountGroup.getId());
        this.entityManager.remove(dbAccountGroup);
    }
}

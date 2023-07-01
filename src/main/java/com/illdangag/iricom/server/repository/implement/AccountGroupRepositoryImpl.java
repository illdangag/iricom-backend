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
        final String jpql = "SELECT biag.board FROM BoardInAccountGroup biag WHERE biag.accountGroup = :accountGroup";

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

        List<AccountInAccountGroup> dbAccountInAccountGroupList = this.getAccountInAccountGroupList(entityManager, accountGroup);
        List<AccountInAccountGroup> addAccountInAccountGroupList = accountInAccountGroupList.stream()
                .filter(item -> !dbAccountInAccountGroupList.contains(item)).collect(Collectors.toList());
        List<AccountInAccountGroup> removeAccountInAccountGroupList = dbAccountInAccountGroupList.stream()
                .filter(item -> !accountInAccountGroupList.contains(item)).collect(Collectors.toList());

        List<BoardInAccountGroup> dbBoardInAccountGroupList = this.getBoardInAccountGroupList(entityManager, accountGroup);
        List<BoardInAccountGroup> addBoardInAccountGroupList = boardInAccountGroupList.stream()
                .filter(item -> !dbBoardInAccountGroupList.contains(item)).collect(Collectors.toList());
        List<BoardInAccountGroup> removeBoardInAccountGroupList = dbBoardInAccountGroupList.stream()
                .filter(item -> !boardInAccountGroupList.contains(item)).collect(Collectors.toList());


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
}

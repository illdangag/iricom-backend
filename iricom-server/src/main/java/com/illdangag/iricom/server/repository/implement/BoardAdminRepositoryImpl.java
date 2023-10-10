package com.illdangag.iricom.server.repository.implement;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.Board;
import com.illdangag.iricom.server.data.entity.BoardAdmin;
import com.illdangag.iricom.server.repository.BoardAdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

// TODO 유효한 게시판 관리자 목록 메서드 정리
@Repository
public class BoardAdminRepositoryImpl implements BoardAdminRepository {
    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    public BoardAdminRepositoryImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public List<BoardAdmin> getBoardAdminList(List<Board> boardList) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT ba FROM BoardAdmin ba" +
                " WHERE ba.board IN :boards" +
                " ORDER BY ba.board.title ASC, ba.account.email ASC, ba.createDate DESC";

        TypedQuery<BoardAdmin> query = entityManager.createQuery(jpql, BoardAdmin.class);
        query.setParameter("boards", boardList);
        List<BoardAdmin> resultList = query.getResultList();
        entityManager.close();
        return resultList;
    }

    private List<LocalDateTime> getLastBoardAdminCreateDateList(EntityManager entityManager, Account account) {
        final String maxJpql = "SELECT new Map(ba.account AS account, ba.board AS board, MAX(ba.createDate) AS createDate)" +
                " FROM BoardAdmin ba" +
                " WHERE ba.account = :account" +
                " GROUP BY ba.account, ba.board";
        Query query = entityManager.createQuery(maxJpql)
                .setParameter("account", account);
        List<Map<String, Object>> maxCreateDateResultList = query.getResultList();
        return maxCreateDateResultList.stream().map(item -> (LocalDateTime) item.get("createDate"))
                .collect(Collectors.toList());
    }

    @Override
    public List<BoardAdmin> getLastBoardAdminList(Account account) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        List<LocalDateTime> createDateList = this.getLastBoardAdminCreateDateList(entityManager, account);

        final String jpql = "SELECT ba" +
                " FROM BoardAdmin ba" +
                " WHERE ba.account = :account" +
                " AND ba.createDate IN :createDateList";
        TypedQuery<BoardAdmin> query = entityManager.createQuery(jpql, BoardAdmin.class)
                .setParameter("account", account)
                .setParameter("createDateList", createDateList);
        List<BoardAdmin> boardAdminList = query.getResultList();
        entityManager.close();
        return boardAdminList;
    }

    @Override
    public Optional<BoardAdmin> getLastBoardAdmin(Account account, Board board, boolean deleted) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        List<LocalDateTime> createDateList = this.getLastBoardAdminCreateDateList(entityManager, account);

        final String jpql = "SELECT ba" +
                " FROM BoardAdmin ba" +
                " WHERE ba.account = :account" +
                " AND ba.board = :board " +
                " AND ba.deleted = :deleted" +
                " AND ba.createDate IN :createDateList";
        TypedQuery<BoardAdmin> query = entityManager.createQuery(jpql, BoardAdmin.class)
                .setParameter("account", account)
                .setParameter("board", board)
                .setParameter("deleted", deleted)
                .setParameter("createDateList", createDateList);
        List<BoardAdmin> boardAdminList = query.getResultList();
        entityManager.close();
        if (boardAdminList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(boardAdminList.get(0));
        }
    }

    @Override
    public List<BoardAdmin> getLastBoardAdminList(Account account, boolean deleted, int offset, int limit) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        List<LocalDateTime> createDateList = this.getLastBoardAdminCreateDateList(entityManager, account);

        final String jpql = "SELECT ba" +
                " FROM BoardAdmin ba" +
                " WHERE ba.account = :account" +
                " AND ba.createDate IN :createDateList" +
                " AND ba.deleted = :deleted";
        TypedQuery<BoardAdmin> query = entityManager.createQuery(jpql, BoardAdmin.class)
                .setParameter("account", account)
                .setParameter("createDateList", createDateList)
                .setParameter("deleted", deleted)
                .setFirstResult(offset)
                .setMaxResults(limit);

        List<BoardAdmin> boardAdminList = query.getResultList();
        entityManager.close();
        return boardAdminList;
    }

    @Override
    public long getLastBoardAdminCount(Account account, boolean deleted) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        List<LocalDateTime> createDateList = this.getLastBoardAdminCreateDateList(entityManager, account);

        final String jpql = "SELECT COUNT(*)" +
                " FROM BoardAdmin ba" +
                " WHERE ba.account = :account" +
                " AND ba.createDate IN :createDateList" +
                " AND ba.deleted = :deleted";
        TypedQuery<Long> countQuery = entityManager.createQuery(jpql, Long.class)
                .setParameter("account", account)
                .setParameter("createDateList", createDateList)
                .setParameter("deleted", deleted);

        long result = countQuery.getSingleResult();
        entityManager.close();
        return result;
    }

    @Override
    public List<BoardAdmin> getBoardAdminList(Account account, boolean deleted) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT ba FROM BoardAdmin ba" +
                " WHERE ba.account = :account" +
                " AND ba.deleted = :deleted" +
                " ORDER BY ba.createDate DESC";

        TypedQuery<BoardAdmin> query = entityManager.createQuery(jpql, BoardAdmin.class)
                .setParameter("account", account)
                .setParameter("deleted", deleted);
        List<BoardAdmin> resultList = query.getResultList();
        entityManager.close();
        return resultList;
    }

    @Override
    public List<BoardAdmin> getBoardAdminList(Board board, Account account) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT ba FROM BoardAdmin ba" +
                " WHERE ba.board = :board" +
                " AND ba.account = :account" +
                " ORDER BY ba.createDate DESC";

        TypedQuery<BoardAdmin> query = entityManager.createQuery(jpql, BoardAdmin.class);
        query.setParameter("board", board)
                .setParameter("account", account);
        List<BoardAdmin> resultList = query.getResultList();
        entityManager.close();
        return resultList;
    }

    @Override
    public Optional<BoardAdmin> getEnableBoardAdmin(Board board, Account account) {
        BoardAdmin boardAdmin = null;

        List<BoardAdmin> boardAdminList = this.getBoardAdminList(board, account);

        if (!boardAdminList.isEmpty() && !boardAdminList.get(0).getDeleted()) {
            boardAdmin = boardAdminList.get(0);
        }

        return Optional.ofNullable(boardAdmin);
    }

    @Override
    public Optional<BoardAdmin> getBoardAdmin(Board board, Account account) {
        List<BoardAdmin> boardAdminList = this.getBoardAdminList(board, account);
        return boardAdminList.isEmpty() ? Optional.empty() : Optional.of(boardAdminList.get(0));
    }

    @Override
    public void save(BoardAdmin boardAdmin) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        if (boardAdmin.getId() == null) {
            entityManager.persist(boardAdmin);
        } else {
            entityManager.merge(boardAdmin);
        }
        transaction.commit();
        entityManager.close();
    }
}

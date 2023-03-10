package com.illdangag.iricom.server.repository.implement;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.Board;
import com.illdangag.iricom.server.data.entity.BoardAdmin;
import com.illdangag.iricom.server.repository.BoardAdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

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
        final String jpql = "SELECT ba FROM BoardAdmin ba " +
                "WHERE ba.board IN (:boards) " +
                "ORDER BY ba.board.title ASC, ba.account.email ASC, ba.createDate DESC";
        TypedQuery<BoardAdmin> query = entityManager.createQuery(jpql, BoardAdmin.class);
        query.setParameter("boards", boardList);
        List<BoardAdmin> resultList = query.getResultList();
        entityManager.close();
        return resultList;
    }

    @Override
    public List<BoardAdmin> getBoardAdminList(Account account, boolean deleted) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT ba FROM BoardAdmin ba " +
                "WHERE ba.account = :account " +
                "AND ba.deleted = :deleted " +
                "ORDER BY ba.createDate DESC";
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
        final String jpql = "SELECT ba FROM BoardAdmin ba " +
                "WHERE ba.board = :board " +
                "AND ba.account = :account " +
                "ORDER BY ba.createDate DESC";
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

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
    private final EntityManager entityManager;

    @Autowired
    public BoardAdminRepositoryImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManager = entityManagerFactory.createEntityManager();
    }

    @Override
    public List<BoardAdmin> getBoardAdminList(List<Board> boardList) {
        final String jpql = "SELECT ba FROM BoardAdmin ba " +
                "WHERE ba.board IN (:boards) " +
                "ORDER BY ba.board.title ASC, ba.account.email ASC, ba.createDate DESC";
        TypedQuery<BoardAdmin> query = this.entityManager.createQuery(jpql, BoardAdmin.class);
        query.setParameter("boards", boardList);
        return query.getResultList();
    }

    @Override
    public List<BoardAdmin> getBoardAdminList(Account account) {
        final String jpql = "SELECT ba FROM BoardAdmin ba " +
                "WHERE ba.account = :admin " +
                "ORDER BY ba.createDate DESC";
        TypedQuery<BoardAdmin> query = this.entityManager.createQuery(jpql, BoardAdmin.class);
        query.setParameter("admin", account);
        return query.getResultList();
    }

    @Override
    public List<BoardAdmin> getBoardAdminList(Board board, Account account) {
        final String jpql = "SELECT ba FROM BoardAdmin ba " +
                "WHERE ba.board = :board " +
                "AND ba.account = :account " +
                "ORDER BY ba.createDate DESC";
        TypedQuery<BoardAdmin> query = this.entityManager.createQuery(jpql, BoardAdmin.class);
        query.setParameter("board", board)
                .setParameter("account", account);
        return query.getResultList();
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
        EntityTransaction transaction = this.entityManager.getTransaction();
        transaction.begin();
        if (boardAdmin.getId() == null) {
            this.entityManager.persist(boardAdmin);
        } else {
            this.entityManager.merge(boardAdmin);
        }
        transaction.commit();
    }
}

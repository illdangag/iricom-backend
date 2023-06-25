package com.illdangag.iricom.server.repository.implement;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.Board;
import com.illdangag.iricom.server.repository.BoardRepository;
import com.illdangag.iricom.server.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class BoardRepositoryImpl implements BoardRepository {
    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    public BoardRepositoryImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public long getBoardCount() {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT COUNT(*) FROM Board b";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        long result = query.getSingleResult();
        entityManager.close();
        return result;
    }

    @Override
    public List<Board> getBoardList(String id) {
        long boardId = -1;
        try {
            boardId = Long.parseLong(id);
        } catch (Exception exception) {
            return Collections.emptyList();
        }

        return this.getBoardList(boardId);
    }

    @Override
    public List<Board> getBoardList(long id) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT b FROM Board b" +
                " WHERE b.id = :id";

        TypedQuery<Board> query = entityManager.createQuery(jpql, Board.class);
        query.setParameter("id", id);
        List<Board> resultList = query.getResultList();
        entityManager.close();
        return resultList;
    }

    @Override
    public List<Board> getBoardList(String title, int offset, int limit) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT b FROM Board b" +
                " WHERE UPPER(b.title) LIKE UPPER(:title)" +
                " ORDER BY title";

        TypedQuery<Board> query = entityManager.createQuery(jpql, Board.class);
        query.setParameter("title", "%" + StringUtils.escape(title) + "%")
                .setFirstResult(offset)
                .setMaxResults(limit);
        List<Board> resultList = query.getResultList();
        entityManager.close();
        return resultList;
    }

    @Override
    public long getBoardCount(String title) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT COUNT(*) FROM Board b" +
                " WHERE UPPER(b.title) LIKE UPPER(:title)";

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("title", "%" + StringUtils.escape(title) + "%");
        long result = query.getSingleResult();
        entityManager.close();
        return result;
    }

    @Override
    public List<Board> getBoardList(String title, boolean enabled, int offset, int limit) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT b FROM Board b" +
                " WHERE UPPER(b.title) LIKE UPPER(:title)" +
                " AND b.enabled = :enabled";

        TypedQuery<Board> query = entityManager.createQuery(jpql, Board.class);
        query.setParameter("title", "%" + StringUtils.escape(title) + "%")
                .setParameter("enabled", enabled)
                .setFirstResult(offset)
                .setMaxResults(limit);
        List<Board> resultList = query.getResultList();
        entityManager.close();
        return resultList;
    }

    @Override
    public long getBoardCount(String title, boolean enabled) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT COUNT(*) FROM Board b" +
                " WHERE UPPER(b.title) LIKE UPPER(:title)" +
                " AND b.enabled = :enabled";

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("title", "%" + StringUtils.escape(title) + "%")
                .setParameter("enabled", enabled);
        long result = query.getSingleResult();
        entityManager.close();
        return result;
    }

    @Override
    public List<Board> getBoardList(Account account, String title, boolean enabled, int offset, int limit) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();

        List<Long> boardIdList = this.getBoardIdByAccount(entityManager, account);

        final String jpql = "SELECT b FROM Board b" +
                " WHERE (UPPER(b.title) LIKE UPPER(:title) AND b.enabled = :enabled AND b.undisclosed = false)" +
                " OR (UPPER(b.title) LIKE UPPER(:title) AND b.enabled = :enabled AND b.id IN (:boardId))";

        TypedQuery<Board> query = entityManager.createQuery(jpql, Board.class);
        query.setParameter("title", "%" + StringUtils.escape(title) + "%")
                .setParameter("enabled", enabled)
                .setParameter("boardId", boardIdList)
                .setFirstResult(offset)
                .setMaxResults(limit);
        List<Board> resultList = query.getResultList();
        entityManager.close();
        return resultList;
    }

    @Override
    public long getBoardCount(Account account, String title, boolean enabled) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();

        List<Long> boardIdList = this.getBoardIdByAccount(entityManager, account);

        final String jpql = "SELECT COUNT(*) FROM Board b" +
                " WHERE (UPPER(b.title) LIKE UPPER(:title) AND b.enabled = :enabled AND b.undisclosed = false)" +
                " OR (UPPER(b.title) LIKE UPPER(:title) AND b.enabled = :enabled AND b.id IN (:boardId))";

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
                .setParameter("title", "%" + StringUtils.escape(title) + "%")
                .setParameter("enabled", enabled)
                .setParameter("boardId", boardIdList);
        Long result = query.getSingleResult();
        entityManager.close();
        return result;
    }

    private List<Long> getBoardIdByAccount(EntityManager entityManager, Account account) {
        final String jpql = "SELECT b.id FROM Board b RIGHT JOIN UndisclosedBoardAccount uba ON b = uba.board WHERE uba.account = :account";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
                .setParameter("account", account);
        return query.getResultList();
    }

//    private void testQuery00(Account account) {
//        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
//        final String jpql = "SELECT b FROM Board b RIGHT JOIN UndisclosedBoardAccount uba ON b = uba.board";
//
//        Query query = entityManager.createQuery(jpql);
//
//        List<Object[]> resultList = query.getResultList();
//        entityManager.close();
//    }
//
//    private void testQuery01(Account account) {
//        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
//        final String jpql = "SELECT b FROM Board b RIGHT JOIN UndisclosedBoardAccount uba ON b = uba.board WHERE uba.account = :account";
//
//        TypedQuery<Board> query = entityManager.createQuery(jpql, Board.class)
//                .setParameter("account", account);
//
//        List<Board> resultList = query.getResultList();
//        entityManager.close();
//    }
//
//    private void testQuery02(Account account) {
//        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
//        final String jpql = "SELECT b.id FROM Board b RIGHT JOIN UndisclosedBoardAccount uba ON b = uba.board WHERE uba.account = :account";
//
//        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
//                .setParameter("account", account);
//
//        List<Long> resultList = query.getResultList();
//        entityManager.close();
//    }
//
//    private void testQuery(Account account) {
//        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
//        final String jpql = "SELECT b.id FROM Board b RIGHT JOIN UndisclosedBoardAccount uba ON b = uba.board WHERE uba.account = :account";
//
//        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
//                .setParameter("account", account);
//
//        List<Long> resultList = query.getResultList();
//        entityManager.close();
//    }

    @Override
    public Optional<Board> getBoard(String id) {
        long boardId = -1;
        try {
            boardId = Long.parseLong(id);
        } catch (Exception exception) {
            return Optional.empty();
        }

        return this.getBoard(boardId);
    }

    @Override
    public Optional<Board> getBoard(long id) {
        List<Board> boardList = getBoardList(id);
        if (boardList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(boardList.get(0));
        }
    }

    @Override
    public void save(Board board) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();
        if (board.getId() == null) {
            entityManager.persist(board);
        } else {
            entityManager.merge(board);
        }
        entityTransaction.commit();
        entityManager.close();
    }

    @Override
    public void saveAll(Collection<Board> boards) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();
        for (Board board : boards) {
            if (board.getId() == null) {
                entityManager.persist(board);
            } else {
                entityManager.merge(board);
            }
        }
        entityTransaction.commit();
        entityManager.close();
    }
}

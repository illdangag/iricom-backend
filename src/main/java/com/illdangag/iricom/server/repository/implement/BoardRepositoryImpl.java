package com.illdangag.iricom.server.repository.implement;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.Board;
import com.illdangag.iricom.server.repository.BoardRepository;
import com.illdangag.iricom.server.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class BoardRepositoryImpl implements BoardRepository {
    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    public BoardRepositoryImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Optional<Board> getDisclosedBoard(Account account, long id) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();


        final String jpql = "SELECT b FROM Board b" +
                " WHERE b.id = :id AND b.undisclosed = false";

        TypedQuery<Board> query = entityManager.createQuery(jpql, Board.class)
                .setParameter("id", id);
        List<Board> resultList = query.getResultList();
        entityManager.close();

        if (resultList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(resultList.get(0));
        }
    }

    @Override
    public List<Board> getBoardList(String title, int offset, int limit) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT b FROM Board b" +
                " WHERE UPPER(b.title) LIKE UPPER(:title) AND b.undisclosed = false" +
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
                " WHERE UPPER(b.title) LIKE UPPER(:title) AND b.undisclosed = false";

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
                " WHERE UPPER(b.title) LIKE UPPER(:title) AND b.enabled = :enabled AND b.undisclosed = false";

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
                " WHERE UPPER(b.title) LIKE UPPER(:title) AND b.enabled = :enabled AND b.undisclosed = false";

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

        final String jpql = "SELECT b FROM Board b" +
                " WHERE (UPPER(b.title) LIKE UPPER(:title) AND b.enabled = :enabled AND b.undisclosed = false)";

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
    public long getBoardCount(Account account, String title, boolean enabled) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();

        final String jpql = "SELECT COUNT(*) FROM Board b" +
                " WHERE (UPPER(b.title) LIKE UPPER(:title) AND b.enabled = :enabled AND b.undisclosed = false)";

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
                .setParameter("title", "%" + StringUtils.escape(title) + "%")
                .setParameter("enabled", enabled);
        Long result = query.getSingleResult();
        entityManager.close();
        return result;
    }

    @Override
    public List<Board> getBoardList(Account account, String title, int offset, int limit) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();

        final String jpql = "SELECT b FROM Board b" +
                " WHERE (UPPER(b.title) LIKE UPPER(:title) AND b.undisclosed = false)";

        TypedQuery<Board> query = entityManager.createQuery(jpql, Board.class);
        query.setParameter("title", "%" + StringUtils.escape(title) + "%")
                .setFirstResult(offset)
                .setMaxResults(limit);
        List<Board> resultList = query.getResultList();
        entityManager.close();
        return resultList;
    }

    @Override
    public long getBoardCount(Account account, String title) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();

        final String jpql = "SELECT COUNT(*) FROM Board b" +
                " WHERE (UPPER(b.title) LIKE UPPER(:title) AND b.undisclosed = false)";

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
                .setParameter("title", "%" + StringUtils.escape(title) + "%");
        Long result = query.getSingleResult();
        entityManager.close();
        return result;
    }

    @Override
    public Optional<Board> getBoard(long id) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT b FROM Board b" +
                " WHERE b.id = :id";

        TypedQuery<Board> query = entityManager.createQuery(jpql, Board.class);
        query.setParameter("id", id);
        List<Board> resultList = query.getResultList();
        entityManager.close();

        if (resultList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(resultList.get(0));
        }
    }

    @Override
    public Optional<Board> getDisclosedBoard(long id) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT b FROM Board b" +
                " WHERE b.id = :id AND b.undisclosed = false";

        TypedQuery<Board> query = entityManager.createQuery(jpql, Board.class);
        query.setParameter("id", id);
        List<Board> resultList = query.getResultList();
        entityManager.close();

        if (resultList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(resultList.get(0));
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
}

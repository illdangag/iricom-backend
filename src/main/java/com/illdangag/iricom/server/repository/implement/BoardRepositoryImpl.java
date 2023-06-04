package com.illdangag.iricom.server.repository.implement;

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
        final String jpql = "SELECT b FROM Board b WHERE b.id = :id";
        TypedQuery<Board> query = entityManager.createQuery(jpql, Board.class);
        query.setParameter("id", id);
        List<Board> resultList = query.getResultList();
        entityManager.close();
        return resultList;
    }

    @Override
    public List<Board> getBoardList(String containTitle, int offset, int limit) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT b FROM Board b WHERE b.title LIKE :title ORDER BY title";
        TypedQuery<Board> query = entityManager.createQuery(jpql, Board.class);
        query.setParameter("title", "%" + StringUtils.escape(containTitle) + "%")
                .setFirstResult(offset)
                .setMaxResults(limit);
        List<Board> resultList = query.getResultList();
        entityManager.close();
        return resultList;
    }

    @Override
    public long getBoardCount(String containTitle) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT COUNT(*)FROM Board b WHERE b.title LIKE :title";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("title", "%" + StringUtils.escape(containTitle) + "%");
        long result = query.getSingleResult();
        entityManager.close();
        return result;
    }

    @Override
    public List<Board> getBoardList(String containTitle, boolean enabled, int offset, int limit) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT b FROM Board b WHERE b.title LIKE :title AND b.enabled = :enabled";
        TypedQuery<Board> query = entityManager.createQuery(jpql, Board.class);
        query.setParameter("title", "%" + StringUtils.escape(containTitle) + "%")
                .setParameter("enabled", enabled)
                .setFirstResult(offset)
                .setMaxResults(limit);
        List<Board> resultList = query.getResultList();
        entityManager.close();
        return resultList;
    }

    @Override
    public long getBoardCount(String containTitle, boolean enabled) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT COUNT(*) FROM Board b WHERE b.title LIKE :title AND b.enabled = :enabled";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("title", "%" + StringUtils.escape(containTitle) + "%")
                .setParameter("enabled", enabled);
        long result = query.getSingleResult();
        entityManager.close();
        return result;
    }

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

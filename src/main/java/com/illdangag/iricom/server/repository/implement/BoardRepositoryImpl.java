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
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class BoardRepositoryImpl implements BoardRepository {
    private final EntityManager entityManager;

    @Autowired
    public BoardRepositoryImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManager = entityManagerFactory.createEntityManager();
    }

    @Override
    public long getBoardCount() {
        this.entityManager.clear();
        final String jpql = "SELECT COUNT(*) FROM Board b";
        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class);
        return query.getSingleResult();
    }

    @Override
    public List<Board> getBoardList(long id) {
        this.entityManager.clear();
        final String jpql = "SELECT b FROM Board b WHERE b.id = :id";
        TypedQuery<Board> query = this.entityManager.createQuery(jpql, Board.class);
        query.setParameter("id", id);
        return query.getResultList();
    }

    @Override
    public List<Board> getBoardList(String containTitle, int offset, int limit) {
        this.entityManager.clear();
        final String jpql = "SELECT b FROM Board b WHERE b.title LIKE :title ORDER BY title";
        TypedQuery<Board> query = this.entityManager.createQuery(jpql, Board.class);
        query.setParameter("title", "%" + StringUtils.escape(containTitle) + "%")
                .setFirstResult(offset)
                .setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    public long getBoardCount(String containTitle) {
        this.entityManager.clear();
        final String jpql = "SELECT COUNT(*)FROM Board b WHERE b.title LIKE :title";
        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class);
        query.setParameter("title", "%" + StringUtils.escape(containTitle) + "%");
        return query.getSingleResult();
    }

    @Override
    public List<Board> getBoardList(String containTitle, boolean enabled, int offset, int limit) {
        this.entityManager.clear();
        final String jpql = "SELECT b FROM Board b WHERE b.title LIKE :title AND b.enabled = :enabled";
        TypedQuery<Board> query = this.entityManager.createQuery(jpql, Board.class);
        query.setParameter("title", "%" + StringUtils.escape(containTitle) + "%")
                .setParameter("enabled", enabled)
                .setFirstResult(offset)
                .setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    public long getBoardCount(String containTitle, boolean enabled) {
        this.entityManager.clear();
        final String jpql = "SELECT COUNT(*) FROM Board b WHERE b.title LIKE :title AND b.enabled = :enabled";
        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class);
        query.setParameter("title", "%" + StringUtils.escape(containTitle) + "%")
                .setParameter("enabled", enabled);
        return query.getSingleResult();
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
        EntityTransaction entityTransaction = this.entityManager.getTransaction();
        entityTransaction.begin();
        if (board.getId() == null) {
            this.entityManager.persist(board);
        } else {
            this.entityManager.merge(board);
        }
        entityTransaction.commit();
    }

    @Override
    public void saveAll(Collection<Board> boards) {
        EntityTransaction entityTransaction = this.entityManager.getTransaction();
        entityTransaction.begin();
        for (Board board : boards) {
            if (board.getId() == null) {
                this.entityManager.persist(board);
            } else {
                this.entityManager.merge(board);
            }
        }
        entityTransaction.commit();
    }
}

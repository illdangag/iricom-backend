package com.illdangag.iricom.server.repository.implement;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.repository.BanRepository;
import com.illdangag.iricom.server.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class BanRepositoryImpl implements BanRepository {
    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    public BanRepositoryImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public List<PostBan> getPostBanList(Post post) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT pb FROM PostBan pb" +
                " WHERE pb.post = :post" +
                " AND pb.enabled = true" +
                " ORDER BY pb.createDate ASC";

        TypedQuery<PostBan> query = entityManager.createQuery(jpql, PostBan.class)
                .setParameter("post", post);
        List<PostBan> resultList = query.getResultList();
        entityManager.close();
        return resultList;
    }

    @Override
    public long getPostBanCount(Post post) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT COUNT(*) FROM PostBan pb" +
                " WHERE pb.post = :post" +
                " AND pb.enabled = true";

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
                .setParameter("post", post);
        long result = query.getSingleResult();
        entityManager.close();
        return result;
    }

    @Override
    public List<PostBan> getPostBanList(Board board, String reason, int offset, int limit) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT pb FROM PostBan pb" +
                " WHERE pb.post.board = :board" +
                " AND pb.enabled = true" +
                " AND UPPER(pb.reason) LIKE UPPER(:reason)" +
                " ORDER BY pb.createDate ASC";

        TypedQuery<PostBan> query = entityManager.createQuery(jpql, PostBan.class)
                .setParameter("board", board)
                .setParameter("reason", "%" + StringUtils.escape(reason) + "%")
                .setFirstResult(offset)
                .setMaxResults(limit);
        List<PostBan> resultList = query.getResultList();
        entityManager.close();
        return resultList;
    }

    @Override
    public long getPostBanListCount(Board board, String reason) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT COUNT(*) FROM PostBan pb" +
                " WHERE pb.post.board = :board" +
                " AND pb.enabled = true" +
                " AND UPPER(pb.reason) LIKE UPPER(:reason)";

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
                .setParameter("board", board)
                .setParameter("reason", "%" + StringUtils.escape(reason) + "%");
        long result = query.getSingleResult();
        entityManager.close();
        return result;
    }

    @Override
    public Optional<PostBan> getPostBan(String id) {
        long postBanId = -1;

        try {
            postBanId = Long.parseLong(id);
        } catch (Exception exception) {
            return Optional.empty();
        }

        return this.getPostBan(postBanId);
    }

    @Override
    public Optional<PostBan> getPostBan(long id) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT pb FROM PostBan pb" +
                " WHERE pb.id = :id";

        TypedQuery<PostBan> query = entityManager.createQuery(jpql, PostBan.class)
                .setParameter("id", id);

        List<PostBan> resultList = query.getResultList();
        entityManager.close();
        if (resultList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(resultList.get(0));
        }
    }

    @Override
    public void save(PostBan postBan) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        if (postBan.getId() == null) {
            entityManager.persist(postBan);
        } else {
            entityManager.merge(postBan);
        }
        transaction.commit();
        entityManager.close();
    }

    @Override
    public List<PostBan> getPostBanList(String reason, int offset, int limit) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT pb FROM PostBan pb" +
                " WHERE pb.enabled = true" +
                " AND UPPER(pb.reason) LIKE UPPER(:reason)" +
                " ORDER BY pb.createDate ASC";

        TypedQuery<PostBan> query = entityManager.createQuery(jpql, PostBan.class)
                .setParameter("reason", "%" + StringUtils.escape(reason) + "%");
        List<PostBan> resultList = query.getResultList();
        entityManager.close();
        return resultList;
    }

    @Override
    public long getPostBanListCount(String reason) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT COUNT(*) FROM PostBan pb" +
                " WHERE pb.enabled = true" +
                " AND UPPER(pb.reason) LIKE UPPER(:reason)";

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
                .setParameter("reason", "%" + StringUtils.escape(reason) + "%");
        long result = query.getSingleResult();
        entityManager.close();
        return result;
    }

    @Override
    public Optional<PostBan> getPostBan(Post post) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT pb FROM PostBan pb" +
                " WHERE pb.post = :post";

        TypedQuery<PostBan> query = entityManager.createQuery(jpql, PostBan.class)
                .setParameter("post", post);

        List<PostBan> resultList = query.getResultList();
        entityManager.close();
        if (resultList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(resultList.get(0));
        }
    }

    @Override
    public Optional<CommentBan> getCommentBan(long id) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        try {
            CommentBan commentBan = entityManager.find(CommentBan.class, id);
            return Optional.of(commentBan);
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    @Override
    public List<CommentBan> getCommentBanList(Comment comment, Boolean enabled, Integer skip, Integer limit) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();

        String jpql = "SELECT cb FROM CommentBan cb" +
                " WHERE cb.comment = :comment";

        if (enabled != null) {
            jpql += " AND cb.enabled = :enabled";
        }

        TypedQuery<CommentBan> query = entityManager.createQuery(jpql, CommentBan.class)
                .setParameter("comment", comment);

        if (enabled != null) {
            query.setParameter("enabled", enabled);
        }

        if (skip != null) {
            query.setFirstResult(skip);
        }

        if (limit != null) {
            query.setMaxResults(limit);
        }

        List<CommentBan> resultList = query.getResultList();
        entityManager.close();
        return resultList;
    }

    @Override
    public void save(CommentBan commentBan) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        if (commentBan.getId() == null) {
            entityManager.persist(commentBan);
        } else {
            entityManager.merge(commentBan);
        }
        transaction.commit();
        entityManager.close();
    }
}

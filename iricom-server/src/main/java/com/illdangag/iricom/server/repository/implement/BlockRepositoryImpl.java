package com.illdangag.iricom.server.repository.implement;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.repository.BlockRepository;
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
public class BlockRepositoryImpl implements BlockRepository {
    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    public BlockRepositoryImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public List<PostBlock> getPostBlockList(Post post) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT pb FROM PostBlock pb" +
                " WHERE pb.post = :post" +
                " AND pb.enabled = true" +
                " ORDER BY pb.createDate ASC";

        TypedQuery<PostBlock> query = entityManager.createQuery(jpql, PostBlock.class)
                .setParameter("post", post);
        List<PostBlock> resultList = query.getResultList();
        entityManager.close();
        return resultList;
    }

    @Override
    public long getPostBlockCount(Post post) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT COUNT(*) FROM PostBlock pb" +
                " WHERE pb.post = :post" +
                " AND pb.enabled = true";

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
                .setParameter("post", post);
        long result = query.getSingleResult();
        entityManager.close();
        return result;
    }

    @Override
    public List<PostBlock> getPostBlockList(Board board, String reason, int offset, int limit) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT pb FROM PostBlock pb" +
                " WHERE pb.post.board = :board" +
                " AND pb.enabled = true" +
                " AND UPPER(pb.reason) LIKE UPPER(:reason)" +
                " ORDER BY pb.createDate ASC";

        TypedQuery<PostBlock> query = entityManager.createQuery(jpql, PostBlock.class)
                .setParameter("board", board)
                .setParameter("reason", "%" + StringUtils.escape(reason) + "%")
                .setFirstResult(offset)
                .setMaxResults(limit);
        List<PostBlock> resultList = query.getResultList();
        entityManager.close();
        return resultList;
    }

    @Override
    public long getPostBlockListCount(Board board, String reason) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT COUNT(*) FROM PostBlock pb" +
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
    public Optional<PostBlock> getPostBlock(String id) {
        long postBlockId = -1;

        try {
            postBlockId = Long.parseLong(id);
        } catch (Exception exception) {
            return Optional.empty();
        }

        return this.getPostBlock(postBlockId);
    }

    @Override
    public Optional<PostBlock> getPostBlock(long id) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT pb FROM PostBlock pb" +
                " WHERE pb.id = :id";

        TypedQuery<PostBlock> query = entityManager.createQuery(jpql, PostBlock.class)
                .setParameter("id", id);

        List<PostBlock> resultList = query.getResultList();
        entityManager.close();
        if (resultList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(resultList.get(0));
        }
    }

    @Override
    public void save(PostBlock postBlock) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        if (postBlock.getId() == null) {
            entityManager.persist(postBlock);
        } else {
            entityManager.merge(postBlock);
        }
        transaction.commit();
        entityManager.close();
    }

    @Override
    public List<PostBlock> getPostBlockList(String reason, int offset, int limit) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT pb FROM PostBlock pb" +
                " WHERE pb.enabled = true" +
                " AND UPPER(pb.reason) LIKE UPPER(:reason)" +
                " ORDER BY pb.createDate ASC";

        TypedQuery<PostBlock> query = entityManager.createQuery(jpql, PostBlock.class)
                .setParameter("reason", "%" + StringUtils.escape(reason) + "%");
        List<PostBlock> resultList = query.getResultList();
        entityManager.close();
        return resultList;
    }

    @Override
    public long getPostBlockListCount(String reason) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT COUNT(*) FROM PostBlock pb" +
                " WHERE pb.enabled = true" +
                " AND UPPER(pb.reason) LIKE UPPER(:reason)";

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
                .setParameter("reason", "%" + StringUtils.escape(reason) + "%");
        long result = query.getSingleResult();
        entityManager.close();
        return result;
    }

    @Override
    public Optional<PostBlock> getPostBlock(Post post) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT pb FROM PostBlock pb" +
                " WHERE pb.post = :post";

        TypedQuery<PostBlock> query = entityManager.createQuery(jpql, PostBlock.class)
                .setParameter("post", post);

        List<PostBlock> resultList = query.getResultList();
        entityManager.close();
        if (resultList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(resultList.get(0));
        }
    }

    @Override
    public Optional<CommentBlock> getCommentBlock(long id) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        try {
            CommentBlock commentBlock = entityManager.find(CommentBlock.class, id);
            return Optional.of(commentBlock);
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    @Override
    public List<CommentBlock> getCommentBlockList(Comment comment, Boolean enabled, Integer skip, Integer limit) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();

        String jpql = "SELECT cb FROM CommentBlock cb" +
                " WHERE cb.comment = :comment";

        if (enabled != null) {
            jpql += " AND cb.enabled = :enabled";
        }

        TypedQuery<CommentBlock> query = entityManager.createQuery(jpql, CommentBlock.class)
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

        List<CommentBlock> resultList = query.getResultList();
        entityManager.close();
        return resultList;
    }

    @Override
    public void save(CommentBlock commentBlock) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        if (commentBlock.getId() == null) {
            entityManager.persist(commentBlock);
        } else {
            entityManager.merge(commentBlock);
        }
        transaction.commit();
        entityManager.close();
    }
}

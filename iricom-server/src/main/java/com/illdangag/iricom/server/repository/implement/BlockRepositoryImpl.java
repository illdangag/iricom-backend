package com.illdangag.iricom.server.repository.implement;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.repository.BlockRepository;
import com.illdangag.iricom.server.util.StringUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public class BlockRepositoryImpl implements BlockRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<PostBlock> getPostBlockList(Post post) {
        final String jpql = "SELECT pb FROM PostBlock pb" +
                " WHERE pb.post = :post" +
                " AND pb.enabled = true" +
                " ORDER BY pb.createDate ASC";

        TypedQuery<PostBlock> query = this.entityManager.createQuery(jpql, PostBlock.class)
                .setParameter("post", post);
        return query.getResultList();
    }

    @Override
    public long getPostBlockCount(Post post) {
        final String jpql = "SELECT COUNT(*) FROM PostBlock pb" +
                " WHERE pb.post = :post" +
                " AND pb.enabled = true";

        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class)
                .setParameter("post", post);
        return query.getSingleResult();
    }

    @Override
    public List<PostBlock> getPostBlockList(Board board, String reason, int offset, int limit) {
        final String jpql = "SELECT pb FROM PostBlock pb" +
                " WHERE pb.post.board = :board" +
                " AND pb.enabled = true" +
                " AND UPPER(pb.reason) LIKE UPPER(:reason)" +
                " ORDER BY pb.createDate ASC";

        TypedQuery<PostBlock> query = this.entityManager.createQuery(jpql, PostBlock.class)
                .setParameter("board", board)
                .setParameter("reason", "%" + StringUtils.escape(reason) + "%")
                .setFirstResult(offset)
                .setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    public long getPostBlockListCount(Board board, String reason) {
        final String jpql = "SELECT COUNT(*) FROM PostBlock pb" +
                " WHERE pb.post.board = :board" +
                " AND pb.enabled = true" +
                " AND UPPER(pb.reason) LIKE UPPER(:reason)";

        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class)
                .setParameter("board", board)
                .setParameter("reason", "%" + StringUtils.escape(reason) + "%");
        return query.getSingleResult();
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
        final String jpql = "SELECT pb FROM PostBlock pb" +
                " WHERE pb.id = :id";

        TypedQuery<PostBlock> query = this.entityManager.createQuery(jpql, PostBlock.class)
                .setParameter("id", id);

        List<PostBlock> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(resultList.get(0));
        }
    }

    @Override
    public void save(PostBlock postBlock) {
        if (postBlock.getId() == null) {
            entityManager.persist(postBlock);
        } else {
            entityManager.merge(postBlock);
        }
        this.entityManager.flush();
    }

    @Override
    public List<PostBlock> getPostBlockList(String reason, int offset, int limit) {
        final String jpql = "SELECT pb FROM PostBlock pb" +
                " WHERE pb.enabled = true" +
                " AND UPPER(pb.reason) LIKE UPPER(:reason)" +
                " ORDER BY pb.createDate ASC";

        TypedQuery<PostBlock> query = this.entityManager.createQuery(jpql, PostBlock.class)
                .setParameter("reason", "%" + StringUtils.escape(reason) + "%");
        return query.getResultList();
    }

    @Override
    public long getPostBlockListCount(String reason) {
        final String jpql = "SELECT COUNT(*) FROM PostBlock pb" +
                " WHERE pb.enabled = true" +
                " AND UPPER(pb.reason) LIKE UPPER(:reason)";

        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class)
                .setParameter("reason", "%" + StringUtils.escape(reason) + "%");
        return query.getSingleResult();
    }

    @Override
    public Optional<PostBlock> getPostBlock(Post post) {
        final String jpql = "SELECT pb FROM PostBlock pb" +
                " WHERE pb.post = :post";

        TypedQuery<PostBlock> query = this.entityManager.createQuery(jpql, PostBlock.class)
                .setParameter("post", post);

        List<PostBlock> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(resultList.get(0));
        }
    }

    @Override
    public Optional<CommentBlock> getCommentBlock(long id) {
        try {
            CommentBlock commentBlock = entityManager.find(CommentBlock.class, id);
            return Optional.of(commentBlock);
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    @Override
    public List<CommentBlock> getCommentBlockList(Comment comment, Boolean enabled, Integer skip, Integer limit) {

        String jpql = "SELECT cb FROM CommentBlock cb" +
                " WHERE cb.comment = :comment";

        if (enabled != null) {
            jpql += " AND cb.enabled = :enabled";
        }

        TypedQuery<CommentBlock> query = this.entityManager.createQuery(jpql, CommentBlock.class)
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

        return query.getResultList();
    }

    @Override
    public void save(CommentBlock commentBlock) {
        if (commentBlock.getId() == null) {
            this.entityManager.persist(commentBlock);
        } else {
            this.entityManager.merge(commentBlock);
        }
        this.entityManager.flush();
    }
}

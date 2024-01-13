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

@Transactional
@Repository
public class BlockRepositoryImpl implements BlockRepository {
    @PersistenceContext
    private EntityManager entityManager;

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
}

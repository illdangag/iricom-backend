package com.illdangag.iricom.core.repository.implement;

import com.illdangag.iricom.core.data.entity.Board;
import com.illdangag.iricom.core.data.entity.Comment;
import com.illdangag.iricom.core.data.entity.CommentBlock;
import com.illdangag.iricom.core.data.entity.PostBlock;
import com.illdangag.iricom.core.repository.BlockRepository;
import com.illdangag.iricom.core.util.StringUtils;
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
                " AND UPPER(pb.reason) LIKE UPPER(:reason)";

        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class)
                .setParameter("board", board)
                .setParameter("reason", "%" + StringUtils.escape(reason) + "%");
        return query.getSingleResult();
    }

    @Override
    public List<PostBlock> getPostBlockList(String reason, int offset, int limit) {
        final String jpql = "SELECT pb FROM PostBlock pb" +
                " WHERE UPPER(pb.reason) LIKE UPPER(:reason)" +
                " ORDER BY pb.createDate ASC";

        TypedQuery<PostBlock> query = this.entityManager.createQuery(jpql, PostBlock.class)
                .setParameter("reason", "%" + StringUtils.escape(reason) + "%");
        return query.getResultList();
    }

    @Override
    public long getPostBlockListCount(String reason) {
        final String jpql = "SELECT COUNT(*) FROM PostBlock pb" +
                " WHERE UPPER(pb.reason) LIKE UPPER(:reason)";

        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class)
                .setParameter("reason", "%" + StringUtils.escape(reason) + "%");
        return query.getSingleResult();
    }

    @Override
    public List<CommentBlock> getCommentBlockList(Comment comment, Integer skip, Integer limit) {
        String jpql = "SELECT cb FROM CommentBlock cb" +
                " WHERE cb.comment = :comment";

        TypedQuery<CommentBlock> query = this.entityManager.createQuery(jpql, CommentBlock.class)
                .setParameter("comment", comment);

        if (skip != null) {
            query.setFirstResult(skip);
        }

        if (limit != null) {
            query.setMaxResults(limit);
        }

        return query.getResultList();
    }

    @Override
    public void remove(PostBlock postBlock) {
        this.entityManager.remove(postBlock);
        this.entityManager.flush();
    }

    @Override
    public void remove(CommentBlock commentBlock) {
        this.entityManager.remove(commentBlock);
        this.entityManager.flush();
    }
}

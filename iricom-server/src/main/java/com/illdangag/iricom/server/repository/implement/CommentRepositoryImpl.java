package com.illdangag.iricom.server.repository.implement;

import com.illdangag.iricom.server.data.entity.Comment;
import com.illdangag.iricom.server.data.entity.Post;
import com.illdangag.iricom.server.repository.CommentRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public class CommentRepositoryImpl implements CommentRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Comment> getComment(long id) {
        final String jpql = "SELECT c FROM Comment c" +
                " WHERE c.id = :id";

        TypedQuery<Comment> query = this.entityManager.createQuery(jpql, Comment.class);
        query.setParameter("id", id);
        Comment comment = query.getSingleResult();

        return Optional.ofNullable(comment);
    }

    public List<Comment> getCommentList(Post post, int offset, int limit) {
        final String jpql = "SELECT c FROM Comment c" +
                " WHERE c.post = :post" +
                " AND c.referenceComment IS NULL" +
                " ORDER BY c.createDate ASC";

        TypedQuery<Comment> query = this.entityManager.createQuery(jpql, Comment.class)
                .setParameter("post", post)
                .setFirstResult(offset)
                .setMaxResults(limit);
        List<Comment> resultList = query.getResultList();

        return resultList;
    }

    public long getCommentCount(Post post) {
        final String jpql = "SELECT COUNT(*) FROM Comment c" +
                " WHERE c.post = :post " +
                " AND c.referenceComment IS NULL";

        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class)
                .setParameter("post", post);
        long result = query.getSingleResult();

        return result;
    }

    @Override
    public List<Comment> getCommentList(Post post, Comment referenceComment, int offset, int limit) {
        final String jpql = "SELECT c FROM Comment c" +
                " WHERE c.post = :post" +
                " AND c.referenceComment = :referenceComment" +
                " ORDER BY c.createDate ASC";

        TypedQuery<Comment> query = this.entityManager.createQuery(jpql, Comment.class)
                .setParameter("post", post)
                .setParameter("referenceComment", referenceComment)
                .setFirstResult(offset)
                .setMaxResults(limit);
        List<Comment> resultList = query.getResultList();

        return resultList;
    }

    @Override
    public long getCommentListSize(Post post, Comment referenceComment) {
        final String jpql = "SELECT COUNT(*) FROM Comment c" +
                " WHERE c.post = :post" +
                " AND c.referenceComment = :referenceComment";

        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class)
                .setParameter("post", post)
                .setParameter("referenceComment", referenceComment);
        long result = query.getSingleResult();

        return result;
    }

    @Override
    public long getCommentListSize(Post post) {
        final String jpql = "SELECT COUNT(*) FROM Comment c" +
                " WHERE c.post = :post";

        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class)
                .setParameter("post", post);
        long result = query.getSingleResult();
        return result;
    }

    @Override
    public void save(Comment comment) {
        if (comment.getId() == null) {
            this.entityManager.persist(comment);
        } else {
            this.entityManager.merge(comment);
        }
    }
}

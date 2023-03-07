package com.illdangag.iricom.server.repository.implement;

import com.illdangag.iricom.server.data.entity.Comment;
import com.illdangag.iricom.server.data.entity.Post;
import com.illdangag.iricom.server.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class CommentRepositoryImpl implements CommentRepository {
    private final EntityManager entityManager;

    @Autowired
    public CommentRepositoryImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManager = entityManagerFactory.createEntityManager();
    }

    @Override
    public Optional<Comment> getComment(long id) {
        this.entityManager.clear();
        final String jpql = "SELECT c FROM Comment c WHERE c.id = :id";
        TypedQuery<Comment> query = this.entityManager.createQuery(jpql, Comment.class);
        query.setParameter("id", id);
        Comment comment = query.getSingleResult();
        return Optional.ofNullable(comment);
    }

    public List<Comment> getCommentList(Post post, int offset, int limit) {
        this.entityManager.clear();
        final String jpql = "SELECT c FROM Comment c " +
                "WHERE c.post = :post AND c.referenceComment IS NULL " +
                "ORDER BY c.createDate ASC";
        TypedQuery<Comment> query = this.entityManager.createQuery(jpql, Comment.class)
                .setParameter("post", post)
                .setFirstResult(offset)
                .setMaxResults(limit);
        return query.getResultList();
    }

    public long getCommentCount(Post post) {
        this.entityManager.clear();
        final String jpql = "SELECT COUNT(*) FROM Comment c " +
                "WHERE c.post = :post " +
                "AND c.referenceComment IS NULL";
        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class)
                .setParameter("post", post);
        return query.getSingleResult();
    }

    @Override
    public List<Comment> getCommentList(Post post, Comment referenceComment, int offset, int limit) {
        this.entityManager.clear();
        final String jpql = "SELECT c FROM Comment c " +
                "WHERE c.post = :post " +
                "AND c.referenceComment = :referenceComment " +
                "ORDER BY c.createDate ASC";
        TypedQuery<Comment> query = this.entityManager.createQuery(jpql, Comment.class)
                .setParameter("post", post)
                .setParameter("referenceComment", referenceComment)
                .setFirstResult(offset)
                .setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    public long getCommentListSize(Post post, Comment referenceComment) {
        this.entityManager.clear();
        final String jpql = "SELECT COUNT(*) FROM Comment c " +
                "WHERE c.post = :post " +
                "AND c.referenceComment = :referenceComment";
        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class)
                .setParameter("post", post)
                .setParameter("referenceComment", referenceComment);
        return query.getSingleResult();
    }

    @Override
    public long getCommentListSize(Post post) {
        this.entityManager.clear();
        final String jpql = "SELECT COUNT(*) FROM Comment c WHERE c.post = :post";
        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class)
                .setParameter("post", post);
        return query.getSingleResult();
    }

    @Override
    public void save(Comment comment) {
        EntityTransaction transaction = this.entityManager.getTransaction();
        transaction.begin();
        if (comment.getId() == null) {
            this.entityManager.persist(comment);
        } else {
            this.entityManager.merge(comment);
        }
        transaction.commit();
    }
}

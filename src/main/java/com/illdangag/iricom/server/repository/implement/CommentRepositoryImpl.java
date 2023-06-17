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
    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    public CommentRepositoryImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Optional<Comment> getComment(String id) {
        long commentId = -1;

        try {
            commentId = Long.parseLong(id);
        } catch (Exception exception) {
            return Optional.empty();
        }

        return this.getComment(commentId);
    }

    @Override
    public Optional<Comment> getComment(long id) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT c FROM Comment c" +
                " WHERE c.id = :id";

        TypedQuery<Comment> query = entityManager.createQuery(jpql, Comment.class);
        query.setParameter("id", id);
        Comment comment = query.getSingleResult();
        entityManager.close();
        return Optional.ofNullable(comment);
    }

    public List<Comment> getCommentList(Post post, int offset, int limit) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT c FROM Comment c" +
                " WHERE c.post = :post" +
                " AND c.referenceComment IS NULL" +
                " ORDER BY c.createDate ASC";

        TypedQuery<Comment> query = entityManager.createQuery(jpql, Comment.class)
                .setParameter("post", post)
                .setFirstResult(offset)
                .setMaxResults(limit);
        List<Comment> resultList = query.getResultList();
        entityManager.close();
        return resultList;
    }

    public long getCommentCount(Post post) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT COUNT(*) FROM Comment c" +
                " WHERE c.post = :post " +
                " AND c.referenceComment IS NULL";

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
                .setParameter("post", post);
        long result = query.getSingleResult();
        entityManager.close();
        return result;
    }

    @Override
    public List<Comment> getCommentList(Post post, Comment referenceComment, int offset, int limit) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT c FROM Comment c" +
                " WHERE c.post = :post" +
                " AND c.referenceComment = :referenceComment" +
                " ORDER BY c.createDate ASC";

        TypedQuery<Comment> query = entityManager.createQuery(jpql, Comment.class)
                .setParameter("post", post)
                .setParameter("referenceComment", referenceComment)
                .setFirstResult(offset)
                .setMaxResults(limit);
        List<Comment> resultList = query.getResultList();
        entityManager.close();
        return resultList;
    }

    @Override
    public long getCommentListSize(Post post, Comment referenceComment) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT COUNT(*) FROM Comment c" +
                " WHERE c.post = :post" +
                " AND c.referenceComment = :referenceComment";

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
                .setParameter("post", post)
                .setParameter("referenceComment", referenceComment);
        long result = query.getSingleResult();
        entityManager.close();
        return result;
    }

    @Override
    public long getCommentListSize(Post post) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT COUNT(*) FROM Comment c" +
                " WHERE c.post = :post";

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
                .setParameter("post", post);
        long result = query.getSingleResult();
        entityManager.close();
        return result;
    }

    @Override
    public void save(Comment comment) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        if (comment.getId() == null) {
            entityManager.persist(comment);
        } else {
            entityManager.merge(comment);
        }
        transaction.commit();
        entityManager.close();
    }
}

package com.illdangag.iricom.server.repository.implement;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.repository.PostRepository;
import com.illdangag.iricom.server.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class PostRepositoryImpl implements PostRepository {
    private final EntityManager entityManager;

    @Autowired
    public PostRepositoryImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManager = entityManagerFactory.createEntityManager();
    }

    @Override
    public Optional<Post> getPost(long id) {
        this.entityManager.clear();
        final String jpql = "SELECT p FROM Post p WHERE p.id = :id";
        TypedQuery<Post> query = this.entityManager.createQuery(jpql, Post.class)
                .setParameter("id", id);
        List<Post> postList = query.getResultList();
        if (postList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(postList.get(0));
        }
    }

    @Override
    public List<Post> getPublishPostList(Board board, PostType postType, int offset, int limit) {
        this.entityManager.clear();
        final String jpql = "SELECT p FROM Post p " +
                "WHERE p.board = :board " +
                "AND p.content IS NOT NULL " +
                "AND p.content.type = :type " +
                "ORDER BY p.createDate DESC";
        TypedQuery<Post> query = this.entityManager.createQuery(jpql, Post.class)
                .setParameter("board", board)
                .setParameter("type", postType)
                .setFirstResult(offset)
                .setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    public List<Post> getPublishPostList(Board board, int offset, int limit) {
        this.entityManager.clear();
        final String jpql = "SELECT p FROM Post p " +
                "WHERE p.board = :board " +
                "AND p.content IS NOT NULL " +
                "ORDER BY p.createDate DESC";
        TypedQuery<Post> query = this.entityManager.createQuery(jpql, Post.class)
                .setParameter("board", board)
                .setFirstResult(offset)
                .setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    public List<Post> getPostList(Account account, int offset, int limit) {
        this.entityManager.clear();
        final String jpql = "SELECT p FROM Post p " +
                "WHERE p.account = :account " +
                "ORDER BY p.createDate DESC";
        TypedQuery<Post> query = this.entityManager.createQuery(jpql, Post.class)
                .setParameter("account", account)
                .setFirstResult(offset)
                .setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    public long getPostCount(Account account) {
        this.entityManager.clear();
        final String jpql = "SELECT COUNT(*) FROM Post p WHERE p.account = :account";
        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class)
                .setParameter("account", account);
        return query.getSingleResult();
    }

    @Override
    public long getPublishPostCount(Board board, PostType postType) {
        this.entityManager.clear();
        final String jpql = "SELECT COUNT(*) FROM Post p " +
                "WHERE p.board = :board " +
                "AND p.content IS NOT NULL AND " +
                "p.content.type = :type";
        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class)
                .setParameter("board", board)
                .setParameter("type", postType);
        return query.getSingleResult();
    }

    @Override
    public long getPublishPostCount(Board board) {
        this.entityManager.clear();
        final String jpql = "SELECT COUNT(*) FROM Post p " +
                "WHERE p.board = :board " +
                "AND p.content IS NOT NULL AND " +
                "p.content.type = :type";
        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class)
                .setParameter("board", board);
        return query.getSingleResult();
    }

    @Override
    public List<Post> getPublishPostList(Board board, PostType postType, String containTitle, int offset, int limit) {
        this.entityManager.clear();
        final String jpql = "SELECT p FROM Post p " +
                "WHERE p.board = :board " +
                "AND p.content IS NOT NULL " +
                "AND p.content.type = :type " +
                "AND p.content.title LIKE :title " +
                "ORDER BY p.createDate DESC";
        TypedQuery<Post> query = this.entityManager.createQuery(jpql, Post.class)
                .setParameter("board", board)
                .setParameter("type", postType)
                .setParameter("title", "%" + StringUtils.escape(containTitle) + "%")
                .setFirstResult(offset)
                .setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    public List<Post> getPublishPostList(Board board, String containTitle, int offset, int limit) {
        this.entityManager.clear();
        final String jpql = "SELECT p FROM Post p " +
                "WHERE p.board = :board " +
                "AND p.content IS NOT NULL " +
                "AND p.content.title LIKE :title " +
                "ORDER BY p.createDate DESC";
        TypedQuery<Post> query = this.entityManager.createQuery(jpql, Post.class)
                .setParameter("board", board)
                .setParameter("title", "%" + StringUtils.escape(containTitle) + "%")
                .setFirstResult(offset)
                .setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    public long getPublishPostCount(Board board, PostType postType, String containTitle) {
        this.entityManager.clear();
        final String jpql = "SELECT COUNT(*) FROM Post p " +
                "WHERE p.board = :board " +
                "AND p.content IS NOT NULL " +
                "AND p.content.type = :type " +
                "AND p.content.title LIKE :title";
        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class)
                .setParameter("board", board)
                .setParameter("type", postType)
                .setParameter("title", "%" + StringUtils.escape(containTitle) + "%");
        return query.getSingleResult();
    }

    @Override
    public long getPublishPostCount(Board board, String containTitle) {
        this.entityManager.clear();
        final String jpql = "SELECT COUNT(*) FROM Post p " +
                "WHERE p.board = :board " +
                "AND p.content IS NOT NULL " +
                "AND p.content.title LIKE :title";
        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class)
                .setParameter("board", board)
                .setParameter("title", "%" + StringUtils.escape(containTitle) + "%");
        return query.getSingleResult();
    }

    @Override
    public void save(Post post) {
        EntityTransaction entityTransaction = this.entityManager.getTransaction();
        entityTransaction.begin();
        if (post.getId() == null) {
            this.entityManager.persist(post);
        } else {
            this.entityManager.merge(post);
        }
        entityTransaction.commit();
    }

    @Override
    public void save(PostContent postContent) {
        EntityTransaction entityTransaction = this.entityManager.getTransaction();
        entityTransaction.begin();
        if (postContent.getId() == null) {
            this.entityManager.persist(postContent);
        } else {
            this.entityManager.merge(postContent);
        }
        entityTransaction.commit();
    }

    @Override
    public void save(Post post, PostContent postContent) {
        EntityTransaction entityTransaction = this.entityManager.getTransaction();
        entityTransaction.begin();
        if (post.getId() == null) {
            this.entityManager.persist(post);
        } else {
            this.entityManager.merge(post);
        }
        if (postContent.getId() == null) {
            this.entityManager.persist(postContent);
        } else {
            this.entityManager.merge(postContent);
        }
        entityTransaction.commit();
    }
}

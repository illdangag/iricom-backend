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
    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    public PostRepositoryImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Optional<Post> getPost(String id) {
        long postId = -1;
        try {
            postId = Long.parseLong(id);
        } catch (Exception exception) {
            return Optional.empty();
        }

        return this.getPost(postId);
    }

    @Override
    public Optional<Post> getPost(long id) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT p FROM Post p" +
                " WHERE p.id = :id";

        TypedQuery<Post> query = entityManager.createQuery(jpql, Post.class)
                .setParameter("id", id);
        List<Post> postList = query.getResultList();
        entityManager.close();
        if (postList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(postList.get(0));
        }
    }

    @Override
    public List<Post> getPublishPostList(Board board, PostType postType, int offset, int limit) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT p FROM Post p " +
                " WHERE p.board = :board" +
                " AND p.deleted = false" +
                " AND p.content IS NOT NULL" +
                " AND p.content.type = :type" +
                " ORDER BY p.createDate DESC";

        TypedQuery<Post> query = entityManager.createQuery(jpql, Post.class)
                .setParameter("board", board)
                .setParameter("type", postType)
                .setFirstResult(offset)
                .setMaxResults(limit);
        List<Post> resultList = query.getResultList();
        entityManager.close();
        return resultList;
    }

    @Override
    public List<Post> getPublishPostList(Board board, int offset, int limit) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT p FROM Post p" +
                " WHERE p.board = :board" +
                " AND p.deleted = false" +
                " AND p.content IS NOT NULL" +
                " ORDER BY p.createDate DESC";

        TypedQuery<Post> query = entityManager.createQuery(jpql, Post.class)
                .setParameter("board", board)
                .setFirstResult(offset)
                .setMaxResults(limit);
        List<Post> resultList = query.getResultList();
        entityManager.close();
        return resultList;
    }

    @Override
    public List<Post> getPostList(Account account, List<Long> boardIdList, int offset, int limit) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();

        final String jpql = "SELECT p FROM Post p" +
                " WHERE p.account = :account" +
                " AND p.deleted = false" +
                " AND (p.board.undisclosed = false OR p.board.id IN :boardIdList)" +
                " ORDER BY p.createDate DESC";

        TypedQuery<Post> query = entityManager.createQuery(jpql, Post.class)
                .setParameter("account", account)
                .setParameter("boardIdList", boardIdList)
                .setFirstResult(offset)
                .setMaxResults(limit);
        List<Post> resultList = query.getResultList();
        entityManager.close();
        return resultList;
    }

    @Override
    public long getPostCount(Account account, List<Long> boardIdList) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT COUNT(*) FROM Post p" +
                " WHERE p.account = :account" +
                " AND p.deleted = false" +
                " AND (p.board.undisclosed = false OR p.board.id IN :boardIdList)";

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
                .setParameter("account", account)
                .setParameter("boardIdList", boardIdList);
        long result = query.getSingleResult();
        entityManager.close();
        return result;
    }

    @Override
    public long getPublishPostCount(Board board, PostType postType) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT COUNT(*) FROM Post p" +
                " WHERE p.board = :board" +
                " AND p.deleted = false" +
                " AND p.content IS NOT NULL" +
                " AND p.content.type = :type";

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
                .setParameter("board", board)
                .setParameter("type", postType);
        long result = query.getSingleResult();
        entityManager.close();
        return result;
    }

    @Override
    public long getPublishPostCount(Board board) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT COUNT(*) FROM Post p" +
                " WHERE p.board = :board " +
                " AND p.deleted = false" +
                " AND p.content IS NOT NULL";

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
                .setParameter("board", board);
        long result = query.getSingleResult();
        entityManager.close();
        return result;
    }

    @Override
    public List<Post> getPublishPostList(Board board, PostType postType, String containTitle, int offset, int limit) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT p FROM Post p" +
                " WHERE p.board = :board" +
                " AND p.deleted = false" +
                " AND p.content IS NOT NULL" +
                " AND p.content.type = :type" +
                " AND UPPER(p.content.title) LIKE UPPER(:title)" +
                " ORDER BY p.createDate DESC";

        TypedQuery<Post> query = entityManager.createQuery(jpql, Post.class)
                .setParameter("board", board)
                .setParameter("type", postType)
                .setParameter("title", "%" + StringUtils.escape(containTitle) + "%")
                .setFirstResult(offset)
                .setMaxResults(limit);
        List<Post> resultList = query.getResultList();
        entityManager.close();
        return resultList;
    }

    @Override
    public List<Post> getPublishPostList(Board board, String containTitle, int offset, int limit) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT p FROM Post p" +
                " WHERE p.board = :board" +
                " AND p.deleted = false" +
                " AND p.content IS NOT NULL" +
                " AND UPPER(p.content.title) LIKE UPPER(:title)" +
                " ORDER BY p.createDate DESC";

        TypedQuery<Post> query = entityManager.createQuery(jpql, Post.class)
                .setParameter("board", board)
                .setParameter("title", "%" + StringUtils.escape(containTitle) + "%")
                .setFirstResult(offset)
                .setMaxResults(limit);
        List<Post> resultList = query.getResultList();
        entityManager.close();
        return resultList;
    }

    @Override
    public long getPublishPostCount(Board board, PostType postType, String containTitle) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT COUNT(*) FROM Post p " +
                " WHERE p.board = :board" +
                " AND p.deleted = false" +
                " AND p.content IS NOT NULL" +
                " AND p.content.type = :type" +
                " AND UPPER(p.content.title) LIKE UPPER(:title)";

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
                .setParameter("board", board)
                .setParameter("type", postType)
                .setParameter("title", "%" + StringUtils.escape(containTitle) + "%");
        long result = query.getSingleResult();
        entityManager.close();
        return result;
    }

    @Override
    public long getPublishPostCount(Board board, String containTitle) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT COUNT(*) FROM Post p" +
                " WHERE p.board = :board" +
                " AND p.deleted = false" +
                " AND p.content IS NOT NULL" +
                " AND UPPER(p.content.title) LIKE UPPER(:title)";

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
                .setParameter("board", board)
                .setParameter("title", "%" + StringUtils.escape(containTitle) + "%");
        long result = query.getSingleResult();
        entityManager.close();
        return result;
    }

    @Override
    public void save(Post post) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();
        if (post.getId() == null) {
            entityManager.persist(post);
        } else {
            entityManager.merge(post);
        }
        entityTransaction.commit();
        entityManager.close();
    }

    @Override
    public void save(PostContent postContent) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();
        if (postContent.getId() == null) {
            entityManager.persist(postContent);
        } else {
            entityManager.merge(postContent);
        }
        entityTransaction.commit();
        entityManager.close();
    }

    @Override
    public void save(Post post, PostContent postContent) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();
        if (postContent.getId() == null) {
            entityManager.persist(postContent);
        } else {
            entityManager.merge(postContent);
        }

        if (post.getId() == null) {
            entityManager.persist(post);
        } else {
            entityManager.merge(post);
        }
        entityTransaction.commit();
        entityManager.close();
    }
}

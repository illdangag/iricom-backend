package com.illdangag.iricom.core.repository.implement;

import com.illdangag.iricom.core.data.entity.*;
import com.illdangag.iricom.core.data.entity.type.PostType;
import com.illdangag.iricom.core.repository.PostRepository;
import com.illdangag.iricom.core.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional
@Repository
public class PostRepositoryImpl implements PostRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Post> getPost(long id) {
        final String jpql = "SELECT p FROM Post p" +
                " WHERE p.id = :id";

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
        final String jpql = "SELECT p FROM Post p " +
                " WHERE p.board = :board" +
                " AND p.deleted = false" +
                " AND p.content IS NOT NULL" +
                " AND p.content.type = :type" +
                " ORDER BY p.createDate DESC";

        TypedQuery<Post> query = this.entityManager.createQuery(jpql, Post.class)
                .setParameter("board", board)
                .setParameter("type", postType)
                .setFirstResult(offset)
                .setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    public List<Post> getPublishPostList(Board board, int offset, int limit) {
        final String jpql = "SELECT p FROM Post p" +
                " WHERE p.board = :board" +
                " AND p.deleted = false" +
                " AND p.content IS NOT NULL" +
                " ORDER BY p.createDate DESC";

        TypedQuery<Post> query = this.entityManager.createQuery(jpql, Post.class)
                .setParameter("board", board)
                .setFirstResult(offset)
                .setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    public List<Post> getPostList(Account account, List<Long> boardIdList, int offset, int limit) {

        final String jpql = "SELECT p FROM Post p" +
                " WHERE p.account = :account" +
                " AND p.deleted = false" +
                " AND (p.board.undisclosed = false OR p.board.id IN :boardIdList)" +
                " ORDER BY p.createDate DESC";

        TypedQuery<Post> query = this.entityManager.createQuery(jpql, Post.class)
                .setParameter("account", account)
                .setParameter("boardIdList", boardIdList)
                .setFirstResult(offset)
                .setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    public long getPostCount(Account account, List<Long> boardIdList) {
        final String jpql = "SELECT COUNT(*) FROM Post p" +
                " WHERE p.account = :account" +
                " AND p.deleted = false" +
                " AND (p.board.undisclosed = false OR p.board.id IN :boardIdList)";

        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class)
                .setParameter("account", account)
                .setParameter("boardIdList", boardIdList);
        return query.getSingleResult();
    }

    @Override
    public long getPublishPostCount(Board board, PostType postType) {
        final String jpql = "SELECT COUNT(*) FROM Post p" +
                " WHERE p.board = :board" +
                " AND p.deleted = false" +
                " AND p.content IS NOT NULL" +
                " AND p.content.type = :type";

        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class)
                .setParameter("board", board)
                .setParameter("type", postType);
        return query.getSingleResult();
    }

    @Override
    public long getPublishPostCount(Board board) {
        final String jpql = "SELECT COUNT(*) FROM Post p" +
                " WHERE p.board = :board " +
                " AND p.deleted = false" +
                " AND p.content IS NOT NULL";

        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class)
                .setParameter("board", board);
        return query.getSingleResult();
    }

    @Override
    public List<Post> getPublishPostList(Board board, PostType postType, String containTitle, int offset, int limit) {
        final String jpql = "SELECT p FROM Post p" +
                " WHERE p.board = :board" +
                " AND p.deleted = false" +
                " AND p.content IS NOT NULL" +
                " AND p.content.type = :type" +
                " AND UPPER(p.content.title) LIKE UPPER(:title)" +
                " ORDER BY p.createDate DESC";

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
        final String jpql = "SELECT p FROM Post p" +
                " WHERE p.board = :board" +
                " AND p.deleted = false" +
                " AND p.content IS NOT NULL" +
                " AND UPPER(p.content.title) LIKE UPPER(:title)" +
                " ORDER BY p.createDate DESC";

        TypedQuery<Post> query = this.entityManager.createQuery(jpql, Post.class)
                .setParameter("board", board)
                .setParameter("title", "%" + StringUtils.escape(containTitle) + "%")
                .setFirstResult(offset)
                .setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    public long getPublishPostCount(Board board, PostType postType, String containTitle) {
        final String jpql = "SELECT COUNT(*) FROM Post p " +
                " WHERE p.board = :board" +
                " AND p.deleted = false" +
                " AND p.content IS NOT NULL" +
                " AND p.content.type = :type" +
                " AND UPPER(p.content.title) LIKE UPPER(:title)";

        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class)
                .setParameter("board", board)
                .setParameter("type", postType)
                .setParameter("title", "%" + StringUtils.escape(containTitle) + "%");
        return query.getSingleResult();
    }

    @Override
    public long getPublishPostCount(Board board, String containTitle) {
        final String jpql = "SELECT COUNT(*) FROM Post p" +
                " WHERE p.board = :board" +
                " AND p.deleted = false" +
                " AND p.content IS NOT NULL" +
                " AND UPPER(p.content.title) LIKE UPPER(:title)";

        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class)
                .setParameter("board", board)
                .setParameter("title", "%" + StringUtils.escape(containTitle) + "%");
        return query.getSingleResult();
    }

    @Override
    public void save(Post post) {
        if (post.getId() == null) {
            this.entityManager.persist(post);
        } else {
            this.entityManager.merge(post);
        }

        if (post.getPostBlock() != null) {
            PostBlock postBlock = post.getPostBlock();

            if (postBlock.getId() == null) {
                this.entityManager.persist(postBlock);
            } else {
                this.entityManager.merge(postBlock);
            }
        }

        this.entityManager.flush();
    }

    @Override
    public void save(PostContent postContent) {
        if (postContent.getId() == null) {
            this.entityManager.persist(postContent);
        } else {
            this.entityManager.merge(postContent);
        }
        this.entityManager.flush();
    }

    @Override
    public void save(Post post, PostContent postContent) {
        if (postContent.getId() == null) {
            this.entityManager.persist(postContent);
        } else {
            this.entityManager.merge(postContent);
        }

        if (post.getId() == null) {
            this.entityManager.persist(post);
        } else {
            this.entityManager.merge(post);
        }

        this.entityManager.flush();
    }
}

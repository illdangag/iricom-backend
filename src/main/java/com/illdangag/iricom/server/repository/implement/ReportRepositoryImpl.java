package com.illdangag.iricom.server.repository.implement;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.repository.ReportRepository;
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
public class ReportRepositoryImpl implements ReportRepository {
    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    public ReportRepositoryImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public long getPostReportListTotalCount(Board board, ReportType type, String reason, String postTitle) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT COUNT(*) FROM PostReport pr" +
                " WHERE pr.type = :type" +
                " AND pr.post.board = :board" +
                " AND UPPER(pr.reason) LIKE UPPER(:reason)" +
                " AND UPPER(pr.post.content.title) LIKE UPPER(:postTitle)";

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
                .setParameter("type", type)
                .setParameter("board", board)
                .setParameter("reason", "%" + StringUtils.escape(reason) + "%")
                .setParameter("postTitle", "%" + StringUtils.escape(postTitle) + "%");
        long result = query.getSingleResult();
        entityManager.close();
        return result;
    }

    @Override
    public List<PostReport> getPostReportList(Board board, ReportType type, String reason, String postTitle, int offset, int limit) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT pr FROM PostReport pr" +
                " WHERE pr.type = :type" +
                " AND pr.post.board = :board" +
                " AND UPPER(pr.reason) LIKE UPPER(:reason)" +
                " AND UPPER(pr.post.content.title) LIKE UPPER(:postTitle)" +
                " ORDER BY pr.createDate DESC";

        TypedQuery<PostReport> query = entityManager.createQuery(jpql, PostReport.class)
                .setParameter("type", type)
                .setParameter("board", board)
                .setParameter("reason", "%" + StringUtils.escape(reason) + "%")
                .setParameter("postTitle", "%" + StringUtils.escape(postTitle) + "%")
                .setFirstResult(offset)
                .setMaxResults(limit);
        List<PostReport> resultList = query.getResultList();
        entityManager.close();
        return resultList;
    }

    @Override
    public long getPostReportListTotalCount(Board board, Post post, String reason, String postTitle) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT COUNT(*) FROM PostReport pr" +
                " WHERE pr.post = :post" +
                " AND pr.post.board = :board" +
                " AND UPPER(pr.reason) LIKE UPPER(:reason)" +
                " AND UPPER(pr.post.content.title) LIKE UPPER(:postTitle)";

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
                .setParameter("post", post)
                .setParameter("board", board)
                .setParameter("reason", "%" + StringUtils.escape(reason) + "%")
                .setParameter("postTitle", "%" + StringUtils.escape(postTitle) + "%");
        long result = query.getSingleResult();
        entityManager.close();
        return result;
    }

    @Override
    public List<PostReport> getPostReportList(Board board, Post post, String reason, String postTitle, int offset, int limit) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT pr FROM PostReport pr" +
                " WHERE pr.post = :post" +
                " AND pr.post.board = :board" +
                " AND UPPER(pr.reason) LIKE UPPER(:reason)" +
                " AND UPPER(pr.post.content.title) LIKE UPPER(:postTitle)" +
                " ORDER BY pr.createDate DESC";

        TypedQuery<PostReport> query = entityManager.createQuery(jpql, PostReport.class)
                .setParameter("post", post)
                .setParameter("board", board)
                .setParameter("reason", "%" + StringUtils.escape(reason) + "%")
                .setParameter("postTitle", "%" + StringUtils.escape(postTitle) + "%")
                .setFirstResult(offset)
                .setMaxResults(limit);
        List<PostReport> resultList = query.getResultList();
        entityManager.close();
        return resultList;
    }

    @Override
    public long getPostReportListTotalCount(Board board, Post post, ReportType type, String reason, String postTitle) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT COUNT(*) FROM PostReport pr" +
                " WHERE pr.type = :type" +
                " AND pr.post = :post" +
                " AND pr.post.board = :board" +
                " AND UPPER(pr.reason) LIKE UPPER(:reason)" +
                " AND UPPER(pr.post.content.title) LIKE UPPER(:postTitle)";

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
                .setParameter("type", type)
                .setParameter("post", post)
                .setParameter("board", board)
                .setParameter("reason", "%" + StringUtils.escape(reason) + "%")
                .setParameter("postTitle", "%" + StringUtils.escape(postTitle) + "%");
        long result = query.getSingleResult();
        entityManager.close();
        return result;
    }

    @Override
    public List<PostReport> getPostReportList(Board board, Post post, ReportType type, String reason, String postTitle, int offset, int limit) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT pr FROM PostReport pr" +
                " WHERE pr.type = :type" +
                " AND pr.post = :post" +
                " AND pr.post.board = :board" +
                " AND UPPER(pr.reason) LIKE UPPER(:reason)" +
                " AND UPPER(pr.post.content.title) LIKE UPPER(:postTitle)" +
                " ORDER BY pr.createDate DESC";

        TypedQuery<PostReport> query = entityManager.createQuery(jpql, PostReport.class)
                .setParameter("type", type)
                .setParameter("post", post)
                .setParameter("board", board)
                .setParameter("reason", "%" + StringUtils.escape(reason) + "%")
                .setParameter("postTitle", "%" + StringUtils.escape(postTitle) + "%")
                .setFirstResult(offset)
                .setMaxResults(limit);
        List<PostReport> resultList = query.getResultList();
        entityManager.close();
        return resultList;
    }

    @Override
    public long getPostReportListTotalCount(Board board, String reason, String postTitle) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT COUNT(*) FROM PostReport pr" +
                " WHERE pr.post.board = :board" +
                " AND UPPER(pr.reason) LIKE UPPER(:reason)" +
                " AND UPPER(pr.post.content.title) LIKE UPPER(:postTitle)";

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
                .setParameter("board", board)
                .setParameter("reason", "%" + StringUtils.escape(reason) + "%")
                .setParameter("postTitle", "%" + StringUtils.escape(postTitle) + "%");
        long result = query.getSingleResult();
        entityManager.close();
        return result;
    }

    @Override
    public List<PostReport> getPostReportList(Board board, String reason, String postTitle, int offset, int limit) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT pr FROM PostReport pr" +
                " WHERE pr.post.board = :board" +
                " AND UPPER(pr.reason) LIKE UPPER(:reason)" +
                " AND UPPER(pr.post.content.title) LIKE UPPER(:postTitle)" +
                " ORDER BY pr.createDate DESC";

        TypedQuery<PostReport> query = entityManager.createQuery(jpql, PostReport.class)
                .setParameter("board", board)
                .setParameter("reason", "%" + StringUtils.escape(reason) + "%")
                .setParameter("postTitle", "%" + StringUtils.escape(postTitle) + "%")
                .setFirstResult(offset)
                .setMaxResults(limit);
        List<PostReport> resultList = query.getResultList();
        entityManager.close();
        return resultList;
    }

    @Override
    public List<PostReport> getPostReportList(Account account, Post post) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT pr FROM PostReport pr " +
                "WHERE pr.account = :account " +
                "AND pr.post = :post";

        TypedQuery<PostReport> query = entityManager.createQuery(jpql, PostReport.class)
                .setParameter("account", account)
                .setParameter("post", post);
        List<PostReport> reportList = query.getResultList();
        entityManager.close();

        return reportList;
    }

    @Override
    public Optional<PostReport> getPostReport(String id) {
        long postReportId = -1;
        try {
            postReportId = Long.parseLong(id);
        } catch (Exception exception) {
            return Optional.empty();
        }

        return this.getPostReport(postReportId);
    }

    @Override
    public Optional<PostReport> getPostReport(long id) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT pr FROM PostReport pr " +
                "WHERE pr.id = :id";

        TypedQuery<PostReport> query = entityManager.createQuery(jpql, PostReport.class)
                .setParameter("id", id);
        List<PostReport> resultList = query.getResultList();
        entityManager.close();

        if (resultList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(resultList.get(0));
        }
    }

    @Override
    public void savePostReport(PostReport postReport) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        if (postReport.getId() == null) {
            entityManager.persist(postReport);
        } else {
            entityManager.merge(postReport);
        }
        transaction.commit();
        entityManager.close();
    }

    @Override
    public List<CommentReport> getCommentReportList(Account account, Comment comment) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT cr FROM CommentReport cr " +
                "WHERE cr.account = :account " +
                "AND cr.comment = :comment";

        TypedQuery<CommentReport> query = entityManager.createQuery(jpql, CommentReport.class)
                .setParameter("account", account)
                .setParameter("comment", comment);
        List<CommentReport> reportList = query.getResultList();
        entityManager.close();

        return reportList;
    }

    @Override
    public void saveCommentReport(CommentReport commentReport) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        if (commentReport.getId() == null) {
            entityManager.persist(commentReport);
        } else {
            entityManager.merge(commentReport);
        }
        transaction.commit();
        entityManager.close();
    }

    @Override
    public Optional<CommentReport> getCommentReport(String id) {
        long commentReportId = -1;

        try {
            commentReportId = Long.parseLong(id);
        } catch (Exception exception) {
            return Optional.empty();
        }

        return this.getCommentReport(commentReportId);
    }

    @Override
    public Optional<CommentReport> getCommentReport(long id) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT cr FROM CommentReport cr " +
                "WHERE cr.id = :id";

        TypedQuery<CommentReport> query = entityManager.createQuery(jpql, CommentReport.class)
                .setParameter("id", id);
        List<CommentReport> resultList = query.getResultList();
        entityManager.close();

        if (resultList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(resultList.get(0));
        }
    }

    @Override
    public long getPortReportCount(Post post) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT COUNT(*) FROM PostReport pr " +
                "WHERE pr.post = :post";

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
                .setParameter("post", post);
        long result = query.getSingleResult();
        entityManager.close();
        return result;
    }

    @Override
    public long getCommentReportCount(Comment comment) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        final String jpql = "SELECT COUNT(*) FROM CommentReport cr " +
                "WHERE cr.comment = :comment";

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
                .setParameter("comment", comment);
        long result = query.getSingleResult();
        entityManager.close();
        return result;
    }
}

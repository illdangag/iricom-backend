package com.illdangag.iricom.server.repository.implement;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.data.entity.type.ReportType;
import com.illdangag.iricom.server.repository.ReportRepository;
import com.illdangag.iricom.server.util.StringUtils;
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
public class ReportRepositoryImpl implements ReportRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public long getPostReportListTotalCount(Board board, ReportType type, String reason) {
        final String jpql = "SELECT COUNT(*) FROM PostReport pr" +
                " WHERE pr.type = :type" +
                " AND pr.post.board = :board" +
                " AND UPPER(pr.reason) LIKE UPPER(:reason)";

        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class)
                .setParameter("type", type)
                .setParameter("board", board)
                .setParameter("reason", "%" + StringUtils.escape(reason) + "%");
        long result = query.getSingleResult();
        return result;
    }

    @Override
    public List<PostReport> getPostReportList(Board board, ReportType type, String reason, int offset, int limit) {
        final String jpql = "SELECT pr FROM PostReport pr" +
                " WHERE pr.type = :type" +
                " AND pr.post.board = :board" +
                " AND UPPER(pr.reason) LIKE UPPER(:reason)" +
                " ORDER BY pr.createDate DESC";

        TypedQuery<PostReport> query = this.entityManager.createQuery(jpql, PostReport.class)
                .setParameter("type", type)
                .setParameter("board", board)
                .setParameter("reason", "%" + StringUtils.escape(reason) + "%")
                .setFirstResult(offset)
                .setMaxResults(limit);
        List<PostReport> resultList = query.getResultList();
        return resultList;
    }

    @Override
    public long getPostReportListTotalCount(Board board, Post post, String reason) {
        final String jpql = "SELECT COUNT(*) FROM PostReport pr" +
                " WHERE pr.post = :post" +
                " AND pr.post.board = :board" +
                " AND UPPER(pr.reason) LIKE UPPER(:reason)";

        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class)
                .setParameter("post", post)
                .setParameter("board", board)
                .setParameter("reason", "%" + StringUtils.escape(reason) + "%");
        long result = query.getSingleResult();
        return result;
    }

    @Override
    public List<PostReport> getPostReportList(Board board, Post post, String reason, int offset, int limit) {
        final String jpql = "SELECT pr FROM PostReport pr" +
                " WHERE pr.post = :post" +
                " AND pr.post.board = :board" +
                " AND UPPER(pr.reason) LIKE UPPER(:reason)" +
                " ORDER BY pr.createDate DESC";

        TypedQuery<PostReport> query = this.entityManager.createQuery(jpql, PostReport.class)
                .setParameter("post", post)
                .setParameter("board", board)
                .setParameter("reason", "%" + StringUtils.escape(reason) + "%")
                .setFirstResult(offset)
                .setMaxResults(limit);
        List<PostReport> resultList = query.getResultList();
        return resultList;
    }

    @Override
    public long getPostReportListTotalCount(Board board, Post post, ReportType type, String reason) {
        final String jpql = "SELECT COUNT(*) FROM PostReport pr" +
                " WHERE pr.type = :type" +
                " AND pr.post = :post" +
                " AND pr.post.board = :board" +
                " AND UPPER(pr.reason) LIKE UPPER(:reason)";

        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class)
                .setParameter("type", type)
                .setParameter("post", post)
                .setParameter("board", board)
                .setParameter("reason", "%" + StringUtils.escape(reason) + "%");
        long result = query.getSingleResult();
        return result;
    }

    @Override
    public List<PostReport> getPostReportList(Board board, Post post, ReportType type, String reason, int offset, int limit) {
        final String jpql = "SELECT pr FROM PostReport pr" +
                " WHERE pr.type = :type" +
                " AND pr.post = :post" +
                " AND pr.post.board = :board" +
                " AND UPPER(pr.reason) LIKE UPPER(:reason)" +
                " ORDER BY pr.createDate DESC";

        TypedQuery<PostReport> query = this.entityManager.createQuery(jpql, PostReport.class)
                .setParameter("type", type)
                .setParameter("post", post)
                .setParameter("board", board)
                .setParameter("reason", "%" + StringUtils.escape(reason) + "%")
                .setFirstResult(offset)
                .setMaxResults(limit);
        List<PostReport> resultList = query.getResultList();
        return resultList;
    }

    @Override
    public long getPostReportListTotalCount(Board board, String reason) {
        final String jpql = "SELECT COUNT(*) FROM PostReport pr" +
                " WHERE pr.post.board = :board" +
                " AND UPPER(pr.reason) LIKE UPPER(:reason)";

        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class)
                .setParameter("board", board)
                .setParameter("reason", "%" + StringUtils.escape(reason) + "%");
        long result = query.getSingleResult();
        return result;
    }

    @Override
    public List<PostReport> getPostReportList(Board board, String reason, int offset, int limit) {
        final String jpql = "SELECT pr FROM PostReport pr" +
                " WHERE pr.post.board = :board" +
                " AND UPPER(pr.reason) LIKE UPPER(:reason)" +
                " ORDER BY pr.createDate DESC";

        TypedQuery<PostReport> query = this.entityManager.createQuery(jpql, PostReport.class)
                .setParameter("board", board)
                .setParameter("reason", "%" + StringUtils.escape(reason) + "%")
                .setFirstResult(offset)
                .setMaxResults(limit);
        List<PostReport> resultList = query.getResultList();
        return resultList;
    }

    @Override
    public List<PostReport> getPostReportList(Account account, Post post) {
        final String jpql = "SELECT pr FROM PostReport pr " +
                "WHERE pr.account = :account " +
                "AND pr.post = :post";

        TypedQuery<PostReport> query = this.entityManager.createQuery(jpql, PostReport.class)
                .setParameter("account", account)
                .setParameter("post", post);
        List<PostReport> reportList = query.getResultList();

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
        final String jpql = "SELECT pr FROM PostReport pr " +
                "WHERE pr.id = :id";

        TypedQuery<PostReport> query = this.entityManager.createQuery(jpql, PostReport.class)
                .setParameter("id", id);
        List<PostReport> resultList = query.getResultList();

        if (resultList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(resultList.get(0));
        }
    }

    @Override
    public void savePostReport(PostReport postReport) {
        if (postReport.getId() == null) {
            this.entityManager.persist(postReport);
        } else {
            this.entityManager.merge(postReport);
        }
    }

    @Override
    public long getCommentReportListTotalCount(Board board, ReportType reportType, String reason) {
        final String jpql = "SELECT COUNT(*) FROM CommentReport cr" +
                " WHERE cr.type = :type" +
                " AND cr.comment.post.board = :board" +
                " AND UPPER(cr.reason) LIKE UPPER(:reason)";

        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class)
                .setParameter("type", reportType)
                .setParameter("board", board)
                .setParameter("reason", "%" + StringUtils.escape(reason) + "%");
        long result = query.getSingleResult();
        return result;
    }

    @Override
    public List<CommentReport> getCommentReportList(Board board, ReportType reportType, String reason, int offset, int limit) {
        final String jpql = "SELECT cr FROM CommentReport cr" +
                " WHERE cr.type = :type" +
                " AND cr.comment.post.board = :board" +
                " AND UPPER(cr.reason) LIKE UPPER(:reason)" +
                " ORDER BY cr.createDate DESC";

        TypedQuery<CommentReport> query = this.entityManager.createQuery(jpql, CommentReport.class)
                .setParameter("type", reportType)
                .setParameter("board", board)
                .setParameter("reason", "%" + StringUtils.escape(reason) + "%")
                .setFirstResult(offset)
                .setMaxResults(limit);
        List<CommentReport> reportList = query.getResultList();
        return reportList;
    }

    @Override
    public long getCommentReportListTotalCount(Board board, String reason) {
        final String jpql = "SELECT COUNT(*) FROM CommentReport cr" +
                " WHERE cr.comment.post.board = :board" +
                " AND UPPER(cr.reason) LIKE UPPER(:reason)";

        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class)
                .setParameter("board", board)
                .setParameter("reason", "%" + StringUtils.escape(reason) + "%");
        long result = query.getSingleResult();
        return result;
    }

    @Override
    public List<CommentReport> getCommentReportList(Board board, String reason, int offset, int limit) {
        final String jpql = "SELECT cr FROM CommentReport cr" +
                " WHERE cr.comment.post.board = :board" +
                " AND UPPER(cr.reason) LIKE UPPER(:reason)" +
                " ORDER BY cr.createDate DESC";

        TypedQuery<CommentReport> query = this.entityManager.createQuery(jpql, CommentReport.class)
                .setParameter("board", board)
                .setParameter("reason", "%" + StringUtils.escape(reason) + "%")
                .setFirstResult(offset)
                .setMaxResults(limit);
        List<CommentReport> reportList = query.getResultList();
        return reportList;
    }

    @Override
    public long getCommentReportListTotalCount(Board board, Post post, ReportType reportType, String reason) {
        final String jpql = "SELECT COUNT(*) FROM CommentReport cr" +
                " WHERE cr.type = :type" +
                " AND cr.comment.post = :post" +
                " AND cr.comment.post.board = :board" +
                " AND UPPER(cr.reason) LIKE UPPER(:reason)";

        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class)
                .setParameter("type", reportType)
                .setParameter("post", post)
                .setParameter("board", board)
                .setParameter("reason", "%" + StringUtils.escape(reason) + "%");
        long result = query.getSingleResult();
        return result;
    }

    @Override
    public List<CommentReport> getCommentReportList(Board board, Post post, ReportType reportType, String reason, int offset, int limit) {
        final String jpql = "SELECT cr FROM CommentReport cr" +
                " WHERE cr.type = :type" +
                " AND cr.comment.post = :post" +
                " AND cr.comment.post.board = :board" +
                " AND UPPER(cr.reason) LIKE UPPER(:reason)" +
                " ORDER BY cr.createDate DESC";

        TypedQuery<CommentReport> query = this.entityManager.createQuery(jpql, CommentReport.class)
                .setParameter("type", reportType)
                .setParameter("post", post)
                .setParameter("board", board)
                .setParameter("reason", "%" + StringUtils.escape(reason) + "%")
                .setFirstResult(offset)
                .setMaxResults(limit);
        List<CommentReport> reportList = query.getResultList();
        return reportList;
    }

    @Override
    public long getCommentReportListTotalCount(Board board, Post post, String reason) {
        final String jpql = "SELECT COUNT(*) FROM CommentReport cr" +
                " WHERE cr.comment.post = :post" +
                " AND cr.comment.post.board = :board" +
                " AND UPPER(cr.reason) LIKE UPPER(:reason)";

        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class)
                .setParameter("post", post)
                .setParameter("board", board)
                .setParameter("reason", "%" + StringUtils.escape(reason) + "%");
        long result = query.getSingleResult();
        return result;
    }

    @Override
    public List<CommentReport> getCommentReportList(Board board, Post post, String reason, int offset, int limit) {
        final String jpql = "SELECT cr FROM CommentReport cr" +
                " WHERE cr.comment.post = :post" +
                " AND cr.comment.post.board = :board" +
                " AND UPPER(cr.reason) LIKE UPPER(:reason)" +
                " ORDER BY cr.createDate DESC";

        TypedQuery<CommentReport> query = this.entityManager.createQuery(jpql, CommentReport.class)
                .setParameter("post", post)
                .setParameter("board", board)
                .setParameter("reason", "%" + StringUtils.escape(reason) + "%")
                .setFirstResult(offset)
                .setMaxResults(limit);
        List<CommentReport> reportList = query.getResultList();
        return reportList;
    }

    @Override
    public long getCommentReportListTotalCount(Board board, Post post, Comment comment, ReportType reportType, String reason) {
        final String jpql = "SELECT COUNT(*) FROM CommentReport cr" +
                " WHERE cr.type = :type" +
                " AND cr.comment = :comment " +
                " AND cr.comment.post = :post" +
                " AND cr.comment.post.board = :board" +
                " AND UPPER(cr.reason) LIKE UPPER(:reason)";

        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class)
                .setParameter("type", reportType)
                .setParameter("comment", comment)
                .setParameter("post", post)
                .setParameter("board", board)
                .setParameter("reason", "%" + StringUtils.escape(reason) + "%");
        long result = query.getSingleResult();
        return result;
    }

    @Override
    public List<CommentReport> getCommentReportList(Board board, Post post, Comment comment, ReportType reportType, String reason, int offset, int limit) {
        final String jpql = "SELECT cr FROM CommentReport cr" +
                " WHERE cr.type = :type" +
                " AND cr.comment = :comment" +
                " AND cr.comment.post = :post" +
                " AND cr.comment.post.board = :board" +
                " AND UPPER(cr.reason) LIKE UPPER(:reason)" +
                " ORDER BY cr.createDate DESC";

        TypedQuery<CommentReport> query = this.entityManager.createQuery(jpql, CommentReport.class)
                .setParameter("type", reportType)
                .setParameter("comment", comment)
                .setParameter("post", post)
                .setParameter("board", board)
                .setParameter("reason", "%" + StringUtils.escape(reason) + "%")
                .setFirstResult(offset)
                .setMaxResults(limit);
        List<CommentReport> reportList = query.getResultList();
        return reportList;
    }

    @Override
    public long getCommentReportListTotalCount(Board board, Post post, Comment comment, String reason) {
        final String jpql = "SELECT COUNT(*) FROM CommentReport cr" +
                " WHERE cr.comment = :comment " +
                " AND cr.comment.post = :post" +
                " AND cr.comment.post.board = :board" +
                " AND UPPER(cr.reason) LIKE UPPER(:reason)";

        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class)
                .setParameter("comment", comment)
                .setParameter("post", post)
                .setParameter("board", board)
                .setParameter("reason", "%" + StringUtils.escape(reason) + "%");
        long result = query.getSingleResult();
        return result;
    }

    @Override
    public List<CommentReport> getCommentReportList(Board board, Post post, Comment comment, String reason, int offset, int limit) {
        final String jpql = "SELECT cr FROM CommentReport cr" +
                " WHERE cr.comment = :comment" +
                " AND cr.comment.post = :post" +
                " AND cr.comment.post.board = :board" +
                " AND UPPER(cr.reason) LIKE UPPER(:reason)" +
                " ORDER BY cr.createDate DESC";

        TypedQuery<CommentReport> query = this.entityManager.createQuery(jpql, CommentReport.class)
                .setParameter("comment", comment)
                .setParameter("post", post)
                .setParameter("board", board)
                .setParameter("reason", "%" + StringUtils.escape(reason) + "%")
                .setFirstResult(offset)
                .setMaxResults(limit);
        List<CommentReport> reportList = query.getResultList();
        return reportList;
    }

    @Override
    public List<CommentReport> getCommentReportList(Account account, Comment comment) {
        final String jpql = "SELECT cr FROM CommentReport cr " +
                "WHERE cr.account = :account " +
                "AND cr.comment = :comment";

        TypedQuery<CommentReport> query = this.entityManager.createQuery(jpql, CommentReport.class)
                .setParameter("account", account)
                .setParameter("comment", comment);
        List<CommentReport> reportList = query.getResultList();

        return reportList;
    }

    @Override
    public void saveCommentReport(CommentReport commentReport) {
        if (commentReport.getId() == null) {
            this.entityManager.persist(commentReport);
        } else {
            this.entityManager.merge(commentReport);
        }
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
        final String jpql = "SELECT cr FROM CommentReport cr " +
                "WHERE cr.id = :id";

        TypedQuery<CommentReport> query = this.entityManager.createQuery(jpql, CommentReport.class)
                .setParameter("id", id);
        List<CommentReport> resultList = query.getResultList();

        if (resultList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(resultList.get(0));
        }
    }

    @Override
    public long getPortReportCount(Post post) {
        final String jpql = "SELECT COUNT(*) FROM PostReport pr " +
                "WHERE pr.post = :post";

        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class)
                .setParameter("post", post);
        long result = query.getSingleResult();
        return result;
    }

    @Override
    public long getCommentReportCount(Comment comment) {
        final String jpql = "SELECT COUNT(*) FROM CommentReport cr " +
                "WHERE cr.comment = :comment";

        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class)
                .setParameter("comment", comment);
        long result = query.getSingleResult();
        return result;
    }
}

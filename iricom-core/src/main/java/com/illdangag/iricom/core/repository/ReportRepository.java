package com.illdangag.iricom.core.repository;

import com.illdangag.iricom.core.data.entity.*;
import com.illdangag.iricom.core.data.entity.type.ReportType;

import java.util.List;
import java.util.Optional;

public interface ReportRepository {
    long getPostReportListTotalCount(Board board, String reason);

    List<PostReport> getPostReportList(Board board, String reason, int offset, int limit);

    long getPostReportListTotalCount(Board board, ReportType type, String reason);

    List<PostReport> getPostReportList(Board board, ReportType type,  String reason, int offset, int limit);

    long getPostReportListTotalCount(Board board, Post post, String reason);

    List<PostReport> getPostReportList(Board board, Post post, String reason, int offset, int limit);

    long getPostReportListTotalCount(Board board, Post post, ReportType type, String reason);

    List<PostReport> getPostReportList(Board board, Post post, ReportType type,  String reason, int offset, int limit);

    List<PostReport> getPostReportList(Account account, Post post);

    Optional<PostReport> getPostReport(String id);

    Optional<PostReport> getPostReport(long id);

    long getPortReportCount(Post post);

    void savePostReport(PostReport postReport);

    long getCommentReportListTotalCount(Board board, ReportType reportType, String reason);

    List<CommentReport> getCommentReportList(Board board, ReportType reportType, String reason, int offset, int limit);

    long getCommentReportListTotalCount(Board board, String reason);

    List<CommentReport> getCommentReportList(Board board, String reason, int offset, int limit);

    long getCommentReportListTotalCount(Board board, Post post, ReportType reportType, String reason);

    List<CommentReport> getCommentReportList(Board board, Post post, ReportType reportType, String reason, int offset, int limit);

    long getCommentReportListTotalCount(Board board, Post post, String reason);

    List<CommentReport> getCommentReportList(Board board, Post post, String reason, int offset, int limit);

    long getCommentReportListTotalCount(Board board, Post post, Comment comment, ReportType reportType, String reason);

    List<CommentReport> getCommentReportList(Board board, Post post, Comment comment, ReportType reportType, String reason, int offset, int limit);

    long getCommentReportListTotalCount(Board board, Post post, Comment comment, String reason);

    List<CommentReport> getCommentReportList(Board board, Post post, Comment comment, String reason, int offset, int limit);

    List<CommentReport> getCommentReportList(Account account, Comment comment);

    Optional<CommentReport> getCommentReport(String id);

    Optional<CommentReport> getCommentReport(long id);

    long getCommentReportCount(Comment comment);

    void saveCommentReport(CommentReport commentReport);
}

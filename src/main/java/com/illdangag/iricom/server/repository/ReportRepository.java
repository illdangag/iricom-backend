package com.illdangag.iricom.server.repository;

import com.illdangag.iricom.server.data.entity.*;

import java.util.List;
import java.util.Optional;

public interface ReportRepository {
    long getPostReportListTotalCount(Board board, String reason, String postTitle);

    List<PostReport> getPostReportList(Board board, String reason, String postTitle, int offset, int limit);

    long getPostReportListTotalCount(Board board, ReportType type, String reason, String postTitle);

    List<PostReport> getPostReportList(Board board, ReportType type,  String reason, String postTitle, int offset, int limit);

    long getPostReportListTotalCount(Board board, Post post, String reason, String postTitle);

    List<PostReport> getPostReportList(Board board, Post post, String reason, String postTitle, int offset, int limit);

    long getPostReportListTotalCount(Board board, Post post, ReportType type, String reason, String postTitle);

    List<PostReport> getPostReportList(Board board, Post post, ReportType type,  String reason, String postTitle, int offset, int limit);

    List<PostReport> getPostReportList(Account account, Post post);

    Optional<PostReport> getPostReport(String id);

    Optional<PostReport> getPostReport(long id);

    void savePostReport(PostReport postReport);

    long getPortReportCount(Post post);

    List<CommentReport> getCommentReportList(Account account, Comment comment);

    void saveCommentReport(CommentReport commentReport);

    Optional<CommentReport> getCommentReport(String id);

    Optional<CommentReport> getCommentReport(long id);

    long getCommentReportCount(Comment comment);
}

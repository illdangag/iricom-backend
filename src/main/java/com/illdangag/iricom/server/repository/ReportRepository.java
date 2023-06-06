package com.illdangag.iricom.server.repository;

import com.illdangag.iricom.server.data.entity.*;

import java.util.List;
import java.util.Optional;

public interface ReportRepository {
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

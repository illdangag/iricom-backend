package com.illdangag.iricom.server.repository;

import com.illdangag.iricom.server.data.entity.*;

import java.util.List;

public interface ReportRepository {
    List<PostReport> getPostReport(Account account, Post post);

    void savePostReport(PostReport postReport);

    List<CommentReport> getCommentReportList(Account account, Comment comment);

    void saveCommentReport(CommentReport commentReport);
}

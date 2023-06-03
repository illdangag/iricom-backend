package com.illdangag.iricom.server.repository;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.Post;
import com.illdangag.iricom.server.data.entity.PostReport;

import java.util.List;

public interface ReportRepository {
    List<PostReport> getPostReport(Account account, Post post);

    void savePostReport(PostReport postReport);
}

package com.illdangag.iricom.server.service;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.request.CommentReportCreate;
import com.illdangag.iricom.server.data.request.PostReportCreate;
import com.illdangag.iricom.server.data.response.CommentReportInfo;
import com.illdangag.iricom.server.data.response.PostReportInfo;

import javax.validation.Valid;

public interface ReportService {
    PostReportInfo reportPost(Account account, @Valid PostReportCreate postReportCreate);

    CommentReportInfo reportComment(Account account, @Valid CommentReportCreate commentReportCreate);
}

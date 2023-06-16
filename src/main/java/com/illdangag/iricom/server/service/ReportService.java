package com.illdangag.iricom.server.service;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.Board;
import com.illdangag.iricom.server.data.entity.Post;
import com.illdangag.iricom.server.data.request.CommentReportCreate;
import com.illdangag.iricom.server.data.request.PostBanCreate;
import com.illdangag.iricom.server.data.request.PostReportCreate;
import com.illdangag.iricom.server.data.response.CommentReportInfo;
import com.illdangag.iricom.server.data.response.PostInfo;
import com.illdangag.iricom.server.data.response.PostReportInfo;

import javax.validation.Valid;

public interface ReportService {
    PostReportInfo reportPost(Account account, String boardI, String postId, @Valid PostReportCreate postReportCreate);

    PostReportInfo reportPost(Account account, Board board, Post post, @Valid PostReportCreate postReportCreate);

    CommentReportInfo reportComment(Account account, @Valid CommentReportCreate commentReportCreate);

    PostInfo banPost(Account account, String boardId, PostBanCreate postBanCreate);

    PostInfo banPost(Account account, Board board, PostBanCreate postBanCreate);
}

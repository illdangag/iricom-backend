package com.illdangag.iricom.server.service;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.Board;
import com.illdangag.iricom.server.data.entity.Comment;
import com.illdangag.iricom.server.data.entity.Post;
import com.illdangag.iricom.server.data.request.CommentReportCreate;
import com.illdangag.iricom.server.data.request.PostReportCreate;
import com.illdangag.iricom.server.data.response.CommentReportInfo;
import com.illdangag.iricom.server.data.response.PostReportInfo;

import javax.validation.Valid;

public interface ReportService {
    PostReportInfo reportPost(Account account, String boardId, String postId, @Valid PostReportCreate postReportCreate);

    PostReportInfo reportPost(Account account, Board board, Post post, @Valid PostReportCreate postReportCreate);

    CommentReportInfo reportComment(Account account, String boardId, String postId, String commentId, @Valid CommentReportCreate commentReportCreate);

    CommentReportInfo reportComment(Account account, Board board, Post post, Comment comment, @Valid CommentReportCreate commentReportCreate);
}

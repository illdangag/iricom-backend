package com.illdangag.iricom.server.service;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.Board;
import com.illdangag.iricom.server.data.entity.Comment;
import com.illdangag.iricom.server.data.entity.Post;
import com.illdangag.iricom.server.data.request.CommentReportCreate;
import com.illdangag.iricom.server.data.request.PostReportInfoCreate;
import com.illdangag.iricom.server.data.request.PostReportInfoSearch;
import com.illdangag.iricom.server.data.response.CommentReportInfo;
import com.illdangag.iricom.server.data.response.PostReportInfo;
import com.illdangag.iricom.server.data.response.PostReportInfoList;

import javax.validation.Valid;

public interface ReportService {
    PostReportInfoList getPostReportInfoList(Account account, String boardId, @Valid PostReportInfoSearch postReportInfoSearch);

    PostReportInfoList getPostReportInfoList(Account account, Board board, @Valid PostReportInfoSearch postReportInfoSearch);

    PostReportInfoList getPostReportInfoList(Account account, String boardId, String postId, @Valid PostReportInfoSearch postReportInfoSearch);

    PostReportInfoList getPostReportInfoList(Account account, Board board, Post post, @Valid PostReportInfoSearch postReportInfoSearch);

    PostReportInfo getPostReportInfo(Account account, String boardId, String postId, String reportId);

    PostReportInfo getPostReportInfo(Account account, Board board, Post post, String reportId);

    PostReportInfo reportPost(Account account, String boardId, String postId, @Valid PostReportInfoCreate postReportInfoCreate);

    PostReportInfo reportPost(Account account, Board board, Post post, @Valid PostReportInfoCreate postReportInfoCreate);

    CommentReportInfo reportComment(Account account, String boardId, String postId, String commentId, @Valid CommentReportCreate commentReportCreate);

    CommentReportInfo reportComment(Account account, Board board, Post post, Comment comment, @Valid CommentReportCreate commentReportCreate);
}

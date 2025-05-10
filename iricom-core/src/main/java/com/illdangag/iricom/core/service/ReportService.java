package com.illdangag.iricom.core.service;

import com.illdangag.iricom.core.data.entity.Account;
import com.illdangag.iricom.core.data.entity.Board;
import com.illdangag.iricom.core.data.entity.Comment;
import com.illdangag.iricom.core.data.entity.Post;
import com.illdangag.iricom.core.data.request.CommentReportInfoCreate;
import com.illdangag.iricom.core.data.request.CommentReportInfoSearch;
import com.illdangag.iricom.core.data.request.PostReportInfoCreate;
import com.illdangag.iricom.core.data.request.PostReportInfoSearch;
import com.illdangag.iricom.core.data.response.CommentReportInfo;
import com.illdangag.iricom.core.data.response.CommentReportInfoList;
import com.illdangag.iricom.core.data.response.PostReportInfo;
import com.illdangag.iricom.core.data.response.PostReportInfoList;

import javax.validation.Valid;

public interface ReportService {
    /**
     * 게시물 신고
     */
    PostReportInfo reportPost(String accountId, String boardId, String postId, @Valid PostReportInfoCreate postReportInfoCreate);

    PostReportInfo reportPost(Account account, String boardId, String postId, @Valid PostReportInfoCreate postReportInfoCreate);

    PostReportInfo reportPost(Account account, Board board, Post post, @Valid PostReportInfoCreate postReportInfoCreate);

    /**
     * 신고된 게시물 목록 조회
     */
    PostReportInfoList getPostReportInfoList(String accountId, String boardId, @Valid PostReportInfoSearch postReportInfoSearch);

    PostReportInfoList getPostReportInfoList(Account account, String boardId, @Valid PostReportInfoSearch postReportInfoSearch);

    PostReportInfoList getPostReportInfoList(Account account, Board board, @Valid PostReportInfoSearch postReportInfoSearch);

    PostReportInfoList getPostReportInfoList(Account account, String boardId, String postId, @Valid PostReportInfoSearch postReportInfoSearch);

    PostReportInfoList getPostReportInfoList(Account account, Board board, Post post, @Valid PostReportInfoSearch postReportInfoSearch);

    /**
     * 신고된 게시물 정보 조회
     */
    PostReportInfo getPostReportInfo(String accountId, String boardId, String postId, String reportId);

    PostReportInfo getPostReportInfo(Account account, String boardId, String postId, String reportId);

    PostReportInfo getPostReportInfo(Account account, Board board, Post post, String reportId);

    /**
     * 댓글 신고
     */
    CommentReportInfo reportComment(String accountId, String boardId, String postId, String commentId, @Valid CommentReportInfoCreate commentReportInfoCreate);

    CommentReportInfo reportComment(Account account, String boardId, String postId, String commentId, @Valid CommentReportInfoCreate commentReportInfoCreate);

    CommentReportInfo reportComment(Account account, Board board, Post post, Comment comment, @Valid CommentReportInfoCreate commentReportInfoCreate);

    /**
     * 신고된 댓글 목록 조회
     */
    CommentReportInfoList getCommentReportInfoList(String accountId, String boardId, @Valid CommentReportInfoSearch commentReportInfoSearch);

    CommentReportInfoList getCommentReportInfoList(Account account, String boardId, @Valid CommentReportInfoSearch commentReportInfoSearch);

    CommentReportInfoList getCommentReportInfoList(Account account, Board board, @Valid CommentReportInfoSearch commentReportInfoSearch);

    CommentReportInfoList getCommentReportInfoList(String accountId, String boardId, String postId, @Valid CommentReportInfoSearch commentReportInfoSearch);

    CommentReportInfoList getCommentReportInfoList(Account account, String boardId, String postId, @Valid CommentReportInfoSearch commentReportInfoSearch);

    CommentReportInfoList getCommentReportInfoList(Account account, Board board, Post post, @Valid CommentReportInfoSearch commentReportInfoSearch);

    CommentReportInfoList getCommentReportInfoList(String accountId, String boardId, String postId, String commentId, @Valid CommentReportInfoSearch commentReportInfoSearch);

    CommentReportInfoList getCommentReportInfoList(Account account, String boardId, String postId, String commentId, @Valid CommentReportInfoSearch commentReportInfoSearch);

    CommentReportInfoList getCommentReportInfoList(Account account, Board board, Post post, Comment comment, @Valid CommentReportInfoSearch commentReportInfoSearch);

    /**
     * 신고된 댓글 정보 조회
     */
    CommentReportInfo getCommentReportInfo(Account account, String boardId, String postId, String commentId, String reportId);

    CommentReportInfo getCommentReportInfo(Account account, Board board, Post post, Comment comment, String reportId);
}

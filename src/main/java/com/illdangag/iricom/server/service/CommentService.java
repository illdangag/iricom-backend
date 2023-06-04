package com.illdangag.iricom.server.service;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.data.request.CommentInfoCreate;
import com.illdangag.iricom.server.data.request.CommentInfoSearch;
import com.illdangag.iricom.server.data.request.CommentInfoUpdate;
import com.illdangag.iricom.server.data.response.CommentInfo;
import com.illdangag.iricom.server.data.response.CommentInfoList;

import javax.validation.Valid;

public interface CommentService {
    Comment getComment(String id);

    Comment getComment(long id);

    CommentInfo createCommentInfo(Account account, String boardId, String postId, @Valid CommentInfoCreate commentInfoCreate);

    CommentInfo createCommentInfo(Account account, Board board, Post post, @Valid CommentInfoCreate commentInfoCreate);

    CommentInfoList getComment(String boardId, String postId, @Valid CommentInfoSearch commentInfoSearch);

    CommentInfoList getComment(Board board, Post post, @Valid CommentInfoSearch commentInfoSearch);

    CommentInfo getComment(String boardId, String postId, String commentId);

    CommentInfo getComment(Board board, Post post, Comment comment);

    CommentInfo updateComment(Account account, String boardId, String postId, String commentId, @Valid CommentInfoUpdate commentInfoUpdate);

    CommentInfo updateComment(Account account, Board board, Post post, Comment comment, @Valid CommentInfoUpdate commentInfoUpdate);

    CommentInfo deleteComment(Account account, String boardId, String postId, String commentId);

    CommentInfo deleteComment(Account account, Board board, Post post, Comment comment);

    CommentInfo voteComment(Account account, String boardId, String postId, String commentId, VoteType voteType);

    CommentInfo voteComment(Account account, Board board, Post post, Comment comment, VoteType voteType);
}

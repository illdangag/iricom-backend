package com.illdangag.iricom.server.service;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.data.request.CommentInfoCreate;
import com.illdangag.iricom.server.data.request.CommentInfoSearch;
import com.illdangag.iricom.server.data.request.CommentInfoUpdate;
import com.illdangag.iricom.server.data.response.CommentInfo;
import com.illdangag.iricom.server.data.response.CommentInfoList;

import javax.validation.Valid;

public interface CommentService {
    /**
     * 댓글 생성
     */
    CommentInfo createCommentInfo(Account account, String boardId, String postId, @Valid CommentInfoCreate commentInfoCreate);

    CommentInfo createCommentInfo(Account account, Board board, Post post, @Valid CommentInfoCreate commentInfoCreate);

    /**
     * 게시물에 대한 댓글 조회
     */
    CommentInfoList getComment(String boardId, String postId, @Valid CommentInfoSearch commentInfoSearch);

    CommentInfoList getComment(Account account, String boardId, String postId, @Valid CommentInfoSearch commentInfoSearch);

    CommentInfoList getComment(Board board, Post post, @Valid CommentInfoSearch commentInfoSearch);

    CommentInfoList getComment(Account account, Board board, Post post, @Valid CommentInfoSearch commentInfoSearch);

    /**
     * 댓글 조회
     */
    CommentInfo getComment(String boardId, String postId, String commentId);

    CommentInfo getComment(Account account, String boardId, String postId, String commentId);

    CommentInfo getComment(Board board, Post post, Comment comment);

    CommentInfo getComment(Account account, Board board, Post post, Comment comment);

    /**
     * 댓글 수정
     */
    CommentInfo updateComment(Account account, String boardId, String postId, String commentId, @Valid CommentInfoUpdate commentInfoUpdate);

    CommentInfo updateComment(Account account, Board board, Post post, Comment comment, @Valid CommentInfoUpdate commentInfoUpdate);

    /**
     * 댓글 삭제
     */
    CommentInfo deleteComment(Account account, String boardId, String postId, String commentId);

    CommentInfo deleteComment(Account account, Board board, Post post, Comment comment);

    /**
     * 댓글 좋아요 싫어요
     */
    CommentInfo voteComment(Account account, String boardId, String postId, String commentId, VoteType voteType);

    CommentInfo voteComment(Account account, Board board, Post post, Comment comment, VoteType voteType);
}

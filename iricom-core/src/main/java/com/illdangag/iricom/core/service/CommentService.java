package com.illdangag.iricom.core.service;

import com.illdangag.iricom.core.data.entity.Account;
import com.illdangag.iricom.core.data.entity.Board;
import com.illdangag.iricom.core.data.entity.Comment;
import com.illdangag.iricom.core.data.entity.Post;
import com.illdangag.iricom.core.data.entity.type.VoteType;
import com.illdangag.iricom.core.data.request.CommentInfoCreate;
import com.illdangag.iricom.core.data.request.CommentInfoSearch;
import com.illdangag.iricom.core.data.request.CommentInfoUpdate;
import com.illdangag.iricom.core.data.response.CommentInfo;
import com.illdangag.iricom.core.data.response.CommentInfoList;

import javax.validation.Valid;

public interface CommentService {
    /**
     * 댓글 생성
     */
    CommentInfo createCommentInfo(String accountId, String boardId, String postId, @Valid CommentInfoCreate commentInfoCreate);

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
    CommentInfo updateComment(String accountId, String boardId, String postId, String commentId, @Valid CommentInfoUpdate commentInfoUpdate);

    CommentInfo updateComment(Account account, String boardId, String postId, String commentId, @Valid CommentInfoUpdate commentInfoUpdate);

    CommentInfo updateComment(Account account, Board board, Post post, Comment comment, @Valid CommentInfoUpdate commentInfoUpdate);

    /**
     * 댓글 삭제
     */
    CommentInfo deleteComment(String accountId, String boardId, String postId, String commentId);

    CommentInfo deleteComment(Account account, String boardId, String postId, String commentId);

    CommentInfo deleteComment(Account account, Board board, Post post, Comment comment);

    /**
     * 댓글 좋아요 싫어요
     */
    CommentInfo voteComment(String accountId, String boardId, String postId, String commentId, VoteType voteType);

    CommentInfo voteComment(Account account, String boardId, String postId, String commentId, VoteType voteType);

    CommentInfo voteComment(Account account, Board board, Post post, Comment comment, VoteType voteType);
}

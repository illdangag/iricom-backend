package com.illdangag.iricom.server.service;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.Board;
import com.illdangag.iricom.server.data.entity.Comment;
import com.illdangag.iricom.server.data.entity.Post;
import com.illdangag.iricom.server.data.request.CommentBlockInfoCreate;
import com.illdangag.iricom.server.data.request.PostBlockInfoCreate;
import com.illdangag.iricom.server.data.request.PostBlockInfoSearch;
import com.illdangag.iricom.server.data.request.PostBlockInfoUpdate;
import com.illdangag.iricom.server.data.response.CommentBlockInfo;
import com.illdangag.iricom.server.data.response.PostBlockInfo;
import com.illdangag.iricom.server.data.response.PostBlockInfoList;

import javax.validation.Valid;

public interface BlockService {
    /**
     * 게시물 차단
     */
    PostBlockInfo blockPost(String accountId, String boardId, String postId, @Valid PostBlockInfoCreate postBlockInfoCreate);

    PostBlockInfo blockPost(Account account, String boardId, String postId, @Valid PostBlockInfoCreate postBlockInfoCreate);

    PostBlockInfo blockPost(Account account, Board board, Post post, @Valid PostBlockInfoCreate postBlockInfoCreate);

    /**
     * 게시물 차단 해제
     */
    PostBlockInfo unblockPost(String accountId, String boardId, String postId);

    PostBlockInfo unblockPost(Account account, String boardId, String postId);

    PostBlockInfo unblockPost(Account account, Board board, Post post);

    /**
     * 차단된 게시물 목록 조회
     */
    PostBlockInfoList getPostBlockInfoList(Account account, @Valid PostBlockInfoSearch postBlockInfoSearch);

    PostBlockInfoList getPostBlockInfoList(String accountId, String boardId, @Valid PostBlockInfoSearch postBlockInfoSearch);

    PostBlockInfoList getPostBlockInfoList(Account account, String boardId, @Valid PostBlockInfoSearch postBlockInfoSearch);

    PostBlockInfoList getPostBlockInfoList(Account account, Board board, @Valid PostBlockInfoSearch postBlockInfoSearch);

    /**
     * 차단된 게시물 정보 조회
     */
    PostBlockInfo getPostBlockInfo(String accountId, String boardId, String postId);

    PostBlockInfo getPostBlockInfo(Account account, String boardId, String postId);

    PostBlockInfo getPostBlockInfo(Account account, Board board, Post post);

    /**
     * 차단된 게시물 정보 수정
     */
    PostBlockInfo updatePostBlockInfo(String accountId, String boardId, String postId, @Valid PostBlockInfoUpdate postBlockInfoUpdate);

    PostBlockInfo updatePostBlockInfo(Account account, String boardId, String postId, @Valid PostBlockInfoUpdate postBlockInfoUpdate);

    PostBlockInfo updatePostBlockInfo(Account account, Board board, Post post, @Valid PostBlockInfoUpdate postBlockInfoUpdate);

    /**
     * 댓글 차단
     */
    CommentBlockInfo blockComment(String accountId, String boardId, String postId, String commentId, @Valid CommentBlockInfoCreate commentBlockInfoCreate);

    CommentBlockInfo blockComment(Account account, String boardId, String postId, String commentId, @Valid CommentBlockInfoCreate commentBlockInfoCreate);

    CommentBlockInfo blockComment(Account account, Board board, Post post, Comment comment, @Valid CommentBlockInfoCreate commentBlockInfoCreate);

    /**
     * 댓글 차단 해제
     */
    CommentBlockInfo unblockComment(String accountId, String boardId, String postId, String commentId);

    CommentBlockInfo unblockComment(Account account, String boardId, String postId, String commentId);

    CommentBlockInfo unblockComment(Account account, Board board, Post post, Comment comment);
}

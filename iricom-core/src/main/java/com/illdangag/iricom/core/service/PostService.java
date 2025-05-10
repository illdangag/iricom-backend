package com.illdangag.iricom.core.service;

import com.illdangag.iricom.core.data.entity.Account;
import com.illdangag.iricom.core.data.entity.Board;
import com.illdangag.iricom.core.data.entity.Post;
import com.illdangag.iricom.core.data.entity.type.PostState;
import com.illdangag.iricom.core.data.entity.type.VoteType;
import com.illdangag.iricom.core.data.request.PostInfoCreate;
import com.illdangag.iricom.core.data.request.PostInfoSearch;
import com.illdangag.iricom.core.data.request.PostInfoUpdate;
import com.illdangag.iricom.core.data.response.PostInfo;
import com.illdangag.iricom.core.data.response.PostInfoList;

import javax.validation.Valid;

public interface PostService {
    /**
     * 게시물 생성
     */
    PostInfo createPostInfo(String accountId, String boardId, @Valid PostInfoCreate postInfoCreate);

    PostInfo createPostInfo(Account account, String boardId, @Valid PostInfoCreate postInfoCreate);

    PostInfo createPostInfo(Account account, Board board, @Valid PostInfoCreate postInfoCreate);

    /**
     * 게시물 수정
     */
    PostInfo updatePostInfo(String accountId, String boardId, String postId, @Valid PostInfoUpdate postInfoUpdate);

    PostInfo updatePostInfo(Account account, String boardId, String postId, @Valid PostInfoUpdate postInfoUpdate);

    PostInfo updatePostInfo(Account account, Board board, Post post, @Valid PostInfoUpdate postInfoUpdate);

    /**
     * 게시물 조회
     */
    PostInfo getPostInfo(Account account, String boardId, String postId, PostState postState);

    PostInfo getPostInfo(Account account, Board board, Post post, PostState postState);

    PostInfo getPostInfo(Account account, Post post, PostState postState, boolean includeContent);

    PostInfo getPostInfo(String boardId, String postId, PostState postState, boolean includeContent);

    PostInfo getPostInfo(Board board, Post post, PostState postState, boolean includeContent);

    PostInfo getPostInfo(String boardId, String postId, PostState postState);

    PostInfo getPostInfo(Board board, Post post, PostState postState);

    /**
     * 계정이 작성한 게시물 목록 조회
     */
    PostInfoList getPostInfoList(String accountId, @Valid PostInfoSearch postInfoSearch);

    PostInfoList getPostInfoList(Account account, @Valid PostInfoSearch postInfoSearch);

    /**
     * 발행한 게시물 목록 조회
     */
    PostInfoList getPublishPostInfoList(String boardId, @Valid PostInfoSearch postInfoSearch);

    PostInfoList getPublishPostInfoList(String accountId, String boardId, @Valid PostInfoSearch postInfoSearch);

    PostInfoList getPublishPostInfoList(Account account, String boardId, @Valid PostInfoSearch postInfoSearch);

    PostInfoList getPublishPostInfoList(Account account, Board board, @Valid PostInfoSearch postInfoSearch);

    /**
     * 발행한 게시물 정보 조회
     */
    PostInfo publishPostInfo(String accountId, String boardId, String postId);

    PostInfo publishPostInfo(Account account, String boardId, String postId);

    PostInfo publishPostInfo(Account account, Board board, Post post);

    /**
     * 게시물 삭제
     */
    PostInfo deletePostInfo(String accountId, String boardId, String postId);

    PostInfo deletePostInfo(Account account, String boardId, String postId);

    PostInfo deletePostInfo(Account account, Board board, Post post);

    /**
     * 게시물 좋아요 싫어요
     */
    PostInfo votePost(Account account, String boardId, String postId, VoteType voteType);

    PostInfo votePost(Account account, Board board, Post post, VoteType voteType);
}

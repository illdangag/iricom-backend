package com.illdangag.iricom.server.service;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.Board;
import com.illdangag.iricom.server.data.entity.Post;
import com.illdangag.iricom.server.data.request.PostBanInfoCreate;
import com.illdangag.iricom.server.data.request.PostBanInfoSearch;
import com.illdangag.iricom.server.data.request.PostBanInfoUpdate;
import com.illdangag.iricom.server.data.response.PostBanInfo;
import com.illdangag.iricom.server.data.response.PostBanInfoList;

import javax.validation.Valid;

public interface BanService {
    /**
     * 게시물 차단
     */
    PostBanInfo banPost(Account account, String boardId, String postId, @Valid PostBanInfoCreate postBanInfoCreate);

    PostBanInfo banPost(Account account, Board board, Post post, @Valid PostBanInfoCreate postBanInfoCreate);

    /**
     * 게시물 차단 해제
     */
    PostBanInfo unbanPost(Account account, String boardId, String postId);

    PostBanInfo unbanPost(Account account, Board board, Post post);

    /**
     * 차단된 게시물 목록 조회
     */
    PostBanInfoList getPostBanInfoList(Account account, @Valid PostBanInfoSearch postBanInfoSearch);

    PostBanInfoList getPostBanInfoList(Account account, String boardId, @Valid PostBanInfoSearch postBanInfoSearch);

    PostBanInfoList getPostBanInfoList(Account account, Board board, @Valid PostBanInfoSearch postBanInfoSearch);

    /**
     * 차단된 게시물 정보 조회
     */
    PostBanInfo getPostBanInfo(Account account, String boardId, String postId);

    PostBanInfo getPostBanInfo(Account account, Board board, Post post);

    /**
     * 차단된 게시물 정보 수정
     */
    PostBanInfo updatePostBanInfo(Account account, String boardId, String postId, @Valid PostBanInfoUpdate postBanInfoUpdate);

    PostBanInfo updatePostBanInfo(Account account, Board board, Post post, @Valid PostBanInfoUpdate postBanInfoUpdate);
}

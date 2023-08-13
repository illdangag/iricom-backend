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
    PostBanInfo banPost(Account account, String boardId, String postId, @Valid PostBanInfoCreate postBanInfoCreate);

    PostBanInfo banPost(Account account, Board board, Post post, @Valid PostBanInfoCreate postBanInfoCreate);

    PostBanInfo unbanPost(Account account, String boardId, String postId);

    PostBanInfo unbanPost(Account account, Board board, Post post);

    PostBanInfoList getPostBanInfoList(Account account, @Valid PostBanInfoSearch postBanInfoSearch);

    PostBanInfoList getPostBanInfoList(Account account, String boardId, @Valid PostBanInfoSearch postBanInfoSearch);

    PostBanInfoList getPostBanInfoList(Account account, Board board, @Valid PostBanInfoSearch postBanInfoSearch);

    PostBanInfo getPostBanInfo(Account account, String boardId, String postId);

    PostBanInfo getPostBanInfo(Account account, Board board, Post post);

    PostBanInfo updatePostBanInfo(Account account, String boardId, String postId, @Valid PostBanInfoUpdate postBanInfoUpdate);

    PostBanInfo updatePostBanInfo(Account account, Board board, Post post, @Valid PostBanInfoUpdate postBanInfoUpdate);
}

package com.illdangag.iricom.server.service;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.data.request.PostBanCreate;
import com.illdangag.iricom.server.data.request.PostInfoCreate;
import com.illdangag.iricom.server.data.request.PostInfoSearch;
import com.illdangag.iricom.server.data.request.PostInfoUpdate;
import com.illdangag.iricom.server.data.response.PostInfo;
import com.illdangag.iricom.server.data.response.PostInfoList;

import javax.validation.Valid;

public interface PostService {
    PostInfo createPostInfo(Account account, String boardId, @Valid PostInfoCreate postInfoCreate);

    PostInfo createPostInfo(Account account, Board board, @Valid PostInfoCreate postInfoCreate);

    PostInfo updatePostInfo(Account account, String boardId, String postId, @Valid PostInfoUpdate postInfoUpdate);

    PostInfo updatePostInfo(Account account, Board board, Post post, @Valid PostInfoUpdate postInfoUpdate);

    PostInfo getPostInfo(Account account, String boardId, String postId, PostState postState);

    PostInfo getPostInfo(Account account, Board board, Post post, PostState postState);

    PostInfo getPostInfo(Post post, PostState postState, boolean includeContent);

    PostInfoList getPublishPostInfoList(String boardId, @Valid PostInfoSearch postInfoSearch);

    PostInfo publishPostInfo(Account account, String boardId, String postId);

    PostInfo publishPostInfo(Account account, Board board, Post post);

    PostInfo deletePostInfo(Account account, String boardId, String postId);

    PostInfo deletePostInfo(Account account, Board board, Post post);

    PostInfo votePost(Account account, String boardId, String postId, VoteType voteType);

    PostInfo votePost(Account account, Board board, Post post, VoteType voteType);

    PostInfoList getPostInfoList(Account account, @Valid PostInfoSearch postInfoSearch);
}

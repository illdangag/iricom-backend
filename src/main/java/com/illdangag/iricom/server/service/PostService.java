package com.illdangag.iricom.server.service;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.Board;
import com.illdangag.iricom.server.data.entity.Post;
import com.illdangag.iricom.server.data.entity.PostState;
import com.illdangag.iricom.server.data.request.PostInfoCreate;
import com.illdangag.iricom.server.data.request.PostInfoSearch;
import com.illdangag.iricom.server.data.request.PostInfoUpdate;
import com.illdangag.iricom.server.data.response.PostInfo;
import com.illdangag.iricom.server.data.response.PostInfoList;

import javax.validation.Valid;

public interface PostService {
    PostInfo createPostInfo(Account account, String boardId, PostInfoCreate postInfoCreate);

    PostInfo createPostInfo(Account account, Board board, @Valid PostInfoCreate postInfoCreate);

    PostInfo updatePostInfo(Account account, String boardId, String postId, PostInfoUpdate postInfoUpdate);

    PostInfo updatePostInfo(Account account, Board board, Post post, @Valid PostInfoUpdate postInfoUpdate);

    PostInfo getPostInfo(String boardId, String postId, PostState postState);

    PostInfo getPostInfo(Board board, Post post, PostState postState);

    PostInfoList getPostInfoList(Board board, @Valid PostInfoSearch postInfoSearch);

    PostInfo publishPostInfo(Account account, String boardId, String postId);

    PostInfo publishPostInfo(Account account, Board board, Post post);

    PostInfo deletePostInfo(Account account, String boardId, String postId);

    PostInfo deletePostInfo(Account account, Board board, Post post);

    Post getPost(String id);

    Post getPost(long id);
}

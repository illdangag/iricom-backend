package com.illdangag.iricom.server.service;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.Board;
import com.illdangag.iricom.server.data.entity.Post;
import com.illdangag.iricom.server.data.request.PostBanCreate;
import com.illdangag.iricom.server.data.response.PostInfo;

import javax.validation.Valid;

public interface BanService {
    PostInfo banPost(Account account, String boardId, String postId, @Valid PostBanCreate postBanCreate);

    PostInfo banPost(Account account, Board board, Post post, @Valid PostBanCreate postBanCreate);

    PostInfo unbanPost(Account account, String boardId, String postId);

    PostInfo unbanPost(Account account, Board board, Post post);
}

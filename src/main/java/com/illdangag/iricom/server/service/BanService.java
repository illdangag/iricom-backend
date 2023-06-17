package com.illdangag.iricom.server.service;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.Board;
import com.illdangag.iricom.server.data.request.PostBanCreate;
import com.illdangag.iricom.server.data.response.PostInfo;

public interface BanService {
    PostInfo banPost(Account account, String boardId, PostBanCreate postBanCreate);

    PostInfo banPost(Account account, Board board, PostBanCreate postBanCreate);
}

package com.illdangag.iricom.server.service;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.Board;
import com.illdangag.iricom.server.data.entity.BoardAdmin;
import com.illdangag.iricom.server.data.request.BoardAdminInfoCreate;
import com.illdangag.iricom.server.data.request.BoardAdminInfoDelete;
import com.illdangag.iricom.server.data.request.BoardAdminInfoSearch;
import com.illdangag.iricom.server.data.response.BoardAdminInfoList;

import javax.validation.Valid;

public interface BoardAuthorizationService {
    void createBoardAdminAuth(BoardAdminInfoCreate boardAdminInfoCreate);

    void deleteBoardAdminAuth(BoardAdminInfoDelete boardAdminInfoDelete);

    BoardAdminInfoList getBoardAdminInfoList(@Valid BoardAdminInfoSearch boardAdminInfoSearch);

    BoardAdmin getBoardAdmin(Account account, Board board);
}

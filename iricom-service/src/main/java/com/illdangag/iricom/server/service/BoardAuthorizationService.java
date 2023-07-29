package com.illdangag.iricom.server.service;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.Board;
import com.illdangag.iricom.server.data.request.BoardAdminInfoCreate;
import com.illdangag.iricom.server.data.request.BoardAdminInfoDelete;
import com.illdangag.iricom.server.data.request.BoardAdminInfoSearch;
import com.illdangag.iricom.server.data.response.BoardAdminInfo;
import com.illdangag.iricom.server.data.response.BoardAdminInfoList;

import javax.validation.Valid;

public interface BoardAuthorizationService {
    BoardAdminInfo createBoardAdminAuth(@Valid BoardAdminInfoCreate boardAdminInfoCreate);

    BoardAdminInfo deleteBoardAdminAuth(@Valid BoardAdminInfoDelete boardAdminInfoDelete);

    BoardAdminInfoList getBoardAdminInfoList(@Valid BoardAdminInfoSearch boardAdminInfoSearch);

    BoardAdminInfo getBoardAdminInfo(String boardId);

    boolean hasAuthorization(Account account, Board board);
}

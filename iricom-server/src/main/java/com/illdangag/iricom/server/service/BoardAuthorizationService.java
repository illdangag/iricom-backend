package com.illdangag.iricom.server.service;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.Board;
import com.illdangag.iricom.server.data.request.*;
import com.illdangag.iricom.server.data.response.BoardAdminInfo;
import com.illdangag.iricom.server.data.response.BoardAdminInfoList;
import com.illdangag.iricom.server.data.response.BoardInfoList;

import javax.validation.Valid;

public interface BoardAuthorizationService {
    BoardAdminInfo createBoardAdminAuth(@Valid BoardAdminInfoCreate boardAdminInfoCreate);

    BoardAdminInfo deleteBoardAdminAuth(@Valid BoardAdminInfoDelete boardAdminInfoDelete);

    BoardAdminInfoList getBoardAdminInfoList(@Valid BoardAdminInfoSearch boardAdminInfoSearch);

    BoardAdminInfo getBoardAdminInfo(String boardId);

    /**
     * 해당 계정이 게시판 관리자로 등록된 게시판 목록 조회
     */
    BoardInfoList getBoardInfoListByBoardAdmin(Account account, @Valid BoardInfoByBoardAdminSearch boardInfoByBoardAdminSearch);

    boolean hasAuthorization(Account account, Board board);
}

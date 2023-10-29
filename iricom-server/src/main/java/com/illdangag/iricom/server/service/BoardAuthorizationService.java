package com.illdangag.iricom.server.service;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.Board;
import com.illdangag.iricom.server.data.request.BoardAdminInfoCreate;
import com.illdangag.iricom.server.data.request.BoardAdminInfoDelete;
import com.illdangag.iricom.server.data.request.BoardAdminInfoSearch;
import com.illdangag.iricom.server.data.request.BoardInfoByBoardAdminSearch;
import com.illdangag.iricom.server.data.response.BoardAdminInfo;
import com.illdangag.iricom.server.data.response.BoardAdminInfoList;
import com.illdangag.iricom.server.data.response.BoardInfoList;

import javax.validation.Valid;

public interface BoardAuthorizationService {
    BoardAdminInfo createBoardAdminAuth(@Valid BoardAdminInfoCreate boardAdminInfoCreate);

    BoardAdminInfo deleteBoardAdminAuth(@Valid BoardAdminInfoDelete boardAdminInfoDelete);

    BoardAdminInfoList getBoardAdminInfoList(Account account, @Valid BoardAdminInfoSearch boardAdminInfoSearch);

    BoardAdminInfo getBoardAdminInfo(String boardId);

    /**
     * 해당 계정이 게시판 관리자로 등록된 게시판 목록 조회
     */
    BoardInfoList getBoardInfoListByBoardAdmin(Account account, @Valid BoardInfoByBoardAdminSearch boardInfoByBoardAdminSearch);

    /**
     * 게시판 관리자 권한 여부 조회
     */
    boolean hasAuthorization(Account account, Board board);
}

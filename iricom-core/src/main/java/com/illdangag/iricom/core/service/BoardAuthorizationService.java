package com.illdangag.iricom.core.service;

import com.illdangag.iricom.core.data.entity.Account;
import com.illdangag.iricom.core.data.entity.Board;
import com.illdangag.iricom.core.data.request.BoardAdminInfoCreate;
import com.illdangag.iricom.core.data.request.BoardAdminInfoDelete;
import com.illdangag.iricom.core.data.request.BoardAdminInfoSearch;
import com.illdangag.iricom.core.data.request.BoardInfoByBoardAdminSearch;
import com.illdangag.iricom.core.data.response.BoardAdminInfo;
import com.illdangag.iricom.core.data.response.BoardAdminInfoList;
import com.illdangag.iricom.core.data.response.BoardInfoList;

import javax.validation.Valid;

public interface BoardAuthorizationService {
    BoardAdminInfo createBoardAdminAuth(@Valid BoardAdminInfoCreate boardAdminInfoCreate);

    BoardAdminInfo deleteBoardAdminAuth(@Valid BoardAdminInfoDelete boardAdminInfoDelete);

    BoardAdminInfoList getBoardAdminInfoList(String accountId, @Valid BoardAdminInfoSearch boardAdminInfoSearch);

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

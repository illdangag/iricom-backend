package com.illdangag.iricom.server.service;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.Board;
import com.illdangag.iricom.server.data.request.BoardInfoCreate;
import com.illdangag.iricom.server.data.request.BoardInfoSearch;
import com.illdangag.iricom.server.data.request.BoardInfoUpdate;
import com.illdangag.iricom.server.data.response.BoardInfo;
import com.illdangag.iricom.server.data.response.BoardInfoList;

import javax.validation.Valid;

public interface BoardService {
    BoardInfo createBoardInfo(@Valid BoardInfoCreate boardInfoCreate);

    /**
     * 공개 게시판 정보 반환
     */
    BoardInfo getBoardInfo(String id);

    /**
     * 공개 게시판을 포함하고 사용자가 조회 할 수 있는 비공개 게시판에 대하여 정보 반환
     */
    BoardInfo getBoardInfo(Account account, String id);

    /**
     * 공개 게시판 목록 반환
     */
    BoardInfoList getBoardInfoList(@Valid BoardInfoSearch boardInfoSearch);

    /**
     * 공개 게시판을 포함하고 사용자가 조회 할 수 있는 비공개 게시판을 포함하여 반환
     */
    BoardInfoList getBoardInfoList(Account account, @Valid BoardInfoSearch boardInfoSearch);

    BoardInfo updateBoardInfo(String id, @Valid BoardInfoUpdate boardInfoUpdate);

    BoardInfo updateBoardInfo(Board board, @Valid BoardInfoUpdate boardInfoUpdate);
}

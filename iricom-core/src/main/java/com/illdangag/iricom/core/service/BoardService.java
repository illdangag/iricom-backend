package com.illdangag.iricom.core.service;

import com.illdangag.iricom.core.data.entity.Account;
import com.illdangag.iricom.core.data.entity.Board;
import com.illdangag.iricom.core.data.request.BoardInfoCreate;
import com.illdangag.iricom.core.data.request.BoardInfoSearch;
import com.illdangag.iricom.core.data.request.BoardInfoUpdate;
import com.illdangag.iricom.core.data.response.BoardInfo;
import com.illdangag.iricom.core.data.response.BoardInfoList;

import javax.validation.Valid;

public interface BoardService {
    /**
     * 게시판 생성
     */
    BoardInfo createBoardInfo(String accountId, @Valid BoardInfoCreate boardInfoCreate);

    BoardInfo createBoardInfo(Account account, @Valid BoardInfoCreate boardInfoCreate);

    /**
     * 게시판 정보 반환
     */
    BoardInfo getBoardInfo(String boardId);

    BoardInfo getBoardInfo(String accountId, String boardId);

    BoardInfo getBoardInfo(Account account, String boardId);

    /**
     * 게시판 목록 반환
     */
    BoardInfoList getBoardInfoList(@Valid BoardInfoSearch boardInfoSearch);

    BoardInfoList getBoardInfoList(String accountId, @Valid BoardInfoSearch boardInfoSearch);

    BoardInfoList getBoardInfoList(Account account, @Valid BoardInfoSearch boardInfoSearch);

    /**
     * 게시판 정보 수정
     */
    BoardInfo updateBoardInfo(String accountId, String boardId, @Valid BoardInfoUpdate boardInfoUpdate);

    BoardInfo updateBoardInfo(Account account, String boardId, @Valid BoardInfoUpdate boardInfoUpdate);

    BoardInfo updateBoardInfo(Account account, Board board, @Valid BoardInfoUpdate boardInfoUpdate);
}

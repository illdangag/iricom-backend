package com.illdangag.iricom.server.service;

import com.illdangag.iricom.server.data.entity.Board;
import com.illdangag.iricom.server.data.request.BoardInfoCreate;
import com.illdangag.iricom.server.data.request.BoardInfoSearch;
import com.illdangag.iricom.server.data.request.BoardInfoUpdate;
import com.illdangag.iricom.server.data.response.BoardInfo;
import com.illdangag.iricom.server.data.response.BoardInfoList;

import javax.validation.Valid;

public interface BoardService {
    BoardInfo getBoardInfo(String id);

    BoardInfo createBoardInfo(@Valid BoardInfoCreate boardInfoCreate);

    BoardInfoList getBoardInfoList(@Valid BoardInfoSearch boardInfoSearch);

    BoardInfo updateBoardInfo(String id, BoardInfoUpdate boardInfoUpdate);

    BoardInfo updateBoardInfo(Board board, @Valid BoardInfoUpdate boardInfoUpdate);
}

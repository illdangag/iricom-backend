package com.illdangag.iricom.core.controller.v1;

import com.illdangag.iricom.core.configuration.annotation.ApiCallLog;
import com.illdangag.iricom.core.configuration.annotation.Auth;
import com.illdangag.iricom.core.configuration.annotation.AuthRole;
import com.illdangag.iricom.core.configuration.annotation.RequestContext;
import com.illdangag.iricom.core.data.entity.Account;
import com.illdangag.iricom.core.data.request.BoardInfoCreate;
import com.illdangag.iricom.core.data.request.BoardInfoSearch;
import com.illdangag.iricom.core.data.request.BoardInfoUpdate;
import com.illdangag.iricom.core.data.response.BoardInfo;
import com.illdangag.iricom.core.data.response.BoardInfoList;
import com.illdangag.iricom.core.exception.IricomErrorCode;
import com.illdangag.iricom.core.exception.IricomException;
import com.illdangag.iricom.core.service.BoardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(value = "/v1/boards")
public class BoardController {
    private final BoardService boardService;

    @Autowired
    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    /**
     * 게시판 생성
     */
    @ApiCallLog(apiCode = "BD_001")
    @Auth(role = AuthRole.SYSTEM_ADMIN)
    @RequestMapping(method = RequestMethod.POST, value = "")
    public ResponseEntity<BoardInfo> createBoard(@RequestBody @Valid BoardInfoCreate boardInfoCreate,
                                                 @RequestContext Account account) {
        BoardInfo boardInfo = this.boardService.createBoardInfo(account, boardInfoCreate);
        return ResponseEntity.status(HttpStatus.OK).body(boardInfo);
    }

    /**
     * 게시판 목록 조회
     */
    @ApiCallLog(apiCode = "BD_002")
    @Auth(role = AuthRole.NONE)
    @RequestMapping(method = RequestMethod.GET, value = "")
    public ResponseEntity<BoardInfoList> getBoardList(@RequestParam(name = "skip", defaultValue = "0", required = false) String skipVariable,
                                                      @RequestParam(name = "limit", defaultValue = "20", required = false) String limitVariable,
                                                      @RequestParam(name = "keyword", defaultValue = "", required = false) String keyword,
                                                      @RequestParam(name = "enabled", defaultValue = "", required = false) String enabledVariable,
                                                      @RequestContext Account account) {
        int skip;
        int limit;
        Boolean enabled = null;

        try {
            skip = Integer.parseInt(skipVariable);
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.INVALID_REQUEST, "Skip value is invalid");
        }

        try {
            limit = Integer.parseInt(limitVariable);
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.INVALID_REQUEST, "Limit value is invalid");
        }

        try {
            if ("true".equalsIgnoreCase(enabledVariable)) {
                enabled = Boolean.TRUE;
            } else if ("false".equalsIgnoreCase(enabledVariable)) {
                enabled = Boolean.FALSE;
            } else if (enabledVariable == null || enabledVariable.isEmpty()) {
                enabled = null;
            } else {
                throw new Exception();
            }
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.INVALID_REQUEST, "Enabled value is invalid");
        }

        BoardInfoSearch boardInfoSearch = BoardInfoSearch.builder()
                .skip(skip)
                .limit(limit)
                .keyword(keyword)
                .enabled(enabled)
                .build();

        BoardInfoList boardInfoList = null;

        if (account != null) {
            boardInfoList = this.boardService.getBoardInfoList(account, boardInfoSearch);
        } else {
            boardInfoList = this.boardService.getBoardInfoList(boardInfoSearch);
        }

        return ResponseEntity.status(HttpStatus.OK).body(boardInfoList);
    }

    /**
     * 게시판 정보 조회
     */
    @ApiCallLog(apiCode = "BD_003")
    @Auth(role = AuthRole.NONE)
    @RequestMapping(method = RequestMethod.GET, value = "/{board_id}")
    public ResponseEntity<BoardInfo> getBoard(@PathVariable(value = "board_id") String boardId,
                                              @RequestContext Account account) {
        BoardInfo boardInfo = null;

        if (account != null) {
            boardInfo = this.boardService.getBoardInfo(account, boardId);
        } else {
            boardInfo = this.boardService.getBoardInfo(boardId);
        }

        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(boardInfo);
    }


    /**
     * 게시판 정보 수정
     */
    @ApiCallLog(apiCode = "BD_004")
    @Auth(role = AuthRole.SYSTEM_ADMIN)
    @RequestMapping(method = RequestMethod.PATCH, value = "/{board_id}")
    public ResponseEntity<BoardInfo> updateBoard(@PathVariable(value = "board_id") String boardId,
                                                 @RequestBody @Valid BoardInfoUpdate boardInfoUpdate,
                                                 @RequestContext Account account) {
        BoardInfo boardInfo = this.boardService.updateBoardInfo(account, boardId, boardInfoUpdate);
        return ResponseEntity.status(HttpStatus.OK).body(boardInfo);
    }
}

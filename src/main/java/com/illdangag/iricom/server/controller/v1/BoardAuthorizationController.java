package com.illdangag.iricom.server.controller.v1;

import com.illdangag.iricom.server.configuration.annotation.ApiCallLog;
import com.illdangag.iricom.server.configuration.annotation.Auth;
import com.illdangag.iricom.server.configuration.annotation.AuthRole;
import com.illdangag.iricom.server.data.request.BoardAdminInfoCreate;
import com.illdangag.iricom.server.data.request.BoardAdminInfoDelete;
import com.illdangag.iricom.server.data.request.BoardAdminInfoSearch;
import com.illdangag.iricom.server.data.response.BoardAdminInfo;
import com.illdangag.iricom.server.data.response.BoardAdminInfoList;
import com.illdangag.iricom.server.exception.IricomErrorCode;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.service.BoardAuthorizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/v1/auth")
public class BoardAuthorizationController {
    private final BoardAuthorizationService boardAuthorizationService;

    @Autowired
    public BoardAuthorizationController(BoardAuthorizationService boardAuthorizationService) {
        this.boardAuthorizationService = boardAuthorizationService;
    }

    /**
     * 게시판 관리자 추가
     */
    @ApiCallLog(apiCode = "AT_001")
    @Auth(role = AuthRole.SYSTEM_ADMIN)
    @RequestMapping(method = RequestMethod.POST, value = "/board")
    public ResponseEntity<Void> setBoardAdminAuth(@RequestBody @Validated BoardAdminInfoCreate boardAdminInfoCreate) {
        this.boardAuthorizationService.createBoardAdminAuth(boardAdminInfoCreate);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 게시판 관리자 목록 조회
     * 게시판 제목의 오름차순 정렬
     */
    @ApiCallLog(apiCode = "AT_002")
    @Auth(role = AuthRole.SYSTEM_ADMIN)
    @RequestMapping(method = RequestMethod.GET, value = "/board")
    public ResponseEntity<BoardAdminInfoList> getBoardAdminAuthList(@RequestParam(name = "skip", defaultValue = "0", required = false) String skipVariable,
                                                                    @RequestParam(name = "limit", defaultValue = "20", required = false) String limitVariable,
                                                                    @RequestParam(name = "keyword", defaultValue = "", required = false) String keyword,
                                                                    @RequestParam(name = "enabled", defaultValue = "", required = false) String enabledVariable) {
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

        BoardAdminInfoSearch boardAdminInfoSearch = BoardAdminInfoSearch.builder()
                .skip(skip)
                .limit(limit)
                .keyword(keyword)
                .enabled(enabled)
                .build();

        BoardAdminInfoList boardAdminInfoList = this.boardAuthorizationService.getBoardAdminInfoList(boardAdminInfoSearch);
        return ResponseEntity.status(HttpStatus.OK).body(boardAdminInfoList);
    }

    /**
     * 게시판 관리자 조회
     */
    @ApiCallLog(apiCode = "AT_003")
    @Auth(role = AuthRole.SYSTEM_ADMIN)
    @RequestMapping(method = RequestMethod.GET, value = "/board/{board_id}")
    public ResponseEntity<BoardAdminInfo> getBoardAdmin(@PathVariable(name = "board_id") String boardId) {
        BoardAdminInfo boardAdminInfo = this.boardAuthorizationService.getBoardAdminInfo(boardId);
        return ResponseEntity.status(HttpStatus.OK).body(boardAdminInfo);
    }

    /**
     * 게시판 관리자 삭제
     */
    @ApiCallLog(apiCode = "AT_004")
    @Auth(role = AuthRole.SYSTEM_ADMIN)
    @RequestMapping(method = RequestMethod.DELETE, value = "/board")
    public ResponseEntity<Void> deleteBoardAdminAuth(@RequestBody @Validated BoardAdminInfoDelete boardAdminInfoDelete) {
        this.boardAuthorizationService.deleteBoardAdminAuth(boardAdminInfoDelete);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}

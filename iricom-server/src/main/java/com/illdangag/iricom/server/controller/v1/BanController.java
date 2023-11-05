package com.illdangag.iricom.server.controller.v1;

import com.illdangag.iricom.server.configuration.annotation.ApiCallLog;
import com.illdangag.iricom.server.configuration.annotation.Auth;
import com.illdangag.iricom.server.configuration.annotation.AuthRole;
import com.illdangag.iricom.server.configuration.annotation.RequestContext;
import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.request.CommentBanInfoCreate;
import com.illdangag.iricom.server.data.request.PostBanInfoCreate;
import com.illdangag.iricom.server.data.request.PostBanInfoSearch;
import com.illdangag.iricom.server.data.request.PostBanInfoUpdate;
import com.illdangag.iricom.server.data.response.CommentBanInfo;
import com.illdangag.iricom.server.data.response.PostBanInfo;
import com.illdangag.iricom.server.data.response.PostBanInfoList;
import com.illdangag.iricom.server.exception.IricomErrorCode;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.service.BanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(value = "/v1")
public class BanController {
    private final BanService banService;

    @Autowired
    public BanController(BanService banService) {
        this.banService = banService;
    }

    @ApiCallLog(apiCode = "BP_001")
    @Auth(role = AuthRole.BOARD_ADMIN)
    @RequestMapping(method = RequestMethod.POST, value = "/ban/post/boards/{board_id}/posts/{post_id}")
    public ResponseEntity<PostBanInfo> banPost(@PathVariable(value = "board_id") String boardId,
                                               @PathVariable(value = "post_id") String postId,
                                               @RequestBody @Valid PostBanInfoCreate postBanInfoCreate,
                                               @RequestContext Account account) {
        PostBanInfo postBanInfo = this.banService.banPost(account, boardId, postId, postBanInfoCreate);
        return ResponseEntity.status(HttpStatus.OK).body(postBanInfo);
    }

    @ApiCallLog(apiCode = "BP_002")
    @Auth(role = AuthRole.SYSTEM_ADMIN)
    @RequestMapping(method = RequestMethod.GET, value = "/ban/post/boards")
    public ResponseEntity<PostBanInfoList> getBanPostInfoList(@RequestParam(name = "skip", defaultValue = "0", required = false) String skipVariable,
                                                          @RequestParam(name = "limit", defaultValue = "20", required = false) String limitVariable,
                                                          @RequestParam(name = "reason", defaultValue = "", required = false) String reason,
                                                          @RequestContext Account account) {
        int skip;
        int limit;

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

        PostBanInfoSearch postBanInfoSearch = PostBanInfoSearch.builder()
                .skip(skip).limit(limit).reason(reason)
                .build();
        PostBanInfoList postBanInfoList = this.banService.getPostBanInfoList(account, postBanInfoSearch);
        return ResponseEntity.status(HttpStatus.OK).body(postBanInfoList);
    }

    @ApiCallLog(apiCode = "BP_003")
    @Auth(role = AuthRole.BOARD_ADMIN)
    @RequestMapping(method = RequestMethod.GET, value = "/ban/post/boards/{board_id}")
    public ResponseEntity<PostBanInfoList> getBanPostInfoListByBoardId(@PathVariable("board_id") String boardId,
                                                                   @RequestParam(name = "skip", defaultValue = "0", required = false) String skipVariable,
                                                                   @RequestParam(name = "limit", defaultValue = "20", required = false) String limitVariable,
                                                                   @RequestParam(name = "reason", defaultValue = "", required = false) String reason,
                                                                   @RequestContext Account account) {
        int skip;
        int limit;

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

        PostBanInfoSearch postBanInfoSearch = PostBanInfoSearch.builder()
                .skip(skip).limit(limit).reason(reason)
                .build();
        PostBanInfoList postBanInfoList = this.banService.getPostBanInfoList(account, boardId, postBanInfoSearch);
        return ResponseEntity.status(HttpStatus.OK).body(postBanInfoList);
    }

    @ApiCallLog(apiCode = "BP_004")
    @Auth(role = AuthRole.BOARD_ADMIN)
    @RequestMapping(method = RequestMethod.GET, value = "/ban/post/boards/{board_id}/posts/{post_id}")
    public ResponseEntity<PostBanInfo> getBanPostInfo(@PathVariable("board_id") String boardId,
                                                          @PathVariable("post_id") String postId,
                                                          @RequestContext Account account) {
        PostBanInfo postBanInfo = this.banService.getPostBanInfo(account, boardId, postId);
        return ResponseEntity.status(HttpStatus.OK).body(postBanInfo);
    }

    @ApiCallLog(apiCode = "BP_005")
    @Auth(role = AuthRole.BOARD_ADMIN)
    @RequestMapping(method = RequestMethod.PATCH, value = "/ban/post/boards/{board_id}/posts/{post_id}")
    public ResponseEntity<PostBanInfo> updateBanPostInfo(@PathVariable(value = "board_id") String boardId,
                                                         @PathVariable(value = "post_id") String postId,
                                                         @RequestBody @Valid PostBanInfoUpdate postBanInfoUpdate,
                                                         @RequestContext Account account) {
        PostBanInfo postBanInfo = this.banService.updatePostBanInfo(account, boardId, postId, postBanInfoUpdate);
        return ResponseEntity.status(HttpStatus.OK).body(postBanInfo);
    }

    @ApiCallLog(apiCode = "BP_006")
    @Auth(role = AuthRole.BOARD_ADMIN)
    @RequestMapping(method = RequestMethod.DELETE, value = "/ban/post/boards/{board_id}/posts/{post_id}")
    public ResponseEntity<PostBanInfo> unbanPost(@PathVariable(value = "board_id") String boardId,
                                                 @PathVariable(value = "post_id") String postId,
                                                 @RequestContext Account account) {
        PostBanInfo postBanInfo = this.banService.unbanPost(account, boardId, postId);
        return ResponseEntity.status(HttpStatus.OK).body(postBanInfo);
    }

    @ApiCallLog(apiCode =  "BC_001")
    @Auth(role = AuthRole.BOARD_ADMIN)
    @RequestMapping(method = RequestMethod.POST, value = "/ban/comment/boards/{boardId}/posts/{postId}/comments/{commentId}")
    public ResponseEntity<CommentBanInfo> unbanComment(@PathVariable(value = "boardId") String boardId,
                                                       @PathVariable(value = "postId") String postId,
                                                       @PathVariable(value = "commentId") String commentId,
                                                       @RequestBody @Valid CommentBanInfoCreate commentBanInfoCreate,
                                                       @RequestContext Account account) {
        CommentBanInfo commentBanInfo = this.banService.banComment(account, boardId, postId, commentId, commentBanInfoCreate);
        return ResponseEntity.status(HttpStatus.OK).body(commentBanInfo);
    }
}

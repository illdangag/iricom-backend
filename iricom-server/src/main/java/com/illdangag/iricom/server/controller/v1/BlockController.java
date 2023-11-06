package com.illdangag.iricom.server.controller.v1;

import com.illdangag.iricom.server.configuration.annotation.ApiCallLog;
import com.illdangag.iricom.server.configuration.annotation.Auth;
import com.illdangag.iricom.server.configuration.annotation.AuthRole;
import com.illdangag.iricom.server.configuration.annotation.RequestContext;
import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.request.CommentBlockInfoCreate;
import com.illdangag.iricom.server.data.request.PostBlockInfoCreate;
import com.illdangag.iricom.server.data.request.PostBlockInfoSearch;
import com.illdangag.iricom.server.data.request.PostBlockInfoUpdate;
import com.illdangag.iricom.server.data.response.CommentBlockInfo;
import com.illdangag.iricom.server.data.response.PostBlockInfo;
import com.illdangag.iricom.server.data.response.PostBlockInfoList;
import com.illdangag.iricom.server.exception.IricomErrorCode;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.service.BlockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(value = "/v1")
public class BlockController {
    private final BlockService blockService;

    @Autowired
    public BlockController(BlockService blockService) {
        this.blockService = blockService;
    }

    /**
     * 게시물 차단
     */
    @ApiCallLog(apiCode = "BP_001")
    @Auth(role = AuthRole.BOARD_ADMIN)
    @RequestMapping(method = RequestMethod.POST, value = "/block/post/boards/{board_id}/posts/{post_id}")
    public ResponseEntity<PostBlockInfo> blockPost(@PathVariable(value = "board_id") String boardId,
                                                   @PathVariable(value = "post_id") String postId,
                                                   @RequestBody @Valid PostBlockInfoCreate postBlockInfoCreate,
                                                   @RequestContext Account account) {
        PostBlockInfo postBlockInfo = this.blockService.blockPost(account, boardId, postId, postBlockInfoCreate);
        return ResponseEntity.status(HttpStatus.OK).body(postBlockInfo);
    }

    /**
     * 차단된 게시물 조회
     */
    @ApiCallLog(apiCode = "BP_002")
    @Auth(role = AuthRole.SYSTEM_ADMIN)
    @RequestMapping(method = RequestMethod.GET, value = "/block/post/boards")
    public ResponseEntity<PostBlockInfoList> getBlockPostInfoList(@RequestParam(name = "skip", defaultValue = "0", required = false) String skipVariable,
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

        PostBlockInfoSearch postBlockInfoSearch = PostBlockInfoSearch.builder()
                .skip(skip).limit(limit).reason(reason)
                .build();
        PostBlockInfoList postBlockInfoList = this.blockService.getPostBlockInfoList(account, postBlockInfoSearch);
        return ResponseEntity.status(HttpStatus.OK).body(postBlockInfoList);
    }

    /**
     * 게시판 기준으로 차단된 게시물 조회
     */
    @ApiCallLog(apiCode = "BP_003")
    @Auth(role = AuthRole.BOARD_ADMIN)
    @RequestMapping(method = RequestMethod.GET, value = "/block/post/boards/{board_id}")
    public ResponseEntity<PostBlockInfoList> getBlockPostInfoListByBoardId(@PathVariable("board_id") String boardId,
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

        PostBlockInfoSearch postBlockInfoSearch = PostBlockInfoSearch.builder()
                .skip(skip).limit(limit).reason(reason)
                .build();
        PostBlockInfoList postBlockInfoList = this.blockService.getPostBlockInfoList(account, boardId, postBlockInfoSearch);
        return ResponseEntity.status(HttpStatus.OK).body(postBlockInfoList);
    }

    /**
     * 차단된 게시물 정보 조회
     */
    @ApiCallLog(apiCode = "BP_004")
    @Auth(role = AuthRole.BOARD_ADMIN)
    @RequestMapping(method = RequestMethod.GET, value = "/block/post/boards/{board_id}/posts/{post_id}")
    public ResponseEntity<PostBlockInfo> getBlockPostInfo(@PathVariable("board_id") String boardId,
                                                          @PathVariable("post_id") String postId,
                                                          @RequestContext Account account) {
        PostBlockInfo postBlockInfo = this.blockService.getPostBlockInfo(account, boardId, postId);
        return ResponseEntity.status(HttpStatus.OK).body(postBlockInfo);
    }

    /**
     * 차단된 게시물의 차단 정보 수정
     */
    @ApiCallLog(apiCode = "BP_005")
    @Auth(role = AuthRole.BOARD_ADMIN)
    @RequestMapping(method = RequestMethod.PATCH, value = "/block/post/boards/{board_id}/posts/{post_id}")
    public ResponseEntity<PostBlockInfo> updateBlockPostInfo(@PathVariable(value = "board_id") String boardId,
                                                             @PathVariable(value = "post_id") String postId,
                                                             @RequestBody @Valid PostBlockInfoUpdate postBlockInfoUpdate,
                                                             @RequestContext Account account) {
        PostBlockInfo postBlockInfo = this.blockService.updatePostBlockInfo(account, boardId, postId, postBlockInfoUpdate);
        return ResponseEntity.status(HttpStatus.OK).body(postBlockInfo);
    }

    /**
     * 차단된 게시물의 차단 해제
     */
    @ApiCallLog(apiCode = "BP_006")
    @Auth(role = AuthRole.BOARD_ADMIN)
    @RequestMapping(method = RequestMethod.DELETE, value = "/block/post/boards/{board_id}/posts/{post_id}")
    public ResponseEntity<PostBlockInfo> unblockPost(@PathVariable(value = "board_id") String boardId,
                                                     @PathVariable(value = "post_id") String postId,
                                                     @RequestContext Account account) {
        PostBlockInfo postBlockInfo = this.blockService.unblockPost(account, boardId, postId);
        return ResponseEntity.status(HttpStatus.OK).body(postBlockInfo);
    }

    /**
     * 댓글 차단
     */
    @ApiCallLog(apiCode =  "BC_001")
    @Auth(role = AuthRole.BOARD_ADMIN)
    @RequestMapping(method = RequestMethod.POST, value = "/block/comment/boards/{boardId}/posts/{postId}/comments/{commentId}")
    public ResponseEntity<CommentBlockInfo> unblockComment(@PathVariable(value = "boardId") String boardId,
                                                           @PathVariable(value = "postId") String postId,
                                                           @PathVariable(value = "commentId") String commentId,
                                                           @RequestBody @Valid CommentBlockInfoCreate commentBlockInfoCreate,
                                                           @RequestContext Account account) {
        CommentBlockInfo commentBlockInfo = this.blockService.blockComment(account, boardId, postId, commentId, commentBlockInfoCreate);
        return ResponseEntity.status(HttpStatus.OK).body(commentBlockInfo);
    }
}

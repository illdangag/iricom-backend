package com.illdangag.iricom.server.controller.v1;

import com.illdangag.iricom.server.configuration.annotation.ApiCallLog;
import com.illdangag.iricom.server.configuration.annotation.Auth;
import com.illdangag.iricom.server.configuration.annotation.AuthRole;
import com.illdangag.iricom.server.configuration.annotation.RequestContext;
import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.VoteType;
import com.illdangag.iricom.server.data.request.CommentInfoCreate;
import com.illdangag.iricom.server.data.request.CommentInfoSearch;
import com.illdangag.iricom.server.data.request.CommentInfoUpdate;
import com.illdangag.iricom.server.data.request.CommentInfoVote;
import com.illdangag.iricom.server.data.response.CommentInfo;
import com.illdangag.iricom.server.data.response.CommentInfoList;
import com.illdangag.iricom.server.exception.IricomErrorCode;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(value = "/v1/boards/{board_id}/posts/{post_id}")
public class CommentController {
    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * 댓글 생성
     */
    @ApiCallLog(apiCode = "CM_001")
    @Auth(role = AuthRole.ACCOUNT)
    @RequestMapping(method = RequestMethod.POST, value = "/comments")
    public ResponseEntity<CommentInfo> createComment(@PathVariable(value = "board_id") String boardId,
                                                     @PathVariable(value = "post_id") String postId,
                                                     @RequestBody @Valid CommentInfoCreate commentInfoCreate,
                                                     @RequestContext Account account) {
        CommentInfo commentInfo = this.commentService.createCommentInfo(account, boardId, postId, commentInfoCreate);
        return ResponseEntity.status(HttpStatus.OK).body(commentInfo);
    }

    /**
     * 댓글 목록 조회
     */
    @ApiCallLog(apiCode = "CM_002")
    @Auth(role = AuthRole.NONE)
    @RequestMapping(method = RequestMethod.GET, value = "/comments")
    public ResponseEntity<CommentInfoList> getCommentList(@PathVariable(value = "board_id") String boardId,
                                                          @PathVariable(value = "post_id") String postId,
                                                          @RequestParam(name = "skip", defaultValue = "0", required = false) String skipVariable,
                                                          @RequestParam(name = "limit", defaultValue = "20", required = false) String limitVariable,
                                                          @RequestParam(name = "includeComment", defaultValue = "false", required = false) String includeCommentVariable,
                                                          @RequestParam(name = "referenceCommentId", defaultValue = "", required = false) String referenceCommentId,
                                                          @RequestParam(name = "includeCommentLimit", defaultValue = "5", required = false) String includeCommentLimitVariable,
                                                          @RequestContext Account account) {
        int skip;
        int limit;
        boolean includeComment;
        int includeCommentLimit;

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
            includeComment = Boolean.parseBoolean(includeCommentVariable);
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.INVALID_REQUEST, "IncludeComment value is invalid");
        }

        try {
            includeCommentLimit = Integer.parseInt(includeCommentLimitVariable);
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.INVALID_REQUEST, "IncludeCommentLimit value is invalid");
        }

        CommentInfoSearch commentInfoSearch = CommentInfoSearch.builder()
                .skip(skip)
                .limit(limit)
                .includeComment(includeComment)
                .referenceCommentId(referenceCommentId)
                .includeCommentLimit(includeCommentLimit)
                .build();

        CommentInfoList commentInfoList;

        if (account != null) {
            commentInfoList = this.commentService.getComment(account, boardId, postId, commentInfoSearch);
        } else {
            commentInfoList = this.commentService.getComment(boardId, postId, commentInfoSearch);
        }

        return ResponseEntity.status(HttpStatus.OK).body(commentInfoList);
    }

    /**
     * 댓글 정보 조회
     */
    @ApiCallLog(apiCode = "CM_003")
    @Auth(role = AuthRole.NONE)
    @RequestMapping(method = RequestMethod.GET, value = "/comments/{comment_id}")
    public ResponseEntity<CommentInfo> getCommentInfo(@PathVariable(value = "board_id") String boardId,
                                                      @PathVariable(value = "post_id") String postId,
                                                      @PathVariable(value = "comment_id") String commentId,
                                                      @RequestContext Account account) {
        CommentInfo commentInfo;

        if (account != null) {
            commentInfo = this.commentService.getComment(account, boardId, postId, commentId);
        } else {
            commentInfo = this.commentService.getComment(boardId, postId, commentId);
        }

        return ResponseEntity.status(HttpStatus.OK).body(commentInfo);
    }

    /**
     * 댓글 수정
     */
    @ApiCallLog(apiCode = "CM_004")
    @Auth(role = AuthRole.ACCOUNT)
    @RequestMapping(method = RequestMethod.PATCH, value = "/comments/{comment_id}")
    public ResponseEntity<CommentInfo> updateComment(@PathVariable(value = "board_id") String boardId,
                                                     @PathVariable(value = "post_id") String postId,
                                                     @PathVariable(value = "comment_id") String commentId,
                                                     @RequestBody @Valid CommentInfoUpdate commentInfoUpdate,
                                                     @RequestContext Account account) {
        CommentInfo commentInfo = this.commentService.updateComment(account, boardId, postId, commentId, commentInfoUpdate);
        return ResponseEntity.status(HttpStatus.OK).body(commentInfo);
    }

    /**
     * 댓글 삭제
     */
    @ApiCallLog(apiCode = "CM_005")
    @Auth(role = AuthRole.ACCOUNT)
    @RequestMapping(method = RequestMethod.DELETE, value = "/comments/{comment_id}")
    public ResponseEntity<CommentInfo> deleteComment(@PathVariable(value = "board_id") String boardId,
                                                     @PathVariable(value = "post_id") String postId,
                                                     @PathVariable(value = "comment_id") String commentId,
                                                     @RequestContext Account account) {
        CommentInfo commentInfo = this.commentService.deleteComment(account, boardId, postId, commentId);
        return ResponseEntity.status(HttpStatus.OK).body(commentInfo);
    }

    /**
     * 댓글 좋아요, 싫어요
     */
    @ApiCallLog(apiCode = "CM_006")
    @Auth(role = AuthRole.ACCOUNT)
    @RequestMapping(method = RequestMethod.PATCH, value = "/comments/{comment_id}/vote")
    public ResponseEntity<CommentInfo> voteComment(@PathVariable(value = "board_id") String boardId,
                                                   @PathVariable(value = "post_id") String postId,
                                                   @PathVariable(value = "comment_id") String commentId,
                                                   @RequestBody CommentInfoVote commentInfoVote,
                                                   @RequestContext Account account) {
        VoteType voteType = commentInfoVote.getType();
        CommentInfo commentInfo = this.commentService.voteComment(account, boardId, postId, commentId, voteType);
        return ResponseEntity.status(HttpStatus.OK).body(commentInfo);
    }
}

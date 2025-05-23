package com.illdangag.iricom.core.controller.v1;

import com.illdangag.iricom.core.configuration.annotation.ApiCallLog;
import com.illdangag.iricom.core.configuration.annotation.Auth;
import com.illdangag.iricom.core.configuration.annotation.AuthRole;
import com.illdangag.iricom.core.configuration.annotation.RequestContext;
import com.illdangag.iricom.core.data.entity.Account;
import com.illdangag.iricom.core.data.entity.type.ReportType;
import com.illdangag.iricom.core.data.request.CommentReportInfoCreate;
import com.illdangag.iricom.core.data.request.CommentReportInfoSearch;
import com.illdangag.iricom.core.data.request.PostReportInfoCreate;
import com.illdangag.iricom.core.data.request.PostReportInfoSearch;
import com.illdangag.iricom.core.data.response.CommentReportInfo;
import com.illdangag.iricom.core.data.response.CommentReportInfoList;
import com.illdangag.iricom.core.data.response.PostReportInfo;
import com.illdangag.iricom.core.data.response.PostReportInfoList;
import com.illdangag.iricom.core.exception.IricomErrorCode;
import com.illdangag.iricom.core.exception.IricomException;
import com.illdangag.iricom.core.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping()
public class ReportController {
    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * 게시물 신고
     */
    @ApiCallLog(apiCode = "RP_001")
    @Auth(role = AuthRole.ACCOUNT)
    @RequestMapping(method = RequestMethod.POST, value = "/v1/report/post/boards/{board_id}/posts/{post_id}")
    public ResponseEntity<PostReportInfo> reportPost(@PathVariable(value = "board_id") String boardId,
                                                     @PathVariable(value = "post_id") String postId,
                                                     @RequestBody @Valid PostReportInfoCreate postReportInfoCreate,
                                                     @RequestContext Account account) {
        PostReportInfo postReportInfo = this.reportService.reportPost(account, boardId, postId, postReportInfoCreate);
        return ResponseEntity.status(HttpStatus.OK).body(postReportInfo);
    }

    /**
     * 게시물 신고 목록 조회 (게시판)
     */
    @ApiCallLog(apiCode = "RP_002")
    @Auth(role = AuthRole.BOARD_ADMIN)
    @RequestMapping(method = RequestMethod.GET, value = "/v1/report/post/boards/{board_id}")
    public ResponseEntity<PostReportInfoList> getBoardReportInfoListByBoard(@PathVariable(value = "board_id") String boardId,
                                                                            @RequestParam(name = "skip", defaultValue = "0", required = false) String skipVariable,
                                                                            @RequestParam(name = "limit", defaultValue = "20", required = false) String limitVariable,
                                                                            @RequestParam(name = "type", defaultValue = "", required = false) String typeVariable,
                                                                            @RequestParam(name = "reason", defaultValue = "", required = false) String reason,
                                                                            @RequestContext Account account) {
        ReportType type = null;
        int skip;
        int limit;

        try {
            skip = Integer.parseInt(skipVariable);
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.INVALID_REQUEST, "Skip value is invalid.");
        }

        try {
            limit = Integer.parseInt(limitVariable);
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.INVALID_REQUEST, "Limit value is invalid.");
        }

        if (!typeVariable.isEmpty()) {
            try {
                type = ReportType.setValue(typeVariable);
            } catch (Exception exception) {
                throw new IricomException(IricomErrorCode.INVALID_REQUEST, "Type value is invalid.");
            }
        }

        PostReportInfoSearch search = PostReportInfoSearch.builder()
                .type(type)
                .reason(reason)
                .skip(skip)
                .limit(limit)
                .build();

        PostReportInfoList postReportInfoList = this.reportService.getPostReportInfoList(account, boardId, search);
        return ResponseEntity.status(HttpStatus.OK).body(postReportInfoList);
    }

    /**
     * 게시물 신고 목록 조회 (게시판, 게시물)
     */
    @ApiCallLog(apiCode = "RP_003")
    @Auth(role = AuthRole.BOARD_ADMIN)
    @RequestMapping(method = RequestMethod.GET, value = "/v1/report/post/boards/{board_id}/posts/{post_id}")
    public ResponseEntity<PostReportInfoList> getBoardReportInfoListByPost(@PathVariable(value = "board_id") String boardId,
                                                                           @PathVariable(value = "post_id") String postId,
                                                                           @RequestParam(name = "skip", defaultValue = "0", required = false) String skipVariable,
                                                                           @RequestParam(name = "limit", defaultValue = "20", required = false) String limitVariable,
                                                                           @RequestParam(name = "type", defaultValue = "", required = false) String typeVariable,
                                                                           @RequestParam(name = "reason", defaultValue = "", required = false) String reason,
                                                                           @RequestContext Account account) {
        ReportType type = null;
        int skip;
        int limit;

        try {
            skip = Integer.parseInt(skipVariable);
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.INVALID_REQUEST, "Skip value is invalid.");
        }

        try {
            limit = Integer.parseInt(limitVariable);
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.INVALID_REQUEST, "Limit value is invalid.");
        }

        if (!typeVariable.isEmpty()) {
            try {
                type = ReportType.setValue(typeVariable);
            } catch (Exception exception) {
                throw new IricomException(IricomErrorCode.INVALID_REQUEST, "Type value is invalid.");
            }
        }

        PostReportInfoSearch search = PostReportInfoSearch.builder()
                .type(type)
                .reason(reason)
                .skip(skip)
                .limit(limit)
                .build();

        PostReportInfoList postReportInfoList = this.reportService.getPostReportInfoList(account, boardId, postId, search);
        return ResponseEntity.status(HttpStatus.OK).body(postReportInfoList);
    }

    /**
     * 게시물 신고 정보 조회
     */
    @ApiCallLog(apiCode = "RP_004")
    @Auth(role = AuthRole.BOARD_ADMIN)
    @RequestMapping(method = RequestMethod.GET, value = "/v1/report/post/boards/{board_id}/posts/{post_id}/reports/{report_id}")
    public ResponseEntity<PostReportInfo> getPostReportInfo(@PathVariable(value = "board_id") String boardId,
                                                            @PathVariable(value = "post_id") String postId,
                                                            @PathVariable(value = "report_id") String reportId,
                                                            @RequestContext Account account) {
        PostReportInfo postReportInfo = this.reportService.getPostReportInfo(account, boardId, postId, reportId);
        return ResponseEntity.status(HttpStatus.OK).body(postReportInfo);
    }

    /**
     * 댓글 신고
     */
    @ApiCallLog(apiCode = "RC_001")
    @Auth(role = AuthRole.ACCOUNT)
    @RequestMapping(method = RequestMethod.POST, value = "/v1/report/comment/boards/{board_id}/posts/{post_id}/comments/{comment_id}")
    public ResponseEntity<CommentReportInfo> reportComment(@PathVariable(value = "board_id") String boardId,
                                                           @PathVariable(value = "post_id") String postId,
                                                           @PathVariable(value = "comment_id") String commentId,
                                                           @RequestBody @Valid CommentReportInfoCreate commentReportInfoCreate,
                                                           @RequestContext Account account) {
        CommentReportInfo commentReportInfo = this.reportService.reportComment(account, boardId, postId, commentId, commentReportInfoCreate);
        return ResponseEntity.status(HttpStatus.OK).body(commentReportInfo);
    }

    /**
     * 댓글 신고 목록 조회 (게시판)
     */
    @ApiCallLog(apiCode = "RC_002")
    @Auth(role = AuthRole.BOARD_ADMIN)
    @RequestMapping(method = RequestMethod.GET, value = "/v1/report/comment/boards/{board_id}")
    public ResponseEntity<CommentReportInfoList> getCommentReportInfoListByBoard(@PathVariable(value = "board_id") String boardId,
                                                                                 @RequestParam(name = "skip", defaultValue = "0", required = false) String skipVariable,
                                                                                 @RequestParam(name = "limit", defaultValue = "20", required = false) String limitVariable,
                                                                                 @RequestParam(name = "type", defaultValue = "", required = false) String typeVariable,
                                                                                 @RequestParam(name = "reason", defaultValue = "", required = false) String reason,
                                                                                 @RequestContext Account account) {
        ReportType type = null;
        int skip;
        int limit;

        try {
            skip = Integer.parseInt(skipVariable);
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.INVALID_REQUEST, "Skip value is invalid.");
        }

        try {
            limit = Integer.parseInt(limitVariable);
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.INVALID_REQUEST, "Limit value is invalid.");
        }

        if (!typeVariable.isEmpty()) {
            try {
                type = ReportType.setValue(typeVariable);
            } catch (Exception exception) {
                throw new IricomException(IricomErrorCode.INVALID_REQUEST, "Type value is invalid.");
            }
        }

        CommentReportInfoSearch search = CommentReportInfoSearch.builder()
                .type(type)
                .reason(reason)
                .skip(skip)
                .limit(limit)
                .build();

        CommentReportInfoList commentReportInfoList = this.reportService.getCommentReportInfoList(account, boardId, search);
        return ResponseEntity.status(HttpStatus.OK).body(commentReportInfoList);
    }

    /**
     * 댓글 신고 목록 조회 (게시판, 게시물)
     */
    @ApiCallLog(apiCode = "RC_003")
    @Auth(role = AuthRole.BOARD_ADMIN)
    @RequestMapping(method = RequestMethod.GET, value = "/v1/report/comment/boards/{board_id}/posts/{post_id}")
    public ResponseEntity<CommentReportInfoList> getCommentReportInfoListByPost(@PathVariable(value = "board_id") String boardId,
                                                                                @PathVariable(value = "post_id") String postId,
                                                                                @RequestParam(name = "skip", defaultValue = "0", required = false) String skipVariable,
                                                                                @RequestParam(name = "limit", defaultValue = "20", required = false) String limitVariable,
                                                                                @RequestParam(name = "type", defaultValue = "", required = false) String typeVariable,
                                                                                @RequestParam(name = "reason", defaultValue = "", required = false) String reason,
                                                                                @RequestContext Account account) {
        ReportType type = null;
        int skip;
        int limit;

        try {
            skip = Integer.parseInt(skipVariable);
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.INVALID_REQUEST, "Skip value is invalid.");
        }

        try {
            limit = Integer.parseInt(limitVariable);
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.INVALID_REQUEST, "Limit value is invalid.");
        }

        if (!typeVariable.isEmpty()) {
            try {
                type = ReportType.setValue(typeVariable);
            } catch (Exception exception) {
                throw new IricomException(IricomErrorCode.INVALID_REQUEST, "Type value is invalid.");
            }
        }

        CommentReportInfoSearch search = CommentReportInfoSearch.builder()
                .type(type)
                .reason(reason)
                .skip(skip)
                .limit(limit)
                .build();

        CommentReportInfoList commentReportInfoList = this.reportService.getCommentReportInfoList(account, boardId, postId, search);
        return ResponseEntity.status(HttpStatus.OK).body(commentReportInfoList);
    }

    /**
     * 댓글 신고 목록 조회 (게시판, 게시물, 댓글)
     */
    @ApiCallLog(apiCode = "RC_004")
    @Auth(role = AuthRole.BOARD_ADMIN)
    @RequestMapping(method = RequestMethod.GET, value = "/v1/report/comment/boards/{board_id}/posts/{post_id}/comments/{comment_id}")
    public ResponseEntity<CommentReportInfoList> getCommentReportInfoListByComment(@PathVariable(value = "board_id") String boardId,
                                                                                   @PathVariable(value = "post_id") String postId,
                                                                                   @PathVariable(value = "comment_id") String commentId,
                                                                                   @RequestParam(name = "skip", defaultValue = "0", required = false) String skipVariable,
                                                                                   @RequestParam(name = "limit", defaultValue = "20", required = false) String limitVariable,
                                                                                   @RequestParam(name = "type", defaultValue = "", required = false) String typeVariable,
                                                                                   @RequestParam(name = "reason", defaultValue = "", required = false) String reason,
                                                                                   @RequestContext Account account) {
        ReportType type = null;
        int skip;
        int limit;

        try {
            skip = Integer.parseInt(skipVariable);
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.INVALID_REQUEST, "Skip value is invalid.");
        }

        try {
            limit = Integer.parseInt(limitVariable);
        } catch (Exception exception) {
            throw new IricomException(IricomErrorCode.INVALID_REQUEST, "Limit value is invalid.");
        }

        if (!typeVariable.isEmpty()) {
            try {
                type = ReportType.setValue(typeVariable);
            } catch (Exception exception) {
                throw new IricomException(IricomErrorCode.INVALID_REQUEST, "Type value is invalid.");
            }
        }

        CommentReportInfoSearch search = CommentReportInfoSearch.builder()
                .type(type)
                .reason(reason)
                .skip(skip)
                .limit(limit)
                .build();

        CommentReportInfoList commentReportInfoList = this.reportService.getCommentReportInfoList(account, boardId, postId, commentId, search);
        return ResponseEntity.status(HttpStatus.OK).body(commentReportInfoList);
    }

    /**
     * 댓글 신고 목록 조회 (게시판, 게시물, 댓글)
     */
    @ApiCallLog(apiCode = "RC_005")
    @Auth(role = AuthRole.BOARD_ADMIN)
    @RequestMapping(method = RequestMethod.GET, value = "/v1/report/comment/boards/{board_id}/posts/{post_id}/comments/{comment_id}/reports/{report_id}")
    public ResponseEntity<CommentReportInfo> getCommentReportInfo(@PathVariable(value = "board_id") String boardId,
                                                                      @PathVariable(value = "post_id") String postId,
                                                                      @PathVariable(value = "comment_id") String commentId,
                                                                      @PathVariable(value = "report_id") String reportId,
                                                                      @RequestContext Account account) {
        CommentReportInfo commentReportInfo = this.reportService.getCommentReportInfo(account, boardId, postId, commentId, reportId);
        return ResponseEntity.status(HttpStatus.OK).body(commentReportInfo);
    }
}

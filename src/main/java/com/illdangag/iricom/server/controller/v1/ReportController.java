package com.illdangag.iricom.server.controller.v1;

import com.illdangag.iricom.server.configuration.annotation.ApiCallLog;
import com.illdangag.iricom.server.configuration.annotation.Auth;
import com.illdangag.iricom.server.configuration.annotation.AuthRole;
import com.illdangag.iricom.server.configuration.annotation.RequestContext;
import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.request.CommentReportCreate;
import com.illdangag.iricom.server.data.request.CommentReportInfoSearch;
import com.illdangag.iricom.server.data.request.PostReportInfoCreate;
import com.illdangag.iricom.server.data.request.PostReportInfoSearch;
import com.illdangag.iricom.server.data.response.CommentReportInfo;
import com.illdangag.iricom.server.data.response.CommentReportInfoList;
import com.illdangag.iricom.server.data.response.PostReportInfo;
import com.illdangag.iricom.server.data.response.PostReportInfoList;
import com.illdangag.iricom.server.service.ReportService;
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

    @ApiCallLog(apiCode = "RP_001")
    @Auth(role = AuthRole.BOARD_ADMIN)
    @RequestMapping(method = RequestMethod.GET, value = "/v1/boards/{board_id}/report")
    public ResponseEntity<PostReportInfoList> getPostReportInfoList(@PathVariable(value = "board_id") String boardId,
                                                                    @RequestBody @Valid PostReportInfoSearch postReportInfoSearch,
                                                                    @RequestContext Account account) {
        PostReportInfoList postReportInfoList = this.reportService.getPostReportInfoList(account, boardId, postReportInfoSearch);
        return ResponseEntity.status(HttpStatus.OK).body(postReportInfoList);
    }

    @ApiCallLog(apiCode = "RP_002")
    @Auth(role = AuthRole.BOARD_ADMIN)
    @RequestMapping(method = RequestMethod.GET, value = "/v1/boards/{board_id}/posts/{post_id}/report/{report_id}")
    public ResponseEntity<PostReportInfo> getPostReportInfo(@PathVariable(value = "board_id") String boardId,
                                                            @PathVariable(value = "post_id") String postId,
                                                            @PathVariable(value = "report_id") String reportId,
                                                            @RequestContext Account account) {
        PostReportInfo postReportInfo = this.reportService.getPostReportInfo(account, boardId, postId, reportId);
        return ResponseEntity.status(HttpStatus.OK).body(postReportInfo);
    }

    @ApiCallLog(apiCode = "RP_003")
    @Auth(role = AuthRole.ACCOUNT)
    @RequestMapping(method = RequestMethod.POST, value = "/v1/boards/{board_id}/posts/{post_id}/report")
    public ResponseEntity<PostReportInfo> reportPost(@PathVariable(value = "board_id") String boardId,
                                                     @PathVariable(value = "post_id") String postId,
                                                     @RequestBody @Valid PostReportInfoCreate postReportInfoCreate,
                                                     @RequestContext Account account) {
        PostReportInfo postReportInfo = this.reportService.reportPost(account, boardId, postId, postReportInfoCreate);
        return ResponseEntity.status(HttpStatus.OK).body(postReportInfo);
    }

    @ApiCallLog(apiCode = "RC_001")
    @Auth(role = AuthRole.BOARD_ADMIN)
    @RequestMapping(method = RequestMethod.GET, value = "/v1/boards/{board_id}/posts/{post_id}/comments/{comment_id}/report")
    public ResponseEntity<CommentReportInfoList> getCommentReportInfoList(@PathVariable(value = "board_id") String boardId,
                                                                          @PathVariable(value = "post_id") String postId,
                                                                          @PathVariable(value = "comment_id") String commentId,
                                                                          @RequestBody @Valid CommentReportInfoSearch commentReportInfoSearch,
                                                                          @RequestContext Account account) {
        // TODO
        return null;
    }

    @ApiCallLog(apiCode = "RC_002")
    @Auth(role = AuthRole.BOARD_ADMIN)
    @RequestMapping(method = RequestMethod.GET, value = "/v1/boards/{board_id}/posts/{post_id}/comments/{comment_id}/report/{report_id}")
    public ResponseEntity<CommentReportInfo> getCommentReportInfo(@PathVariable(value = "board_id") String boardId,
                                                               @PathVariable(value = "post_id") String postId,
                                                               @PathVariable(value = "comment_id") String commentId,
                                                               @PathVariable(value = "report_id") String reportId,
                                                               @RequestBody @Valid PostReportInfoSearch postReportInfoSearch,
                                                               @RequestContext Account account) {
        // TODO
        return null;
    }

    @ApiCallLog(apiCode = "RC_003")
    @Auth(role = AuthRole.ACCOUNT)
    @RequestMapping(method = RequestMethod.POST, value = "/v1/boards/{board_id}/posts/{post_id}/comments/{comment_id}/report")
    public ResponseEntity<CommentReportInfo> reportComment(@PathVariable(value = "board_id") String boardId,
                                                           @PathVariable(value = "post_id") String postId,
                                                           @PathVariable(value = "comment_id") String commentId,
                                                           @RequestBody @Valid CommentReportCreate commentReportCreate,
                                                           @RequestContext Account account) {
        CommentReportInfo commentReportInfo = this.reportService.reportComment(account, boardId, postId, commentId, commentReportCreate);
        return ResponseEntity.status(HttpStatus.OK).body(commentReportInfo);
    }
}

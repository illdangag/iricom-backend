package com.illdangag.iricom.server.controller.v1;

import com.illdangag.iricom.server.configuration.annotation.ApiCallLog;
import com.illdangag.iricom.server.configuration.annotation.Auth;
import com.illdangag.iricom.server.configuration.annotation.AuthRole;
import com.illdangag.iricom.server.configuration.annotation.RequestContext;
import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.PostState;
import com.illdangag.iricom.server.data.request.CommentReportCreate;
import com.illdangag.iricom.server.data.request.PostReportCreate;
import com.illdangag.iricom.server.data.response.CommentInfo;
import com.illdangag.iricom.server.data.response.PostInfo;
import com.illdangag.iricom.server.service.CommentService;
import com.illdangag.iricom.server.service.PostService;
import com.illdangag.iricom.server.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(value = "/v1/report")
public class ReportController {
    private final ReportService reportService;
    private final PostService postService;
    private final CommentService commentService;

    @Autowired
    public ReportController(ReportService reportService, PostService postService, CommentService commentService) {
        this.reportService = reportService;
        this.postService = postService;
        this.commentService = commentService;
    }

    @ApiCallLog(apiCode = "RP_001")
    @Auth(role = AuthRole.ACCOUNT)
    @RequestMapping(method = RequestMethod.POST, value = "/post")
    public ResponseEntity<PostInfo> reportPost(@RequestBody @Valid PostReportCreate postReportCreate,
                                               @RequestContext Account account) {
        this.reportService.reportPost(account, postReportCreate);

        String boardId = postReportCreate.getBoardId();
        String postId = postReportCreate.getPostId();
        PostInfo postInfo = this.postService.getPostInfo(account, boardId, postId, PostState.PUBLISH);
        return ResponseEntity.status(HttpStatus.OK).body(postInfo);
    }

    @ApiCallLog(apiCode = "RP_002")
    @Auth(role = AuthRole.ACCOUNT)
    @RequestMapping(method = RequestMethod.POST, value = "/comment")
    public ResponseEntity<CommentInfo> reportComment(@RequestBody @Valid CommentReportCreate commentReportCreate,
                                                     @RequestContext Account account) {
        this.reportService.reportComment(account, commentReportCreate);

        String boardId = commentReportCreate.getBoardId();
        String postId = commentReportCreate.getPostId();
        String commentId = commentReportCreate.getCommentId();
        CommentInfo commentInfo = this.commentService.getComment(boardId, postId, commentId);
        return ResponseEntity.status(HttpStatus.OK).body(commentInfo);
    }
}

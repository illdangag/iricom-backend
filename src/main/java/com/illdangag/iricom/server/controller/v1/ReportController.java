package com.illdangag.iricom.server.controller.v1;

import com.illdangag.iricom.server.configuration.annotation.ApiCallLog;
import com.illdangag.iricom.server.configuration.annotation.Auth;
import com.illdangag.iricom.server.configuration.annotation.AuthRole;
import com.illdangag.iricom.server.configuration.annotation.RequestContext;
import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.request.CommentReportCreate;
import com.illdangag.iricom.server.data.request.PostReportCreate;
import com.illdangag.iricom.server.data.response.CommentReportInfo;
import com.illdangag.iricom.server.data.response.PostReportInfo;
import com.illdangag.iricom.server.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(value = "/v1/report")
public class ReportController {
    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @ApiCallLog(apiCode = "RP_001")
    @Auth(role = AuthRole.ACCOUNT)
    @RequestMapping(method = RequestMethod.POST, value = "/post")
    public ResponseEntity<PostReportInfo> reportPost(@RequestBody @Valid PostReportCreate postReportCreate,
                                               @RequestContext Account account) {
        PostReportInfo postReportInfo = this.reportService.reportPost(account, postReportCreate);
        return ResponseEntity.status(HttpStatus.OK).body(postReportInfo);
    }

    @ApiCallLog(apiCode = "RP_002")
    @Auth(role = AuthRole.ACCOUNT)
    @RequestMapping(method = RequestMethod.POST, value = "/comment")
    public ResponseEntity<CommentReportInfo> reportComment(@RequestBody @Valid CommentReportCreate commentReportCreate,
                                                     @RequestContext Account account) {
        CommentReportInfo commentReportInfo = this.reportService.reportComment(account, commentReportCreate);
        return ResponseEntity.status(HttpStatus.OK).body(commentReportInfo);
    }
}

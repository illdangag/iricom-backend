package com.illdangag.iricom.server.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.illdangag.iricom.server.data.entity.CommentReport;
import com.illdangag.iricom.server.util.DateTimeUtils;
import lombok.Getter;

@Getter
public class CommentReportInfo {
    private String id;

    private Long createDate;

    private Long updateDate;

    private String type;

    private String reason;

    @JsonProperty("reporter")
    private AccountInfo accountInfo;

    @JsonProperty("comment")
    private CommentInfo commentInfo;

    public CommentReportInfo(CommentReport commentReport, CommentInfo commentInfo) {
        this.id = String.valueOf(commentReport.getId());
        this.createDate = DateTimeUtils.getLong(commentReport.getCreateDate());
        this.updateDate = DateTimeUtils.getLong(commentReport.getUpdateDate());
        this.type = commentReport.getType().getText();
        this.reason = commentReport.getReason();
        this.accountInfo = new AccountInfo(commentReport.getAccount());
        this.commentInfo = commentInfo;
    }
}

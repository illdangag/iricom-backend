package com.illdangag.iricom.core.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.illdangag.iricom.core.data.entity.PostReport;
import com.illdangag.iricom.core.util.DateTimeUtils;
import lombok.Getter;

@Getter
public class PostReportInfo {
    private String id;

    private Long createDate;

    private Long updateDate;

    private String type;

    private String reason;

    @JsonProperty("reporter")
    private AccountInfo accountInfo;

    @JsonProperty("post")
    private PostInfo postInfo;

    public PostReportInfo(PostReport postReport, PostInfo postInfo) {
        this.id = String.valueOf(postReport.getId());
        this.createDate = DateTimeUtils.getLong(postReport.getCreateDate());
        this.updateDate = DateTimeUtils.getLong(postReport.getUpdateDate());
        this.type = postReport.getType().getText();
        this.reason = postReport.getReason();
        this.accountInfo = new AccountInfo(postReport.getAccount());
        this.postInfo = postInfo;
    }
}

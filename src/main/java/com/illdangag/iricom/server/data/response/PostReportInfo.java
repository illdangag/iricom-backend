package com.illdangag.iricom.server.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.illdangag.iricom.server.data.entity.PostReport;
import com.illdangag.iricom.server.util.DateTimeUtils;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PostReportInfo {
    private String id;

    private Long createDate;

    private Long updateDate;

    private String type;

    private String reason;

    @JsonProperty("post")
    private PostInfo postInfo;

    public PostReportInfo(PostReport postReport, PostInfo postInfo) {
        this.id = String.valueOf(postReport.getId());
        this.createDate = DateTimeUtils.getLong(postReport.getCreateDate());
        this.updateDate = DateTimeUtils.getLong(postReport.getUpdateDate());
        this.type = postReport.getType().getText();
        this.reason = postReport.getReason();
        this.postInfo = postInfo;
    }
}

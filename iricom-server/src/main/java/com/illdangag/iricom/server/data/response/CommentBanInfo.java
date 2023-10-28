package com.illdangag.iricom.server.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.illdangag.iricom.server.data.entity.CommentBan;
import com.illdangag.iricom.server.util.DateTimeUtils;
import lombok.Getter;

@Getter
public class CommentBanInfo {
    private String id;

    private Long createDate;

    private Long updateDate;

    private String reason;

    private Boolean enabled;

    @JsonProperty("comment")
    private CommentInfo commentInfo;

    public CommentBanInfo(CommentBan commentBan, CommentInfo commentInfo) {
        this.id = String.valueOf(commentBan.getId());
        this.createDate = DateTimeUtils.getLong(commentBan.getCreateDate());
        this.updateDate = DateTimeUtils.getLong(commentBan.getUpdateDate());
        this.reason = commentBan.getReason();
        this.enabled = commentBan.getEnabled();
        this.commentInfo = commentInfo;
    }
}

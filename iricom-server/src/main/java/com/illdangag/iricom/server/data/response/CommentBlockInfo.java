package com.illdangag.iricom.server.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.illdangag.iricom.server.data.entity.CommentBlock;
import com.illdangag.iricom.server.util.DateTimeUtils;
import lombok.Getter;

@Getter
public class CommentBlockInfo {
    private String id;

    private Long createDate;

    private Long updateDate;

    private String reason;

    private Boolean enabled;

    @JsonProperty("comment")
    private CommentInfo commentInfo;

    public CommentBlockInfo(CommentBlock commentBlock, CommentInfo commentInfo) {
        this.id = String.valueOf(commentBlock.getId());
        this.createDate = DateTimeUtils.getLong(commentBlock.getCreateDate());
        this.updateDate = DateTimeUtils.getLong(commentBlock.getUpdateDate());
        this.reason = commentBlock.getReason();
        this.enabled = commentBlock.getEnabled();
        this.commentInfo = commentInfo;
    }
}

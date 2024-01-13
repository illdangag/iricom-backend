package com.illdangag.iricom.server.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.illdangag.iricom.server.data.entity.PostBlock;
import com.illdangag.iricom.server.util.DateTimeUtils;
import lombok.Getter;

@Getter
public class PostBlockInfo {
    private String id;

    private Long createDate;

    private Long updateDate;

    private String reason;

    @JsonProperty("post")
    private PostInfo postInfo;

    public PostBlockInfo(PostBlock postBlock, PostInfo postInfo) {
        this.id = String.valueOf(postBlock.getId());
        this.createDate = DateTimeUtils.getLong(postBlock.getCreateDate());
        this.updateDate = DateTimeUtils.getLong(postBlock.getUpdateDate());
        this.reason = postBlock.getReason();
        this.postInfo = postInfo;
    }
}

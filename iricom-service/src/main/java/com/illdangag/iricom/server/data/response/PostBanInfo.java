package com.illdangag.iricom.server.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.illdangag.iricom.server.data.entity.PostBan;
import com.illdangag.iricom.server.util.DateTimeUtils;
import lombok.Getter;

@Getter
public class PostBanInfo {
    private String id;

    private Long createDate;

    private Long updateDate;

    private String reason;

    private Boolean enabled;

    @JsonProperty("post")
    private PostInfo postInfo;

    public PostBanInfo(PostBan postBan, PostInfo postInfo) {
        this.id = String.valueOf(postBan.getId());
        this.createDate = DateTimeUtils.getLong(postBan.getCreateDate());
        this.updateDate = DateTimeUtils.getLong(postBan.getUpdateDate());
        this.reason = postBan.getReason();
        this.enabled = postBan.getEnabled();
        this.postInfo = postInfo;
    }
}

package com.illdangag.iricom.server.data.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentInfoList {
    private long total;

    private long skip;

    private long limit;

    @Builder.Default
    @JsonProperty("comments")
    private List<CommentInfo> commentInfoList = new LinkedList<>();
}

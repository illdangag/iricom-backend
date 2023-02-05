package com.illdangag.iricom.server.data.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BoardAdminInfoList {
    private long total;

    private long skip;

    private long limit;

    @Builder.Default
    @JsonProperty("boardAdmins")
    private List<BoardAdminInfo> boardAdminInfoList = new LinkedList<>();
}

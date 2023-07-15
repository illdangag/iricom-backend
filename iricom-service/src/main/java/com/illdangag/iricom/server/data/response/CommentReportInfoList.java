package com.illdangag.iricom.server.data.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.LinkedList;
import java.util.List;

@Getter
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentReportInfoList extends SearchList {
    @Builder.Default
    @JsonProperty("reports")
    private List<CommentReportInfo> commentReportInfoList = new LinkedList<>();
}

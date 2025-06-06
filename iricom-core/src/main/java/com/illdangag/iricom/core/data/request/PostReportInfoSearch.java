package com.illdangag.iricom.core.data.request;

import com.illdangag.iricom.core.data.entity.type.ReportType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@ToString
public class PostReportInfoSearch extends SearchRequest {
    private ReportType type;

    @Builder.Default
    private String reason = "";
}

package com.illdangag.iricom.server.test.data.wrapper;

import com.illdangag.iricom.server.data.entity.type.ReportType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class TestPostReportInfo {
    @Setter
    private String id;

    private TestAccountInfo reportAccount;

    private TestPostInfo post;

    private ReportType type;

    private String reason;
}

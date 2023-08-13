package com.illdangag.iricom.server.test.data.wrapper;

import com.illdangag.iricom.server.data.entity.ReportType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TestPostReportInfo {
    private TestAccountInfo reportAccount;

    private TestPostInfo post;

    private ReportType type;

    private String reason;
}

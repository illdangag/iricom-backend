package com.illdangag.iricom.core.test.data.wrapper;

import com.illdangag.iricom.core.data.entity.type.ReportType;
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

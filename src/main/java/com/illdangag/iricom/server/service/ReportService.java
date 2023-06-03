package com.illdangag.iricom.server.service;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.request.PostReportCreate;

import javax.validation.Valid;

public interface ReportService {
    void reportPost(Account account, @Valid PostReportCreate postReportCreate);
}

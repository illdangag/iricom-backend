package com.illdangag.iricom.server.repository;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.Post;
import com.illdangag.iricom.server.data.entity.PostReport;
import com.illdangag.iricom.server.data.entity.ReportType;
import com.illdangag.iricom.server.test.IricomTestSuite;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Optional;

@Slf4j
@DisplayName("ReportRepository")
public class ReportRepositoryTest extends IricomTestSuite {
    @Autowired
    ReportRepository reportRepository;

    @Autowired
    public ReportRepositoryTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("게시물 신고 내역 저장 및 조회")
    public void testCase00() throws Exception {
        Account account = getAccount(common00);
        Post post = getPost(reportPost00);

        PostReport postReport = PostReport.builder()
                .account(account)
                .post(post)
                .type(ReportType.ETC)
                .reason("repository save test")
                .build();
        this.reportRepository.savePostReport(postReport);

        Optional<PostReport> postReportOptional = this.reportRepository.getPostReport(account, post);
        Assertions.assertTrue(postReportOptional.isPresent());
    }
}

package com.illdangag.iricom.server.repository;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.test.IricomTestSuite;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.List;

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

        List<PostReport> postReportList = this.reportRepository.getPostReport(account, post);
        Assertions.assertFalse(postReportList.isEmpty());
    }

    @Test
    @DisplayName("댓글 신고 내역 저장 및 조회")
    public void testCase01() throws Exception {
        Account account = getAccount(common00);
        Comment comment = getComment(reportComment00);

        CommentReport commentReport = CommentReport.builder()
                .account(account)
                .comment(comment)
                .type(ReportType.ETC)
                .reason("repository save test")
                .build();

        this.reportRepository.saveCommentReport(commentReport);

        List<CommentReport> commentReportList = this.reportRepository.getCommentReportList(account, comment);
        Assertions.assertFalse(commentReportList.isEmpty());
    }
}

package com.illdangag.iricom.server.repository;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestCommentInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestPostInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@DisplayName("ReportRepository")
public class ReportRepositoryTest extends IricomTestSuite {
    @Autowired
    ReportRepository reportRepository;

    private final TestBoardInfo testBoardInfo = TestBoardInfo.builder()
            .title("testBoardInfo00").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();

    private final TestPostInfo testPostInfo = TestPostInfo.builder()
            .title("testPostInfo00").content("testPostInfo00").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(testBoardInfo).build();

    private final TestCommentInfo testCommentInfo = TestCommentInfo.builder()
            .content("testCommentInfo00")
            .creator(common00).post(testPostInfo)
            .build();

    @Autowired
    public ReportRepositoryTest(ApplicationContext context) {
        super(context);

        List<TestBoardInfo> testBoardInfoList = Arrays.asList(testBoardInfo);
        List<TestPostInfo> testPostInfoList = Arrays.asList(testPostInfo);
        List<TestCommentInfo> testCommentInfoList = Arrays.asList(testCommentInfo);

        super.setBoard(testBoardInfoList);
        super.setPost(testPostInfoList);
        super.setComment(testCommentInfoList);
    }

    @Test
    @DisplayName("게시물 신고 내역 저장 및 조회")
    public void reportPost() throws Exception {
        Account account = getAccount(common00);
        Post post = getPost(testPostInfo);

        PostReport postReport = PostReport.builder()
                .account(account)
                .post(post)
                .type(ReportType.ETC)
                .reason("repository save test")
                .build();
        this.reportRepository.savePostReport(postReport);

        List<PostReport> postReportList = this.reportRepository.getPostReportList(account, post);
        Assertions.assertFalse(postReportList.isEmpty());
    }

    @Test
    @DisplayName("댓글 신고 내역 저장 및 조회")
    public void reportComment() throws Exception {
        Account account = getAccount(common00);
        Comment comment = getComment(testCommentInfo);

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

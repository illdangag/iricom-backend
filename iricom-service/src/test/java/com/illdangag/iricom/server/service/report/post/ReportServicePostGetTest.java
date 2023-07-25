package com.illdangag.iricom.server.service.report.post;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.data.response.PostReportInfo;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.service.ReportService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestPostInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestPostReportInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Collections;

@DisplayName("service: 신고 - 게시물 정보 조회")
public class ReportServicePostGetTest extends IricomTestSuite {
    @Autowired
    ReportService reportService;

    // 게시판
    private final TestBoardInfo enableBoard00 = TestBoardInfo.builder()
            .title("enable").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();
    private final TestBoardInfo enableBoard01 = TestBoardInfo.builder()
            .title("enable").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();
    private final TestBoardInfo disableBoard00 = TestBoardInfo.builder()
            .title("disable").isEnabled(false).adminList(Collections.singletonList(allBoardAdmin)).build();
    // 게시물
    private final TestPostInfo reportedPost00 = TestPostInfo.builder()
            .title("reportedPost00").content("report contents").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(enableBoard00).build();
    private final TestPostInfo post00 = TestPostInfo.builder()
            .title("post00").content("contents").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(disableBoard00).build();
    // 게시물 신고
    private final TestPostReportInfo postReport00 = TestPostReportInfo.builder()
            .type(ReportType.HATE).reason("hate").reportAccount(common00).post(reportedPost00)
            .build();

    @Autowired
    public ReportServicePostGetTest(ApplicationContext context) {
        super(context);

        addTestBoardInfo(enableBoard00, enableBoard01, disableBoard00);
        addTestPostInfo(reportedPost00, post00);
        addTestPostReportInfo(postReport00);

        init();
    }

    @Test
    @DisplayName("기본 조회")
    public void getPostReportInfo() throws Exception {
        Account systemAdminAccount = getAccount(systemAdmin);
        PostReport postReport = getPostReport(postReport00);
        Post post = postReport.getPost();
        Board board = post.getBoard();

        String boardId = String.valueOf(board.getId());
        String postId = String.valueOf(post.getId());
        String reportId = String.valueOf(postReport.getId());

        PostReportInfo postReportInfo = reportService.getPostReportInfo(systemAdminAccount, boardId, postId, reportId);
        Assertions.assertEquals(reportId, postReportInfo.getId());
    }

    @Test
    @DisplayName("올바르지 않은 게시판")
    public void invalidBoard() throws Exception {
        Account systemAdminAccount = getAccount(systemAdmin);
        PostReport postReport = getPostReport(postReport00);
        Post post = postReport.getPost();
        String boardId = getBoardId(enableBoard01);

        String postId = String.valueOf(post.getId());
        String reportId = String.valueOf(postReport.getId());

        Assertions.assertThrows(IricomException.class, () -> {
            reportService.getPostReportInfo(systemAdminAccount, boardId, postId, reportId);
        });
    }

    @Test
    @DisplayName("존재하지 않는 게시판")
    public void notExistBoard() throws Exception {
        Account systemAdminAccount = getAccount(systemAdmin);
        PostReport postReport = getPostReport(postReport00);
        Post post = postReport.getPost();

        String boardId = "NOT_EXIST";
        String postId = String.valueOf(post.getId());
        String reportId = String.valueOf(postReport.getId());

        Assertions.assertThrows(IricomException.class, () -> {
            reportService.getPostReportInfo(systemAdminAccount, boardId, postId, reportId);
        });
    }

    @Test
    @DisplayName("올바르지 않은 게시물")
    public void invalidPost() throws Exception {
        Account systemAdminAccount = getAccount(systemAdmin);
        PostReport postReport = getPostReport(postReport00);
        Post post = postReport.getPost();
        Board board = post.getBoard();
        String invalidPostId = getPostId(post00);

        String boardId = String.valueOf(board.getId());
        String reportId = String.valueOf(postReport.getId());

        Assertions.assertThrows(IricomException.class, () -> {
            reportService.getPostReportInfo(systemAdminAccount, boardId, invalidPostId, reportId);
        });
    }

    @Test
    @DisplayName("존재하지 않은 게시물")
    public void notExistPost() throws Exception {
        Account systemAdminAccount = getAccount(systemAdmin);
        PostReport postReport = getPostReport(postReport00);
        Post post = postReport.getPost();
        Board board = post.getBoard();

        String boardId = String.valueOf(board.getId());
        String postId = "NOT_EXIST";
        String reportId = String.valueOf(postReport.getId());

        Assertions.assertThrows(IricomException.class, () -> {
            reportService.getPostReportInfo(systemAdminAccount, boardId, postId, reportId);
        });
    }

    @Test
    @DisplayName("올바르지 않은 신고")
    public void invalidReport() throws Exception {
        Account systemAdminAccount = getAccount(systemAdmin);
        PostReport postReport = getPostReport(postReport00);
        Post post = postReport.getPost();
        Board board = post.getBoard();

        String boardId = String.valueOf(board.getId());
        String postId = String.valueOf(post.getId());
        String reportId = String.valueOf(postReport00);

        Assertions.assertThrows(IricomException.class, () -> {
            reportService.getPostReportInfo(systemAdminAccount, boardId, postId, reportId);
        });
    }

    @Test
    @DisplayName("존재하지 않은 신고")
    public void notExistReport() throws Exception {
        Account systemAdminAccount = getAccount(systemAdmin);
        PostReport postReport = getPostReport(postReport00);
        Post post = postReport.getPost();
        Board board = post.getBoard();

        String boardId = String.valueOf(board.getId());
        String postId = String.valueOf(post.getId());
        String reportId = "NOT_EXIST";

        Assertions.assertThrows(IricomException.class, () -> {
            reportService.getPostReportInfo(systemAdminAccount, boardId, postId, reportId);
        });
    }
}

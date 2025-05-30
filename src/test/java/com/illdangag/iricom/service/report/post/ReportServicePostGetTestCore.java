package com.illdangag.iricom.service.report.post;

import com.illdangag.iricom.core.data.response.PostReportInfo;
import com.illdangag.iricom.core.exception.IricomException;
import com.illdangag.iricom.core.service.ReportService;
import com.illdangag.iricom.core.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.core.test.data.wrapper.TestBoardInfo;
import com.illdangag.iricom.core.test.data.wrapper.TestPostInfo;
import com.illdangag.iricom.core.test.data.wrapper.TestPostReportInfo;
import com.illdangag.iricom.IricomTestServiceSuite;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.transaction.Transactional;

@DisplayName("service: 신고 - 게시물 정보 조회")
@Transactional
public class ReportServicePostGetTestCore extends IricomTestServiceSuite {
    @Autowired
    ReportService reportService;

    @Autowired
    public ReportServicePostGetTestCore(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("기본 조회")
    public void getPostReportInfo() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);
        // 게시물 신고
        TestPostReportInfo postReport = setRandomPostReport(post, account);

        PostReportInfo postReportInfo = reportService.getPostReportInfo(systemAdmin.getId(), board.getId(), post.getId(), postReport.getId());
        Assertions.assertEquals(postReport.getId(), postReportInfo.getId());
    }

    @Test
    @DisplayName("올바르지 않은 게시판")
    public void invalidBoard() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        TestBoardInfo otherBoard = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);
        // 게시물 신고
        TestPostReportInfo postReport = setRandomPostReport(post, account);

        Assertions.assertThrows(IricomException.class, () -> {
            reportService.getPostReportInfo(systemAdmin.getId(), otherBoard.getId(), post.getId(), postReport.getId());
        });
    }

    @Test
    @DisplayName("존재하지 않는 게시판")
    public void notExistBoard() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);
        // 게시물 신고
        TestPostReportInfo postReport = setRandomPostReport(post, account);
        String boardId = "NOT_EXIST";

        Assertions.assertThrows(IricomException.class, () -> {
            reportService.getPostReportInfo(systemAdmin.getId(), boardId, post.getId(), postReport.getId());
        });
    }

    @Test
    @DisplayName("올바르지 않은 게시물")
    public void invalidPost() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);
        TestPostInfo otherPost = setRandomPost(board, account);
        // 게시물 신고
        TestPostReportInfo postReport = setRandomPostReport(post, account);

        Assertions.assertThrows(IricomException.class, () -> {
            reportService.getPostReportInfo(systemAdmin.getId(), board.getId(), otherPost.getId(), postReport.getId());
        });
    }

    @Test
    @DisplayName("존재하지 않은 게시물")
    public void notExistPost() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);
        // 게시물 신고
        TestPostReportInfo postReport = setRandomPostReport(post, account);
        String postId = "NOT_EXIST";

        Assertions.assertThrows(IricomException.class, () -> {
            reportService.getPostReportInfo(systemAdmin.getId(), board.getId(), postId, postReport.getId());
        });
    }

    @Test
    @DisplayName("존재하지 않은 신고")
    public void notExistReport() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);
        // 게시물 신고
        setRandomPostReport(post, account);
        String postReportId = "NOT_EXIST";

        Assertions.assertThrows(IricomException.class, () -> {
            reportService.getPostReportInfo(systemAdmin.getId(), board.getId(), post.getId(), postReportId);
        });
    }
}

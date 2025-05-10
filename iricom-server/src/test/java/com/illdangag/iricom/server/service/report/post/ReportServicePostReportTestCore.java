package com.illdangag.iricom.server.service.report.post;

import com.illdangag.iricom.core.data.entity.type.ReportType;
import com.illdangag.iricom.core.data.request.PostReportInfoCreate;
import com.illdangag.iricom.core.exception.IricomException;
import com.illdangag.iricom.core.service.ReportService;
import com.illdangag.iricom.core.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.core.test.data.wrapper.TestBoardInfo;
import com.illdangag.iricom.core.test.data.wrapper.TestPostInfo;
import com.illdangag.iricom.server.IricomTestServiceSuite;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.transaction.Transactional;

@DisplayName("service: 신고 - 게시물 신고")
@Slf4j
@Transactional
public class ReportServicePostReportTestCore extends IricomTestServiceSuite {
    @Autowired
    ReportService reportService;

    @Autowired
    public ReportServicePostReportTestCore(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("게시물 신고")
    public void reportPost() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);

        PostReportInfoCreate postReportInfoCreate = PostReportInfoCreate.builder()
                .type(ReportType.ETC)
                .reason("report test")
                .build();

        reportService.reportPost(account.getId(), board.getId(), post.getId(), postReportInfoCreate);
    }

    @Test
    @DisplayName("중복 게시물 신고")
    public void duplicateReportPost() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);

        PostReportInfoCreate postReportInfoCreate = PostReportInfoCreate.builder()
                .type(ReportType.ETC)
                .reason("report test")
                .build();

        // 첫번째 신고
        reportService.reportPost(account.getId(), board.getId(), post.getId(), postReportInfoCreate);

        // 두번째 신고
        Assertions.assertThrows(IricomException.class, () -> {
            reportService.reportPost(account.getId(), board.getId(), post.getId(), postReportInfoCreate);
        });
    }

    @Test
    @DisplayName("게시물의 게시판 불일치")
    public void notMatchPostAndBoard() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        TestBoardInfo otherBoard = setRandomBoard();
        // 게시물 생성
        TestPostInfo post = setRandomPost(board, account);

        PostReportInfoCreate postReportInfoCreate = PostReportInfoCreate.builder()
                .type(ReportType.ETC)
                .reason("report test")
                .build();

        Assertions.assertThrows(IricomException.class, () -> {
            reportService.reportPost(account.getId(), otherBoard.getId(), post.getId(), postReportInfoCreate);
        });
    }

    @Test
    @DisplayName("존재하지 않는 게시물")
    public void notExistPost() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        setRandomPost(board, account);

        PostReportInfoCreate postReportInfoCreate = PostReportInfoCreate.builder()
                .type(ReportType.ETC)
                .reason("report test")
                .build();

        Assertions.assertThrows(IricomException.class, () -> {
            reportService.reportPost(account.getId(), board.getId(), "NOT_EXIST_POST", postReportInfoCreate);
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

        PostReportInfoCreate postReportInfoCreate = PostReportInfoCreate.builder()
                .type(ReportType.ETC)
                .reason("report test")
                .build();

        Assertions.assertThrows(IricomException.class, () -> {
            reportService.reportPost(account.getId(), "NOT_EXIST_BOARD", post.getId(), postReportInfoCreate);
        });
    }
}

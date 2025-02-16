package com.illdangag.iricom.server.service.report.post;

import com.illdangag.iricom.server.data.entity.type.PostState;
import com.illdangag.iricom.server.data.entity.type.PostType;
import com.illdangag.iricom.server.data.entity.type.ReportType;
import com.illdangag.iricom.server.data.request.PostReportInfoCreate;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.service.ReportService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestPostInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.transaction.Transactional;
import java.util.Arrays;

@DisplayName("service: 신고 - 게시물 신고")
@Slf4j
@Transactional
public class ReportServicePostReportTest extends IricomTestSuite {
    @Autowired
    ReportService reportService;

    @Autowired
    public ReportServicePostReportTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("게시물 신고")
    public void reportPost() throws Exception {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        TestPostInfo post = this.setRandomPost(board, account);

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
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        TestPostInfo post = this.setRandomPost(board, account);

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
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        TestBoardInfo otherBoard = this.setRandomBoard();
        // 게시물 생성
        TestPostInfo post = this.setRandomPost(board, account);

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
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        this.setRandomPost(board, account);

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
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        TestPostInfo post = this.setRandomPost(board, account);

        PostReportInfoCreate postReportInfoCreate = PostReportInfoCreate.builder()
                .type(ReportType.ETC)
                .reason("report test")
                .build();

        Assertions.assertThrows(IricomException.class, () -> {
            reportService.reportPost(account.getId(), "NOT_EXIST_BOARD", post.getId(), postReportInfoCreate);
        });
    }
}

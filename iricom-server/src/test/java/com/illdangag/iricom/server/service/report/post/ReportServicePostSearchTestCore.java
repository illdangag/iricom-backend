package com.illdangag.iricom.server.service.report.post;

import com.illdangag.iricom.core.data.entity.type.ReportType;
import com.illdangag.iricom.core.data.request.PostReportInfoSearch;
import com.illdangag.iricom.core.data.response.PostReportInfoList;
import com.illdangag.iricom.core.service.ReportService;
import com.illdangag.iricom.core.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.core.test.data.wrapper.TestBoardInfo;
import com.illdangag.iricom.core.test.data.wrapper.TestPostInfo;
import com.illdangag.iricom.core.test.data.wrapper.TestPostReportInfo;
import com.illdangag.iricom.server.IricomTestServiceSuite;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

@DisplayName("service: 신고 - 게시물 목록 조회")
@Slf4j
@Transactional
public class ReportServicePostSearchTestCore extends IricomTestServiceSuite {
    @Autowired
    ReportService reportService;

    @Autowired
    public ReportServicePostSearchTestCore(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("기본 조회")
    public void searchPostReportInfoList() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        List<TestPostInfo> postList = setRandomPost(board, account, 8);
        // 게시물 신고
        for (TestPostInfo post : postList) {
            setRandomPostReport(post, account);
        }

        PostReportInfoSearch postReportInfoSearch = PostReportInfoSearch.builder()
                .reason("")
                .build();

        PostReportInfoList postReportInfoList = reportService.getPostReportInfoList(systemAdmin.getId(), board.getId(), postReportInfoSearch);
        Assertions.assertEquals(0, postReportInfoList.getSkip());
        Assertions.assertEquals(20, postReportInfoList.getLimit());
        Assertions.assertEquals(8, postReportInfoList.getTotal());
    }

    @Test
    @DisplayName("종류")
    public void searchPostReportInfoListByType() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        setRandomPost(board, account, 1);
        List<TestPostInfo> hatePostList = setRandomPost(board, account, 2);
        List<TestPostInfo> politicalPostList = setRandomPost(board, account, 3);
        List<TestPostInfo> pornographyPostList = setRandomPost(board, account, 4);
        List<TestPostInfo> etcPostList = setRandomPost(board, account, 5);
        // 게시물 신고
        for (TestPostInfo post : hatePostList) {
            setRandomPostReport(post, account, ReportType.HATE);
        }
        for (TestPostInfo post : politicalPostList) {
            setRandomPostReport(post, account, ReportType.POLITICAL);
        }
        for (TestPostInfo post : pornographyPostList) {
            setRandomPostReport(post, account, ReportType.PORNOGRAPHY);
        }
        for (TestPostInfo post : etcPostList) {
            setRandomPostReport(post, account, ReportType.ETC);
        }


        PostReportInfoSearch hatePostReportInfoSearch = PostReportInfoSearch.builder()
                .type(ReportType.HATE)
                .reason("")
                .build();

        PostReportInfoList hatePostReportInfoList = reportService.getPostReportInfoList(systemAdmin.getId(), board.getId(), hatePostReportInfoSearch);
        Assertions.assertEquals(0, hatePostReportInfoList.getSkip());
        Assertions.assertEquals(20, hatePostReportInfoList.getLimit());
        Assertions.assertEquals(2, hatePostReportInfoList.getTotal());

        PostReportInfoSearch politicalPostReportInfoSearch = PostReportInfoSearch.builder()
                .type(ReportType.POLITICAL)
                .reason("")
                .build();

        PostReportInfoList politicalPostReportInfoList = reportService.getPostReportInfoList(systemAdmin.getId(), board.getId(), politicalPostReportInfoSearch);
        Assertions.assertEquals(0, politicalPostReportInfoList.getSkip());
        Assertions.assertEquals(20, politicalPostReportInfoList.getLimit());
        Assertions.assertEquals(3, politicalPostReportInfoList.getTotal());

        PostReportInfoSearch pornographyPostReportInfoSearch = PostReportInfoSearch.builder()
                .type(ReportType.PORNOGRAPHY)
                .reason("")
                .build();

        PostReportInfoList pornographyPostReportInfoList = reportService.getPostReportInfoList(systemAdmin.getId(), board.getId(), pornographyPostReportInfoSearch);
        Assertions.assertEquals(0, pornographyPostReportInfoList.getSkip());
        Assertions.assertEquals(20, pornographyPostReportInfoList.getLimit());
        Assertions.assertEquals(4, pornographyPostReportInfoList.getTotal());

        PostReportInfoSearch etcPostReportInfoSearch = PostReportInfoSearch.builder()
                .type(ReportType.ETC)
                .reason("")
                .build();

        PostReportInfoList etcPostReportInfoList = reportService.getPostReportInfoList(systemAdmin.getId(), board.getId(), etcPostReportInfoSearch);
        Assertions.assertEquals(0, etcPostReportInfoList.getSkip());
        Assertions.assertEquals(20, etcPostReportInfoList.getLimit());
        Assertions.assertEquals(5, etcPostReportInfoList.getTotal());
    }

    @Test
    @DisplayName("skip")
    public void searchPostReportInfoListSkip() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        setRandomPost(board, account, 10);
        List<TestPostInfo> postList = setRandomPost(board, account, 4);
        // 게시물 신고
        for (TestPostInfo post : postList) {
            setRandomPostReport(post, account);
        }

        PostReportInfoSearch postReportInfoSearch = PostReportInfoSearch.builder()
                .skip(2)
                .build();

        PostReportInfoList postReportInfoList = reportService.getPostReportInfoList(systemAdmin.getId(), board.getId(), postReportInfoSearch);
        Assertions.assertEquals(2, postReportInfoList.getSkip());
        Assertions.assertEquals(20, postReportInfoList.getLimit());
        Assertions.assertEquals(4, postReportInfoList.getTotal());
        Assertions.assertEquals(2, postReportInfoList.getPostReportInfoList().size());
    }

    @Test
    @DisplayName("limit")
    public void searchPostReportInfoListLimit() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        setRandomPost(board, account, 8);
        List<TestPostInfo> postList = setRandomPost(board, account, 4);
        // 게시물 신고
        for (TestPostInfo post : postList) {
            setRandomPostReport(post, account);
        }

        PostReportInfoSearch postReportInfoSearch = PostReportInfoSearch.builder()
                .limit(3)
                .build();

        PostReportInfoList postReportInfoList = reportService.getPostReportInfoList(systemAdmin.getId(), board.getId(), postReportInfoSearch);
        Assertions.assertEquals(0, postReportInfoList.getSkip());
        Assertions.assertEquals(3, postReportInfoList.getLimit());
        Assertions.assertEquals(4, postReportInfoList.getTotal());
        Assertions.assertEquals(3, postReportInfoList.getPostReportInfoList().size());
    }

    @Test
    @DisplayName("skip, limit")
    public void searchPostReportInfoListSkipLimit() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        setRandomPost(board, account, 2);
        List<TestPostInfo> postList = setRandomPost(board, account, 4);
        // 게시물 신고
        for (TestPostInfo post : postList) {
            setRandomPostReport(post, account);
        }

        PostReportInfoSearch postReportInfoSearch = PostReportInfoSearch.builder()
                .skip(1)
                .limit(2)
                .build();

        PostReportInfoList postReportInfoList = reportService.getPostReportInfoList(systemAdmin.getId(), board.getId(), postReportInfoSearch);
        Assertions.assertEquals(1, postReportInfoList.getSkip());
        Assertions.assertEquals(2, postReportInfoList.getLimit());
        Assertions.assertEquals(4, postReportInfoList.getTotal());
        Assertions.assertEquals(2, postReportInfoList.getPostReportInfoList().size());
    }

    @Test
    @DisplayName("reason")
    public void searchPostReportInfoListByReason() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        List<TestPostInfo> postList = setRandomPost(board, account, 4);
        TestPostInfo post00 = setRandomPost(board, account);
        TestPostInfo post01 = setRandomPost(board, account);
        TestPostInfo post02 = setRandomPost(board, account);
        TestPostInfo post03 = setRandomPost(board, account);
        // 게시물 신고
        for (TestPostInfo post : postList) {
            setRandomPostReport(post, account);
        }
        List<TestPostReportInfo> reportList = Arrays.asList(
                TestPostReportInfo.builder()
                        .post(post00).reportAccount(account)
                        .type(ReportType.ETC)
                        .reason("TEST! 00!")
                        .build(),
                TestPostReportInfo.builder()
                        .post(post01).reportAccount(account)
                        .type(ReportType.HATE)
                        .reason("00!")
                        .build(),
                TestPostReportInfo.builder()
                        .post(post02).reportAccount(account)
                        .type(ReportType.POLITICAL)
                        .reason("TEST!")
                        .build(),
                TestPostReportInfo.builder()
                        .post(post03).reportAccount(account)
                        .type(ReportType.PORNOGRAPHY)
                        .reason("NO_REASON!")
                        .build()
        );
        this.setPostReport(reportList);

        PostReportInfoSearch postReportInfoSearch00 = PostReportInfoSearch.builder()
                .reason("00!")
                .build();

        PostReportInfoList postReportInfoList00 = reportService.getPostReportInfoList(systemAdmin.getId(), board.getId(), postReportInfoSearch00);
        Assertions.assertEquals(0, postReportInfoList00.getSkip());
        Assertions.assertEquals(20, postReportInfoList00.getLimit());
        Assertions.assertEquals(2, postReportInfoList00.getTotal());
        Assertions.assertEquals(2, postReportInfoList00.getPostReportInfoList().size());

        PostReportInfoSearch postReportInfoSearch01 = PostReportInfoSearch.builder()
                .reason("TEST!")
                .build();

        PostReportInfoList postReportInfoList01 = reportService.getPostReportInfoList(systemAdmin.getId(), board.getId(), postReportInfoSearch01);
        Assertions.assertEquals(0, postReportInfoList01.getSkip());
        Assertions.assertEquals(20, postReportInfoList01.getLimit());
        Assertions.assertEquals(2, postReportInfoList01.getTotal());
        Assertions.assertEquals(2, postReportInfoList01.getPostReportInfoList().size());

        PostReportInfoSearch postReportInfoSearch02 = PostReportInfoSearch.builder()
                .reason("T! 00!")
                .build();

        PostReportInfoList postReportInfoList02 = reportService.getPostReportInfoList(systemAdmin.getId(), board.getId(), postReportInfoSearch02);
        Assertions.assertEquals(0, postReportInfoList02.getSkip());
        Assertions.assertEquals(20, postReportInfoList02.getLimit());
        Assertions.assertEquals(1, postReportInfoList02.getTotal());
        Assertions.assertEquals(1, postReportInfoList02.getPostReportInfoList().size());

        PostReportInfoSearch postReportInfoSearch03 = PostReportInfoSearch.builder()
                .reason("NO_REASON!")
                .build();

        PostReportInfoList postReportInfoList03 = reportService.getPostReportInfoList(systemAdmin.getId(), board.getId(), postReportInfoSearch03);
        Assertions.assertEquals(0, postReportInfoList03.getSkip());
        Assertions.assertEquals(20, postReportInfoList03.getLimit());
        Assertions.assertEquals(1, postReportInfoList03.getTotal());
        Assertions.assertEquals(1, postReportInfoList03.getPostReportInfoList().size());
    }

    @Test
    @DisplayName("complex")
    public void searchPostReportInfoListByComplex() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        List<TestPostInfo> postList = setRandomPost(board, account, 4);
        TestPostInfo post00 = setRandomPost(board, account);
        TestPostInfo post01 = setRandomPost(board, account);
        TestPostInfo post02 = setRandomPost(board, account);
        TestPostInfo post03 = setRandomPost(board, account);
        // 게시물 신고
        for (TestPostInfo post : postList) {
            setRandomPostReport(post, account);
        }
        List<TestPostReportInfo> reportList = Arrays.asList(
                TestPostReportInfo.builder()
                        .post(post00).reportAccount(account)
                        .type(ReportType.ETC)
                        .reason("TEST! 00!")
                        .build(),
                TestPostReportInfo.builder()
                        .post(post01).reportAccount(account)
                        .type(ReportType.HATE)
                        .reason("00!")
                        .build(),
                TestPostReportInfo.builder()
                        .post(post02).reportAccount(account)
                        .type(ReportType.PORNOGRAPHY)
                        .reason("TEST!")
                        .build(),
                TestPostReportInfo.builder()
                        .post(post03).reportAccount(account)
                        .type(ReportType.POLITICAL)
                        .reason("NO_REASON!")
                        .build()
        );
        this.setPostReport(reportList);

        PostReportInfoSearch postReportInfoSearch = PostReportInfoSearch.builder()
                .type(ReportType.POLITICAL)
                .skip(1)
                .limit(1)
                .reason("_REASON!")
                .build();

        PostReportInfoList postReportInfoList = reportService.getPostReportInfoList(systemAdmin.getId(), board.getId(), postReportInfoSearch);
        Assertions.assertEquals(1, postReportInfoList.getSkip());
        Assertions.assertEquals(1, postReportInfoList.getLimit());
        Assertions.assertEquals(1, postReportInfoList.getTotal());
        Assertions.assertEquals(0, postReportInfoList.getPostReportInfoList().size());
    }
}

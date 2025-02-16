package com.illdangag.iricom.server.service.report.post;

import com.illdangag.iricom.server.data.entity.type.PostState;
import com.illdangag.iricom.server.data.entity.type.PostType;
import com.illdangag.iricom.server.data.entity.type.ReportType;
import com.illdangag.iricom.server.data.request.PostReportInfoSearch;
import com.illdangag.iricom.server.data.response.PostReportInfoList;
import com.illdangag.iricom.server.service.ReportService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestPostInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestPostReportInfo;
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
public class ReportServicePostSearchTest extends IricomTestSuite {
    @Autowired
    ReportService reportService;

    // 게시판
    private final TestBoardInfo testBoardInfo00 = TestBoardInfo.builder()
            .title("testBoardInfo00").isEnabled(true).adminList(Arrays.asList(allBoardAdmin)).build();
    // 게시물
    private final TestPostInfo testPostInfo00 = TestPostInfo.builder()
            .title("testPostInfo00").content("content").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(testBoardInfo00).build();
    private final TestPostInfo testPostInfo01 = TestPostInfo.builder()
            .title("testPostInfo01").content("content").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(testBoardInfo00).build();
    private final TestPostInfo testPostInfo02 = TestPostInfo.builder()
            .title("testPostInfo02").content("content").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(testBoardInfo00).build();
    private final TestPostInfo testPostInfo03 = TestPostInfo.builder()
            .title("testPostInfo03").content("content").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(testBoardInfo00).build();
    // 게시물 신고
    private final TestPostReportInfo postReport00 = TestPostReportInfo.builder()
            .type(ReportType.HATE).reason("hate report").reportAccount(common00).post(testPostInfo00)
            .build();
    private final TestPostReportInfo postReport01 = TestPostReportInfo.builder()
            .type(ReportType.POLITICAL).reason("political report").reportAccount(common00).post(testPostInfo01)
            .build();
    private final TestPostReportInfo postReport02 = TestPostReportInfo.builder()
            .type(ReportType.PORNOGRAPHY).reason("pornography report").reportAccount(common00).post(testPostInfo02)
            .build();
    private final TestPostReportInfo postReport03 = TestPostReportInfo.builder()
            .type(ReportType.ETC).reason("etc report").reportAccount(common00).post(testPostInfo03)
            .build();

    @Autowired
    public ReportServicePostSearchTest(ApplicationContext context) {
        super(context);

        addTestBoardInfo(testBoardInfo00);
        addTestPostInfo(testPostInfo00, testPostInfo01, testPostInfo02, testPostInfo03);
        addTestPostReportInfo(postReport00, postReport01, postReport02, postReport03);

        init();
    }

    @Test
    @DisplayName("기본 조회")
    public void searchPostReportInfoList() throws Exception {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        List<TestPostInfo> postList = this.setRandomPost(board, account, 8);
        // 게시물 신고
        for (TestPostInfo post : postList) {
            this.setRandomPostReport(post, account);
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
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        this.setRandomPost(board, account, 1);
        List<TestPostInfo> hatePostList = this.setRandomPost(board, account, 2);
        List<TestPostInfo> politicalPostList = this.setRandomPost(board, account, 3);
        List<TestPostInfo> pornographyPostList = this.setRandomPost(board, account, 4);
        List<TestPostInfo> etcPostList = this.setRandomPost(board, account, 5);
        // 게시물 신고
        for (TestPostInfo post : hatePostList) {
            this.setRandomPostReport(post, account, ReportType.HATE);
        }
        for (TestPostInfo post : politicalPostList) {
            this.setRandomPostReport(post, account, ReportType.POLITICAL);
        }
        for (TestPostInfo post : pornographyPostList) {
            this.setRandomPostReport(post, account, ReportType.PORNOGRAPHY);
        }
        for (TestPostInfo post : etcPostList) {
            this.setRandomPostReport(post, account, ReportType.ETC);
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
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        this.setRandomPost(board, account, 10);
        List<TestPostInfo> postList = this.setRandomPost(board, account, 4);
        // 게시물 신고
        for (TestPostInfo post : postList) {
            this.setRandomPostReport(post, account);
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
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        this.setRandomPost(board, account, 8);
        List<TestPostInfo> postList = this.setRandomPost(board, account, 4);
        // 게시물 신고
        for (TestPostInfo post : postList) {
            this.setRandomPostReport(post, account);
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
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        this.setRandomPost(board, account, 2);
        List<TestPostInfo> postList = this.setRandomPost(board, account, 4);
        // 게시물 신고
        for (TestPostInfo post : postList) {
            this.setRandomPostReport(post, account);
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
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        List<TestPostInfo> postList = this.setRandomPost(board, account, 4);
        TestPostInfo post00 = this.setRandomPost(board, account);
        TestPostInfo post01 = this.setRandomPost(board, account);
        TestPostInfo post02 = this.setRandomPost(board, account);
        TestPostInfo post03 = this.setRandomPost(board, account);
        // 게시물 신고
        for (TestPostInfo post : postList) {
            this.setRandomPostReport(post, account);
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
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        List<TestPostInfo> postList = this.setRandomPost(board, account, 4);
        TestPostInfo post00 = this.setRandomPost(board, account);
        TestPostInfo post01 = this.setRandomPost(board, account);
        TestPostInfo post02 = this.setRandomPost(board, account);
        TestPostInfo post03 = this.setRandomPost(board, account);
        // 게시물 신고
        for (TestPostInfo post : postList) {
            this.setRandomPostReport(post, account);
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

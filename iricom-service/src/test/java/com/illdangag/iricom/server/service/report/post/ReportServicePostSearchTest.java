package com.illdangag.iricom.server.service.report.post;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.data.request.PostReportInfoSearch;
import com.illdangag.iricom.server.data.response.PostReportInfoList;
import com.illdangag.iricom.server.service.ReportService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestPostInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestPostReportInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;

@DisplayName("service: 신고 - 게시물 목록 조회")
@Slf4j
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
        Account systemAdminAccount = getAccount(systemAdmin);
        Board board = getBoard(testBoardInfo00);
        PostReportInfoSearch postReportInfoSearch = PostReportInfoSearch.builder()
                .reason("")
                .build();

        PostReportInfoList postReportInfoList = reportService.getPostReportInfoList(systemAdminAccount, board, postReportInfoSearch);
        Assertions.assertEquals(0, postReportInfoList.getSkip());
        Assertions.assertEquals(20, postReportInfoList.getLimit());
        Assertions.assertEquals(4, postReportInfoList.getTotal());
    }

    @Test
    @DisplayName("종류")
    public void searchPostReportInfoListByType() throws Exception {
        Account systemAdminAccount = getAccount(systemAdmin);
        Board board = getBoard(testBoardInfo00);
        PostReportInfoSearch postReportInfoSearch = PostReportInfoSearch.builder()
                .type(ReportType.HATE)
                .reason("")
                .build();

        PostReportInfoList postReportInfoList = reportService.getPostReportInfoList(systemAdminAccount, board, postReportInfoSearch);
        Assertions.assertEquals(0, postReportInfoList.getSkip());
        Assertions.assertEquals(20, postReportInfoList.getLimit());
        Assertions.assertEquals(1, postReportInfoList.getTotal());
    }

    @Test
    @DisplayName("skip")
    public void searchPostReportInfoListSkip() throws Exception {
        Account systemAdminAccount = getAccount(systemAdmin);
        Board board = getBoard(testBoardInfo00);
        PostReportInfoSearch postReportInfoSearch = PostReportInfoSearch.builder()
                .skip(2)
                .build();

        PostReportInfoList postReportInfoList = reportService.getPostReportInfoList(systemAdminAccount, board, postReportInfoSearch);
        Assertions.assertEquals(2, postReportInfoList.getSkip());
        Assertions.assertEquals(20, postReportInfoList.getLimit());
        Assertions.assertEquals(4, postReportInfoList.getTotal());
        Assertions.assertEquals(2, postReportInfoList.getPostReportInfoList().size());
    }

    @Test
    @DisplayName("limit")
    public void searchPostReportInfoListLimit() throws Exception {
        Account systemAdminAccount = getAccount(systemAdmin);
        Board board = getBoard(testBoardInfo00);
        PostReportInfoSearch postReportInfoSearch = PostReportInfoSearch.builder()
                .limit(3)
                .build();

        PostReportInfoList postReportInfoList = reportService.getPostReportInfoList(systemAdminAccount, board, postReportInfoSearch);
        Assertions.assertEquals(0, postReportInfoList.getSkip());
        Assertions.assertEquals(3, postReportInfoList.getLimit());
        Assertions.assertEquals(4, postReportInfoList.getTotal());
        Assertions.assertEquals(3, postReportInfoList.getPostReportInfoList().size());
    }

    @Test
    @DisplayName("skip, limit")
    public void searchPostReportInfoListSkipLimit() throws Exception {
        Account systemAdminAccount = getAccount(systemAdmin);
        Board board = getBoard(testBoardInfo00);
        PostReportInfoSearch postReportInfoSearch = PostReportInfoSearch.builder()
                .skip(1)
                .limit(2)
                .build();

        PostReportInfoList postReportInfoList = reportService.getPostReportInfoList(systemAdminAccount, board, postReportInfoSearch);
        Assertions.assertEquals(1, postReportInfoList.getSkip());
        Assertions.assertEquals(2, postReportInfoList.getLimit());
        Assertions.assertEquals(4, postReportInfoList.getTotal());
        Assertions.assertEquals(2, postReportInfoList.getPostReportInfoList().size());
    }

    @Test
    @DisplayName("reason")
    public void searchPostReportInfoListByReason() throws Exception {
        Account systemAdminAccount = getAccount(systemAdmin);
        Board board = getBoard(testBoardInfo00);
        PostReportInfoSearch postReportInfoSearch = PostReportInfoSearch.builder()
                .reason("political")
                .build();

        PostReportInfoList postReportInfoList = reportService.getPostReportInfoList(systemAdminAccount, board, postReportInfoSearch);
        Assertions.assertEquals(0, postReportInfoList.getSkip());
        Assertions.assertEquals(20, postReportInfoList.getLimit());
        Assertions.assertEquals(1, postReportInfoList.getTotal());
        Assertions.assertEquals(1, postReportInfoList.getPostReportInfoList().size());
    }

    @Test
    @DisplayName("complex")
    public void searchPostReportInfoListByComplex() throws Exception {
        Account systemAdminAccount = getAccount(systemAdmin);
        Board board = getBoard(testBoardInfo00);
        PostReportInfoSearch postReportInfoSearch = PostReportInfoSearch.builder()
                .type(ReportType.HATE)
                .skip(1)
                .limit(1)
                .reason("hate")
                .build();

        PostReportInfoList postReportInfoList = reportService.getPostReportInfoList(systemAdminAccount, board, postReportInfoSearch);
        Assertions.assertEquals(1, postReportInfoList.getSkip());
        Assertions.assertEquals(1, postReportInfoList.getLimit());
        Assertions.assertEquals(1, postReportInfoList.getTotal());
        Assertions.assertEquals(0, postReportInfoList.getPostReportInfoList().size());
    }
}

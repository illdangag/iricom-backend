package com.illdangag.iricom.server.service.report.comment;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.type.PostState;
import com.illdangag.iricom.server.data.entity.type.PostType;
import com.illdangag.iricom.server.data.entity.type.ReportType;
import com.illdangag.iricom.server.data.request.CommentReportInfoSearch;
import com.illdangag.iricom.server.data.response.CommentReportInfoList;
import com.illdangag.iricom.server.service.ReportService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestCommentInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestCommentReportInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestPostInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;

@DisplayName("service: 신고 - 댓글 목록 조회")
@Slf4j
public class ReportServiceCommentSearchTest extends IricomTestSuite {
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
    // 댓글
    private final TestCommentInfo testCommentInfo00 = TestCommentInfo.builder()
            .content("testCommentInfo00").creator(common00).post(testPostInfo00)
            .build();
    private final TestCommentInfo testCommentInfo01 = TestCommentInfo.builder()
            .content("testCommentInfo01").creator(common00).post(testPostInfo00)
            .build();
    private final TestCommentInfo testCommentInfo02 = TestCommentInfo.builder()
            .content("testCommentInfo02").creator(common00).post(testPostInfo01)
            .build();
    // 댓글 신고
    private final TestCommentReportInfo testCommentReportInfo00 = TestCommentReportInfo.builder()
            .type(ReportType.HATE).reason("hate report").reportAccount(common00).comment(testCommentInfo00)
            .build();
    private final TestCommentReportInfo testCommentReportInfo01 = TestCommentReportInfo.builder()
            .type(ReportType.POLITICAL).reason("political report").reportAccount(common01).comment(testCommentInfo00)
            .build();
    private final TestCommentReportInfo testCommentReportInfo02 = TestCommentReportInfo.builder()
            .type(ReportType.PORNOGRAPHY).reason("pornography report").reportAccount(common00).comment(testCommentInfo01)
            .build();
    private final TestCommentReportInfo testCommentReportInfo03 = TestCommentReportInfo.builder()
            .type(ReportType.ETC).reason("etc report").reportAccount(common01).comment(testCommentInfo01)
            .build();
    private final TestCommentReportInfo testCommentReportInfo04 = TestCommentReportInfo.builder()
            .type(ReportType.HATE).reason("hate report").reportAccount(common00).comment(testCommentInfo02)
            .build();
    private final TestCommentReportInfo testCommentReportInfo05 = TestCommentReportInfo.builder()
            .type(ReportType.POLITICAL).reason("political report").reportAccount(common01).comment(testCommentInfo02)
            .build();
    private final TestCommentReportInfo testCommentReportInfo06 = TestCommentReportInfo.builder()
            .type(ReportType.PORNOGRAPHY).reason("pornography report").reportAccount(common02).comment(testCommentInfo02)
            .build();
    private final TestCommentReportInfo testCommentReportInfo07 = TestCommentReportInfo.builder()
            .type(ReportType.ETC).reason("etc report").reportAccount(common03).comment(testCommentInfo02)
            .build();


    @Autowired
    public ReportServiceCommentSearchTest(ApplicationContext context) {
        super(context);

        addTestBoardInfo(testBoardInfo00);
        addTestPostInfo(testPostInfo00, testPostInfo01);
        addTestCommentInfo(testCommentInfo00, testCommentInfo01, testCommentInfo02);
        addTestCommentReportInfo(testCommentReportInfo00, testCommentReportInfo01, testCommentReportInfo02, testCommentReportInfo03,
                testCommentReportInfo04, testCommentReportInfo05, testCommentReportInfo06, testCommentReportInfo07);
        init();
    }

    @Test
    @DisplayName("게시판 기준 기본 조회")
    public void getBoardSearch() throws Exception {
        Account account = getAccount(systemAdmin);
        String boardId = getBoardId(testCommentReportInfo00.getComment().getPost().getBoard());

        CommentReportInfoSearch commentReportInfoSearch = CommentReportInfoSearch.builder().build();
        CommentReportInfoList commentReportInfoList = reportService.getCommentReportInfoList(account, boardId, commentReportInfoSearch);
        Assertions.assertEquals(0, commentReportInfoList.getSkip());
        Assertions.assertEquals(20, commentReportInfoList.getLimit());
        Assertions.assertEquals(8, commentReportInfoList.getTotal());
    }

    @Test
    @DisplayName("게시물 기준 기본 조회")
    public void getPostSearch() throws Exception {
        Account account = getAccount(allBoardAdmin);
        String postId = getPostId(testCommentReportInfo00.getComment().getPost());
        String boardId = getBoardId(testCommentReportInfo00.getComment().getPost().getBoard());

        CommentReportInfoSearch commentReportInfoSearch = CommentReportInfoSearch.builder().build();
        CommentReportInfoList commentReportInfoList = reportService.getCommentReportInfoList(account, boardId, postId, commentReportInfoSearch);
        Assertions.assertEquals(0, commentReportInfoList.getSkip());
        Assertions.assertEquals(20, commentReportInfoList.getLimit());
        Assertions.assertEquals(4, commentReportInfoList.getTotal());
    }

    @Test
    @DisplayName("댓글 기준 기본 조회")
    public void getCommentSearch() throws Exception {
        Account account = getAccount(allBoardAdmin);
        String commentId = getCommentId(testCommentReportInfo02.getComment());
        String postId = getPostId(testCommentReportInfo02.getComment().getPost());
        String boardId = getBoardId(testCommentReportInfo02.getComment().getPost().getBoard());

        CommentReportInfoSearch commentReportInfoSearch = CommentReportInfoSearch.builder().build();
        CommentReportInfoList commentReportInfoList = reportService.getCommentReportInfoList(account, boardId, postId, commentId, commentReportInfoSearch);
        Assertions.assertEquals(0, commentReportInfoList.getSkip());
        Assertions.assertEquals(20, commentReportInfoList.getLimit());
        Assertions.assertEquals(2, commentReportInfoList.getTotal());
    }
}

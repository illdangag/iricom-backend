package com.illdangag.iricom.server.service.report.post;

import com.illdangag.iricom.server.data.entity.type.PostState;
import com.illdangag.iricom.server.data.entity.type.PostType;
import com.illdangag.iricom.server.data.entity.type.ReportType;
import com.illdangag.iricom.server.data.request.PostReportInfoCreate;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.service.ReportService;
import com.illdangag.iricom.server.test.IricomTestSuite;
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

    // 게시판
    private final TestBoardInfo testBoardInfo00 = TestBoardInfo.builder()
            .title("testBoardInfo00").isEnabled(true).adminList(Arrays.asList(allBoardAdmin)).build();
    private final TestBoardInfo testBoardInfo01 = TestBoardInfo.builder()
            .title("testBoardInfo01").isEnabled(true).adminList(Arrays.asList(allBoardAdmin)).build();
    // 게시물
    private final TestPostInfo testPostInfo00 = TestPostInfo.builder()
            .title("testPostInfo00").content("report contents").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(testBoardInfo00).build();
    private final TestPostInfo testPostInfo01 = TestPostInfo.builder()
            .title("testPostInfo01").content("report contents").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(testBoardInfo00).build();

    @Autowired
    public ReportServicePostReportTest(ApplicationContext context) {
        super(context);

        addTestBoardInfo(testBoardInfo00, testBoardInfo01);
        addTestPostInfo(testPostInfo00, testPostInfo01);

        init();
    }

    @Test
    @DisplayName("게시물 신고")
    public void reportPost() throws Exception {
        String accountId = getAccountId(common01);
        String boardId = getBoardId(testPostInfo00.getBoard());
        String postId = getPostId(testPostInfo00);

        PostReportInfoCreate postReportInfoCreate = PostReportInfoCreate.builder()
                .type(ReportType.ETC)
                .reason("report test")
                .build();

        reportService.reportPost(accountId, boardId, postId, postReportInfoCreate);
    }

    @Test
    @DisplayName("중복 게시물 신고")
    public void duplicateReportPost() throws Exception {
        String accountId = getAccountId(common00);
        String boardId = getBoardId(testPostInfo01.getBoard());
        String postId = getPostId(testPostInfo01);

        PostReportInfoCreate postReportInfoCreate = PostReportInfoCreate.builder()
                .type(ReportType.ETC)
                .reason("report test")
                .build();

        // 첫번째 신고
        reportService.reportPost(accountId, boardId, postId, postReportInfoCreate);

        // 두번째 신고
        Assertions.assertThrows(IricomException.class, () -> {
            reportService.reportPost(accountId, boardId, postId, postReportInfoCreate);
        });
    }

    @Test
    @DisplayName("게시물의 게시판 불일치")
    public void notMatchPostAndBoard() throws Exception {
        String accountId = getAccountId(common00);
        String boardId = getBoardId(testBoardInfo01);
        String postId = getPostId(testPostInfo01);

        PostReportInfoCreate postReportInfoCreate = PostReportInfoCreate.builder()
                .type(ReportType.ETC)
                .reason("report test")
                .build();

        Assertions.assertThrows(IricomException.class, () -> {
            reportService.reportPost(accountId, boardId, postId, postReportInfoCreate);
        });
    }

    @Test
    @DisplayName("존재하지 않는 게시물")
    public void notExistPost() throws Exception {
        String accountId = getAccountId(common00);
        String boardId = getBoardId(testBoardInfo00);

        PostReportInfoCreate postReportInfoCreate = PostReportInfoCreate.builder()
                .type(ReportType.ETC)
                .reason("report test")
                .build();

        Assertions.assertThrows(IricomException.class, () -> {
            reportService.reportPost(accountId, boardId, "NOT_EXIST_POST", postReportInfoCreate);
        });
    }

    @Test
    @DisplayName("존재하지 않는 게시판")
    public void notExistBoard() throws Exception {
        String accountId = getAccountId(common00);
        String postId = getPostId(testPostInfo01);

        PostReportInfoCreate postReportInfoCreate = PostReportInfoCreate.builder()
                .type(ReportType.ETC)
                .reason("report test")
                .build();

        Assertions.assertThrows(IricomException.class, () -> {
            reportService.reportPost(accountId, "NOT_EXIST_BOARD", postId, postReportInfoCreate);
        });
    }
}

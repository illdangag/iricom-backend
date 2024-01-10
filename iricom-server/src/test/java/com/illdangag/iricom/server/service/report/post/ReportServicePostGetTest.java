package com.illdangag.iricom.server.service.report.post;

import com.illdangag.iricom.server.data.entity.type.PostState;
import com.illdangag.iricom.server.data.entity.type.PostType;
import com.illdangag.iricom.server.data.entity.type.ReportType;
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

import javax.transaction.Transactional;
import java.util.Collections;

@DisplayName("service: 신고 - 게시물 정보 조회")
@Transactional
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
        String systemAdminAccountId = getAccountId(systemAdmin);
        String postReportId = getPostReportId(postReport00);
        String postId = getPostId(postReport00.getPost());
        String boardId = getBoardId(postReport00.getPost().getBoard());

        PostReportInfo postReportInfo = reportService.getPostReportInfo(systemAdminAccountId, boardId, postId, postReportId);
        Assertions.assertEquals(postReportId, postReportInfo.getId());
    }

    @Test
    @DisplayName("올바르지 않은 게시판")
    public void invalidBoard() throws Exception {
        String systemAdminAccountId = getAccountId(systemAdmin);
        String postReportId = getPostReportId(postReport00);
        String postId = getPostId(postReport00.getPost());
        String boardId = getBoardId(enableBoard01);

        Assertions.assertThrows(IricomException.class, () -> {
            reportService.getPostReportInfo(systemAdminAccountId, boardId, postId, postReportId);
        });
    }

    @Test
    @DisplayName("존재하지 않는 게시판")
    public void notExistBoard() throws Exception {
        String systemAdminAccountId = getAccountId(systemAdmin);
        String postReportId = getPostReportId(postReport00);
        String postId = getPostId(postReport00.getPost());
        String boardId = "NOT_EXIST";

        Assertions.assertThrows(IricomException.class, () -> {
            reportService.getPostReportInfo(systemAdminAccountId, boardId, postId, postReportId);
        });
    }

    @Test
    @DisplayName("올바르지 않은 게시물")
    public void invalidPost() throws Exception {
        String systemAdminAccountId = getAccountId(systemAdmin);
        String postReportId = getPostReportId(postReport00);
        String postId = getPostId(postReport00.getPost());
        String boardId = getBoardId(postReport00.getPost().getBoard());
        String invalidPostId = getPostId(post00);

        Assertions.assertThrows(IricomException.class, () -> {
            reportService.getPostReportInfo(systemAdminAccountId, boardId, invalidPostId, postReportId);
        });
    }

    @Test
    @DisplayName("존재하지 않은 게시물")
    public void notExistPost() throws Exception {
        String systemAdminAccountId = getAccountId(systemAdmin);
        String postReportId = getPostReportId(postReport00);
        String boardId = getBoardId(postReport00.getPost().getBoard());
        String postId = "NOT_EXIST";

        Assertions.assertThrows(IricomException.class, () -> {
            reportService.getPostReportInfo(systemAdminAccountId, boardId, postId, postReportId);
        });
    }

    @Test
    @DisplayName("존재하지 않은 신고")
    public void notExistReport() throws Exception {
        String systemAdminAccountId = getAccountId(systemAdmin);
        String postId = getPostId(postReport00.getPost());
        String boardId = getBoardId(postReport00.getPost().getBoard());
        String postReportId = "NOT_EXIST";

        Assertions.assertThrows(IricomException.class, () -> {
            reportService.getPostReportInfo(systemAdminAccountId, boardId, postId, postReportId);
        });
    }
}

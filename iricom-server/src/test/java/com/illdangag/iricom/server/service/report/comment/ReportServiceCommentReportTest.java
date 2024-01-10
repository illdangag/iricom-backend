package com.illdangag.iricom.server.service.report.comment;

import com.illdangag.iricom.server.data.entity.type.PostState;
import com.illdangag.iricom.server.data.entity.type.PostType;
import com.illdangag.iricom.server.data.entity.type.ReportType;
import com.illdangag.iricom.server.data.request.CommentReportInfoCreate;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.service.ReportService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestCommentInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestPostInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.transaction.Transactional;
import java.util.Arrays;

@DisplayName("service: 신고 - 댓글 신고")
@Slf4j
@Transactional
public class ReportServiceCommentReportTest extends IricomTestSuite {
    @Autowired
    private ReportService reportService;

    // 게시판
    private final TestBoardInfo enableBoard00 = TestBoardInfo.builder()
            .title("enable").isEnabled(true).adminList(Arrays.asList(allBoardAdmin)).build();
    private final TestBoardInfo enableBoard01 = TestBoardInfo.builder()
            .title("enable").isEnabled(true).adminList(Arrays.asList(allBoardAdmin)).build();
    // 게시물
    private final TestPostInfo post00 = TestPostInfo.builder()
            .title("post00").content("report contents").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(enableBoard00).build();
    private final TestPostInfo post01 = TestPostInfo.builder()
            .title("post01").content("report contents").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(enableBoard00).build();
    // 댓글
    private final TestCommentInfo comment00 = TestCommentInfo.builder()
            .content("comment00").creator(common00).post(post00)
            .build();
    private final TestCommentInfo comment01 = TestCommentInfo.builder()
            .content("comment01").creator(common00).post(post00)
            .build();

    @Autowired
    public ReportServiceCommentReportTest(ApplicationContext context) {
        super(context);

        addTestBoardInfo(enableBoard00, enableBoard01);
        addTestPostInfo(post00, post01);
        addTestCommentInfo(comment00, comment01);

        init();
    }

    @Test
    @DisplayName("댓글 신고")
    public void reportComment() throws Exception {
        String accountId = getAccountId(common00);
        String commentId = getCommentId(comment00);
        String postId = getPostId(comment00.getPost());
        String boardId = getBoardId(comment00.getPost().getBoard());

        CommentReportInfoCreate commentReportInfoCreate = CommentReportInfoCreate.builder()
                .type(ReportType.ETC)
                .reason("report test")
                .build();

        reportService.reportComment(accountId, boardId, postId, commentId, commentReportInfoCreate);
    }

    @Test
    @DisplayName("중복 댓글 신고")
    public void duplicationReportComment() throws Exception {
        String accountId = getAccountId(common00);
        String commentId = getCommentId(comment01);
        String postId = getPostId(comment01.getPost());
        String boardId = getBoardId(comment01.getPost().getBoard());

        CommentReportInfoCreate commentReportInfoCreate = CommentReportInfoCreate.builder()
                .type(ReportType.ETC)
                .reason("report test")
                .build();

        reportService.reportComment(accountId, boardId, postId, commentId, commentReportInfoCreate);

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            reportService.reportComment(accountId, boardId, postId, commentId, commentReportInfoCreate);
        });

        Assertions.assertEquals("06010001", iricomException.getErrorCode());
        Assertions.assertEquals("Already report comment.", iricomException.getMessage());
    }

    @Test
    @DisplayName("댓글의 게시물 불일치")
    public void notMatchPost() throws Exception {
        String accountId = getAccountId(common00);
        String commentId = getCommentId(comment01);
        String postId = getPostId(comment01.getPost());
        String boardId = getBoardId(comment01.getPost().getBoard());
        String invalidPostId = getPostId(post01);

        CommentReportInfoCreate commentReportInfoCreate = CommentReportInfoCreate.builder()
                .type(ReportType.ETC)
                .reason("report test")
                .build();

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            reportService.reportComment(accountId, boardId, invalidPostId, commentId, commentReportInfoCreate);
        });

        Assertions.assertEquals("05000000", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist comment.", iricomException.getMessage());
    }

    @Test
    @DisplayName("댓글의 게시물의 게시판 불일치")
    public void notMatchBoard() throws Exception {
        String accountId = getAccountId(common00);
        String commentId = getCommentId(comment01);
        String postId = getPostId(comment01.getPost());
        String invalidBoardId = getBoardId(enableBoard01);

        CommentReportInfoCreate commentReportInfoCreate = CommentReportInfoCreate.builder()
                .type(ReportType.ETC)
                .reason("report test")
                .build();

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            reportService.reportComment(accountId, invalidBoardId, postId, commentId, commentReportInfoCreate);
        });

        Assertions.assertEquals("04000000", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist post.", iricomException.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 댓글")
    public void notExistComment() throws Exception {
        String accountId = getAccountId(common00);
        String postId = getPostId(comment01.getPost());
        String boardId = getBoardId(comment01.getPost().getBoard());
        String commentId = "NOT_EXIST_COMMENT";

        CommentReportInfoCreate commentReportInfoCreate = CommentReportInfoCreate.builder()
                .type(ReportType.ETC)
                .reason("report test")
                .build();

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            reportService.reportComment(accountId, boardId, postId, commentId, commentReportInfoCreate);
        });

        Assertions.assertEquals("05000000", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist comment.", iricomException.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 게시물")
    public void notExistPost() throws Exception {
        String accountId = getAccountId(common00);
        String commentId = getCommentId(comment01);
        String boardId = getBoardId(comment01.getPost().getBoard());
        String postId = "NOT_EXIST_POST";

        CommentReportInfoCreate commentReportInfoCreate = CommentReportInfoCreate.builder()
                .type(ReportType.ETC)
                .reason("report test")
                .build();

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            reportService.reportComment(accountId, boardId, postId, commentId, commentReportInfoCreate);
        });

        Assertions.assertEquals("04000000", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist post.", iricomException.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 게시판")
    public void notExistBoard() throws Exception {
        String accountId = getAccountId(common00);
        String commentId = getCommentId(comment01);
        String postId = getPostId(comment01.getPost());
        String boardId = "NOT_EXIST_BOARD";

        CommentReportInfoCreate commentReportInfoCreate = CommentReportInfoCreate.builder()
                .type(ReportType.ETC)
                .reason("report test")
                .build();

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            reportService.reportComment(accountId, boardId, postId, commentId, commentReportInfoCreate);
        });

        Assertions.assertEquals("03000000", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist board.", iricomException.getMessage());
    }
}

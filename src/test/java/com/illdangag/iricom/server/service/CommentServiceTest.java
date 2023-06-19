package com.illdangag.iricom.server.service;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.data.request.CommentInfoCreate;
import com.illdangag.iricom.server.data.response.CommentInfo;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.TestBoardInfo;
import com.illdangag.iricom.server.test.data.TestCommentInfo;
import com.illdangag.iricom.server.test.data.TestCommentReportInfo;
import com.illdangag.iricom.server.test.data.TestPostInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
public class CommentServiceTest extends IricomTestSuite {
    @Autowired
    private CommentService commentService;

    protected static final TestBoardInfo commentTestBoardInfo = TestBoardInfo.builder()
            .title("commentTestBoardInfo").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();

    protected static final TestPostInfo commentTestPostInfo00 = TestPostInfo.builder()
            .title("commentTestPostInfo").content("commentTestPostInfo").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(commentTestBoardInfo)
            .build();

    protected static final TestCommentInfo commentInfo00 = TestCommentInfo.builder()
            .content("commentInfo00").creator(allBoardAdmin).post(commentTestPostInfo00)
            .build();

    protected static final TestCommentInfo reportedCommentInfo00 = TestCommentInfo.builder()
            .content("reportedCommentInfo00").creator(allBoardAdmin).post(commentTestPostInfo00)
            .build();

    protected static final TestCommentReportInfo commentReport00 = TestCommentReportInfo.builder()
            .type(ReportType.HATE).reason("test comment report").reportAccount(common00).comment(reportedCommentInfo00)
            .build();
    protected static final TestCommentReportInfo commentReport01 = TestCommentReportInfo.builder()
            .type(ReportType.PORNOGRAPHY).reason("test comment report").reportAccount(common01).comment(reportedCommentInfo00)
            .build();
    protected static final TestCommentReportInfo commentReport02 = TestCommentReportInfo.builder()
            .type(ReportType.POLITICAL).reason("test comment report").reportAccount(common02).comment(reportedCommentInfo00)
            .build();
    protected static final TestCommentReportInfo commentReport03 = TestCommentReportInfo.builder()
            .type(ReportType.ETC).reason("test comment report").reportAccount(common03).comment(reportedCommentInfo00)
            .build();
    protected static final TestCommentReportInfo commentReport04 = TestCommentReportInfo.builder()
            .type(ReportType.HATE).reason("test comment report").reportAccount(common04).comment(reportedCommentInfo00)
            .build();
    protected static final TestCommentReportInfo commentReport05 = TestCommentReportInfo.builder()
            .type(ReportType.PORNOGRAPHY).reason("test comment report").reportAccount(common05).comment(reportedCommentInfo00)
            .build();
    protected static final TestCommentReportInfo commentReport06 = TestCommentReportInfo.builder()
            .type(ReportType.POLITICAL).reason("test comment report").reportAccount(common06).comment(reportedCommentInfo00)
            .build();
    protected static final TestCommentReportInfo commentReport07 = TestCommentReportInfo.builder()
            .type(ReportType.ETC).reason("test comment report").reportAccount(common07).comment(reportedCommentInfo00)
            .build();
    protected static final TestCommentReportInfo commentReport08 = TestCommentReportInfo.builder()
            .type(ReportType.HATE).reason("test comment report").reportAccount(common08).comment(reportedCommentInfo00)
            .build();
    protected static final TestCommentReportInfo commentReport09 = TestCommentReportInfo.builder()
            .type(ReportType.PORNOGRAPHY).reason("test comment report").reportAccount(common09).comment(reportedCommentInfo00)
            .build();

    @Autowired
    public CommentServiceTest(ApplicationContext context) {
        super(context);

        List<TestBoardInfo> testBoardInfoList = Arrays.asList(commentTestBoardInfo);
        List<TestPostInfo> testPostInfoList = Arrays.asList(commentTestPostInfo00);
        List<TestCommentInfo> testCommentInfoList = Arrays.asList(commentInfo00, reportedCommentInfo00);
        List<TestCommentReportInfo> testCommentReportInfoList = Arrays.asList(
                commentReport00, commentReport01, commentReport02, commentReport03, commentReport04,
                commentReport05, commentReport06, commentReport07, commentReport08, commentReport09);

        super.setBoard(testBoardInfoList);
        super.setPost(testPostInfoList);
        super.setComment(testCommentInfoList);
        super.setCommentReport(testCommentReportInfoList);
    }

    @Nested
    @DisplayName("생성")
    class Create {
        @Test
        @DisplayName("댓글 생성")
        public void createComment() throws Exception {
            Account account = getAccount(common00);
            Post post = getPost(commentTestPostInfo00);
            Board board = post.getBoard();

            CommentInfoCreate commentInfoCreate = CommentInfoCreate.builder()
                    .content("댓글 생성")
                    .build();
            CommentInfo commentInfo = commentService.createCommentInfo(account, board, post, commentInfoCreate);

            Assertions.assertEquals("댓글 생성", commentInfo.getContent());
            Assertions.assertNull(commentInfo.getReferenceCommentId());
            Assertions.assertFalse(commentInfo.getIsDeleted());
            Assertions.assertFalse(commentInfo.getIsReport());
        }

        @Test
        @DisplayName("대댓글 생성")
        public void createNestedComment() throws Exception {
            Account account = getAccount(common00);
            Post post = getPost(commentTestPostInfo00);
            Board board = post.getBoard();
            Comment referenceComment = getComment(commentInfo00);
            String referenceCommentId = String.valueOf(referenceComment.getId());

            CommentInfoCreate commentInfoCreate = CommentInfoCreate.builder()
                    .content("대댓글 생성").referenceCommentId(referenceCommentId)
                    .build();

            CommentInfo commentInfo = commentService.createCommentInfo(account, board, post, commentInfoCreate);
            Assertions.assertEquals("대댓글 생성", commentInfo.getContent());
            Assertions.assertEquals(referenceCommentId, commentInfo.getReferenceCommentId());
            Assertions.assertFalse(commentInfo.getIsDeleted());
            Assertions.assertFalse(commentInfo.getIsReport());
        }
    }

    @Nested
    @DisplayName("신고")
    class Report {
        @Test
        @DisplayName("신고된 댓글 조회")
        public void getReportedComment() throws Exception {
            Comment comment = getComment(reportedCommentInfo00);
            Post post = comment.getPost();
            Board board = post.getBoard();

            CommentInfo commentInfo = commentService.getComment(board, post, comment);
            Assertions.assertTrue(commentInfo.getIsReport());
            Assertions.assertNull(commentInfo.getContent());
        }
    }
}

package com.illdangag.iricom.server.service.comment;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.data.response.CommentInfo;
import com.illdangag.iricom.server.service.CommentService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestCommentInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestCommentReportInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestPostInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Nested
@DisplayName("service: 댓글 - 신고")
@Slf4j
public class CommentReportServiceTest extends IricomTestSuite {
    @Autowired
    private CommentService commentService;

    private static final TestBoardInfo commentTestBoardInfo = TestBoardInfo.builder()
            .title("commentTestBoardInfo").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();

    private static final TestPostInfo commentTestPostInfo00 = TestPostInfo.builder()
            .title("commentTestPostInfo").content("commentTestPostInfo").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(allBoardAdmin).board(commentTestBoardInfo)
            .build();

    private static final TestCommentInfo reportedCommentInfo00 = TestCommentInfo.builder()
            .content("reportedCommentInfo00").creator(allBoardAdmin).post(commentTestPostInfo00)
            .build();

    @Autowired
    public CommentReportServiceTest(ApplicationContext context) {
        super(context);

        List<TestBoardInfo> testBoardInfoList = Arrays.asList(commentTestBoardInfo);
        List<TestPostInfo> testPostInfoList = Arrays.asList(commentTestPostInfo00);
        List<TestCommentInfo> testCommentInfoList = Arrays.asList(reportedCommentInfo00);

        List<TestCommentReportInfo> testCommentReportInfoList = new ArrayList<>();
        testCommentReportInfoList.addAll(this.createTestCommentReportInfo(reportedCommentInfo00));

        super.setBoard(testBoardInfoList);
        super.setPost(testPostInfoList);
        super.setComment(testCommentInfoList);
        super.setCommentReport(testCommentReportInfoList);
        super.setDeletedComment(testCommentInfoList);
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

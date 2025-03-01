package com.illdangag.iricom.server.service.report.comment;

import com.illdangag.iricom.server.data.request.CommentReportInfoSearch;
import com.illdangag.iricom.server.data.response.CommentReportInfoList;
import com.illdangag.iricom.server.service.ReportService;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestAccountInfo;
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
import java.util.List;

@DisplayName("service: 신고 - 댓글 목록 조회")
@Slf4j
@Transactional
public class ReportServiceCommentSearchTest extends IricomTestSuite {
    @Autowired
    ReportService reportService;

    @Autowired
    public ReportServiceCommentSearchTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("게시판 기준 기본 조회")
    public void getBoardSearch() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        List<TestPostInfo> postList = setRandomPost(board, account, 3);
        // 댓글 생성
        for (TestPostInfo post : postList) {
            List<TestCommentInfo> commentList = setRandomComment(post, account, 10);
            // 댓글 신고
            for (TestCommentInfo comment : commentList) {
                setRandomCommentReport(comment, account);
            }
        }

        CommentReportInfoSearch commentReportInfoSearch = CommentReportInfoSearch.builder().build();
        CommentReportInfoList commentReportInfoList = reportService.getCommentReportInfoList(systemAdmin.getId(), board.getId(), commentReportInfoSearch);
        Assertions.assertEquals(0, commentReportInfoList.getSkip());
        Assertions.assertEquals(20, commentReportInfoList.getLimit());
        Assertions.assertEquals(30, commentReportInfoList.getTotal());
    }

    @Test
    @DisplayName("게시물 기준 기본 조회")
    public void getPostSearch() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        List<TestPostInfo> postList = setRandomPost(board, account, 3);
        // 댓글 생성
        for (TestPostInfo post : postList) {
            List<TestCommentInfo> commentList = setRandomComment(post, account, 10);
            // 댓글 신고
            for (TestCommentInfo comment : commentList) {
                setRandomCommentReport(comment, account);
            }
        }
        TestPostInfo post = postList.get(0);

        CommentReportInfoSearch commentReportInfoSearch = CommentReportInfoSearch.builder().build();
        CommentReportInfoList commentReportInfoList = reportService.getCommentReportInfoList(systemAdmin.getId(), board.getId(), post.getId(), commentReportInfoSearch);
        Assertions.assertEquals(0, commentReportInfoList.getSkip());
        Assertions.assertEquals(20, commentReportInfoList.getLimit());
        Assertions.assertEquals(10, commentReportInfoList.getTotal());
    }

    @Test
    @DisplayName("댓글 기준 기본 조회")
    public void getCommentSearch() throws Exception {
        // 계정 생성
        TestAccountInfo account = setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = setRandomBoard();
        // 게시물 생성
        List<TestPostInfo> postList = setRandomPost(board, account, 3);
        String postId = "";
        // 댓글 생성
        String commentId = "";
        for (TestPostInfo post : postList) {
            List<TestCommentInfo> commentList = setRandomComment(post, account, 10);
            // 댓글 신고
            for (TestCommentInfo comment : commentList) {
                setRandomCommentReport(comment, account);
                postId = post.getId();
                commentId = comment.getId();
            }
        }

        CommentReportInfoSearch commentReportInfoSearch = CommentReportInfoSearch.builder().build();
        CommentReportInfoList commentReportInfoList = reportService.getCommentReportInfoList(systemAdmin.getId(), board.getId(), postId, commentId, commentReportInfoSearch);
        Assertions.assertEquals(0, commentReportInfoList.getSkip());
        Assertions.assertEquals(20, commentReportInfoList.getLimit());
        Assertions.assertEquals(1, commentReportInfoList.getTotal());
    }
}

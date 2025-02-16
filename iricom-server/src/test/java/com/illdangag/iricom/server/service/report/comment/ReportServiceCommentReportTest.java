package com.illdangag.iricom.server.service.report.comment;

import com.illdangag.iricom.server.data.entity.type.PostState;
import com.illdangag.iricom.server.data.entity.type.PostType;
import com.illdangag.iricom.server.data.entity.type.ReportType;
import com.illdangag.iricom.server.data.request.CommentReportInfoCreate;
import com.illdangag.iricom.server.exception.IricomException;
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
import java.util.Arrays;

@DisplayName("service: 신고 - 댓글 신고")
@Slf4j
@Transactional
public class ReportServiceCommentReportTest extends IricomTestSuite {
    @Autowired
    private ReportService reportService;

    @Autowired
    public ReportServiceCommentReportTest(ApplicationContext context) {
        super(context);
    }

    @Test
    @DisplayName("댓글 신고")
    public void reportComment() throws Exception {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        TestPostInfo post = this.setRandomPost(board, account);
        // 댓글 생성
        TestCommentInfo comment = this.setRandomComment(post, account);

        CommentReportInfoCreate commentReportInfoCreate = CommentReportInfoCreate.builder()
                .type(ReportType.ETC)
                .reason("report test")
                .build();

        reportService.reportComment(account.getId(), board.getId(), post.getId(), comment.getId(), commentReportInfoCreate);
    }

    @Test
    @DisplayName("중복 댓글 신고")
    public void duplicationReportComment() throws Exception {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        TestPostInfo post = this.setRandomPost(board, account);
        // 댓글 생성
        TestCommentInfo comment = this.setRandomComment(post, account);

        CommentReportInfoCreate commentReportInfoCreate = CommentReportInfoCreate.builder()
                .type(ReportType.ETC)
                .reason("report test")
                .build();

        reportService.reportComment(account.getId(), board.getId(), post.getId(), comment.getId(), commentReportInfoCreate);

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            reportService.reportComment(account.getId(), board.getId(), post.getId(), comment.getId(), commentReportInfoCreate);
        });

        Assertions.assertEquals("06010001", iricomException.getErrorCode());
        Assertions.assertEquals("Already report comment.", iricomException.getMessage());
    }

    @Test
    @DisplayName("댓글의 게시물 불일치")
    public void notMatchPost() throws Exception {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        TestPostInfo post = this.setRandomPost(board, account);
        TestPostInfo otherPost = this.setRandomPost(board, account);
        // 댓글 생성
        TestCommentInfo comment = this.setRandomComment(post, account);

        CommentReportInfoCreate commentReportInfoCreate = CommentReportInfoCreate.builder()
                .type(ReportType.ETC)
                .reason("report test")
                .build();

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            reportService.reportComment(account.getId(), board.getId(), otherPost.getId(), comment.getId(), commentReportInfoCreate);
        });

        Assertions.assertEquals("05000000", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist comment.", iricomException.getMessage());
    }

    @Test
    @DisplayName("댓글의 게시물의 게시판 불일치")
    public void notMatchBoard() throws Exception {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        TestBoardInfo otherBoard = this.setRandomBoard();
        // 게시물 생성
        TestPostInfo post = this.setRandomPost(board, account);
        // 댓글 생성
        TestCommentInfo comment = this.setRandomComment(post, account);

        CommentReportInfoCreate commentReportInfoCreate = CommentReportInfoCreate.builder()
                .type(ReportType.ETC)
                .reason("report test")
                .build();

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            reportService.reportComment(account.getId(), otherBoard.getId(), post.getId(), comment.getId(), commentReportInfoCreate);
        });

        Assertions.assertEquals("04000000", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist post.", iricomException.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 댓글")
    public void notExistComment() throws Exception {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        TestPostInfo post = this.setRandomPost(board, account);
        // 댓글 생성
        this.setRandomComment(post, account);
        String commentId = "NOT_EXIST_COMMENT";

        CommentReportInfoCreate commentReportInfoCreate = CommentReportInfoCreate.builder()
                .type(ReportType.ETC)
                .reason("report test")
                .build();

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            reportService.reportComment(account.getId(), board.getId(), post.getId(), commentId, commentReportInfoCreate);
        });

        Assertions.assertEquals("05000000", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist comment.", iricomException.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 게시물")
    public void notExistPost() throws Exception {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        TestPostInfo post = this.setRandomPost(board, account);
        // 댓글 생성
        TestCommentInfo comment = this.setRandomComment(post, account);
        String postId = "NOT_EXIST_POST";

        CommentReportInfoCreate commentReportInfoCreate = CommentReportInfoCreate.builder()
                .type(ReportType.ETC)
                .reason("report test")
                .build();

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            reportService.reportComment(account.getId(), board.getId(), postId, comment.getId(), commentReportInfoCreate);
        });

        Assertions.assertEquals("04000000", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist post.", iricomException.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 게시판")
    public void notExistBoard() throws Exception {
        // 계정 생성
        TestAccountInfo account = this.setRandomAccount();
        // 게시판 생성
        TestBoardInfo board = this.setRandomBoard();
        // 게시물 생성
        TestPostInfo post = this.setRandomPost(board, account);
        // 댓글 생성
        TestCommentInfo comment = this.setRandomComment(post, account);
        String boardId = "NOT_EXIST_BOARD";

        CommentReportInfoCreate commentReportInfoCreate = CommentReportInfoCreate.builder()
                .type(ReportType.ETC)
                .reason("report test")
                .build();

        IricomException iricomException = Assertions.assertThrows(IricomException.class, () -> {
            reportService.reportComment(account.getId(), boardId, post.getId(), comment.getId(), commentReportInfoCreate);
        });

        Assertions.assertEquals("03000000", iricomException.getErrorCode());
        Assertions.assertEquals("Not exist board.", iricomException.getMessage());
    }
}

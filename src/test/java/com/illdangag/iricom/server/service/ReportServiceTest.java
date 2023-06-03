package com.illdangag.iricom.server.service;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.data.request.CommentReportCreate;
import com.illdangag.iricom.server.data.request.PostReportCreate;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.test.IricomTestSuite;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

@Slf4j
public class ReportServiceTest extends IricomTestSuite {
    @Autowired
    ReportService reportService;

    @Autowired
    public ReportServiceTest(ApplicationContext context) {
        super(context);
    }

    @Nested
    class PostTest {
        @Test
        @DisplayName("게시물 신고")
        public void testCase00() throws Exception {
            Account account = getAccount(common01);
            Post post = getPost(reportPost00);
            Board board = post.getBoard();

            PostReportCreate postReportCreate = PostReportCreate.builder()
                    .boardId(String.valueOf(board.getId()))
                    .postId(String.valueOf(post.getId()))
                    .type(ReportType.ETC)
                    .reason("report test")
                    .build();

            reportService.reportPost(account, postReportCreate);
        }

        @Test
        @DisplayName("중복 게시물 신고")
        public void testCase01() throws Exception {
            Account account = getAccount(common00);
            Post post = getPost(reportPost01);
            Board board = post.getBoard();

            PostReportCreate postReportCreate = PostReportCreate.builder()
                    .boardId(String.valueOf(board.getId()))
                    .postId(String.valueOf(post.getId()))
                    .type(ReportType.ETC)
                    .reason("report test")
                    .build();

            reportService.reportPost(account, postReportCreate);

            Assertions.assertThrows(IricomException.class, () -> {
                reportService.reportPost(account, postReportCreate);
            });
        }

        @Test
        @DisplayName("게시물의 게시판 불일치")
        public void testCase02() throws Exception {
            Account account = getAccount(common00);
            Post post = getPost(reportPost01);
            Board board = getBoard(disableBoard);

            PostReportCreate postReportCreate = PostReportCreate.builder()
                    .boardId(String.valueOf(board.getId()))
                    .postId(String.valueOf(post.getId()))
                    .type(ReportType.ETC)
                    .reason("report test")
                    .build();

            Assertions.assertThrows(IricomException.class, () -> {
                reportService.reportPost(account, postReportCreate);
            });
        }

        @Test
        @DisplayName("게시물 ID 누락")
        public void testCase03() throws Exception {
            Account account = getAccount(common00);
            Post post = getPost(reportPost01);
            Board board = post.getBoard();

            PostReportCreate postReportCreate = PostReportCreate.builder()
                    .boardId(String.valueOf(board.getId()))
                    .type(ReportType.ETC)
                    .reason("report test")
                    .build();

            Assertions.assertThrows(IricomException.class, () -> {
                reportService.reportPost(account, postReportCreate);
            });
        }

        @Test
        @DisplayName("게시판 ID 누락")
        public void testCase04() throws Exception {
            Account account = getAccount(common00);
            Post post = getPost(reportPost01);

            PostReportCreate postReportCreate = PostReportCreate.builder()
                    .postId(String.valueOf(post.getId()))
                    .type(ReportType.ETC)
                    .reason("report test")
                    .build();

            Assertions.assertThrows(IricomException.class, () -> {
                reportService.reportPost(account, postReportCreate);
            });
        }

        @Test
        @DisplayName("존재하지 않는 게시물")
        public void testCase05() throws Exception {
            Account account = getAccount(common00);
            Board board = getBoard(enableBoard);

            PostReportCreate postReportCreate = PostReportCreate.builder()
                    .boardId(String.valueOf(board.getId()))
                    .postId("NOT_EXIST_POST")
                    .type(ReportType.ETC)
                    .reason("report test")
                    .build();

            Assertions.assertThrows(IricomException.class, () -> {
                reportService.reportPost(account, postReportCreate);
            });
        }

        @Test
        @DisplayName("존재하지 않는 게시판")
        public void testCase06() throws Exception {
            Account account = getAccount(common00);
            Post post = getPost(reportPost01);

            PostReportCreate postReportCreate = PostReportCreate.builder()
                    .boardId("NOT_EXIST_BOARD")
                    .postId(String.valueOf(post.getId()))
                    .type(ReportType.ETC)
                    .reason("report test")
                    .build();

            Assertions.assertThrows(IricomException.class, () -> {
                reportService.reportPost(account, postReportCreate);
            });
        }
    }

    @Nested
    class CommentTest {
        @Test
        @DisplayName("댓글 신고")
        public void testCase00() throws Exception {
            Account account = getAccount(common00);
            Comment comment = getComment(reportComment01);
            Post post = comment.getPost();
            Board board = post.getBoard();

            CommentReportCreate commentReportCreate = CommentReportCreate.builder()
                    .boardId(String.valueOf(board.getId()))
                    .postId(String.valueOf(post.getId()))
                    .commentId(String.valueOf(comment.getId()))
                    .type(ReportType.ETC)
                    .reason("report test")
                    .build();

            reportService.reportComment(account, commentReportCreate);
        }

        @Test
        @DisplayName("중복 댓글 신고")
        public void testCase01() throws Exception {
            Account account = getAccount(common00);
            Comment comment = getComment(reportComment02);
            Post post = comment.getPost();
            Board board = post.getBoard();

            CommentReportCreate commentReportCreate = CommentReportCreate.builder()
                    .boardId(String.valueOf(board.getId()))
                    .postId(String.valueOf(post.getId()))
                    .commentId(String.valueOf(comment.getId()))
                    .type(ReportType.ETC)
                    .reason("report test")
                    .build();

            reportService.reportComment(account, commentReportCreate);

            Assertions.assertThrows(IricomException.class, () -> {
                reportService.reportComment(account, commentReportCreate);
            });
        }

        @Test
        @DisplayName("댓글의 게시물 불일치")
        public void testCase02() throws Exception {
            Account account = getAccount(common00);
            Comment comment = getComment(reportComment03);
            Post post = comment.getPost();
            Board board = post.getBoard();
            Post invalidPost = getPost(disableBoardPost00);

            CommentReportCreate commentReportCreate = CommentReportCreate.builder()
                    .boardId(String.valueOf(board.getId()))
                    .postId(String.valueOf(invalidPost.getId()))
                    .commentId(String.valueOf(comment.getId()))
                    .type(ReportType.ETC)
                    .reason("report test")
                    .build();

            Assertions.assertThrows(IricomException.class, () -> {
                reportService.reportComment(account, commentReportCreate);
            });
        }

        @Test
        @DisplayName("댓글의 게시물의 게시판 불일치")
        public void testCase03() throws Exception {
            Account account = getAccount(common00);
            Comment comment = getComment(reportComment03);
            Post post = comment.getPost();
            Board invalidBoard = getBoard(disableBoard);

            CommentReportCreate commentReportCreate = CommentReportCreate.builder()
                    .boardId(String.valueOf(invalidBoard.getId()))
                    .postId(String.valueOf(post.getId()))
                    .commentId(String.valueOf(comment.getId()))
                    .type(ReportType.ETC)
                    .reason("report test")
                    .build();

            Assertions.assertThrows(IricomException.class, () -> {
                reportService.reportComment(account, commentReportCreate);
            });
        }

        @Test
        @DisplayName("게시판 ID 누락")
        public void testCase04() throws Exception {
            Account account = getAccount(common00);
            Comment comment = getComment(reportComment03);
            Post post = comment.getPost();

            CommentReportCreate commentReportCreate = CommentReportCreate.builder()
                    .postId(String.valueOf(post.getId()))
                    .commentId(String.valueOf(comment.getId()))
                    .type(ReportType.ETC)
                    .reason("report test")
                    .build();

            Assertions.assertThrows(IricomException.class, () -> {
                reportService.reportComment(account, commentReportCreate);
            });
        }

        @Test
        @DisplayName("게시물 ID 누락")
        public void testCase05() throws Exception {
            Account account = getAccount(common00);
            Comment comment = getComment(reportComment03);
            Post post = comment.getPost();
            Board board = post.getBoard();

            CommentReportCreate commentReportCreate = CommentReportCreate.builder()
                    .boardId(String.valueOf(board.getId()))
                    .commentId(String.valueOf(comment.getId()))
                    .type(ReportType.ETC)
                    .reason("report test")
                    .build();

            Assertions.assertThrows(IricomException.class, () -> {
                reportService.reportComment(account, commentReportCreate);
            });
        }

        @Test
        @DisplayName("댓글 ID 누락")
        public void testCase06() throws Exception {
            Account account = getAccount(common00);
            Comment comment = getComment(reportComment03);
            Post post = comment.getPost();
            Board board = post.getBoard();

            CommentReportCreate commentReportCreate = CommentReportCreate.builder()
                    .boardId(String.valueOf(board.getId()))
                    .postId(String.valueOf(post.getId()))
                    .type(ReportType.ETC)
                    .reason("report test")
                    .build();

            Assertions.assertThrows(IricomException.class, () -> {
                reportService.reportComment(account, commentReportCreate);
            });
        }

        @Test
        @DisplayName("존재하지 않는 댓글")
        public void testCase07() throws Exception {
            Account account = getAccount(common00);
            Comment comment = getComment(reportComment03);
            Post post = comment.getPost();
            Board board = post.getBoard();

            CommentReportCreate commentReportCreate = CommentReportCreate.builder()
                    .boardId(String.valueOf(board.getId()))
                    .postId(String.valueOf(post.getId()))
                    .commentId("NOT_EXIST_COMMENT")
                    .type(ReportType.ETC)
                    .reason("report test")
                    .build();

            Assertions.assertThrows(IricomException.class, () -> {
                reportService.reportComment(account, commentReportCreate);
            });
        }

        @Test
        @DisplayName("존재하지 않는 게시물")
        public void testCase08() throws Exception {
            Account account = getAccount(common00);
            Comment comment = getComment(reportComment03);
            Post post = comment.getPost();
            Board board = post.getBoard();

            CommentReportCreate commentReportCreate = CommentReportCreate.builder()
                    .boardId(String.valueOf(board.getId()))
                    .postId("NOT_EXIST_POST")
                    .commentId(String.valueOf(comment.getId()))
                    .type(ReportType.ETC)
                    .reason("report test")
                    .build();

            Assertions.assertThrows(IricomException.class, () -> {
                reportService.reportComment(account, commentReportCreate);
            });
        }

        @Test
        @DisplayName("존재하지 않는 게시판")
        public void testCase09() throws Exception {
            Account account = getAccount(common00);
            Comment comment = getComment(reportComment03);
            Post post = comment.getPost();

            CommentReportCreate commentReportCreate = CommentReportCreate.builder()
                    .boardId("NOT_EXIST_BOARD")
                    .postId(String.valueOf(post.getId()))
                    .commentId(String.valueOf(comment.getId()))
                    .type(ReportType.ETC)
                    .reason("report test")
                    .build();

            Assertions.assertThrows(IricomException.class, () -> {
                reportService.reportComment(account, commentReportCreate);
            });
        }
    }
}

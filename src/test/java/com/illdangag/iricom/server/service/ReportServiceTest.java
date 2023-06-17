package com.illdangag.iricom.server.service;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.data.request.CommentReportCreate;
import com.illdangag.iricom.server.data.request.PostReportInfoCreate;
import com.illdangag.iricom.server.data.request.PostReportInfoSearch;
import com.illdangag.iricom.server.data.response.PostReportInfoList;
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
@DisplayName("신고")
public class ReportServiceTest extends IricomTestSuite {
    @Autowired
    ReportService reportService;

    @Autowired
    public ReportServiceTest(ApplicationContext context) {
        super(context);
    }

    @Nested
    @DisplayName("게시물")
    class PostTest {
        @Nested
        @DisplayName("신고")
        class ReportTest {
            @Test
            @DisplayName("게시물 신고")
            public void testCase00() throws Exception {
                Account account = getAccount(common01);
                Post post = getPost(reportPost00);
                Board board = post.getBoard();

                String boardId = String.valueOf(board.getId());
                String postId = String.valueOf(post.getId());

                PostReportInfoCreate postReportInfoCreate = PostReportInfoCreate.builder()
                        .type(ReportType.ETC)
                        .reason("report test")
                        .build();

                reportService.reportPost(account, boardId, postId, postReportInfoCreate);
            }

            @Test
            @DisplayName("중복 게시물 신고")
            public void testCase01() throws Exception {
                Account account = getAccount(common00);
                Post post = getPost(reportPost01);
                Board board = post.getBoard();

                String boardId = String.valueOf(board.getId());
                String postId = String.valueOf(post.getId());

                PostReportInfoCreate postReportInfoCreate = PostReportInfoCreate.builder()
                        .type(ReportType.ETC)
                        .reason("report test")
                        .build();

                reportService.reportPost(account, boardId, postId, postReportInfoCreate);

                Assertions.assertThrows(IricomException.class, () -> {
                    reportService.reportPost(account, boardId, postId, postReportInfoCreate);
                });
            }

            @Test
            @DisplayName("게시물의 게시판 불일치")
            public void testCase02() throws Exception {
                Account account = getAccount(common00);
                Post post = getPost(reportPost01);
                Board board = getBoard(disableBoard);

                String boardId = String.valueOf(board.getId());
                String postId = String.valueOf(post.getId());

                PostReportInfoCreate postReportInfoCreate = PostReportInfoCreate.builder()
                        .type(ReportType.ETC)
                        .reason("report test")
                        .build();

                Assertions.assertThrows(IricomException.class, () -> {
                    reportService.reportPost(account, boardId, postId, postReportInfoCreate);
                });
            }

            @Test
            @DisplayName("존재하지 않는 게시물")
            public void testCase05() throws Exception {
                Account account = getAccount(common00);
                Board board = getBoard(enableBoard);

                String boardId = String.valueOf(board.getId());

                PostReportInfoCreate postReportInfoCreate = PostReportInfoCreate.builder()
                        .type(ReportType.ETC)
                        .reason("report test")
                        .build();

                Assertions.assertThrows(IricomException.class, () -> {
                    reportService.reportPost(account, boardId, "NOT_EXIST_POST", postReportInfoCreate);
                });
            }

            @Test
            @DisplayName("존재하지 않는 게시판")
            public void testCase06() throws Exception {
                Account account = getAccount(common00);
                Post post = getPost(reportPost01);

                String postId = String.valueOf(post.getId());

                PostReportInfoCreate postReportInfoCreate = PostReportInfoCreate.builder()
                        .type(ReportType.ETC)
                        .reason("report test")
                        .build();

                Assertions.assertThrows(IricomException.class, () -> {
                    reportService.reportPost(account, "NOT_EXIST_BOARD", postId, postReportInfoCreate);
                });
            }
        }

        @Nested
        @DisplayName("조회")
        class SearchTest {
            @Test
            @DisplayName("기본 조회")
            public void searchPostReportInfoList() throws Exception {
                Account systemAdminAccount = getAccount(systemAdmin);
                Board board = getBoard(reportSearchBoard);
                PostReportInfoSearch postReportInfoSearch = PostReportInfoSearch.builder()
                        .postTitle("")
                        .reason("")
                        .build();

                PostReportInfoList postReportInfoList = reportService.getPostReportInfoList(systemAdminAccount, board, postReportInfoSearch);
                Assertions.assertEquals(0, postReportInfoList.getSkip());
                Assertions.assertEquals(20, postReportInfoList.getLimit());
                Assertions.assertEquals(8, postReportInfoList.getTotal());
            }

            @Test
            @DisplayName("종류")
            public void searchPostReportInfoListByType() throws Exception {
                Account systemAdminAccount = getAccount(systemAdmin);
                Board board = getBoard(reportSearchBoard);
                PostReportInfoSearch postReportInfoSearch = PostReportInfoSearch.builder()
                        .type(ReportType.HATE)
                        .postTitle("")
                        .reason("")
                        .build();

                PostReportInfoList postReportInfoList = reportService.getPostReportInfoList(systemAdminAccount, board, postReportInfoSearch);
                Assertions.assertEquals(0, postReportInfoList.getSkip());
                Assertions.assertEquals(20, postReportInfoList.getLimit());
                Assertions.assertEquals(2, postReportInfoList.getTotal());
            }

            @Test
            @DisplayName("skip")
            public void searchPostReportInfoListSkip() throws Exception {
                Account systemAdminAccount = getAccount(systemAdmin);
                Board board = getBoard(reportSearchBoard);
                PostReportInfoSearch postReportInfoSearch = PostReportInfoSearch.builder()
                        .skip(5)
                        .build();

                PostReportInfoList postReportInfoList = reportService.getPostReportInfoList(systemAdminAccount, board, postReportInfoSearch);
                Assertions.assertEquals(5, postReportInfoList.getSkip());
                Assertions.assertEquals(20, postReportInfoList.getLimit());
                Assertions.assertEquals(8, postReportInfoList.getTotal());
                Assertions.assertEquals(3, postReportInfoList.getPostReportInfoList().size());
            }

            @Test
            @DisplayName("limit")
            public void searchPostReportInfoListLimit() throws Exception {
                Account systemAdminAccount = getAccount(systemAdmin);
                Board board = getBoard(reportSearchBoard);
                PostReportInfoSearch postReportInfoSearch = PostReportInfoSearch.builder()
                        .limit(5)
                        .build();

                PostReportInfoList postReportInfoList = reportService.getPostReportInfoList(systemAdminAccount, board, postReportInfoSearch);
                Assertions.assertEquals(0, postReportInfoList.getSkip());
                Assertions.assertEquals(5, postReportInfoList.getLimit());
                Assertions.assertEquals(8, postReportInfoList.getTotal());
                Assertions.assertEquals(5, postReportInfoList.getPostReportInfoList().size());
            }

            @Test
            @DisplayName("skip, limit")
            public void searchPostReportInfoListSkipLimit() throws Exception {
                Account systemAdminAccount = getAccount(systemAdmin);
                Board board = getBoard(reportSearchBoard);
                PostReportInfoSearch postReportInfoSearch = PostReportInfoSearch.builder()
                        .skip(5)
                        .limit(5)
                        .build();

                PostReportInfoList postReportInfoList = reportService.getPostReportInfoList(systemAdminAccount, board, postReportInfoSearch);
                Assertions.assertEquals(5, postReportInfoList.getSkip());
                Assertions.assertEquals(5, postReportInfoList.getLimit());
                Assertions.assertEquals(8, postReportInfoList.getTotal());
                Assertions.assertEquals(3, postReportInfoList.getPostReportInfoList().size());
            }

            @Test
            @DisplayName("reason")
            public void searchPostReportInfoListByReason() throws Exception {
                Account systemAdminAccount = getAccount(systemAdmin);
                Board board = getBoard(reportSearchBoard);
                PostReportInfoSearch postReportInfoSearch = PostReportInfoSearch.builder()
                        .reason("political")
                        .build();

                PostReportInfoList postReportInfoList = reportService.getPostReportInfoList(systemAdminAccount, board, postReportInfoSearch);
                Assertions.assertEquals(0, postReportInfoList.getSkip());
                Assertions.assertEquals(20, postReportInfoList.getLimit());
                Assertions.assertEquals(2, postReportInfoList.getTotal());
                Assertions.assertEquals(2, postReportInfoList.getPostReportInfoList().size());
            }

            @Test
            @DisplayName("post title")
            public void searchPostReportInfoListByPostTitle() throws Exception {
                Account systemAdminAccount = getAccount(systemAdmin);
                Board board = getBoard(reportSearchBoard);
                PostReportInfoSearch postReportInfoSearch = PostReportInfoSearch.builder()
                        .postTitle("Post01")
                        .build();

                PostReportInfoList postReportInfoList = reportService.getPostReportInfoList(systemAdminAccount, board, postReportInfoSearch);
                Assertions.assertEquals(0, postReportInfoList.getSkip());
                Assertions.assertEquals(20, postReportInfoList.getLimit());
                Assertions.assertEquals(4, postReportInfoList.getTotal());
                Assertions.assertEquals(4, postReportInfoList.getPostReportInfoList().size());
            }

            @Test
            @DisplayName("complex")
            public void searchPostReportInfoListByComplex() throws Exception {
                Account systemAdminAccount = getAccount(systemAdmin);
                Board board = getBoard(reportSearchBoard);
                PostReportInfoSearch postReportInfoSearch = PostReportInfoSearch.builder()
                        .type(ReportType.HATE)
                        .postTitle("post01")
                        .skip(1)
                        .limit(1)
                        .build();

                PostReportInfoList postReportInfoList = reportService.getPostReportInfoList(systemAdminAccount, board, postReportInfoSearch);
                Assertions.assertEquals(1, postReportInfoList.getSkip());
                Assertions.assertEquals(1, postReportInfoList.getLimit());
                Assertions.assertEquals(1, postReportInfoList.getTotal());
                Assertions.assertEquals(0, postReportInfoList.getPostReportInfoList().size());
            }
        }
    }

    @Nested
    @DisplayName("댓글")
    class CommentTest {
        @Test
        @DisplayName("댓글 신고")
        public void testCase00() throws Exception {
            Account account = getAccount(common00);
            Comment comment = getComment(reportComment01);
            Post post = comment.getPost();
            Board board = post.getBoard();

            String boardId = String.valueOf(board.getId());
            String postId = String.valueOf(post.getId());
            String commentId = String.valueOf(comment.getId());

            CommentReportCreate commentReportCreate = CommentReportCreate.builder()
                    .type(ReportType.ETC)
                    .reason("report test")
                    .build();

            reportService.reportComment(account, boardId, postId, commentId, commentReportCreate);
        }

        @Test
        @DisplayName("중복 댓글 신고")
        public void testCase01() throws Exception {
            Account account = getAccount(common00);
            Comment comment = getComment(reportComment02);
            Post post = comment.getPost();
            Board board = post.getBoard();

            String boardId = String.valueOf(board.getId());
            String postId = String.valueOf(post.getId());
            String commentId = String.valueOf(comment.getId());

            CommentReportCreate commentReportCreate = CommentReportCreate.builder()
                    .type(ReportType.ETC)
                    .reason("report test")
                    .build();

            reportService.reportComment(account, boardId, postId, commentId, commentReportCreate);

            Assertions.assertThrows(IricomException.class, () -> {
                reportService.reportComment(account, boardId, postId, commentId, commentReportCreate);
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

            String boardId = String.valueOf(board.getId());
            String invalidPostId = String.valueOf(invalidPost.getId());
            String commentId = String.valueOf(comment.getId());

            CommentReportCreate commentReportCreate = CommentReportCreate.builder()
                    .type(ReportType.ETC)
                    .reason("report test")
                    .build();

            Assertions.assertThrows(IricomException.class, () -> {
                reportService.reportComment(account, boardId, invalidPostId, commentId, commentReportCreate);
            });
        }

        @Test
        @DisplayName("댓글의 게시물의 게시판 불일치")
        public void testCase03() throws Exception {
            Account account = getAccount(common00);
            Comment comment = getComment(reportComment03);
            Post post = comment.getPost();
            Board invalidBoard = getBoard(disableBoard);

            String invalidBoardId = String.valueOf(invalidBoard.getId());
            String postId = String.valueOf(post.getId());
            String commentId = String.valueOf(comment.getId());

            CommentReportCreate commentReportCreate = CommentReportCreate.builder()
                    .type(ReportType.ETC)
                    .reason("report test")
                    .build();

            Assertions.assertThrows(IricomException.class, () -> {
                reportService.reportComment(account, invalidBoardId, postId, commentId, commentReportCreate);
            });
        }

        @Test
        @DisplayName("존재하지 않는 댓글")
        public void testCase07() throws Exception {
            Account account = getAccount(common00);
            Comment comment = getComment(reportComment03);
            Post post = comment.getPost();
            Board board = post.getBoard();

            String boardId = String.valueOf(board.getId());
            String postId = String.valueOf(post.getId());
            String commentId = "NOT_EXIST_COMMENT";

            CommentReportCreate commentReportCreate = CommentReportCreate.builder()
                    .type(ReportType.ETC)
                    .reason("report test")
                    .build();

            Assertions.assertThrows(IricomException.class, () -> {
                reportService.reportComment(account, boardId, postId, commentId, commentReportCreate);
            });
        }

        @Test
        @DisplayName("존재하지 않는 게시물")
        public void testCase08() throws Exception {
            Account account = getAccount(common00);
            Comment comment = getComment(reportComment03);
            Post post = comment.getPost();
            Board board = post.getBoard();

            String boardId = String.valueOf(board.getId());
            String postId = "NOT_EXIST_POST";
            String commentId = String.valueOf(comment.getId());

            CommentReportCreate commentReportCreate = CommentReportCreate.builder()
                    .type(ReportType.ETC)
                    .reason("report test")
                    .build();

            Assertions.assertThrows(IricomException.class, () -> {
                reportService.reportComment(account, boardId, postId, commentId, commentReportCreate);
            });
        }

        @Test
        @DisplayName("존재하지 않는 게시판")
        public void testCase09() throws Exception {
            Account account = getAccount(common00);
            Comment comment = getComment(reportComment03);
            Post post = comment.getPost();

            String boardId = "NOT_EXIST_BOARD";
            String postId = String.valueOf(post.getId());
            String commentId = String.valueOf(comment.getId());

            CommentReportCreate commentReportCreate = CommentReportCreate.builder()
                    .type(ReportType.ETC)
                    .reason("report test")
                    .build();

            Assertions.assertThrows(IricomException.class, () -> {
                reportService.reportComment(account, boardId, postId, commentId, commentReportCreate);
            });
        }
    }
}

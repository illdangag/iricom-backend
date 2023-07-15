package com.illdangag.iricom.server.service;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.data.request.CommentReportInfoCreate;
import com.illdangag.iricom.server.data.request.CommentReportInfoSearch;
import com.illdangag.iricom.server.data.request.PostReportInfoCreate;
import com.illdangag.iricom.server.data.request.PostReportInfoSearch;
import com.illdangag.iricom.server.data.response.CommentReportInfoList;
import com.illdangag.iricom.server.data.response.PostReportInfo;
import com.illdangag.iricom.server.data.response.PostReportInfoList;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@DisplayName("신고")
public class ReportServiceTest extends IricomTestSuite {
    @Autowired
    ReportService reportService;

    protected static final TestBoardInfo enableBoard00 = TestBoardInfo.builder()
            .title("enable").isEnabled(true).adminList(Arrays.asList(allBoardAdmin)).build();
    protected static final TestBoardInfo enableBoard01 = TestBoardInfo.builder()
            .title("enable").isEnabled(true).adminList(Arrays.asList(allBoardAdmin)).build();
    protected static final TestBoardInfo disableBoard00 = TestBoardInfo.builder()
            .title("disable").isEnabled(false).adminList(Arrays.asList(allBoardAdmin)).build();

    protected static final TestPostInfo post00 = TestPostInfo.builder()
            .title("post00").content("report contents").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(enableBoard00).build();
    protected static final TestPostInfo post01 = TestPostInfo.builder()
            .title("post01").content("report contents").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(enableBoard00).build();
    protected static final TestPostInfo post02 = TestPostInfo.builder()
            .title("post01").content("report contents").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(disableBoard00).build();
    protected static final TestPostInfo reportedPost00 = TestPostInfo.builder()
            .title("reportedPost00").content("report contents").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(enableBoard00).build();
    protected static final TestPostInfo reportedPost01 = TestPostInfo.builder()
            .title("reportedPost01").content("report contents").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(enableBoard00).build();

    private static final TestCommentInfo comment00 = TestCommentInfo.builder()
            .content("comment00").creator(common00).post(post00)
            .build();
    private static final TestCommentInfo comment01 = TestCommentInfo.builder()
            .content("comment01").creator(common00).post(post00)
            .build();
    private static final TestCommentInfo reportedComment00 = TestCommentInfo.builder()
            .content("reportedComment00").creator(common00).post(post00)
            .build();
    private static final TestCommentInfo reportedComment01 = TestCommentInfo.builder()
            .content("reportedComment01").creator(common00).post(post00)
            .build();

    private static final TestPostReportInfo postReport00 = TestPostReportInfo.builder()
            .type(ReportType.HATE).reason("hate").reportAccount(common00).post(reportedPost01)
            .build();

    private static final TestCommentReportInfo commentReport00 = TestCommentReportInfo.builder()
            .type(ReportType.HATE).reason("hate").reportAccount(common00).comment(reportedComment01)
            .build();

    @Autowired
    public ReportServiceTest(ApplicationContext context) {
        super(context);

        List<TestBoardInfo> testBoardInfoList = Arrays.asList(enableBoard00, enableBoard01, disableBoard00);
        List<TestPostInfo> testPostInfoList = Arrays.asList(post00, post01, post02, reportedPost00, reportedPost01);
        List<TestCommentInfo> testCommentInfoList = Arrays.asList(comment00, comment01, reportedComment00, reportedComment01);

        List<TestPostReportInfo> testPostReportInfoList = new ArrayList<>(super.createTestPostReportInfo(reportedPost00));
        testPostReportInfoList.add(postReport00);

        List<TestCommentReportInfo> testCommentReportInfoList = new ArrayList<>(super.createTestCommentReportInfo(reportedComment00));
        testCommentReportInfoList.add(commentReport00);

        super.setBoard(testBoardInfoList);
        super.setPost(testPostInfoList);
        super.setComment(testCommentInfoList);
        super.setPostReport(testPostReportInfoList);
        super.setCommentReport(testCommentReportInfoList);

        super.setDisabledBoard(testBoardInfoList);
    }

    @Nested
    @DisplayName("게시물")
    class PostTest {

        @Nested
        @DisplayName("신고")
        class ReportTest {

            @Test
            @DisplayName("게시물 신고")
            public void reportPost() throws Exception {
                Account account = getAccount(common01);
                Post post = getPost(post00);
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
            public void duplicateReportPost() throws Exception {
                Account account = getAccount(common00);
                Post post = getPost(post01);
                Board board = post.getBoard();

                String boardId = String.valueOf(board.getId());
                String postId = String.valueOf(post.getId());

                PostReportInfoCreate postReportInfoCreate = PostReportInfoCreate.builder()
                        .type(ReportType.ETC)
                        .reason("report test")
                        .build();

                // 첫번째 신고
                reportService.reportPost(account, boardId, postId, postReportInfoCreate);

                // 두번째 신고
                Assertions.assertThrows(IricomException.class, () -> {
                    reportService.reportPost(account, boardId, postId, postReportInfoCreate);
                });
            }

            @Test
            @DisplayName("게시물의 게시판 불일치")
            public void notMatchPostAndBoard() throws Exception {
                Account account = getAccount(common00);
                Post post = getPost(post01);
                Board board = getBoard(enableBoard01);

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
            public void notExistPost() throws Exception {
                Account account = getAccount(common00);
                Board board = getBoard(enableBoard00);

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
            public void notExistBoard() throws Exception {
                Account account = getAccount(common00);
                Post post = getPost(post01);

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
        @DisplayName("목록 조회")
        class SearchTest {

            @Nested
            @DisplayName("게시판")
            class BoardSearch {

                @Test
                @DisplayName("기본 조회")
                public void searchPostReportInfoList() throws Exception {
                    Account systemAdminAccount = getAccount(systemAdmin);
                    Board board = getBoard(enableBoard00);
                    PostReportInfoSearch postReportInfoSearch = PostReportInfoSearch.builder()
                            .reason("")
                            .build();

                    PostReportInfoList postReportInfoList = reportService.getPostReportInfoList(systemAdminAccount, board, postReportInfoSearch);
                    Assertions.assertEquals(0, postReportInfoList.getSkip());
                    Assertions.assertEquals(20, postReportInfoList.getLimit());
                    Assertions.assertEquals(11, postReportInfoList.getTotal());
                }

                @Test
                @DisplayName("종류")
                public void searchPostReportInfoListByType() throws Exception {
                    Account systemAdminAccount = getAccount(systemAdmin);
                    Board board = getBoard(enableBoard00);
                    PostReportInfoSearch postReportInfoSearch = PostReportInfoSearch.builder()
                            .type(ReportType.HATE)
                            .reason("")
                            .build();

                    PostReportInfoList postReportInfoList = reportService.getPostReportInfoList(systemAdminAccount, board, postReportInfoSearch);
                    Assertions.assertEquals(0, postReportInfoList.getSkip());
                    Assertions.assertEquals(20, postReportInfoList.getLimit());
                    Assertions.assertEquals(3, postReportInfoList.getTotal());
                }

                @Test
                @DisplayName("skip")
                public void searchPostReportInfoListSkip() throws Exception {
                    Account systemAdminAccount = getAccount(systemAdmin);
                    Board board = getBoard(enableBoard00);
                    PostReportInfoSearch postReportInfoSearch = PostReportInfoSearch.builder()
                            .skip(5)
                            .build();

                    PostReportInfoList postReportInfoList = reportService.getPostReportInfoList(systemAdminAccount, board, postReportInfoSearch);
                    Assertions.assertEquals(5, postReportInfoList.getSkip());
                    Assertions.assertEquals(20, postReportInfoList.getLimit());
                    Assertions.assertEquals(11, postReportInfoList.getTotal());
                    Assertions.assertEquals(6, postReportInfoList.getPostReportInfoList().size());
                }

                @Test
                @DisplayName("limit")
                public void searchPostReportInfoListLimit() throws Exception {
                    Account systemAdminAccount = getAccount(systemAdmin);
                    Board board = getBoard(enableBoard00);
                    PostReportInfoSearch postReportInfoSearch = PostReportInfoSearch.builder()
                            .limit(5)
                            .build();

                    PostReportInfoList postReportInfoList = reportService.getPostReportInfoList(systemAdminAccount, board, postReportInfoSearch);
                    Assertions.assertEquals(0, postReportInfoList.getSkip());
                    Assertions.assertEquals(5, postReportInfoList.getLimit());
                    Assertions.assertEquals(11, postReportInfoList.getTotal());
                    Assertions.assertEquals(5, postReportInfoList.getPostReportInfoList().size());
                }

                @Test
                @DisplayName("skip, limit")
                public void searchPostReportInfoListSkipLimit() throws Exception {
                    Account systemAdminAccount = getAccount(systemAdmin);
                    Board board = getBoard(enableBoard00);
                    PostReportInfoSearch postReportInfoSearch = PostReportInfoSearch.builder()
                            .skip(5)
                            .limit(5)
                            .build();

                    PostReportInfoList postReportInfoList = reportService.getPostReportInfoList(systemAdminAccount, board, postReportInfoSearch);
                    Assertions.assertEquals(5, postReportInfoList.getSkip());
                    Assertions.assertEquals(5, postReportInfoList.getLimit());
                    Assertions.assertEquals(11, postReportInfoList.getTotal());
                    Assertions.assertEquals(5, postReportInfoList.getPostReportInfoList().size());
                }

                @Test
                @DisplayName("reason")
                public void searchPostReportInfoListByReason() throws Exception {
                    Account systemAdminAccount = getAccount(systemAdmin);
                    Board board = getBoard(enableBoard00);
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
                @DisplayName("complex")
                public void searchPostReportInfoListByComplex() throws Exception {
                    Account systemAdminAccount = getAccount(systemAdmin);
                    Board board = getBoard(reportSearchBoard);
                    PostReportInfoSearch postReportInfoSearch = PostReportInfoSearch.builder()
                            .type(ReportType.HATE)
                            .skip(1)
                            .limit(1)
                            .build();

                    PostReportInfoList postReportInfoList = reportService.getPostReportInfoList(systemAdminAccount, board, postReportInfoSearch);
                    Assertions.assertEquals(1, postReportInfoList.getSkip());
                    Assertions.assertEquals(1, postReportInfoList.getLimit());
                    Assertions.assertEquals(2, postReportInfoList.getTotal());
                    Assertions.assertEquals(1, postReportInfoList.getPostReportInfoList().size());
                }
            }

            @Nested
            @DisplayName("게시판 게시물")
            class BoardPost {

                @Test
                @DisplayName("기본 조회")
                public void searchPostReportInfoList() throws Exception {
                    Account systemAdminAccount = getAccount(systemAdmin);
                    Post post = getPost(reportedPost00);
                    Board board = post.getBoard();

                    PostReportInfoSearch postReportInfoSearch = PostReportInfoSearch.builder()
                            .reason("")
                            .build();

                    PostReportInfoList postReportInfoList = reportService.getPostReportInfoList(systemAdminAccount, board, post, postReportInfoSearch);
                    Assertions.assertEquals(0, postReportInfoList.getSkip());
                    Assertions.assertEquals(20, postReportInfoList.getLimit());
                    Assertions.assertEquals(10, postReportInfoList.getTotal());
                }

                @Test
                @DisplayName("종류")
                public void searchPostReportInfoListByType() throws Exception {
                    Account systemAdminAccount = getAccount(systemAdmin);
                    Post post = getPost(reportedPost00);
                    Board board = post.getBoard();

                    PostReportInfoSearch postReportInfoSearch = PostReportInfoSearch.builder()
                            .type(ReportType.HATE)
                            .reason("")
                            .build();

                    PostReportInfoList postReportInfoList = reportService.getPostReportInfoList(systemAdminAccount, board, post, postReportInfoSearch);
                    Assertions.assertEquals(0, postReportInfoList.getSkip());
                    Assertions.assertEquals(20, postReportInfoList.getLimit());
                    Assertions.assertEquals(2, postReportInfoList.getTotal());
                }

                @Test
                @DisplayName("skip")
                public void searchPostReportInfoListSkip() throws Exception {
                    Account systemAdminAccount = getAccount(systemAdmin);
                    Post post = getPost(reportedPost00);
                    Board board = post.getBoard();

                    PostReportInfoSearch postReportInfoSearch = PostReportInfoSearch.builder()
                            .skip(3)
                            .build();

                    PostReportInfoList postReportInfoList = reportService.getPostReportInfoList(systemAdminAccount, board, post, postReportInfoSearch);
                    Assertions.assertEquals(3, postReportInfoList.getSkip());
                    Assertions.assertEquals(20, postReportInfoList.getLimit());
                    Assertions.assertEquals(10, postReportInfoList.getTotal());
                    Assertions.assertEquals(7, postReportInfoList.getPostReportInfoList().size());
                }

                @Test
                @DisplayName("limit")
                public void searchPostReportInfoListLimit() throws Exception {
                    Account systemAdminAccount = getAccount(systemAdmin);
                    Post post = getPost(reportedPost00);
                    Board board = post.getBoard();

                    PostReportInfoSearch postReportInfoSearch = PostReportInfoSearch.builder()
                            .limit(2)
                            .build();

                    PostReportInfoList postReportInfoList = reportService.getPostReportInfoList(systemAdminAccount, board, post, postReportInfoSearch);
                    Assertions.assertEquals(0, postReportInfoList.getSkip());
                    Assertions.assertEquals(2, postReportInfoList.getLimit());
                    Assertions.assertEquals(10, postReportInfoList.getTotal());
                    Assertions.assertEquals(2, postReportInfoList.getPostReportInfoList().size());
                }

                @Test
                @DisplayName("skip, limit")
                public void searchPostReportInfoListSkipLimit() throws Exception {
                    Account systemAdminAccount = getAccount(systemAdmin);
                    Post post = getPost(reportedPost00);
                    Board board = post.getBoard();

                    PostReportInfoSearch postReportInfoSearch = PostReportInfoSearch.builder()
                            .skip(3)
                            .limit(3)
                            .build();

                    PostReportInfoList postReportInfoList = reportService.getPostReportInfoList(systemAdminAccount, board, post, postReportInfoSearch);
                    Assertions.assertEquals(3, postReportInfoList.getSkip());
                    Assertions.assertEquals(3, postReportInfoList.getLimit());
                    Assertions.assertEquals(10, postReportInfoList.getTotal());
                    Assertions.assertEquals(3, postReportInfoList.getPostReportInfoList().size());
                }

                @Test
                @DisplayName("reason")
                public void searchPostReportInfoListByReason() throws Exception {
                    Account systemAdminAccount = getAccount(systemAdmin);
                    Post post = getPost(reportedPost00);
                    Board board = post.getBoard();

                    PostReportInfoSearch postReportInfoSearch = PostReportInfoSearch.builder()
                            .reason("political")
                            .build();

                    PostReportInfoList postReportInfoList = reportService.getPostReportInfoList(systemAdminAccount, board, post, postReportInfoSearch);
                    Assertions.assertEquals(0, postReportInfoList.getSkip());
                    Assertions.assertEquals(20, postReportInfoList.getLimit());
                    Assertions.assertEquals(2, postReportInfoList.getTotal());
                    Assertions.assertEquals(2, postReportInfoList.getPostReportInfoList().size());
                }

                @Test
                @DisplayName("complex")
                public void searchPostReportInfoListByComplex() throws Exception {
                    Account systemAdminAccount = getAccount(systemAdmin);
                    Post post = getPost(reportedPost00);
                    Board board = post.getBoard();

                    PostReportInfoSearch postReportInfoSearch = PostReportInfoSearch.builder()
                            .type(ReportType.HATE)
                            .skip(1)
                            .limit(1)
                            .build();

                    PostReportInfoList postReportInfoList = reportService.getPostReportInfoList(systemAdminAccount, board, post, postReportInfoSearch);
                    Assertions.assertEquals(1, postReportInfoList.getSkip());
                    Assertions.assertEquals(1, postReportInfoList.getLimit());
                    Assertions.assertEquals(2, postReportInfoList.getTotal());
                    Assertions.assertEquals(1, postReportInfoList.getPostReportInfoList().size());
                }
            }
        }

        @Nested
        @DisplayName("조회")
        class GetTest {

            @Test
            @DisplayName("기본 조회")
            public void getPostReportInfo() throws Exception {
                Account systemAdminAccount = getAccount(systemAdmin);
                PostReport postReport = getPostReport(postReport00);
                Post post = postReport.getPost();
                Board board = post.getBoard();

                String boardId = String.valueOf(board.getId());
                String postId = String.valueOf(post.getId());
                String reportId = String.valueOf(postReport.getId());

                PostReportInfo postReportInfo = reportService.getPostReportInfo(systemAdminAccount, boardId, postId, reportId);
                Assertions.assertEquals(reportId, postReportInfo.getId());
            }

            @Test
            @DisplayName("올바르지 않은 게시판")
            public void invalidBoard() throws Exception {
                Account systemAdminAccount = getAccount(systemAdmin);
                PostReport postReport = getPostReport(postReport00);
                Post post = postReport.getPost();
                Board board = getBoard(enableBoard01);

                String boardId = String.valueOf(board.getId());
                String postId = String.valueOf(post.getId());
                String reportId = String.valueOf(postReport.getId());

                Assertions.assertThrows(IricomException.class, () -> {
                    reportService.getPostReportInfo(systemAdminAccount, boardId, postId, reportId);
                });
            }

            @Test
            @DisplayName("존재하지 않는 게시판")
            public void notExistBoard() throws Exception {
                Account systemAdminAccount = getAccount(systemAdmin);
                PostReport postReport = getPostReport(postReport00);
                Post post = postReport.getPost();

                String boardId = "NOT_EXIST";
                String postId = String.valueOf(post.getId());
                String reportId = String.valueOf(postReport.getId());

                Assertions.assertThrows(IricomException.class, () -> {
                    reportService.getPostReportInfo(systemAdminAccount, boardId, postId, reportId);
                });
            }

            @Test
            @DisplayName("올바르지 않은 게시물")
            public void invalidPost() throws Exception {
                Account systemAdminAccount = getAccount(systemAdmin);
                PostReport postReport = getPostReport(postReport00);
                Post post = postReport.getPost();
                Board board = post.getBoard();
                Post invaildPost = getPost(post02);

                String boardId = String.valueOf(board.getId());
                String postId = String.valueOf(invaildPost.getId());
                String reportId = String.valueOf(postReport.getId());

                Assertions.assertThrows(IricomException.class, () -> {
                    reportService.getPostReportInfo(systemAdminAccount, boardId, postId, reportId);
                });
            }

            @Test
            @DisplayName("존재하지 않은 게시물")
            public void notExistPost() throws Exception {
                Account systemAdminAccount = getAccount(systemAdmin);
                PostReport postReport = getPostReport(postReport00);
                Post post = postReport.getPost();
                Board board = post.getBoard();

                String boardId = String.valueOf(board.getId());
                String postId = "NOT_EXIST";
                String reportId = String.valueOf(postReport.getId());

                Assertions.assertThrows(IricomException.class, () -> {
                    reportService.getPostReportInfo(systemAdminAccount, boardId, postId, reportId);
                });
            }

            @Test
            @DisplayName("올바르지 않은 신고")
            public void invalidReport() throws Exception {
                Account systemAdminAccount = getAccount(systemAdmin);
                PostReport postReport = getPostReport(postReport00);
                Post post = postReport.getPost();
                Board board = post.getBoard();

                String boardId = String.valueOf(board.getId());
                String postId = String.valueOf(post.getId());
                String reportId = String.valueOf(postReport00);

                Assertions.assertThrows(IricomException.class, () -> {
                    reportService.getPostReportInfo(systemAdminAccount, boardId, postId, reportId);
                });
            }

            @Test
            @DisplayName("존재하지 않은 신고")
            public void notExistReport() throws Exception {
                Account systemAdminAccount = getAccount(systemAdmin);
                PostReport postReport = getPostReport(postReport00);
                Post post = postReport.getPost();
                Board board = post.getBoard();

                String boardId = String.valueOf(board.getId());
                String postId = String.valueOf(post.getId());
                String reportId = "NOT_EXIST";

                Assertions.assertThrows(IricomException.class, () -> {
                    reportService.getPostReportInfo(systemAdminAccount, boardId, postId, reportId);
                });
            }
        }
    }

    @Nested
    @DisplayName("댓글")
    class CommentTest {

        @Nested
        @DisplayName("신고")
        class ReportTest {

            @Test
            @DisplayName("댓글 신고")
            public void testCase00() throws Exception {
                Account account = getAccount(common00);
                Comment comment = getComment(comment00);
                Post post = comment.getPost();
                Board board = post.getBoard();

                String boardId = String.valueOf(board.getId());
                String postId = String.valueOf(post.getId());
                String commentId = String.valueOf(comment.getId());

                CommentReportInfoCreate commentReportInfoCreate = CommentReportInfoCreate.builder()
                        .type(ReportType.ETC)
                        .reason("report test")
                        .build();

                reportService.reportComment(account, boardId, postId, commentId, commentReportInfoCreate);
            }

            @Test
            @DisplayName("중복 댓글 신고")
            public void testCase01() throws Exception {
                Account account = getAccount(common00);
                Comment comment = getComment(comment01);
                Post post = comment.getPost();
                Board board = post.getBoard();

                String boardId = String.valueOf(board.getId());
                String postId = String.valueOf(post.getId());
                String commentId = String.valueOf(comment.getId());

                CommentReportInfoCreate commentReportInfoCreate = CommentReportInfoCreate.builder()
                        .type(ReportType.ETC)
                        .reason("report test")
                        .build();

                reportService.reportComment(account, boardId, postId, commentId, commentReportInfoCreate);

                Assertions.assertThrows(IricomException.class, () -> {
                    reportService.reportComment(account, boardId, postId, commentId, commentReportInfoCreate);
                });
            }

            @Test
            @DisplayName("댓글의 게시물 불일치")
            public void testCase02() throws Exception {
                Account account = getAccount(common00);
                Comment comment = getComment(comment01);
                Post post = comment.getPost();
                Board board = post.getBoard();
                Post invalidPost = getPost(post01);

                String boardId = String.valueOf(board.getId());
                String invalidPostId = String.valueOf(invalidPost.getId());
                String commentId = String.valueOf(comment.getId());

                CommentReportInfoCreate commentReportInfoCreate = CommentReportInfoCreate.builder()
                        .type(ReportType.ETC)
                        .reason("report test")
                        .build();

                Assertions.assertThrows(IricomException.class, () -> {
                    reportService.reportComment(account, boardId, invalidPostId, commentId, commentReportInfoCreate);
                });
            }

            @Test
            @DisplayName("댓글의 게시물의 게시판 불일치")
            public void testCase03() throws Exception {
                Account account = getAccount(common00);
                Comment comment = getComment(comment01);
                Post post = comment.getPost();
                Board invalidBoard = getBoard(enableBoard01);

                String invalidBoardId = String.valueOf(invalidBoard.getId());
                String postId = String.valueOf(post.getId());
                String commentId = String.valueOf(comment.getId());

                CommentReportInfoCreate commentReportInfoCreate = CommentReportInfoCreate.builder()
                        .type(ReportType.ETC)
                        .reason("report test")
                        .build();

                Assertions.assertThrows(IricomException.class, () -> {
                    reportService.reportComment(account, invalidBoardId, postId, commentId, commentReportInfoCreate);
                });
            }

            @Test
            @DisplayName("존재하지 않는 댓글")
            public void testCase07() throws Exception {
                Account account = getAccount(common00);
                Comment comment = getComment(comment01);
                Post post = comment.getPost();
                Board board = post.getBoard();

                String boardId = String.valueOf(board.getId());
                String postId = String.valueOf(post.getId());
                String commentId = "NOT_EXIST_COMMENT";

                CommentReportInfoCreate commentReportInfoCreate = CommentReportInfoCreate.builder()
                        .type(ReportType.ETC)
                        .reason("report test")
                        .build();

                Assertions.assertThrows(IricomException.class, () -> {
                    reportService.reportComment(account, boardId, postId, commentId, commentReportInfoCreate);
                });
            }

            @Test
            @DisplayName("존재하지 않는 게시물")
            public void testCase08() throws Exception {
                Account account = getAccount(common00);
                Comment comment = getComment(comment01);
                Post post = comment.getPost();
                Board board = post.getBoard();

                String boardId = String.valueOf(board.getId());
                String postId = "NOT_EXIST_POST";
                String commentId = String.valueOf(comment.getId());

                CommentReportInfoCreate commentReportInfoCreate = CommentReportInfoCreate.builder()
                        .type(ReportType.ETC)
                        .reason("report test")
                        .build();

                Assertions.assertThrows(IricomException.class, () -> {
                    reportService.reportComment(account, boardId, postId, commentId, commentReportInfoCreate);
                });
            }

            @Test
            @DisplayName("존재하지 않는 게시판")
            public void testCase09() throws Exception {
                Account account = getAccount(common00);
                Comment comment = getComment(comment01);
                Post post = comment.getPost();

                String boardId = "NOT_EXIST_BOARD";
                String postId = String.valueOf(post.getId());
                String commentId = String.valueOf(comment.getId());

                CommentReportInfoCreate commentReportInfoCreate = CommentReportInfoCreate.builder()
                        .type(ReportType.ETC)
                        .reason("report test")
                        .build();

                Assertions.assertThrows(IricomException.class, () -> {
                    reportService.reportComment(account, boardId, postId, commentId, commentReportInfoCreate);
                });
            }
        }

        @Nested
        @DisplayName("목록 조회")
        class SearchTest {
            @Test
            @DisplayName("게시판 기준 기본 조회")
            public void getBoardSearch() throws Exception {
                Account account = getAccount(systemAdmin);
                CommentReport commentReport = getCommentReport(commentReport00);
                Board board = commentReport.getComment().getPost().getBoard();

                CommentReportInfoSearch commentReportInfoSearch = CommentReportInfoSearch.builder().build();
                CommentReportInfoList commentReportInfoList = reportService.getCommentReportInfoList(account, board, commentReportInfoSearch);
                Assertions.assertEquals(0, commentReportInfoList.getSkip());
                Assertions.assertEquals(20, commentReportInfoList.getLimit());
                Assertions.assertEquals(11, commentReportInfoList.getTotal());
            }

            @Test
            @DisplayName("게시물 기준 기본 조회")
            public void getPostSearch() throws Exception {
                Account account = getAccount(allBoardAdmin);
                CommentReport commentReport = getCommentReport(commentReport00);
                Post post = commentReport.getComment().getPost();
                Board board = post.getBoard();

                CommentReportInfoSearch commentReportInfoSearch = CommentReportInfoSearch.builder().build();
                CommentReportInfoList commentReportInfoList = reportService.getCommentReportInfoList(account, board, post, commentReportInfoSearch);
                Assertions.assertEquals(0, commentReportInfoList.getSkip());
                Assertions.assertEquals(20, commentReportInfoList.getLimit());
                Assertions.assertEquals(11, commentReportInfoList.getTotal());
            }

            @Test
            @DisplayName("댓글 기준 기본 조회")
            public void getCommentSearch() throws Exception {
                Account account = getAccount(allBoardAdmin);
                CommentReport commentReport = getCommentReport(commentReport00);
                Comment comment = commentReport.getComment();
                Post post = comment.getPost();
                Board board = post.getBoard();

                CommentReportInfoSearch commentReportInfoSearch = CommentReportInfoSearch.builder().build();
                CommentReportInfoList commentReportInfoList = reportService.getCommentReportInfoList(account, board, post, comment, commentReportInfoSearch);
                Assertions.assertEquals(0, commentReportInfoList.getSkip());
                Assertions.assertEquals(20, commentReportInfoList.getLimit());
                Assertions.assertEquals(1, commentReportInfoList.getTotal());
            }
        }
    }
}

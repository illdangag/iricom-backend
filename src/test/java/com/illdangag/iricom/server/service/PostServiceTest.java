package com.illdangag.iricom.server.service;

import com.illdangag.iricom.server.data.entity.*;
import com.illdangag.iricom.server.data.request.PostInfoCreate;
import com.illdangag.iricom.server.data.response.PostInfo;
import com.illdangag.iricom.server.exception.IricomException;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.*;
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
@DisplayName("게시물")
public class PostServiceTest extends IricomTestSuite {
    @Autowired
    private PostService postService;

    protected static final TestBoardInfo boardInfo00 = TestBoardInfo.builder()
            .title("boardInfo00").isEnabled(true)
            .adminList(Collections.singletonList(allBoardAdmin)).build();
    protected static final TestBoardInfo undisclosedBoardInfo00 = TestBoardInfo.builder()
            .title("undisclosedBoardInfo00").isEnabled(true).undisclosed(true)
            .adminList(Collections.singletonList(allBoardAdmin)).build();

    private TestAccountGroupInfo accountGroupInfo00 = TestAccountGroupInfo.builder()
            .title("accountGroupInfo00").description("description")
            .accountList(Arrays.asList(common00)).boardList(Arrays.asList(undisclosedBoardInfo00))
            .build();

    protected static final TestPostInfo postInfo00 = TestPostInfo.builder()
            .title("postInfo00").content("content").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(boardInfo00).build();
    protected static final TestPostInfo undisclosedPost00 = TestPostInfo.builder()
            .title("undisclosedPost00").content("content").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(undisclosedBoardInfo00).build();
    protected static final TestPostInfo reportedPost00 = TestPostInfo.builder()
            .title("reportedPost00").content("report contents").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(boardInfo00).build();

    protected static final TestPostReportInfo postReport00 = TestPostReportInfo.builder()
            .type(ReportType.HATE).reason("test post report")
            .reportAccount(common00).post(reportedPost00)
            .build();
    protected static final TestPostReportInfo postReport01 = TestPostReportInfo.builder()
            .type(ReportType.POLITICAL).reason("test post report")
            .reportAccount(common01).post(reportedPost00)
            .build();
    protected static final TestPostReportInfo postReport02 = TestPostReportInfo.builder()
            .type(ReportType.PORNOGRAPHY).reason("test post report")
            .reportAccount(common02).post(reportedPost00)
            .build();
    protected static final TestPostReportInfo postReport03 = TestPostReportInfo.builder()
            .type(ReportType.ETC).reason("test post report")
            .reportAccount(common03).post(reportedPost00)
            .build();
    protected static final TestPostReportInfo postReport04 = TestPostReportInfo.builder()
            .type(ReportType.ETC).reason("test post report")
            .reportAccount(common04).post(reportedPost00)
            .build();
    protected static final TestPostReportInfo postReport05 = TestPostReportInfo.builder()
            .type(ReportType.ETC).reason("test post report")
            .reportAccount(common05).post(reportedPost00)
            .build();
    protected static final TestPostReportInfo postReport06 = TestPostReportInfo.builder()
            .type(ReportType.ETC).reason("test post report")
            .reportAccount(common06).post(reportedPost00)
            .build();
    protected static final TestPostReportInfo postReport07 = TestPostReportInfo.builder()
            .type(ReportType.ETC).reason("test post report")
            .reportAccount(common07).post(reportedPost00)
            .build();
    protected static final TestPostReportInfo postReport08 = TestPostReportInfo.builder()
            .type(ReportType.ETC).reason("test post report")
            .reportAccount(common08).post(reportedPost00)
            .build();
    protected static final TestPostReportInfo postReport09 = TestPostReportInfo.builder()
            .type(ReportType.ETC).reason("test post report")
            .reportAccount(common09).post(reportedPost00)
            .build();

    @Autowired
    public PostServiceTest(ApplicationContext context) {
        super(context);

        List<TestBoardInfo> testBoardInfoList = Arrays.asList(boardInfo00, undisclosedBoardInfo00);
        List<TestPostInfo> testPostInfoList = Arrays.asList(postInfo00, reportedPost00, undisclosedPost00);
        List<TestPostReportInfo> testPostReportInfoList = Arrays.asList(postReport00, postReport01, postReport02,
                postReport03, postReport04, postReport05, postReport06, postReport07, postReport08, postReport09);
        List<TestAccountGroupInfo> testAccountGroupInfoList = Arrays.asList(accountGroupInfo00);

        super.setBoard(testBoardInfoList);
        super.setAccountGroup(testAccountGroupInfoList);

        super.setPost(testPostInfoList);
        super.setPostReport(testPostReportInfoList);

        super.deleteAccountGroup(testAccountGroupInfoList);
    }

    @Nested
    @DisplayName("생성")
    class CreatePostTest {
        @Test
        @DisplayName("닉네임이 등록되지 않은 사용자")
        public void postUnregisteredAccount() throws Exception {
            Account account = getAccount(unknown00);
            Board board = getBoard(createBoard);

            PostInfoCreate postInfoCreate = PostInfoCreate.builder()
                    .title("Unregistered account post title")
                    .content("contents...")
                    .type(PostType.POST)
                    .build();

            Assertions.assertThrows(IricomException.class, () -> {
                postService.createPostInfo(account, board, postInfoCreate);
            });
        }

        @Test
        @DisplayName("계정 그룹에 포함되지 않은 비공개 게시판")
        public void postUndisclosedBoard() throws Exception {
            Account account = getAccount(common01);
            Board board = getBoard(undisclosedBoardInfo00);

            PostInfoCreate postInfoCreate = PostInfoCreate.builder()
                    .title("Unregistered account post title")
                    .content("contents...")
                    .type(PostType.POST)
                    .build();

            Assertions.assertThrows(IricomException.class, () -> {
                postService.createPostInfo(account, board, postInfoCreate);
            });
        }

        @Test
        @DisplayName("계정 그룹에 포함된 비공개 게시판")
        public void postUndisclosedBoardInAccountGroup() throws Exception {
            Account account = getAccount(common00);
            Board board = getBoard(undisclosedBoardInfo00);

            PostInfoCreate postInfoCreate = PostInfoCreate.builder()
                    .title("Unregistered account post title")
                    .content("contents...")
                    .type(PostType.POST)
                    .build();

            postService.createPostInfo(account, board, postInfoCreate);
        }
    }

    @Nested
    @DisplayName("조회")
    class Get {

        @Test
        @DisplayName("공개된 게시판의 게시물을 권한 없이 조회")
        public void getDisclosedBoardPost() throws Exception {
            Post post = getPost(postInfo00);

            PostInfo postInfo = postService.getPostInfo(post, PostState.PUBLISH, true);

            Assertions.assertNotNull(postInfo);
        }

        @Test
        @DisplayName("비공개 게시판의 게시물을 권한 없이 조회")
        public void getUndisclosedBoardPost() throws Exception {
            Post post = getPost(undisclosedPost00);

            Assertions.assertThrows(IricomException.class, () -> {
                postService.getPostInfo(post, PostState.PUBLISH, true);
            });
        }
    }

    @Nested
    @DisplayName("목록 조회")
    class Search {

    }
}

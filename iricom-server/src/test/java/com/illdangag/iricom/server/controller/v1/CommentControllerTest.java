package com.illdangag.iricom.server.controller.v1;

import com.illdangag.iricom.server.data.entity.type.PostState;
import com.illdangag.iricom.server.data.entity.type.PostType;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestCommentInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestPostInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("controller: 댓글")
public class CommentControllerTest extends IricomTestSuite {
    @Autowired
    MockMvc mockMvc;

    private final TestBoardInfo testBoardInfo00 = TestBoardInfo.builder()
            .title("testBoardInfo00").isEnabled(true).build();
    private final TestBoardInfo testDisableBoardInfo00 = TestBoardInfo.builder()
            .title("testDisableBoardInfo00").isEnabled(false).build();
    private final TestBoardInfo testVoteBoardInfo00 = TestBoardInfo.builder()
            .title("testVoteBoardInfo00").isEnabled(true).build();
    private final TestBoardInfo testVoteBoardInfo01 = TestBoardInfo.builder()
            .title("testVoteBoardInfo01").isEnabled(false).build();
    private final TestBoardInfo testVoteBoardInfo02 = TestBoardInfo.builder()
            .title("testVoteBoardInfo02").isEnabled(true).build();
    private final TestBoardInfo testReportBoardInfo00 = TestBoardInfo.builder()
            .title("testReportBoardInfo00").isEnabled(true).build();

    private final TestPostInfo testPostInfo00 = TestPostInfo.builder()
            .title("testPostInfo00").content("testPostInfo00").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(testBoardInfo00).build();
    private final TestPostInfo testPostInfo01 = TestPostInfo.builder()
            .title("testPostInfo01").content("testPostInfo01").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.TEMPORARY)
            .creator(common00).board(testBoardInfo00).build();
    private final TestPostInfo testPostInfo02 = TestPostInfo.builder()
            .title("testPostInfo02").content("testPostInfo02").isAllowComment(false)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(testBoardInfo00).build();
    private final TestPostInfo testPostInfo03 = TestPostInfo.builder()
            .title("testPostInfo03").content("testPostInfo03").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(testDisableBoardInfo00).build();
    private final TestPostInfo testPostInfo04 = TestPostInfo.builder()
            .title("testPostInfo04").content("testPostInfo04").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(testBoardInfo00).build();
    private final TestPostInfo deletePostInfo00 = TestPostInfo.builder()
            .title("testPostInfo05").content("testPostInfo05").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(testBoardInfo00).build();
    private final TestPostInfo votePostInfo00 = TestPostInfo.builder()
            .title("votePostInfo00").content("votePostInfo00").isAllowComment(true)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(testVoteBoardInfo00).build();
    private final TestPostInfo votePostInfo01 = TestPostInfo.builder()
            .title("votePostInfo01").content("votePostInfo00").isAllowComment(false)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(testVoteBoardInfo00).build();
    private final TestPostInfo votePostInfo02 = TestPostInfo.builder()
            .title("votePostInfo01").content("votePostInfo00").isAllowComment(false)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(testVoteBoardInfo01).build();
    private final TestPostInfo reportPostInfo00 = TestPostInfo.builder()
            .title("reportPostInfo00").content("reportPostInfo00").isAllowComment(false)
            .postType(PostType.POST).postState(PostState.PUBLISH)
            .creator(common00).board(testReportBoardInfo00).build();

    private final TestCommentInfo testCommentInfo00 = TestCommentInfo.builder()
            .content("testCommentInfo").creator(common00).post(testPostInfo00)
            .build();
    private final TestCommentInfo testCommentInfo01 = TestCommentInfo.builder()
            .content("testCommentInfo01").creator(common00).post(testPostInfo00)
            .build();
    private final TestCommentInfo testCommentInfo02 = TestCommentInfo.builder()
            .content("testCommentInfo02").creator(common00).post(testPostInfo03)
            .build();
    private final TestCommentInfo testCommentInfo03 = TestCommentInfo.builder()
            .content("testCommentInfo03").creator(common00).post(testPostInfo00)
            .build();
    private final TestCommentInfo testCommentInfo04 = TestCommentInfo.builder()
            .content("testCommentInfo04").creator(common00).post(testPostInfo04)
            .build();
    private final TestCommentInfo testCommentInfo05 = TestCommentInfo.builder()
            .content("testCommentInfo05").creator(common00).post(testPostInfo04)
            .referenceComment(testCommentInfo04).build();
    private final TestCommentInfo deleteCommentInfo00 = TestCommentInfo.builder()
            .content("deleteCommentInfo00").creator(common00).post(deletePostInfo00)
            .build();
    private final TestCommentInfo deleteCommentInfo01 = TestCommentInfo.builder()
            .content("deleteCommentInfo01").creator(common00).post(deletePostInfo00)
            .build();
    private final TestCommentInfo deleteCommentInfo02 = TestCommentInfo.builder()
            .content("deleteCommentInfo01").creator(common00).post(deletePostInfo00)
            .referenceComment(deleteCommentInfo01).build();
    private final TestCommentInfo deleteCommentInfo03 = TestCommentInfo.builder()
            .content("deleteCommentInfo03").creator(common00).post(deletePostInfo00)
            .referenceComment(deleteCommentInfo01).build();
    private final TestCommentInfo deletedCommentInfo00 = TestCommentInfo.builder()
            .content("deleteCommentInfo03").creator(common00).post(deletePostInfo00)
            .deleted(true).build();
    private final TestCommentInfo voteCommentInfo00 = TestCommentInfo.builder()
            .content("voteCommentInfo00").creator(common00).post(votePostInfo00)
            .build();
    private final TestCommentInfo voteCommentInfo01 = TestCommentInfo.builder()
            .content("voteCommentInfo01").creator(common00).post(votePostInfo00)
            .referenceComment(voteCommentInfo00).build();
    private final TestCommentInfo voteCommentInfo02 = TestCommentInfo.builder()
            .content("voteCommentInfo02").creator(common00).post(votePostInfo00)
            .build();
    private final TestCommentInfo voteCommentInfo03 = TestCommentInfo.builder()
            .content("voteCommentInfo03").creator(common00).post(votePostInfo00)
            .build();
    private final TestCommentInfo voteCommentInfo04 = TestCommentInfo.builder()
            .content("voteCommentInfo04").creator(common00).post(votePostInfo01)
            .build();
    private final TestCommentInfo voteCommentInfo05 = TestCommentInfo.builder()
            .content("voteCommentInfo05").creator(common00).post(votePostInfo02)
            .build();
    private final TestCommentInfo voteCommentInfo06 = TestCommentInfo.builder()
            .content("voteCommentInfo06").creator(common00).post(votePostInfo00)
            .build();
    private final TestCommentInfo voteCommentInfo07 = TestCommentInfo.builder()
            .content("voteCommentInfo07").creator(common00).post(votePostInfo00)
            .build();

    @Autowired
    public CommentControllerTest(ApplicationContext context) {
        super(context);

        addTestBoardInfo(testBoardInfo00, testDisableBoardInfo00, testVoteBoardInfo00,
                testVoteBoardInfo01, testVoteBoardInfo02, testReportBoardInfo00);
        addTestPostInfo(testPostInfo00, testPostInfo01, testPostInfo02, testPostInfo03,
                testPostInfo04, deletePostInfo00, votePostInfo00, votePostInfo01, votePostInfo02, reportPostInfo00);
        addTestCommentInfo(testCommentInfo00, testCommentInfo01, testCommentInfo02,
                testCommentInfo03, testCommentInfo04, testCommentInfo05, deleteCommentInfo00, deleteCommentInfo01,
                deleteCommentInfo02, deletedCommentInfo00, deleteCommentInfo03, voteCommentInfo00, voteCommentInfo01,
                voteCommentInfo02, voteCommentInfo03, voteCommentInfo04, voteCommentInfo05, voteCommentInfo06,
                voteCommentInfo07);

        init();
    }

    @Nested
    @DisplayName("생성")
    class CreateTest {

        @Test
        @DisplayName("기본")
        public void createComment() throws Exception {
            String boardId = getBoardId(testPostInfo00.getBoard());
            String postId = getPostId(testPostInfo00);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("content", "comment");

            MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts/{postId}/comments", boardId, postId)
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.content").exists())
                    .andExpect(jsonPath("$.referenceCommentId").doesNotExist())
                    .andDo(print());
        }

        @Test
        @DisplayName("대댓글")
        public void createReferenceComment() throws Exception {
            String commentId = getCommentId(testCommentInfo00);
            String postId = getPostId(testCommentInfo00.getPost());
            String boardId = getBoardId(testCommentInfo00.getPost().getBoard());

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("content", "reference_content");
            requestBody.put("referenceCommentId", commentId);

            MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts/{postId}/comments", boardId, postId)
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common01);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.content").exists())
                    .andExpect(jsonPath("$.referenceCommentId").exists())
                    .andDo(print());
        }

        @Test
        @DisplayName("발행되지 않은 게시물에 댓글")
        public void createCommentTemporaryPost() throws Exception {
            String boardId = getBoardId(testPostInfo00.getBoard());
            String postId = getPostId(testPostInfo01);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("content", "reference_content");

            MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts/{postId}/comments", boardId, postId)
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common01);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(404))
                    .andExpect(jsonPath("$.code").value("04000005"))
                    .andDo(print());
        }

        @Test
        @DisplayName("댓글을 허용하지 않는 게시물에 댓글")
        public void notAllowCommentPost() throws Exception {
            String boardId = getBoardId(testPostInfo00.getBoard());
            String postId = getPostId(testPostInfo02);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("content", "reference_content");

            MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts/{postId}/comments", boardId, postId)
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common01);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("05000002"))
                    .andDo(print());
        }

        @Test
        @DisplayName("비활성화 게시판의 게시물에 댓글")
        public void disabledBoard() throws Exception {
            String boardId = getBoardId(testPostInfo03.getBoard());
            String postId = getPostId(testPostInfo03);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("content", "new_content");

            MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts/{postId}/comments", boardId, postId)
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("03000001"))
                    .andExpect(jsonPath("$.message").value("Board is disabled."))
                    .andDo(print());
        }

        @Test
        @DisplayName("다른 게시판에 존재하는 게시물의 댓글")
        public void testCase05() throws Exception {
            String boardId = getBoardId(testBoardInfo00);
            String postId = getPostId(testPostInfo03);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("content", "new_content");

            MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts/{postId}/comments", boardId, postId)
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(404))
                    .andExpect(jsonPath("$.code").value("04000000"))
                    .andExpect(jsonPath("$.message").value("Not exist post."))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("수정")
    class UpdateTest {

        @Test
        @DisplayName("기본")
        public void update() throws Exception {
            String commentId = getCommentId(testCommentInfo01);
            String postId = getPostId(testCommentInfo01.getPost());
            String boardId = getBoardId(testCommentInfo01.getPost().getBoard());

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("content", "update_comment");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}", boardId, postId, commentId)
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.content").value("update_comment"))
                    .andDo(print());
        }

        @Test
        @DisplayName("존재하지 않는 댓글")
        public void notExistPost() throws Exception {
            String boardId = getBoardId(testPostInfo00.getBoard());
            String postId = getPostId(testPostInfo00);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("content", "update_comment");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/comments/unknown", boardId, postId)
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(404))
                    .andExpect(jsonPath("$.code").value("05000000"))
                    .andDo(print());
        }

        @Test
        @DisplayName("비활성화 게시판의 게시물에 댓글")
        public void disabledBoard() throws Exception {
            String commentId = getCommentId(testCommentInfo02);
            String postId = getPostId(testCommentInfo02.getPost());
            String boardId = getBoardId(testCommentInfo02.getPost().getBoard());

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("content", "update_comment");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}", boardId, postId, commentId)
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("03000001"))
                    .andExpect(jsonPath("$.message").value("Board is disabled."))
                    .andDo(print());
        }

        @Test
        @DisplayName("다른 사람의 댓글 수정")
        public void updateOtherCreator() throws Exception {
            String commentId = getCommentId(testCommentInfo03);
            String postId = getPostId(testCommentInfo03.getPost());
            String boardId = getBoardId(testCommentInfo03.getPost().getBoard());

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("content", "update_comment");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}", boardId, postId, commentId)
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common01);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(401))
                    .andExpect(jsonPath("$.code").value("05000003"))
                    .andExpect(jsonPath("$.message").value("Invalid authorization."))
                    .andDo(print());
        }

        @Test
        @DisplayName("다른 게시판에 존재하는 게시물의 댓글")
        public void updateOtherPost() throws Exception {
            String commentId = getCommentId(testCommentInfo03);
            String postId = getPostId(testCommentInfo03.getPost());
            String boardId = getBoardId(testDisableBoardInfo00);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("content", "update_comment");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}", boardId, postId, commentId)
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(404))
                    .andExpect(jsonPath("$.code").value("04000000"))
                    .andExpect(jsonPath("$.message").value("Not exist post."))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("조회")
    class GetTest {

        @Test
        @DisplayName("목록 조회")
        public void getList() throws Exception {
            String boardId = getBoardId(testPostInfo00.getBoard());
            String postId = getPostId(testPostInfo00);

            MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/{boardId}/posts/{postId}/comments", boardId, postId);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.total").exists())
                    .andExpect(jsonPath("$.skip").value(0))
                    .andExpect(jsonPath("$.limit").value(20))
                    .andExpect(jsonPath("$.comments").isArray())
                    .andDo(print());
        }

        @Test
        @DisplayName("대댓글 포함 조회")
        public void getListIncludeReferenceComment() throws Exception {
            String boardId = getBoardId(testPostInfo00.getBoard());
            String postId = getPostId(testPostInfo04);

            MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/{boardId}/posts/{postId}/comments", boardId, postId)
                    .param("includeComment", "true");
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.total").exists())
                    .andExpect(jsonPath("$.skip").value(0))
                    .andExpect(jsonPath("$.limit").value(20))
                    .andExpect(jsonPath("$.comments").isArray())
                    .andExpect(jsonPath("$.comments[0].nestedComments").isArray())
                    .andDo(print());
        }

        @Test
        @DisplayName("skip")
        public void skip() throws Exception {
            String boardId = getBoardId(testPostInfo00.getBoard());
            String postId = getPostId(testPostInfo04);

            MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/{boardId}/posts/{postId}/comments", boardId, postId)
                    .param("skip", "1");
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.total").value(1))
                    .andExpect(jsonPath("$.skip").value(1))
                    .andExpect(jsonPath("$.limit").value(20))
                    .andExpect(jsonPath("$.comments").isArray())
                    .andDo(print());
        }

        @Test
        @DisplayName("limit")
        public void limit() throws Exception {
            String boardId = getBoardId(testPostInfo00.getBoard());
            String postId = getPostId(testPostInfo04);

            MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/{boardId}/posts/{postId}/comments", boardId, postId)
                    .param("limit", "1");
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.total").exists())
                    .andExpect(jsonPath("$.skip").value(0))
                    .andExpect(jsonPath("$.limit").value(1))
                    .andExpect(jsonPath("$.comments").isArray())
                    .andExpect(jsonPath("$.comments", hasSize(1)))
                    .andDo(print());
        }

        @Test
        @DisplayName("대댓글 기준")
        public void getReferenceComment() throws Exception {
            String commentId = getCommentId(testCommentInfo04);
            String postId = getPostId(testCommentInfo04.getPost());
            String boardId = getBoardId(testCommentInfo04.getPost().getBoard());

            MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/{boardId}/posts/{postId}/comments", boardId, postId)
                    .param("referenceCommentId", String.valueOf(commentId));
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.total").value(1))
                    .andExpect(jsonPath("$.skip").value(0))
                    .andExpect(jsonPath("$.limit").value(20))
                    .andExpect(jsonPath("$.comments").isArray())
                    .andExpect(jsonPath("$.comments", hasSize(1)))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("삭제")
    class DeleteTest {

        @Test
        @DisplayName("기본")
        public void deleteComment() throws Exception {
            String commentId = getCommentId(deleteCommentInfo00);
            String postId = getPostId(deleteCommentInfo00.getPost());
            String boardId = getBoardId(deleteCommentInfo00.getPost().getBoard());

            MockHttpServletRequestBuilder requestBuilder = delete("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}", boardId, postId, commentId);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.deleted").value(true))
                    .andDo(print());
        }

        @Test
        @DisplayName("대댓글")
        public void deleteReferenceComment() throws Exception {
            String commentId = getCommentId(deleteCommentInfo02);
            String postId = getPostId(deleteCommentInfo02.getPost());
            String boardId = getBoardId(deleteCommentInfo02.getPost().getBoard());

            MockHttpServletRequestBuilder requestBuilder = delete("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}", boardId, postId, commentId);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.deleted").value(true))
                    .andDo(print());
        }

        @Test
        @DisplayName("이미 삭제한 댓글")
        public void deletedComment() throws Exception {
            String commentId = getCommentId(deletedCommentInfo00);
            String postId = getPostId(deletedCommentInfo00.getPost());
            String boardId = getBoardId(deletedCommentInfo00.getPost().getBoard());

            MockHttpServletRequestBuilder requestBuilder = delete("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}", boardId, postId, commentId);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.deleted").value(true))
                    .andDo(print());
        }

        @Test
        @DisplayName("다른 사람이 작성한 댓글 삭제")
        public void deleteOtherComment() throws Exception {
            String commentId = getCommentId(deleteCommentInfo03);
            String postId = getPostId(deleteCommentInfo03.getPost());
            String boardId = getBoardId(deleteCommentInfo03.getPost().getBoard());

            MockHttpServletRequestBuilder requestBuilder = delete("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}", boardId, postId, commentId);
            setAuthToken(requestBuilder, common01);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(401))
                    .andExpect(jsonPath("$.code").value("05000004"))
                    .andExpect(jsonPath("$.message").value("Invalid authorization."))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("댓글 투표")
    class VoteTest {
        @Test
        @DisplayName("좋아요")
        public void upvote() throws Exception {
            String commentId = getCommentId(voteCommentInfo00);
            String postId = getPostId(voteCommentInfo00.getPost());
            String boardId = getBoardId(voteCommentInfo00.getPost().getBoard());

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "upvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}/vote", boardId, postId, commentId)
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.upvote").value(1))
                    .andDo(print());
        }

        @Test
        @DisplayName("싫어요")
        public void downvote() throws Exception {
            String commentId = getCommentId(voteCommentInfo00);
            String postId = getPostId(voteCommentInfo00.getPost());
            String boardId = getBoardId(voteCommentInfo00.getPost().getBoard());

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "downvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}/vote", boardId, postId, commentId)
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.downvote").value(1))
                    .andDo(print());
        }

        @Test
        @DisplayName("대댓글에 좋아요")
        public void upvoteReferenceComment() throws Exception {
            String commentId = getCommentId(voteCommentInfo01);
            String postId = getPostId(voteCommentInfo01.getPost());
            String boardId = getBoardId(voteCommentInfo01.getPost().getBoard());

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "upvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}/vote", boardId, postId, commentId)
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.upvote").value(1))
                    .andDo(print());
        }

        @Test
        @DisplayName("대댓글에 싫어요")
        public void downvoteReferenceComment() throws Exception {
            String commentId = getCommentId(voteCommentInfo01);
            String postId = getPostId(voteCommentInfo01.getPost());
            String boardId = getBoardId(voteCommentInfo01.getPost().getBoard());

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "downvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}/vote", boardId, postId, commentId)
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.downvote").value(1))
                    .andDo(print());
        }

        @Test
        @DisplayName("중복 좋아요")
        public void duplicateUpvote() throws Exception {
            String commentId = getCommentId(voteCommentInfo02);
            String postId = getPostId(voteCommentInfo02.getPost());
            String boardId = getBoardId(voteCommentInfo02.getPost().getBoard());

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "upvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}/vote", boardId, postId, commentId)
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder);

            requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}/vote", boardId, postId, commentId)
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("05000005"))
                    .andDo(print());
        }

        @Test
        @DisplayName("중복 싫어요")
        public void duplicateDownvote() throws Exception {
            String commentId = getCommentId(voteCommentInfo02);
            String postId = getPostId(voteCommentInfo02.getPost());
            String boardId = getBoardId(voteCommentInfo02.getPost().getBoard());

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "downvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}/vote", boardId, postId, commentId)
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder);

            requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}/vote", boardId, postId, commentId)
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("05000005"))
                    .andDo(print());
        }

        @Test
        @DisplayName("올바르지 않은 요청")
        public void invalidType() throws Exception {
            String commentId = getCommentId(voteCommentInfo03);
            String postId = getPostId(voteCommentInfo03.getPost());
            String boardId = getBoardId(voteCommentInfo03.getPost().getBoard());

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "unknown");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}/vote", boardId, postId, commentId)
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("01020000"))
                    .andDo(print());
        }

        @Test
        @DisplayName("댓글을 허용하지 않은 게시물")
        public void notAllowCommentPost() throws Exception {
            String commentId = getCommentId(voteCommentInfo04);
            String postId = getPostId(voteCommentInfo04.getPost());
            String boardId = getBoardId(voteCommentInfo04.getPost().getBoard());

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "upvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}/vote", boardId, postId, commentId)
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("05000002"))
                    .andDo(print());
        }

        @Test
        @DisplayName("비활성화 게시판의 게시물의 댓글")
        public void disabledBoard() throws Exception {
            String commentId = getCommentId(voteCommentInfo05);
            String postId = getPostId(voteCommentInfo05.getPost());
            String boardId = getBoardId(voteCommentInfo05.getPost().getBoard());

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "upvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}/vote", boardId, postId, commentId)
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("03000001"))
                    .andExpect(jsonPath("$.message").value("Board is disabled."))
                    .andDo(print());
        }

        @Test
        @DisplayName("해당 게시판에 존재하지 않는 게시물")
        public void notExistPost() throws Exception {
            String commentId = getCommentId(voteCommentInfo06);
            String postId = getPostId(voteCommentInfo06.getPost());
            String boardId = getBoardId(testVoteBoardInfo02);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "upvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}/vote", boardId, postId, commentId)
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(404))
                    .andExpect(jsonPath("$.code").value("04000000"))
                    .andExpect(jsonPath("$.message").value("Not exist post."))
                    .andDo(print());
        }

        @Test
        @DisplayName("다른 게시물의 댓글")
        public void otherPostComment() throws Exception {
            String commentId = getCommentId(voteCommentInfo07);
            String boardId = getBoardId(votePostInfo02.getBoard());
            String postId = getPostId(votePostInfo02);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "upvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}/vote", boardId, postId, commentId)
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(404))
                    .andExpect(jsonPath("$.code").value("05000000"))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("신고된 댓글")
    class ReportTest {
        @Test
        @DisplayName("신고된 댓글 조회")
        public void testCase00() throws Exception {
            String boardId = getBoardId(reportPostInfo00.getBoard());
            String postId = getPostId(reportPostInfo00);

            MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/{boardId}/posts/{postId}/comments", boardId, postId);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.total").value(0))
                    .andExpect(jsonPath("$.skip").value(0))
                    .andExpect(jsonPath("$.limit").value(20))
                    .andExpect(jsonPath("$.comments").isArray())
                    .andDo(print());
        }
    }
}

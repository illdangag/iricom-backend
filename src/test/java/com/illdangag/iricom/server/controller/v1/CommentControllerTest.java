package com.illdangag.iricom.server.controller.v1;

import com.illdangag.iricom.server.data.entity.Board;
import com.illdangag.iricom.server.data.entity.Comment;
import com.illdangag.iricom.server.data.entity.Post;
import com.illdangag.iricom.server.test.IricomTestSuite;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
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

@DisplayName("댓글 테스트")
public class CommentControllerTest extends IricomTestSuite {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    public CommentControllerTest(ApplicationContext context) {
        super(context);
    }

    @Nested
    @DisplayName("생성")
    class CreateTest {

        @Test
        @Order(0)
        @DisplayName("기본")
        public void testCase00() throws Exception {
            Board board = getBoard(commentBoard);
            Post post = getPost(commentUpdatePost00);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("content", "new_content");

            MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/comments")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.comment").exists())
                    .andExpect(jsonPath("$.referenceCommentId").doesNotExist())
                    .andDo(print());
        }

        @Test
        @Order(1)
        @DisplayName("대댓글")
        public void testCase01() throws Exception {
            Board board = getBoard(commentBoard);
            Post post = getPost(commentUpdatePost00);
            Comment comment = getComment(commentUpdateComment00);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("content", "reference_content");
            requestBody.put("referenceCommentId", comment.getId().toString());

            MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/comments")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common01);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.comment").exists())
                    .andExpect(jsonPath("$.referenceCommentId").exists())
                    .andDo(print());
        }

        @Test
        @Order(2)
        @DisplayName("발행되지 않은 게시물에 댓글")
        public void testCase02() throws Exception {
            Board board = getBoard(commentBoard);
            Post post = getPost(commentUpdatePost01);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("content", "reference_content");

            MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/comments")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common01);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(404))
                    .andExpect(jsonPath("$.code").value("04000002"))
                    .andDo(print());
        }

        @Test
        @Order(3)
        @DisplayName("댓글을 허용하지 않는 게시물에 댓글")
        public void testCase03() throws Exception {
            Board board = getBoard(commentBoard);
            Post post = getPost(commentUpdatePost02);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("content", "reference_content");

            MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/comments")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common01);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("05000002"))
                    .andDo(print());
        }

        @Test
        @Order(4)
        @DisplayName("비활성화 게시판의 게시물에 댓글")
        public void testCase04() throws Exception {
            Board board = getBoard(disableBoard);
            Post post = getPost(disableBoardPost00);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("content", "new_content");

            MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/comments")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("05000003"))
                    .andDo(print());
        }

        @Test
        @Order(5)
        @DisplayName("다른 게시판에 존재하는 게시물의 댓글")
        public void testCase05() throws Exception {
            Board board = getBoard(enableBoard);
            Post post = getPost(commentUpdatePost00);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("content", "new_content");

            MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/comments")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(404))
                    .andExpect(jsonPath("$.code").value("04000002"))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("수정")
    class UpdateTest {

        @Test
        @Order(0)
        @DisplayName("기본")
        public void testCase00() throws Exception {
            Board board = getBoard(commentBoard);
            Post post = getPost(commentUpdatePost00);
            Comment comment = getComment(commentUpdateComment00);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("content", "update_comment");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/comments/" + comment.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.comment").value("update_comment"))
                    .andDo(print());
        }

        @Test
        @Order(1)
        @DisplayName("존재하지 않는 댓글")
        public void testCase01() throws Exception {
            Board board = getBoard(commentBoard);
            Post post = getPost(commentUpdatePost00);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("content", "update_comment");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/comments/unknown")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(404))
                    .andExpect(jsonPath("$.code").value("05000000"))
                    .andDo(print());
        }

        @Test
        @Order(2)
        @DisplayName("비활성화 게시판의 게시물에 댓글")
        public void testCase02() throws Exception {
            Board board = getBoard(disableBoard);
            Post post = getPost(disableBoardPost00);
            Comment comment = getComment(disableBoardComment00);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("content", "update_comment");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/comments/" + comment.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("05000003"))
                    .andDo(print());
        }

        @Test
        @Order(3)
        @DisplayName("다른 사람의 댓글 수정")
        public void testCase03() throws Exception {
            Board board = getBoard(commentBoard);
            Post post = getPost(commentUpdatePost00);
            Comment comment = getComment(commentUpdateComment00);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("content", "update_comment");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/comments/" + comment.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common01);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(401))
                    .andExpect(jsonPath("$.code").value("05000004"))
                    .andDo(print());
        }

        @Test
        @Order(4)
        @DisplayName("다른 게시판에 존재하는 게시물의 댓글")
        public void testCase04() throws Exception {
            Board board = getBoard(enableBoard);
            Post post = getPost(commentUpdatePost00);
            Comment comment = getComment(enableBoardComment00);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("content", "update_comment");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/comments/" + comment.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(404))
                    .andExpect(jsonPath("$.code").value("04000002"))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("조회")
    class GetTest {

        @Test
        @Order(0)
        @DisplayName("목록 조회")
        public void testCase00() throws Exception {
            Board board = getBoard(commentBoard);
            Post post = getPost(commentGetPost00);

            MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/comments");
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.total").value(3))
                    .andExpect(jsonPath("$.skip").value(0))
                    .andExpect(jsonPath("$.limit").value(20))
                    .andExpect(jsonPath("$.comments").isArray())
                    .andExpect(jsonPath("$.comments", hasSize(3)))
                    .andDo(print());
        }

        @Test
        @Order(1)
        @DisplayName("대댓글 포함 조회")
        public void testCase01() throws Exception {
            Board board = getBoard(commentBoard);
            Post post = getPost(commentGetPost00);

            MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/comments")
                    .param("includeComment", "true");
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.total").value(3))
                    .andExpect(jsonPath("$.skip").value(0))
                    .andExpect(jsonPath("$.limit").value(20))
                    .andExpect(jsonPath("$.comments").isArray())
                    .andExpect(jsonPath("$.comments", hasSize(3)))
                    .andExpect(jsonPath("$.comments[0].nestedComments").isArray())
                    .andExpect(jsonPath("$.comments[0].nestedComments", hasSize(4)))
                    .andDo(print());
        }

        @Test
        @Order(2)
        @DisplayName("skip")
        public void testCase02() throws Exception {
            Board board = getBoard(commentBoard);
            Post post = getPost(commentGetPost00);

            MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/comments")
                    .param("skip", "1");
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.total").value(3))
                    .andExpect(jsonPath("$.skip").value(1))
                    .andExpect(jsonPath("$.limit").value(20))
                    .andExpect(jsonPath("$.comments").isArray())
                    .andExpect(jsonPath("$.comments", hasSize(2)))
                    .andDo(print());
        }

        @Test
        @Order(3)
        @DisplayName("limit")
        public void testCase03() throws Exception {
            Board board = getBoard(commentBoard);
            Post post = getPost(commentGetPost00);

            MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/comments")
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
        @Order(4)
        @DisplayName("대댓글 기준")
        public void testCase04() throws Exception {
            Board board = getBoard(commentBoard);
            Post post = getPost(commentGetPost00);
            Comment comment = getComment(commentGetComment00);

            MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/comments")
                    .param("referenceCommentId", "" + comment.getId());
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.total").value(4))
                    .andExpect(jsonPath("$.skip").value(0))
                    .andExpect(jsonPath("$.limit").value(20))
                    .andExpect(jsonPath("$.comments").isArray())
                    .andExpect(jsonPath("$.comments", hasSize(4)))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("삭제")
    class DeleteTest {

        @Test
        @Order(0)
        @DisplayName("기본")
        public void testCase00() throws Exception {
            Board board = getBoard(commentBoard);
            Post post = getPost(commentUpdatePost00);
            Comment comment = getComment(commentDeleteComment00);

            MockHttpServletRequestBuilder requestBuilder = delete("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/comments/" + comment.getId());
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.deleted").value(true))
                    .andDo(print());
        }

        @Test
        @Order(1)
        @DisplayName("대댓글")
        public void testCase01() throws Exception {
            Board board = getBoard(commentBoard);
            Post post = getPost(commentUpdatePost00);
            Comment comment = getComment(commentDeleteComment01);

            MockHttpServletRequestBuilder requestBuilder = delete("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/comments/" + comment.getId());
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.deleted").value(true))
                    .andDo(print());
        }

        @Test
        @Order(2)
        @DisplayName("삭제한 댓글")
        public void testCase02() throws Exception {
            Board board = getBoard(commentBoard);
            Post post = getPost(commentUpdatePost00);
            Comment comment = getComment(commentDeleteComment02);

            MockHttpServletRequestBuilder requestBuilder = delete("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/comments/" + comment.getId());
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.deleted").value(true))
                    .andDo(print());
        }

        @Test
        @Order(3)
        @DisplayName("다른 사람이 작성한 댓글 삭제")
        public void testCase03() throws Exception {
            Board board = getBoard(commentBoard);
            Post post = getPost(commentUpdatePost00);
            Comment comment = getComment(commentDeleteComment03);

            MockHttpServletRequestBuilder requestBuilder = delete("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/comments/" + comment.getId());
            setAuthToken(requestBuilder, common01);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(401))
                    .andExpect(jsonPath("$.code").value("05000005"))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("댓글 투표")
    class VoteTest {
        @Test
        @Order(0)
        @DisplayName("좋아요")
        public void testCase00() throws Exception {
            Board board = getBoard(voteBoard);
            Post post = getPost(voteCommentPost00);
            Comment comment = getComment(voteComment00);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "upvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/comments/" + comment.getId() + "/vote")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.upvote").value(1))
                    .andDo(print());
        }

        @Test
        @Order(1)
        @DisplayName("싫어요")
        public void testCase01() throws Exception {
            Board board = getBoard(voteBoard);
            Post post = getPost(voteCommentPost00);
            Comment comment = getComment(voteComment00);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "downvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/comments/" + comment.getId() + "/vote")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.downvote").value(1))
                    .andDo(print());
        }

        @Test
        @Order(2)
        @DisplayName("대댓글에 좋아요")
        public void testCase02() throws Exception {
            Board board = getBoard(voteBoard);
            Post post = getPost(voteCommentPost00);
            Comment comment = getComment(voteComment01);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "upvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/comments/" + comment.getId() + "/vote")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.upvote").value(1))
                    .andDo(print());
        }

        @Test
        @Order(3)
        @DisplayName("대댓글에 싫어요")
        public void testCase03() throws Exception {
            Board board = getBoard(voteBoard);
            Post post = getPost(voteCommentPost00);
            Comment comment = getComment(voteComment01);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "downvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/comments/" + comment.getId() + "/vote")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.downvote").value(1))
                    .andDo(print());
        }

        @Test
        @Order(4)
        @DisplayName("중복 좋아요")
        public void testCase04() throws Exception {
            Board board = getBoard(voteBoard);
            Post post = getPost(voteCommentPost00);
            Comment comment = getComment(voteComment02);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "upvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/comments/" + comment.getId() + "/vote")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder);

            requestBuilder = patch("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/comments/" + comment.getId() + "/vote")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("05000006"))
                    .andDo(print());
        }

        @Test
        @Order(5)
        @DisplayName("중복 싫어요")
        public void testCase05() throws Exception {
            Board board = getBoard(voteBoard);
            Post post = getPost(voteCommentPost00);
            Comment comment = getComment(voteComment02);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "downvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/comments/" + comment.getId() + "/vote")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder);

            requestBuilder = patch("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/comments/" + comment.getId() + "/vote")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("05000006"))
                    .andDo(print());
        }

        @Test
        @Order(6)
        @DisplayName("올바르지 않은 요청")
        public void testCase06() throws Exception {
            Board board = getBoard(voteBoard);
            Post post = getPost(voteCommentPost00);
            Comment comment = getComment(voteComment00);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "unknown");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/comments/" + comment.getId() + "/vote")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("04000007"))
                    .andDo(print());
        }

        @Test
        @Order(7)
        @DisplayName("댓글을 허용하지 않은 게시물")
        public void testCase07() throws Exception {
            Board board = getBoard(voteBoard);
            Post post = getPost(voteCommentPost01);
            Comment comment = getComment(voteComment03);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "upvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/comments/" + comment.getId() + "/vote")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("05000002"))
                    .andDo(print());
        }

        @Test
        @Order(8)
        @DisplayName("비활성화 게시판의 게시물의 댓글")
        public void testCase08() throws Exception {
            Board board = getBoard(disableBoard);
            Post post = getPost(disableBoardPost00);
            Comment comment = getComment(voteComment04);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "upvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/comments/" + comment.getId() + "/vote")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("04000005"))
                    .andDo(print());
        }

        @Test
        @Order(9)
        @DisplayName("해당 게시판에 존재하지 않는 게시물")
        public void testCase09() throws Exception {
            Board board = getBoard(enableBoard);
            Post post = getPost(voteCommentPost00);
            Comment comment = getComment(voteComment01);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "upvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/comments/" + comment.getId() + "/vote")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(404))
                    .andExpect(jsonPath("$.code").value("03000000"))
                    .andDo(print());
        }

        @Test
        @Order(10)
        @DisplayName("다른 게시물의 댓글")
        public void testCase10() throws Exception {
            Board board = getBoard(voteBoard);
            Post post = getPost(voteCommentPost00);
            Comment comment = getComment(voteComment03);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "upvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/comments/" + comment.getId() + "/vote")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(404))
                    .andExpect(jsonPath("$.code").value("05000000"))
                    .andDo(print());
        }
    }
}

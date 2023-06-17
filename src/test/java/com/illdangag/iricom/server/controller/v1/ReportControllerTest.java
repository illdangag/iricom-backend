package com.illdangag.iricom.server.controller.v1;

import com.illdangag.iricom.server.data.entity.Board;
import com.illdangag.iricom.server.data.entity.Comment;
import com.illdangag.iricom.server.data.entity.Post;
import com.illdangag.iricom.server.test.IricomTestSuite;
import lombok.extern.slf4j.Slf4j;
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

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@DisplayName("신고")
public class ReportControllerTest extends IricomTestSuite {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    public ReportControllerTest(ApplicationContext context) {
        super(context);
    }

    @Nested
    @DisplayName("신고")
    class Report {

        @Nested
        @DisplayName("게시물")
        class ReportPost {

            @Test
            @Order(0)
            @DisplayName("게시물 신고")
            public void testCase00() throws Exception {
                Post post = getPost(reportPost03);
                Board board = post.getBoard();

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("type", "hate");
                requestBody.put("reason", "This is a hateful post.");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/report")
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.id").exists())
                        .andExpect(jsonPath("$.createDate").exists())
                        .andExpect(jsonPath("$.updateDate").exists())
                        .andExpect(jsonPath("$.type").value("hate"))
                        .andExpect(jsonPath("$.reason").value("This is a hateful post."))
                        .andExpect(jsonPath("$.post").exists())
                        .andDo(print());
            }

            @Test
            @Order(1)
            @DisplayName("게시물 중복 신고")
            public void testCase01() throws Exception {
                Post post = getPost(reportPost04);
                Board board = post.getBoard();

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("type", "hate");
                requestBody.put("reason", "This is a hateful post.");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/report")
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.id").exists())
                        .andExpect(jsonPath("$.createDate").exists())
                        .andExpect(jsonPath("$.updateDate").exists())
                        .andExpect(jsonPath("$.type").value("hate"))
                        .andExpect(jsonPath("$.reason").value("This is a hateful post."))
                        .andExpect(jsonPath("$.post").exists())
                        .andDo(print());

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(400))
                        .andExpect(jsonPath("$.code").value("06000001"))
                        .andExpect(jsonPath("$.message").value("Already report post."))
                        .andDo(print());
            }

            @Test
            @Order(2)
            @DisplayName("게시물이 포함되지 않은 게시판")
            public void testCase02() throws Exception {
                Post post = getPost(enableBoardPost00);
                Board board = getBoard(disableBoard);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("type", "hate");
                requestBody.put("reason", "This is a hateful post.");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/report")
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
            @Order(5)
            @DisplayName("존재하지 않는 게시물")
            public void testCase05() throws Exception {
                Board board = getBoard(enableBoard);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("type", "hate");
                requestBody.put("reason", "This is a hateful post.");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/" + board.getId() + "/posts/NOT_EXIST_POST/report")
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
            @Order(6)
            @DisplayName("존재하지 않는 게시판")
            public void testCase06() throws Exception {
                Post post = getPost(reportPost00);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("type", "hate");
                requestBody.put("reason", "This is a hateful post.");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/NOT_EXIST_BOARD/posts/" + post.getId() + "/report")
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(404))
                        .andExpect(jsonPath("$.code").value("03000000"))
                        .andExpect(jsonPath("$.message").value("Not exist board."))
                        .andDo(print());
            }
        }

        @Nested
        @DisplayName("댓글")
        class ReportComment {
            @Test
            @Order(0)
            @DisplayName("댓글 신고")
            public void reportComment() throws Exception {
                Comment comment = getComment(reportComment04);
                Post post = comment.getPost();
                Board board = post.getBoard();

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("type", "hate");
                requestBody.put("reason", "This is a hateful comment.");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}/report", board.getId(), post.getId(), comment.getId())
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andDo(print());
            }

            @Test
            @Order(1)
            @DisplayName("댓글 중복 신고")
            public void testCase01() throws Exception {
                Comment comment = getComment(reportComment05);
                Post post = comment.getPost();
                Board board = post.getBoard();

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("type", "hate");
                requestBody.put("reason", "This is a hateful comment.");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}/report", board.getId(), post.getId(), comment.getId())
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andDo(print());

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(400))
                        .andExpect(jsonPath("$.code").value("06010001"))
                        .andExpect(jsonPath("$.message").value("Already report comment."))
                        .andDo(print());
            }

            @Test
            @Order(2)
            @DisplayName("존재하지 않는 게시판")
            public void testCase02() throws Exception {
                Comment comment = getComment(reportComment05);
                Post post = comment.getPost();

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("type", "hate");
                requestBody.put("reason", "This is a hateful comment.");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}/report", "NOT_EXIST_BOARD", post.getId(), comment.getId())
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(404))
                        .andExpect(jsonPath("$.code").value("03000000"))
                        .andExpect(jsonPath("$.message").value("Not exist board."))
                        .andDo(print());
            }

            @Test
            @Order(3)
            @DisplayName("존재하지 않는 게시물")
            public void testCase03() throws Exception {
                Comment comment = getComment(reportComment05);
                Post post = comment.getPost();
                Board board = post.getBoard();

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("type", "hate");
                requestBody.put("reason", "This is a hateful comment.");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}/report", board.getId(), "NOT_EXIST_POST", comment.getId())
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
            @Order(4)
            @DisplayName("존재하지 않는 댓글")
            public void testCase04() throws Exception {
                Comment comment = getComment(reportComment05);
                Post post = comment.getPost();
                Board board = post.getBoard();

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("type", "hate");
                requestBody.put("reason", "This is a hateful comment.");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}/report", board.getId(), post.getId(), "NOT_EXIST_COMMENT")
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(404))
                        .andExpect(jsonPath("$.code").value("05000000"))
                        .andExpect(jsonPath("$.message").value("Not exist comment."))
                        .andDo(print());
            }

            @Test
            @Order(5)
            @DisplayName("올바르지 않은 게시판")
            public void testCase05() throws Exception {
                Comment comment = getComment(reportComment05);
                Post post = comment.getPost();
                Board board = getBoard(disableBoard);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("type", "hate");
                requestBody.put("reason", "This is a hateful comment.");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}/report", board.getId(), post.getId(), comment.getId())
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
            @Order(6)
            @DisplayName("올바르지 않은 게시물")
            public void testCase06() throws Exception {
                Comment comment = getComment(reportComment08);
                Post post = comment.getPost();
                Board board = post.getBoard();
                Post invalidPost = getPost(enableBoardPost01);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("type", "hate");
                requestBody.put("reason", "This is a hateful comment.");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}/report", board.getId(), invalidPost.getId(), comment.getId())
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(404))
                        .andExpect(jsonPath("$.code").value("05000000"))
                        .andExpect(jsonPath("$.message").value("Not exist comment."))
                        .andDo(print());
            }

            @Test
            @Order(7)
            @DisplayName("올바르지 않은 댓글")
            public void testCase07() throws Exception {
                Comment comment = getComment(reportComment05);
                Post post = comment.getPost();
                Board board = post.getBoard();
                Comment invalidComment = getComment(enableBoardComment00);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("type", "hate");
                requestBody.put("reason", "This is a hateful comment.");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}/report", board.getId(), post.getId(), invalidComment.getId())
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(404))
                        .andExpect(jsonPath("$.code").value("05000000"))
                        .andExpect(jsonPath("$.message").value("Not exist comment."))
                        .andDo(print());
            }

            @Test
            @Order(8)
            @DisplayName("비활성화 게시판")
            public void testCase08() throws Exception {
                Comment comment = getComment(reportComment06);
                Post post = comment.getPost();
                Board board = post.getBoard();

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("type", "hate");
                requestBody.put("reason", "This is a hateful comment.");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}/report", board.getId(), post.getId(), comment.getId())
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
            @Order(9)
            @DisplayName("댓글을 허용하지 않은 게시물")
            public void testCase09() throws Exception {
                Comment comment = getComment(reportComment07);
                Post post = comment.getPost();
                Board board = post.getBoard();

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("type", "hate");
                requestBody.put("reason", "This is a hateful comment.");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts/{postId}/comments/{commentId}/report", board.getId(), post.getId(), comment.getId())
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(400))
                        .andExpect(jsonPath("$.code").value("05000002"))
                        .andExpect(jsonPath("$.message").value("This post does not allow comments."))
                        .andDo(print());
            }
        }
    }
}

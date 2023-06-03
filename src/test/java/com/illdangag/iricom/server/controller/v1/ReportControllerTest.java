package com.illdangag.iricom.server.controller.v1;

import com.illdangag.iricom.server.data.entity.Board;
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
                requestBody.put("boardId", board.getId());
                requestBody.put("postId", post.getId());
                requestBody.put("type", "hate");
                requestBody.put("reason", "This is a hateful post.");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/report/post")
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.title").exists())
                        .andExpect(jsonPath("$.type").value("post"))
                        .andExpect(jsonPath("$.content").exists())
                        .andExpect(jsonPath("$.isAllowComment").exists())
                        .andDo(print());
            }

            @Test
            @Order(1)
            @DisplayName("게시물 중복 신고")
            public void testCase01() throws Exception {
                Post post = getPost(reportPost04);
                Board board = post.getBoard();

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("boardId", board.getId());
                requestBody.put("postId", post.getId());
                requestBody.put("type", "hate");
                requestBody.put("reason", "This is a hateful post.");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/report/post")
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.title").exists())
                        .andExpect(jsonPath("$.type").value("post"))
                        .andExpect(jsonPath("$.content").exists())
                        .andExpect(jsonPath("$.isAllowComment").exists())
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
                requestBody.put("boardId", board.getId());
                requestBody.put("postId", post.getId());
                requestBody.put("type", "hate");
                requestBody.put("reason", "This is a hateful post.");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/report/post")
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(404))
                        .andExpect(jsonPath("$.code").value("04000002"))
                        .andExpect(jsonPath("$.message").value("Not exist post."))
                        .andDo(print());
            }

            @Test
            @Order(3)
            @DisplayName("게시물 ID 누락")
            public void testCase03() throws Exception {
                Board board = getBoard(disableBoard);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("boardId", board.getId());
                requestBody.put("type", "hate");
                requestBody.put("reason", "This is a hateful post.");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/report/post")
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(400))
                        .andExpect(jsonPath("$.code").value("01020000"))
                        .andExpect(jsonPath("$.message").value("Post id is required."))
                        .andDo(print());
            }

            @Test
            @Order(4)
            @DisplayName("게시판 ID 누락")
            public void testCase04() throws Exception {
                Post post = getPost(reportPost00);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("postId", post.getId());
                requestBody.put("type", "hate");
                requestBody.put("reason", "This is a hateful post.");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/report/post")
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(400))
                        .andExpect(jsonPath("$.code").value("01020000"))
                        .andExpect(jsonPath("$.message").value("Board id is required."))
                        .andDo(print());
            }

            @Test
            @Order(5)
            @DisplayName("존재하지 않는 게시물")
            public void testCase05() throws Exception {
                Board board = getBoard(enableBoard);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("boardId", board.getId());
                requestBody.put("postId", "NOT_EXIST_POST");
                requestBody.put("type", "hate");
                requestBody.put("reason", "This is a hateful post.");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/report/post")
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(404))
                        .andExpect(jsonPath("$.code").value("04000002"))
                        .andExpect(jsonPath("$.message").value("Not exist post."))
                        .andDo(print());
            }

            @Test
            @Order(6)
            @DisplayName("존재하지 않는 게시판")
            public void testCase06() throws Exception {
                Post post = getPost(reportPost00);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("boardId", "NOT_EXIST_BOARD");
                requestBody.put("postId", post.getId());
                requestBody.put("type", "hate");
                requestBody.put("reason", "This is a hateful post.");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/report/post")
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
    }
}

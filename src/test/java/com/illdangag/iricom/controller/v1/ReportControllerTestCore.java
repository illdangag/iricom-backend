package com.illdangag.iricom.controller.v1;

import com.illdangag.iricom.core.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.core.test.data.wrapper.TestBoardInfo;
import com.illdangag.iricom.core.test.data.wrapper.TestCommentInfo;
import com.illdangag.iricom.core.test.data.wrapper.TestPostInfo;
import com.illdangag.iricom.IricomTestServiceSuite;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@DisplayName("controller: 신고")
public class ReportControllerTestCore extends IricomTestServiceSuite {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    public ReportControllerTestCore(ApplicationContext context) {
        super(context);
    }

    @Nested
    @DisplayName("신고")
    class Report {

        @Nested
        @DisplayName("게시물")
        class ReportPost {

            @Test
            @DisplayName("게시물 신고")
            void reportPost() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();
                // 게시판 생성
                TestBoardInfo board = setRandomBoard();
                // 게시물 생성
                TestPostInfo post = setRandomPost(board, account);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("type", "hate");
                requestBody.put("reason", "This is a hateful post.");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/report/post/boards/{boardId}/posts/{postId}", board.getId(), post.getId())
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, account);

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
            @DisplayName("게시물 중복 신고")
            void duplicateReportPost() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();
                // 게시판 생성
                TestBoardInfo board = setRandomBoard();
                // 게시물 생성
                TestPostInfo post = setRandomPost(board, account);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("type", "hate");
                requestBody.put("reason", "This is a hateful post.");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/report/post/boards/{boardId}/posts/{postId}", board.getId(), post.getId())
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, account);

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
            @DisplayName("다른 게시판의 게시물")
            void notMatchedBoardPost() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();
                // 게시판 생성
                TestBoardInfo board = setRandomBoard();
                TestBoardInfo otherBoard = setRandomBoard();
                // 게시물 생성
                TestPostInfo post = setRandomPost(board, account);
                setRandomPost(otherBoard, account);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("type", "hate");
                requestBody.put("reason", "This is a hateful post.");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/report/post/boards/{boardId}/posts/{postId}", otherBoard.getId(), post.getId())
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, account);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(404))
                        .andExpect(jsonPath("$.code").value("04000000"))
                        .andExpect(jsonPath("$.message").value("Not exist post."))
                        .andDo(print());
            }

            @Test
            @DisplayName("존재하지 않는 게시물")
            void notExistPost() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();
                // 게시판 생성
                TestBoardInfo board = setRandomBoard();


                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("type", "hate");
                requestBody.put("reason", "This is a hateful post.");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/report/post/boards/{boardId}/posts/{postId}", board.getId(), "unknown")
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, account);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(404))
                        .andExpect(jsonPath("$.code").value("04000000"))
                        .andExpect(jsonPath("$.message").value("Not exist post."))
                        .andDo(print());
            }

            @Test
            @DisplayName("존재하지 않는 게시판")
            void notExistBoard() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();
                // 게시판 생성
                TestBoardInfo board = setRandomBoard();
                // 게시물 생성
                TestPostInfo post = setRandomPost(board, account);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("type", "hate");
                requestBody.put("reason", "This is a hateful post.");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/report/post/boards/{boardId}/posts/{postId}", "unknown", post.getId())
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, account);

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
            @DisplayName("댓글 신고")
            void reportComment() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();
                // 게시판 생성
                TestBoardInfo board = setRandomBoard();
                // 게시물 생성
                TestPostInfo post = setRandomPost(board, account);
                // 댓글 생성
                TestCommentInfo comment = setRandomComment(post, account);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("type", "hate");
                requestBody.put("reason", "This is a hateful comment.");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/report/comment/boards/{boardId}/posts/{postId}/comments/{commentId}", board.getId(), post.getId(), comment.getId())
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, account);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andDo(print());
            }

            @Test
            @DisplayName("댓글 중복 신고")
            void testCase01() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();
                // 게시판 생성
                TestBoardInfo board = setRandomBoard();
                // 게시물 생성
                TestPostInfo post = setRandomPost(board, account);
                // 댓글 생성
                TestCommentInfo comment = setRandomComment(post, account);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("type", "hate");
                requestBody.put("reason", "This is a hateful comment.");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/report/comment/boards/{boardId}/posts/{postId}/comments/{commentId}", board.getId(), post.getId(), comment.getId())
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, account);

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
            @DisplayName("존재하지 않는 게시판")
            void testCase02() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();
                // 게시판 생성
                TestBoardInfo board = setRandomBoard();
                // 게시물 생성
                TestPostInfo post = setRandomPost(board, account);
                // 댓글 생성
                TestCommentInfo comment = setRandomComment(post, account);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("type", "hate");
                requestBody.put("reason", "This is a hateful comment.");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/report/comment/boards/{boardId}/posts/{postId}/comments/{commentId}", "unknown", post.getId(), comment.getId())
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, account);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(404))
                        .andExpect(jsonPath("$.code").value("03000000"))
                        .andExpect(jsonPath("$.message").value("Not exist board."))
                        .andDo(print());
            }

            @Test
            @DisplayName("존재하지 않는 게시물")
            void testCase03() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();
                // 게시판 생성
                TestBoardInfo board = setRandomBoard();
                // 게시물 생성
                TestPostInfo post = setRandomPost(board, account);
                // 댓글 생성
                TestCommentInfo comment = setRandomComment(post, account);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("type", "hate");
                requestBody.put("reason", "This is a hateful comment.");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/report/comment/boards/{boardId}/posts/{postId}/comments/{commentId}", board.getId(), "unknown", comment.getId())
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, account);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(404))
                        .andExpect(jsonPath("$.code").value("04000000"))
                        .andExpect(jsonPath("$.message").value("Not exist post."))
                        .andDo(print());
            }

            @Test
            @DisplayName("존재하지 않는 댓글")
            void testCase04() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();
                // 게시판 생성
                TestBoardInfo board = setRandomBoard();
                // 게시물 생성
                TestPostInfo post = setRandomPost(board, account);
                // 댓글 생성
                setRandomComment(post, account);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("type", "hate");
                requestBody.put("reason", "This is a hateful comment.");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/report/comment/boards/{boardId}/posts/{postId}/comments/{commentId}", board.getId(), post.getId(), "unknown")
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, account);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(404))
                        .andExpect(jsonPath("$.code").value("05000000"))
                        .andExpect(jsonPath("$.message").value("Not exist comment."))
                        .andDo(print());
            }

            @Test
            @DisplayName("올바르지 않은 게시판")
            void testCase05() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();
                // 게시판 생성
                TestBoardInfo board = setRandomBoard();
                TestBoardInfo otherBoard = setRandomBoard();
                // 게시물 생성
                TestPostInfo post = setRandomPost(board, account);
                // 댓글 생성
                TestCommentInfo comment = setRandomComment(post, account);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("type", "hate");
                requestBody.put("reason", "This is a hateful comment.");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/report/comment/boards/{boardId}/posts/{postId}/comments/{commentId}", otherBoard.getId(), post.getId(), comment.getId())
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, account);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(404))
                        .andExpect(jsonPath("$.code").value("04000000"))
                        .andExpect(jsonPath("$.message").value("Not exist post."))
                        .andDo(print());
            }

            @Test
            @DisplayName("올바르지 않은 게시물")
            void testCase06() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();
                // 게시판 생성
                TestBoardInfo board = setRandomBoard();
                // 게시물 생성
                TestPostInfo post = setRandomPost(board, account);
                TestPostInfo otherPost = setRandomPost(board, account);
                // 댓글 생성
                TestCommentInfo comment = setRandomComment(post, account);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("type", "hate");
                requestBody.put("reason", "This is a hateful comment.");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/report/comment/boards/{boardId}/posts/{postId}/comments/{commentId}", board.getId(), otherPost.getId(), comment.getId())
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, account);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(404))
                        .andExpect(jsonPath("$.code").value("05000000"))
                        .andExpect(jsonPath("$.message").value("Not exist comment."))
                        .andDo(print());
            }

            @Test
            @DisplayName("올바르지 않은 댓글")
            void testCase07() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();
                // 게시판 생성
                TestBoardInfo board = setRandomBoard();
                // 게시물 생성
                TestPostInfo post = setRandomPost(board, account);
                TestPostInfo otherPost = setRandomPost(board, account);
                // 댓글 생성
                setRandomComment(post, account);
                TestCommentInfo otherComment = setRandomComment(otherPost, account);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("type", "hate");
                requestBody.put("reason", "This is a hateful comment.");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/report/comment/boards/{boardId}/posts/{postId}/comments/{commentId}", board.getId(), post.getId(), otherComment.getId())
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, account);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(404))
                        .andExpect(jsonPath("$.code").value("05000000"))
                        .andExpect(jsonPath("$.message").value("Not exist comment."))
                        .andDo(print());
            }

            @Test
            @DisplayName("비활성화 게시판")
            void testCase08() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();
                // 게시판 생성
                TestBoardInfo board = setRandomBoard();
                // 게시물 생성
                TestPostInfo post = setRandomPost(board, account);
                // 댓글 생성
                TestCommentInfo comment = setRandomComment(post, account);
                // 게시판 비활성화
                setDisabledBoard(Collections.singletonList(board));

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("type", "hate");
                requestBody.put("reason", "This is a hateful comment.");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/report/comment/boards/{boardId}/posts/{postId}/comments/{commentId}", board.getId(), post.getId(), comment.getId())
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, account);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(400))
                        .andExpect(jsonPath("$.code").value("03000001"))
                        .andExpect(jsonPath("$.message").value("Board is disabled."))
                        .andDo(print());
            }

            @Test
            @DisplayName("댓글을 허용하지 않은 게시물")
            void testCase09() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();
                // 게시판 생성
                TestBoardInfo board = setRandomBoard();
                // 게시물 생성
                TestPostInfo post = setRandomPost(board, account);
                // 댓글 생성
                TestCommentInfo comment = setRandomComment(post, account);
                // 게시물 댓글 허용하지 않음
                setDisabledCommentPost(Collections.singletonList(post));

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("type", "hate");
                requestBody.put("reason", "This is a hateful comment.");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/report/comment/boards/{boardId}/posts/{postId}/comments/{commentId}", board.getId(), post.getId(), comment.getId())
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, account);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(400))
                        .andExpect(jsonPath("$.code").value("05000002"))
                        .andExpect(jsonPath("$.message").value("This post does not allow comments."))
                        .andDo(print());
            }
        }
    }
}

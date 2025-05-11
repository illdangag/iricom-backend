package com.illdangag.iricom.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.illdangag.iricom.core.data.entity.type.PostState;
import com.illdangag.iricom.core.data.entity.type.PostType;
import com.illdangag.iricom.core.data.response.PostInfo;
import com.illdangag.iricom.core.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.core.test.data.wrapper.TestBoardInfo;
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
import java.util.concurrent.atomic.AtomicLong;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@DisplayName("controller: 게시물")
public class PostControllerTestCore extends IricomTestServiceSuite {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    public PostControllerTestCore(ApplicationContext context) {
        super(context);
    }

    @Nested
    @DisplayName("게시물 생성")
    class CreateTest {

        @Nested
        @DisplayName("게시물")
        class PostTest {

            @Test
            @DisplayName("제목, 내용, 댓글 허용 여부")
            void titleContentIsAllowComment() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();
                // 게시판 생성
                TestBoardInfo board = setRandomBoard();

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("title", "new_title");
                requestBody.put("type", "post");
                requestBody.put("content", "new_content");
                requestBody.put("isAllowComment", true);

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts", board.getId())
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, account);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.title").value("new_title"))
                        .andExpect(jsonPath("$.type").value("post"))
                        .andExpect(jsonPath("$.content").value("new_content"))
                        .andExpect(jsonPath("$.allowComment").value(true))
                        .andDo(print());
            }

            @Test
            @DisplayName("제목")
            void title() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();
                // 게시판 생성
                TestBoardInfo board = setRandomBoard();

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("title", "only_title");
                requestBody.put("type", "post");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts", board.getId())
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, account);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.title").value("only_title"))
                        .andExpect(jsonPath("$.type").value("post"))
                        .andExpect(jsonPath("$.content").value(""))
                        .andExpect(jsonPath("$.allowComment").value(true))
                        .andDo(print());
            }

            @Test
            @DisplayName("내용")
            void content() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();
                // 게시판 생성
                TestBoardInfo board = setRandomBoard();

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("type", "post");
                requestBody.put("content", "only_content");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts", board.getId())
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, account);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(400))
                        .andExpect(jsonPath("$.code").value("01020000"))
                        .andDo(print());
            }

            @Test
            @DisplayName("댓글 허용 여부")
            void isAllowComment() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();
                // 게시판 생성
                TestBoardInfo board = setRandomBoard();

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("type", "post");
                requestBody.put("isAllowComment", true);

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts", board.getId())
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, account);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(400))
                        .andExpect(jsonPath("$.code").value("01020000"))
                        .andDo(print());
            }

            @Test
            @DisplayName("비활성화 게시판에 생성")
            void postToDisabledBoard() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();
                // 게시판 생성
                TestBoardInfo board = setRandomBoard(false, false);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("title", "new_title");
                requestBody.put("type", "post");
                requestBody.put("content", "new_content");
                requestBody.put("isAllowComment", true);

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts", board.getId())
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, account);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(400))
                        .andExpect(jsonPath("$.code").value("03000001"))
                        .andExpect(jsonPath("$.message").value("Board is disabled."))
                        .andDo(print());
            }
        }

        @Nested
        @DisplayName("공지 사항")
        class NotificationTest {

            @Test
            @DisplayName("제목, 내용, 댓글 허용 여부")
            void titleContentIsAllowComment() throws Exception {
                // 게시판 생성
                TestBoardInfo board = setRandomBoard();

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("title", "new_title");
                requestBody.put("type", "notification");
                requestBody.put("content", "new_content");
                requestBody.put("isAllowComment", true);

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts", board.getId())
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, systemAdmin);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.title").value("new_title"))
                        .andExpect(jsonPath("$.type").value("notification"))
                        .andExpect(jsonPath("$.content").value("new_content"))
                        .andExpect(jsonPath("$.allowComment").value(true))
                        .andDo(print());
            }

            @Test
            @DisplayName("제목")
            void title() throws Exception {
                // 게시판 생성
                TestBoardInfo board = setRandomBoard();

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("title", "only_title");
                requestBody.put("type", "notification");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts", board.getId())
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, systemAdmin);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.title").value("only_title"))
                        .andExpect(jsonPath("$.type").value("notification"))
                        .andExpect(jsonPath("$.content").value(""))
                        .andExpect(jsonPath("$.allowComment").value(true))
                        .andDo(print());
            }

            @Test
            @DisplayName("내용")
            void content() throws Exception {
                // 게시판 생성
                TestBoardInfo board = setRandomBoard();

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("type", "post");
                requestBody.put("content", "notification");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts", board.getId())
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, systemAdmin);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(400))
                        .andExpect(jsonPath("$.code").value("01020000"))
                        .andDo(print());
            }

            @Test
            @DisplayName("댓글 허용 여부")
            void isAllowComment() throws Exception {
                // 게시판 생성
                TestBoardInfo board = setRandomBoard();

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("type", "notification");
                requestBody.put("isAllowComment", true);

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts", board.getId())
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, systemAdmin);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(400))
                        .andExpect(jsonPath("$.code").value("01020000"))
                        .andDo(print());
            }

            @Test
            @DisplayName("비활성화 게시판에 생성")
            void postToDisabledBoard() throws Exception {
                // 게시판 생성
                TestBoardInfo board = setRandomBoard(false, true);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("title", "new_title");
                requestBody.put("type", "notification");
                requestBody.put("content", "new_content");
                requestBody.put("isAllowComment", true);

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts", board.getId())
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, systemAdmin);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(400))
                        .andExpect(jsonPath("$.code").value("03000001"))
                        .andExpect(jsonPath("$.message").value("Board is disabled."))
                        .andDo(print());
            }

            @Nested
            @DisplayName("권한")
            class AuthTest {

                @Test
                @DisplayName("게시판 관리자")
                void boardAdmin() throws Exception {
                    // 계정 생성
                    TestAccountInfo account = setRandomAccount();
                    // 게시판 생성
                    TestBoardInfo board = setRandomBoard(Collections.singletonList(account));

                    Map<String, Object> requestBody = new HashMap<>();
                    requestBody.put("title", "new_title");
                    requestBody.put("type", "notification");
                    requestBody.put("content", "new_content");
                    requestBody.put("isAllowComment", true);

                    MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts", board.getId())
                            .content(getJsonString(requestBody))
                            .contentType(MediaType.APPLICATION_JSON);
                    setAuthToken(requestBuilder, account);

                    mockMvc.perform(requestBuilder)
                            .andExpect(jsonPath("$.title").value("new_title"))
                            .andExpect(jsonPath("$.type").value("notification"))
                            .andExpect(jsonPath("$.content").value("new_content"))
                            .andExpect(jsonPath("$.allowComment").value(true))
                            .andDo(print());
                }

                @Test
                @DisplayName("일반 계정")
                void account() throws Exception {
                    // 계정 생성
                    TestAccountInfo account = setRandomAccount();
                    // 게시판 생성
                    TestBoardInfo board = setRandomBoard();

                    Map<String, Object> requestBody = new HashMap<>();
                    requestBody.put("title", "new_title");
                    requestBody.put("type", "notification");
                    requestBody.put("content", "new_content");
                    requestBody.put("isAllowComment", true);

                    MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts", board.getId())
                            .content(getJsonString(requestBody))
                            .contentType(MediaType.APPLICATION_JSON);
                    setAuthToken(requestBuilder, account);

                    mockMvc.perform(requestBuilder)
                            .andExpect(status().is(401))
                            .andExpect(jsonPath("$.code").value("04000001"))
                            .andDo(print());
                }
            }
        }
    }

    @Nested
    @DisplayName("게시물 조회")
    class GetTest {

        @Nested
        @DisplayName("정보")
        class InfoTest {

            @Test
            @DisplayName("기본")
            void getInfo() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();
                // 게시판 생성
                TestBoardInfo board = setRandomBoard();
                // 게시물 생성
                TestPostInfo post = setRandomPost(board, account);

                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/{boardId}/posts/{postId}", board.getId(), post.getId());
                setAuthToken(requestBuilder, account);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andDo(print());
            }

            @Test
            @DisplayName("임시 저장")
            void getTemporaryPost() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();
                // 게시판 생성
                TestBoardInfo board = setRandomBoard();
                // 게시물 생성
                TestPostInfo post = setRandomPost(board, account, PostType.POST, PostState.TEMPORARY);

                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/{boardId}/posts/{postId}", board.getId(), post.getId())
                        .param("state", "temporary");
                setAuthToken(requestBuilder, account);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.status").value("temporary"))
                        .andDo(print());
            }

            @Test
            @DisplayName("임시 저장 하지 않은 게시물")
            void getPublishPost() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();
                // 게시판 생성
                TestBoardInfo board = setRandomBoard();
                // 게시물 생성
                TestPostInfo post = setRandomPost(board, account, PostType.POST, PostState.PUBLISH);

                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/{boardId}/posts/{postId}", board.getId(), post.getId())
                        .param("state", "temporary");
                setAuthToken(requestBuilder, account);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.hasTemporary").value(false))
                        .andDo(print());
            }

            @Test
            @DisplayName("발행 하지 않은 게시물")
            void getNotPublishPost() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();
                // 게시판 생성
                TestBoardInfo board = setRandomBoard();
                // 게시물 생성
                TestPostInfo post = setRandomPost(board, account, PostType.POST, PostState.TEMPORARY);

                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/{boardId}/posts/{postId}", board.getId(), post.getId())
                        .param("state", "publish");
                setAuthToken(requestBuilder, account);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(404))
                        .andExpect(jsonPath("$.code").value("04000005"))
                        .andDo(print());
            }

            @Test
            @DisplayName("존재하지 않는 게시판")
            void getNotExistBoard() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();
                // 게시판 생성
                TestBoardInfo board = setRandomBoard();
                // 게시물 생성
                TestPostInfo post = setRandomPost(board, account, PostType.POST, PostState.TEMPORARY);

                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/{boardId}/posts/{postId}", "unknown", post.getId())
                        .param("state", "temporary");
                setAuthToken(requestBuilder, account);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(404))
                        .andExpect(jsonPath("$.code").value("03000000"))
                        .andDo(print());
            }

            @Test
            @DisplayName("존재하지 않는 게시물")
            void getNotExistPost() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();
                // 게시판 생성
                TestBoardInfo board = setRandomBoard();
                // 게시물 생성
                setRandomPost(board, account, PostType.POST, PostState.TEMPORARY);

                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/{boardId}/posts/{postId}", board.getId(), "unknown")
                        .param("state", "temporary");
                setAuthToken(requestBuilder, account);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(404))
                        .andExpect(jsonPath("$.code").value("04000000"))
                        .andExpect(jsonPath("$.message").value("Not exist post."))
                        .andDo(print());
            }

            @Test
            @DisplayName("조회수")
            void getViewCount() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();
                // 게시판 생성
                TestBoardInfo board = setRandomBoard();
                // 게시물 생성
                TestPostInfo post = setRandomPost(board, account, PostType.POST, PostState.PUBLISH);

                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/{boardId}/posts/{postId}", board.getId(), post.getId());
                setAuthToken(requestBuilder, account);

                AtomicLong viewCount = new AtomicLong();
                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andDo(mvnResult -> {
                            String responseBody = mvnResult.getResponse().getContentAsString();
                            ObjectMapper mapper = new ObjectMapper();
                            PostInfo postInfo = mapper.readValue(responseBody, PostInfo.class);
                            viewCount.set(postInfo.getViewCount());
                        });

                requestBuilder = get("/v1/boards/{boardId}/posts/{postId}", board.getId(), post.getId());
                setAuthToken(requestBuilder, account);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.viewCount").value(viewCount.get() + 1))
                        .andDo(print());
            }
        }

        @Nested
        @DisplayName("목록")
        class ListTest {

            @Test
            @DisplayName("기본")
            void getList() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();
                // 게시판 생성
                TestBoardInfo board = setRandomBoard();
                // 게시물 생성
                setRandomPost(board, account, 32);

                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/{boardId}/posts", board.getId());
                setAuthToken(requestBuilder, account);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.total").value(32))
                        .andExpect(jsonPath("$.skip").value(0))
                        .andExpect(jsonPath("$.limit").value(20))
                        .andExpect(jsonPath("$.posts", hasSize(20)))
                        .andDo(print());
            }

            @Test
            @DisplayName("skip")
            void skip() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();
                // 게시판 생성
                TestBoardInfo board = setRandomBoard();
                // 게시물 생성
                setRandomPost(board, account, 8);

                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/{boardId}/posts", board.getId())
                        .param("skip", "1");
                setAuthToken(requestBuilder, account);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.total").value(8))
                        .andExpect(jsonPath("$.skip").value(1))
                        .andExpect(jsonPath("$.limit").value(20))
                        .andExpect(jsonPath("$.posts", hasSize(7)))
                        .andDo(print());
            }

            @Test
            @DisplayName("limit")
            void limit() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();
                // 게시판 생성
                TestBoardInfo board = setRandomBoard();
                // 게시물 생성
                setRandomPost(board, account, 11);

                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/{boardId}/posts", board.getId())
                        .param("limit", "3");
                setAuthToken(requestBuilder, account);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.total").value(11))
                        .andExpect(jsonPath("$.skip").value(0))
                        .andExpect(jsonPath("$.limit").value(3))
                        .andExpect(jsonPath("$.posts", hasSize(3)))
                        .andDo(print());
            }

            @Test
            @DisplayName("title")
            void title() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();
                // 게시판 생성
                TestBoardInfo board = setRandomBoard();
                // 게시물 생성
                TestPostInfo post = setRandomPost(board, account);

                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/{boardId}/posts", board.getId())
                        .param("title", post.getTitle());
                setAuthToken(requestBuilder, account);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.total").value(1))
                        .andExpect(jsonPath("$.skip").value(0))
                        .andExpect(jsonPath("$.limit").value(20))
                        .andExpect(jsonPath("$.posts").isArray())
                        .andExpect(jsonPath("$.posts", hasSize(1)))
                        .andExpect(jsonPath("$.posts[0].title").value(post.getTitle()))
                        .andExpect(jsonPath("$.posts[0].content").doesNotExist())
                        .andDo(print());
            }

            @Test
            @DisplayName("공지 사항")
            void notification() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();
                // 게시판 생성
                TestBoardInfo board = setRandomBoard(Collections.singletonList(account));
                // 게시물 생성
                setRandomPost(board, account, PostType.NOTIFICATION, PostState.PUBLISH);
                setRandomPost(board, account, PostType.POST, PostState.PUBLISH);

                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/{boardId}/posts", board.getId())
                        .param("type", "notification");
                setAuthToken(requestBuilder, account);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.total").value(1))
                        .andExpect(jsonPath("$.skip").value(0))
                        .andExpect(jsonPath("$.limit").value(20))
                        .andDo(print());
            }
        }
    }

    @Nested
    @DisplayName("게시물 발행")
    class PublishTest {

        @Test
        @DisplayName("발행")
        void publish() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account, PostType.POST, PostState.TEMPORARY);

            MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts/{postId}/publish", board.getId(), post.getId());
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.status").value("publish"))
                    .andDo(print());
        }

        @Test
        @DisplayName("발행한 게시물 다시 발행")
        void publishAlreadyPublishPost() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard(Collections.singletonList(account));
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account, PostType.POST, PostState.PUBLISH);

            MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts/{postId}/publish", board.getId(), post.getId());
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(404))
                    .andExpect(jsonPath("$.code").value("04000004"))
                    .andExpect(jsonPath("$.message").value("Not exist temporary content."))
                    .andDo(print());
        }

        @Test
        @DisplayName("다른 계정이 생성한 게시물 발행")
        void otherCreatePost() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            TestAccountInfo otherAccount = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account, PostType.POST, PostState.TEMPORARY);

            MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts/{postId}/publish", board.getId(), post.getId());
            setAuthToken(requestBuilder, otherAccount);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(401))
                    .andExpect(jsonPath("$.code").value("04000002"))
                    .andDo(print());
        }

        @Test
        @DisplayName("공지 사항 발행")
        void publishNotification() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard(Collections.singletonList(account));
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account, PostType.NOTIFICATION, PostState.TEMPORARY);

            MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts/{postId}/publish", board.getId(), post.getId());
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.status").value("publish"))
                    .andDo(print());
        }

        @Test
        @DisplayName("비활성화 게시판의 게시물 발행")
        void publishInDisabledBoard() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account, PostType.POST, PostState.TEMPORARY);
            // 게시판 비활성화
            setDisabledBoard(Collections.singletonList(board));

            MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts/{postId}/publish", board.getId(), post.getId());
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("03000001"))
                    .andExpect(jsonPath("$.message").value("Board is disabled."))
                    .andDo(print());
        }

        @Test
        @DisplayName("존재하지 않는 게시물 발행")
        void publishNotExistPost() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            setRandomPost(board, account, PostType.POST, PostState.TEMPORARY);

            MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/{boardId}/posts/{postId}/publish", board.getId(), "unknown");
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(404))
                    .andExpect(jsonPath("$.code").value("04000000"))
                    .andExpect(jsonPath("$.message").value("Not exist post."))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("게시물 수정")
    class UpdateTest {

        @Test
        @DisplayName("제목, 내용, 댓글 허용 여부")
        void titleContentIsAllowComment() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account, PostType.POST, PostState.TEMPORARY);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("title", "update_title");
            requestBody.put("content", "update_content");
            requestBody.put("allowComment", false);

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}", board.getId(), post.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.title").value("update_title"))
                    .andExpect(jsonPath("$.content").value("update_content"))
                    .andExpect(jsonPath("$.allowComment").value(false))
                    .andDo(print());
        }

        @Test
        @DisplayName("제목")
        void title() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account, PostType.POST, PostState.TEMPORARY);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("title", "only_title");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}", board.getId(), post.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.title").value("only_title"))
                    .andDo(print());
        }

        @Test
        @DisplayName("내용")
        void content() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account, PostType.POST, PostState.TEMPORARY);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("content", "only_content");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}", board.getId(), post.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.content").value("only_content"))
                    .andDo(print());
        }

        @Test
        @DisplayName("댓글 허용 여부")
        void isAllowComment() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account, PostType.POST, PostState.TEMPORARY);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("allowComment", false);

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}", board.getId(), post.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.allowComment").value(false))
                    .andDo(print());
        }

        @Test
        @DisplayName("게시물을 공지사항으로 수정")
        void notification() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard(Collections.singletonList(account));
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account, PostType.POST, PostState.TEMPORARY);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "notification");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}", board.getId(), post.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.type").value("notification"))
                    .andDo(print());
        }

        @Test
        @DisplayName("발행한 게시물 수정")
        void updateAlreadyPublishPost() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard(Collections.singletonList(account));
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account, PostType.POST, PostState.PUBLISH);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("title", "post_title");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}", board.getId(), post.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.title").value("post_title"))
                    .andDo(print());
        }

        @Nested
        @DisplayName("권한")
        class AuthTest {

            @Test
            @DisplayName("일반 계정이 게시물을 공지사항으로 수정")
            void updateToNotificationByAccount() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();
                // 게시판 생성
                TestBoardInfo board = setRandomBoard();
                // 게시물 생성
                TestPostInfo post = setRandomPost(board, account, PostType.POST, PostState.PUBLISH);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("type", "notification");

                MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}", board.getId(), post.getId())
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, account);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(401))
                        .andExpect(jsonPath("$.code").value("04000001"))
                        .andDo(print());
            }

            @Test
            @DisplayName("다른 게시판 관리자가 게시물을 공지사항으로 수정")
            void updateToNotificationByOtherBoardAdmin() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();
                // 게시판 생성
                TestBoardInfo board = setRandomBoard();
                setRandomBoard(Collections.singletonList(account));
                // 게시물 생성
                TestPostInfo post = setRandomPost(board, account, PostType.POST, PostState.PUBLISH);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("type", "notification");

                MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}", board.getId(), post.getId())
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, account);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(401))
                        .andExpect(jsonPath("$.code").value("04000001"))
                        .andDo(print());
            }
        }
    }

    @Nested
    @DisplayName("게시물 삭제")
    class DeleteTest {

        @Test
        @DisplayName("임시 게시물 삭제")
        void deleteTemporaryPost() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account, PostType.POST, PostState.TEMPORARY);

            MockHttpServletRequestBuilder requestBuilder = delete("/v1/boards/{boardId}/posts/{postId}", board.getId(), post.getId());
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andDo(print());
        }

        @Test
        @DisplayName("발행한 게시물 삭제")
        void deletePublishPost() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account, PostType.POST, PostState.PUBLISH);

            MockHttpServletRequestBuilder requestBuilder = delete("/v1/boards/{boardId}/posts/{postId}", board.getId(), post.getId());
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andDo(print());
        }

        @Test
        @DisplayName("다른 계정의 게시물 삭제")
        void deleteOtherAccount() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            TestAccountInfo otherAccount = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard(Collections.singletonList(account));
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account, PostType.POST, PostState.PUBLISH);

            MockHttpServletRequestBuilder requestBuilder = delete("/v1/boards/{boardId}/posts/{postId}", board.getId(), post.getId());
            setAuthToken(requestBuilder, otherAccount);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(401))
                    .andExpect(jsonPath("$.code").value("04000002"))
                    .andDo(print());
        }

        @Test
        @DisplayName("비활성화 게시판의 게시물 삭제")
        void deleteInDisabledBoard() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account, PostType.POST, PostState.PUBLISH);
            // 게시판 비활성화
            setDisabledBoard(Collections.singletonList(board));

            MockHttpServletRequestBuilder requestBuilder = delete("/v1/boards/{boardId}/posts/{postId}", board.getId(), post.getId());
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("03000001"))
                    .andExpect(jsonPath("$.message").value("Board is disabled."))
                    .andDo(print());
        }

        @Test
        @DisplayName("존재하지 않는 게시물 삭제")
        void deleteNotExistPost() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            setRandomPost(board, account, PostType.POST, PostState.PUBLISH);

            MockHttpServletRequestBuilder requestBuilder = delete("/v1/boards/{boardId}/posts/{postId}", board.getId(), "unknown");
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(404))
                    .andExpect(jsonPath("$.code").value("04000000"))
                    .andExpect(jsonPath("$.message").value("Not exist post."))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("게시물 좋아요/싫어요")
    class VoteTest {

        @Test
        @DisplayName("좋아요")
        void upvote() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account, PostType.POST, PostState.PUBLISH);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "upvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/vote", board.getId(), post.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.upvote").value(1))
                    .andDo(print());
        }

        @Test
        @DisplayName("싫어요")
        void downvote() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account, PostType.POST, PostState.PUBLISH);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "downvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/vote", board.getId(), post.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.downvote").value(1))
                    .andDo(print());
        }

        @Test
        @DisplayName("중복 좋아요")
        void duplicateUpvote() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account, PostType.POST, PostState.PUBLISH);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "upvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/vote", board.getId(), post.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, account);
            mockMvc.perform(requestBuilder);

            requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/vote", board.getId(), post.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, account);
            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("04000006"))
                    .andDo(print());
        }

        @Test
        @DisplayName("중복 싫어요")
        void duplicateDonwvote() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account, PostType.POST, PostState.PUBLISH);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "downvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/vote", board.getId(), post.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, account);
            mockMvc.perform(requestBuilder);

            requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/vote", board.getId(), post.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, account);
            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("04000006"))
                    .andDo(print());
        }

        @Test
        @DisplayName("발행되지 않은 게시물")
        void temporaryPost() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account, PostType.POST, PostState.TEMPORARY);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "upvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/vote", board.getId(), post.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(404))
                    .andExpect(jsonPath("$.code").value("04000005"))
                    .andDo(print());
        }

        @Test
        @DisplayName("다른 게시판의 게시물")
        void otherBoard() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            TestBoardInfo otherBoard = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account, PostType.POST, PostState.PUBLISH);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "upvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/vote", otherBoard.getId(), post.getId())
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
        @DisplayName("비활성화 게시판")
        void disabledPost() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account, PostType.POST, PostState.PUBLISH);
            // 게시판 비활성화
            setDisabledBoard(Collections.singletonList(board));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "upvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/vote", board.getId(), post.getId())
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
        @DisplayName("올바르지 않은 요청")
        void invalidRequest() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account, PostType.POST, PostState.PUBLISH);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "unknown");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/vote", board.getId(), post.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("01020000"))
                    .andDo(print());
        }

        @Test
        @DisplayName("타입을 포함하지 않은 요청")
        void notIncludeType() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            TestPostInfo post = setRandomPost(board, account, PostType.POST, PostState.PUBLISH);

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}/posts/{postId}/vote", board.getId(), post.getId());
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("00000001"))
                    .andDo(print());
        }
    }
}

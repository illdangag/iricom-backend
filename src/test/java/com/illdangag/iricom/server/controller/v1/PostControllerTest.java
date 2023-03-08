package com.illdangag.iricom.server.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@DisplayName("게시물")
public class PostControllerTest extends IricomTestSuite {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    public PostControllerTest(ApplicationContext context) {
        super(context);
    }

    @Nested
    @DisplayName("게시물 생성")
    class CreateTest {

        @Nested
        @DisplayName("게시물")
        class PostTest {

            @Test
            @Order(0)
            @DisplayName("제목, 내용, 댓글 허용 여부")
            public void testCase00() throws Exception {
                Board board = getBoard(createBoard);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("title", "new_title");
                requestBody.put("type", "post");
                requestBody.put("content", "new_content");
                requestBody.put("isAllowComment", true);

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/" + board.getId() + "/posts")
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.title").value("new_title"))
                        .andExpect(jsonPath("$.type").value("post"))
                        .andExpect(jsonPath("$.content").value("new_content"))
                        .andExpect(jsonPath("$.isAllowComment").value(true))
                        .andDo(print());
            }

            @Test
            @Order(1)
            @DisplayName("제목")
            public void testCase01() throws Exception {
                Board board = getBoard(createBoard);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("title", "only_title");
                requestBody.put("type", "post");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/" + board.getId() + "/posts")
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.title").value("only_title"))
                        .andExpect(jsonPath("$.type").value("post"))
                        .andExpect(jsonPath("$.content").value(""))
                        .andExpect(jsonPath("$.isAllowComment").value(true))
                        .andDo(print());
            }

            @Test
            @Order(2)
            @DisplayName("내용")
            public void testCase02() throws Exception {
                Board board = getBoard(createBoard);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("type", "post");
                requestBody.put("content", "only_content");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/" + board.getId() + "/posts")
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(400))
                        .andExpect(jsonPath("$.code").value("01020000"))
                        .andDo(print());
            }

            @Test
            @Order(3)
            @DisplayName("댓글 허용 여부")
            public void testCase03() throws Exception {
                Board board = getBoard(createBoard);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("type", "post");
                requestBody.put("isAllowComment", true);

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/" + board.getId() + "/posts")
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(400))
                        .andExpect(jsonPath("$.code").value("01020000"))
                        .andDo(print());
            }

            @Test
            @Order(4)
            @DisplayName("비활성화 게시판에 생성")
            public void testCase04() throws Exception {
                Board board = getBoard(disableBoard);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("title", "new_title");
                requestBody.put("type", "post");
                requestBody.put("content", "new_content");
                requestBody.put("isAllowComment", true);

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/" + board.getId() + "/posts")
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(400))
                        .andExpect(jsonPath("$.code").value("04000000"))
                        .andDo(print());
            }
        }

        @Nested
        @DisplayName("공지 사항")
        class NotificationTest {

            @Test
            @Order(0)
            @DisplayName("제목, 내용, 댓글 허용 여부")
            public void testCase00() throws Exception {
                Board board = getBoard(createBoard);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("title", "new_title");
                requestBody.put("type", "notification");
                requestBody.put("content", "new_content");
                requestBody.put("isAllowComment", true);

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/" + board.getId() + "/posts")
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, systemAdmin);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.title").value("new_title"))
                        .andExpect(jsonPath("$.type").value("notification"))
                        .andExpect(jsonPath("$.content").value("new_content"))
                        .andExpect(jsonPath("$.isAllowComment").value(true))
                        .andDo(print());
            }

            @Test
            @Order(1)
            @DisplayName("제목")
            public void testCase01() throws Exception {
                Board board = getBoard(createBoard);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("title", "only_title");
                requestBody.put("type", "notification");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/" + board.getId() + "/posts")
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, systemAdmin);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.title").value("only_title"))
                        .andExpect(jsonPath("$.type").value("notification"))
                        .andExpect(jsonPath("$.content").value(""))
                        .andExpect(jsonPath("$.isAllowComment").value(true))
                        .andDo(print());
            }

            @Test
            @Order(2)
            @DisplayName("내용")
            public void testCase02() throws Exception {
                Board board = getBoard(createBoard);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("type", "post");
                requestBody.put("content", "notification");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/" + board.getId() + "/posts")
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, systemAdmin);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(400))
                        .andExpect(jsonPath("$.code").value("01020000"))
                        .andDo(print());
            }

            @Test
            @Order(3)
            @DisplayName("댓글 허용 여부")
            public void testCase03() throws Exception {
                Board board = getBoard(createBoard);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("type", "notification");
                requestBody.put("isAllowComment", true);

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/" + board.getId() + "/posts")
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, systemAdmin);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(400))
                        .andExpect(jsonPath("$.code").value("01020000"))
                        .andDo(print());
            }

            @Test
            @Order(4)
            @DisplayName("비활성화 게시판에 생성")
            public void testCase04() throws Exception {
                Board board = getBoard(disableBoard);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("title", "new_title");
                requestBody.put("type", "notification");
                requestBody.put("content", "new_content");
                requestBody.put("isAllowComment", true);

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/" + board.getId() + "/posts")
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, systemAdmin);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(400))
                        .andExpect(jsonPath("$.code").value("04000000"))
                        .andDo(print());
            }

            @Nested
            @DisplayName("권한")
            class AuthTest {

                @Test
                @Order(0)
                @DisplayName("게시판 관리자")
                public void testCase00() throws Exception {
                    Board board = getBoard(createBoard);

                    Map<String, Object> requestBody = new HashMap<>();
                    requestBody.put("title", "new_title");
                    requestBody.put("type", "notification");
                    requestBody.put("content", "new_content");
                    requestBody.put("isAllowComment", true);

                    MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/" + board.getId() + "/posts")
                            .content(getJsonString(requestBody))
                            .contentType(MediaType.APPLICATION_JSON);
                    setAuthToken(requestBuilder, allBoardAdmin);

                    mockMvc.perform(requestBuilder)
                            .andExpect(jsonPath("$.title").value("new_title"))
                            .andExpect(jsonPath("$.type").value("notification"))
                            .andExpect(jsonPath("$.content").value("new_content"))
                            .andExpect(jsonPath("$.isAllowComment").value(true))
                            .andDo(print());
                }

                @Test
                @Order(1)
                @DisplayName("일반 계정")
                public void testCase01() throws Exception {
                    Board board = getBoard(createBoard);

                    Map<String, Object> requestBody = new HashMap<>();
                    requestBody.put("title", "new_title");
                    requestBody.put("type", "notification");
                    requestBody.put("content", "new_content");
                    requestBody.put("isAllowComment", true);

                    MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/" + board.getId() + "/posts")
                            .content(getJsonString(requestBody))
                            .contentType(MediaType.APPLICATION_JSON);
                    setAuthToken(requestBuilder, common00);

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
            @Order(0)
            @DisplayName("기본")
            public void testCase00() throws Exception {
                Board board = getBoard(enableBoard);
                Post post = getPost(enableBoardPost00);

                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/" + board.getId() + "/posts/" + post.getId());
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andDo(print());
            }

            @Test
            @Order(1)
            @DisplayName("임시 저장")
            public void testCase01() throws Exception {
                Board board = getBoard(enableBoard);
                Post post = getPost(enableBoardPost03);

                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/" + board.getId() + "/posts/" + post.getId())
                        .param("state", "temporary");
                setAuthToken(requestBuilder, allBoardAdmin);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.status").value("temporary"))
                        .andDo(print());
            }

            @Test
            @Order(2)
            @DisplayName("임시 저장 하지 않은 게시물")
            public void testCase02() throws Exception {
                Board board = getBoard(enableBoard);
                Post post = getPost(enableBoardPost00);

                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/" + board.getId() + "/posts/" + post.getId())
                        .param("state", "temporary");
                setAuthToken(requestBuilder, allBoardAdmin);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.hasTemporary").value(false))
                        .andDo(print());
            }

            @Test
            @Order(3)
            @DisplayName("발행 하지 않은 게시물")
            public void testCase03() throws Exception {
                Board board = getBoard(enableBoard);
                Post post = getPost(enableBoardPost03);

                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/" + board.getId() + "/posts/" + post.getId())
                        .param("state", "publish");
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(404))
                        .andExpect(jsonPath("$.code").value("04000005"))
                        .andDo(print());
            }

            @Test
            @Order(4)
            @DisplayName("존재하지 않는 게시판")
            public void testCase04() throws Exception {
                Post post = getPost(enableBoardPost03);

                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/unknown/posts/" + post.getId())
                        .param("state", "temporary");
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(404))
                        .andExpect(jsonPath("$.code").value("03000000"))
                        .andDo(print());
            }

            @Test
            @Order(5)
            @DisplayName("존재하지 않는 게시물")
            public void testCase05() throws Exception {
                Board board = getBoard(enableBoard);

                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/" + board.getId() + "/posts/unknown")
                        .param("state", "temporary");
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(404))
                        .andExpect(jsonPath("$.code").value("04000002"))
                        .andDo(print());
            }

            @Test
            @Order(6)
            @DisplayName("조회수")
            public void testCase06() throws Exception {
                Board board = getBoard(enableBoard);
                Post post = getPost(enableBoardPost00);

                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/" + board.getId() + "/posts/" + post.getId());
                setAuthToken(requestBuilder, common00);

                AtomicInteger viewCount = new AtomicInteger();
                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andDo(mvnResult -> {
                            String responseBody = mvnResult.getResponse().getContentAsString();
                            ObjectMapper mapper = new ObjectMapper();
                            Map<String, Object> map = mapper.readValue(responseBody, Map.class);
                            viewCount.set((Integer) map.get("viewCount"));
                        });

                requestBuilder = get("/v1/boards/" + board.getId() + "/posts/" + post.getId());
                setAuthToken(requestBuilder, common00);

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
            @Order(0)
            @DisplayName("기본")
            public void testCase00() throws Exception {
                Board board = getBoard(enableBoard);

                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/" + board.getId() + "/posts");
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.total").value(3))
                        .andExpect(jsonPath("$.skip").value(0))
                        .andExpect(jsonPath("$.limit").value(20))
                        .andExpect(jsonPath("$.posts").isArray())
                        .andExpect(jsonPath("$.posts", hasSize(3)))
                        .andExpect(jsonPath("$.posts[0].title").value("enableBoardPost02"))
                        .andExpect(jsonPath("$.posts[0].content").doesNotExist())
                        .andExpect(jsonPath("$.posts[1].title").value("enableBoardPost01"))
                        .andExpect(jsonPath("$.posts[1].content").doesNotExist())
                        .andExpect(jsonPath("$.posts[2].title").value("enableBoardPost00"))
                        .andExpect(jsonPath("$.posts[2].content").doesNotExist())
                        .andDo(print());
            }

            @Test
            @Order(1)
            @DisplayName("skip")
            public void testCase01() throws Exception {
                Board board = getBoard(enableBoard);

                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/" + board.getId() + "/posts")
                        .param("skip", "1");
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.total").value(3))
                        .andExpect(jsonPath("$.skip").value(1))
                        .andExpect(jsonPath("$.limit").value(20))
                        .andExpect(jsonPath("$.posts").isArray())
                        .andExpect(jsonPath("$.posts", hasSize(2)))
                        .andExpect(jsonPath("$.posts[0].title").value("enableBoardPost01"))
                        .andExpect(jsonPath("$.posts[0].content").doesNotExist())
                        .andExpect(jsonPath("$.posts[1].title").value("enableBoardPost00"))
                        .andExpect(jsonPath("$.posts[1].content").doesNotExist())
                        .andDo(print());
            }

            @Test
            @Order(2)
            @DisplayName("limit")
            public void testCase02() throws Exception {
                Board board = getBoard(enableBoard);

                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/" + board.getId() + "/posts")
                        .param("limit", "1");
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.total").value(3))
                        .andExpect(jsonPath("$.skip").value(0))
                        .andExpect(jsonPath("$.limit").value(1))
                        .andExpect(jsonPath("$.posts").isArray())
                        .andExpect(jsonPath("$.posts", hasSize(1)))
                        .andExpect(jsonPath("$.posts[0].title").value("enableBoardPost02"))
                        .andExpect(jsonPath("$.posts[0].content").doesNotExist())
                        .andDo(print());
            }

            @Test
            @Order(3)
            @DisplayName("title")
            public void testCase03() throws Exception {
                Board board = getBoard(enableBoard);

                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/" + board.getId() + "/posts")
                        .param("title", enableBoardPost00.getTitle());
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.total").value(1))
                        .andExpect(jsonPath("$.skip").value(0))
                        .andExpect(jsonPath("$.limit").value(20))
                        .andExpect(jsonPath("$.posts").isArray())
                        .andExpect(jsonPath("$.posts", hasSize(1)))
                        .andExpect(jsonPath("$.posts[0].title").value("enableBoardPost00"))
                        .andExpect(jsonPath("$.posts[0].content").doesNotExist())
                        .andDo(print());
            }

            @Test
            @Order(4)
            @DisplayName("공지 사항")
            public void testCase04() throws Exception {
                Board board = getBoard(enableBoard);

                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/" + board.getId() + "/posts")
                        .param("type", "notification");
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.total").value(1))
                        .andExpect(jsonPath("$.skip").value(0))
                        .andExpect(jsonPath("$.limit").value(20))
                        .andExpect(jsonPath("$.posts").isArray())
                        .andExpect(jsonPath("$.posts", hasSize(1)))
                        .andExpect(jsonPath("$.posts[0].title").value("enableBoardNotification00"))
                        .andExpect(jsonPath("$.posts[0].content").doesNotExist())
                        .andDo(print());
            }
        }
    }

    @Nested
    @DisplayName("게시물 발행")
    class PublishTest {

        @Test
        @Order(0)
        @DisplayName("발행")
        public void testCase00() throws Exception {
            Board board = getBoard(updateBoard);
            Post post = getPost(updateBoardPost00);

            MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/publish");
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.status").value("publish"))
                    .andDo(print());
        }

        @Test
        @Order(1)
        @DisplayName("발행한 게시물 다시 발행")
        public void testCase01() throws Exception {
            Board board = getBoard(updateBoard);
            Post post = getPost(updateBoardPost01);

            MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/publish");
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.status").value("publish"))
                    .andDo(print());
        }

        @Test
        @Order(2)
        @DisplayName("다른 계정이 생성한 게시물 발행")
        public void testCase02() throws Exception {
            Board board = getBoard(updateBoard);
            Post post = getPost(updateBoardPost02);

            MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/publish");
            setAuthToken(requestBuilder, common01);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(401))
                    .andExpect(jsonPath("$.code").value("04000002"))
                    .andDo(print());
        }

        @Test
        @Order(3)
        @DisplayName("공지 사항 발행")
        public void testCase03() throws Exception {
            Board board = getBoard(updateBoard);
            Post post = getPost(updateBoardNotification00);

            MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/publish");
            setAuthToken(requestBuilder, allBoardAdmin);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.status").value("publish"))
                    .andDo(print());
        }

        @Test
        @Order(4)
        @DisplayName("비활성화 게시판의 게시물 발행")
        public void testCase04() throws Exception {
            Board board = getBoard(disableBoard);
            Post post = getPost(disableBoardNotification00);

            MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/publish");
            setAuthToken(requestBuilder, allBoardAdmin);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("04000000"))
                    .andDo(print());
        }

        @Test
        @Order(5)
        @DisplayName("존재하지 않는 게시물 발행")
        public void testCase05() throws Exception {
            Board board = getBoard(updateBoard);

            MockHttpServletRequestBuilder requestBuilder = post("/v1/boards/" + board.getId() + "/posts/unknown/publish");
            setAuthToken(requestBuilder, allBoardAdmin);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(404))
                    .andExpect(jsonPath("$.code").value("04000002"))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("게시물 수정")
    class UpdateTest {

        @Test
        @Order(0)
        @DisplayName("제목, 내용, 댓글 허용 여부")
        public void testCase00() throws Exception {
            Board board = getBoard(updateBoard);
            Post post = getPost(updateBoardPost03);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("title", "update_title");
            requestBody.put("content", "update_content");
            requestBody.put("isAllowComment", false);

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/" + board.getId() + "/posts/" + post.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.title").value("update_title"))
                    .andExpect(jsonPath("$.content").value("update_content"))
                    .andExpect(jsonPath("$.isAllowComment").value(false))
                    .andDo(print());
        }

        @Test
        @Order(1)
        @DisplayName("제목")
        public void testCase01() throws Exception {
            Board board = getBoard(updateBoard);
            Post post = getPost(updateBoardPost03);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("title", "only_title");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/" + board.getId() + "/posts/" + post.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.title").value("only_title"))
                    .andDo(print());
        }

        @Test
        @Order(2)
        @DisplayName("내용")
        public void testCase02() throws Exception {
            Board board = getBoard(updateBoard);
            Post post = getPost(updateBoardPost03);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("content", "only_content");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/" + board.getId() + "/posts/" + post.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.content").value("only_content"))
                    .andDo(print());
        }

        @Test
        @Order(3)
        @DisplayName("댓글 허용 여부")
        public void testCase03() throws Exception {
            Board board = getBoard(updateBoard);
            Post post = getPost(updateBoardPost03);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("isAllowComment", false);

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/" + board.getId() + "/posts/" + post.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.isAllowComment").value(false))
                    .andDo(print());
        }

        @Test
        @Order(4)
        @DisplayName("게시물을 공지사항으로 수정")
        public void testCase04() throws Exception {
            Board board = getBoard(updateBoard);
            Post post = getPost(updateBoardPost04);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "notification");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/" + board.getId() + "/posts/" + post.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, allBoardAdmin);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.type").value("notification"))
                    .andDo(print());
        }

        @Test
        @Order(5)
        @DisplayName("발행한 게시물 수정")
        public void testCase05() throws Exception {
            Board board = getBoard(updateBoard);
            Post post = getPost(updateBoardPost05);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("title", "post_title");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/" + board.getId() + "/posts/" + post.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.title").value("post_title"))
                    .andDo(print());
        }

        @Nested
        @DisplayName("권한")
        class AuthTest {

            @Test
            @Order(0)
            @DisplayName("일반 계정이 게시물을 공지사항으로 수정")
            public void testCase00() throws Exception {
                Board board = getBoard(updateBoard);
                Post post = getPost(updateBoardPost06);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("type", "notification");

                MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/" + board.getId() + "/posts/" + post.getId())
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(401))
                        .andExpect(jsonPath("$.code").value("04000001"))
                        .andDo(print());
            }

            @Test
            @Order(1)
            @DisplayName("다른 게시판 관리자가 게시물을 공지사항으로 수정")
            public void testCase01() throws Exception {
                Board board = getBoard(updateBoard);
                Post post = getPost(updateBoardPost07);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("type", "notification");

                MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/" + board.getId() + "/posts/" + post.getId())
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, enableBoardAdmin);

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
        @Order(0)
        @DisplayName("임시 게시물 삭제")
        public void testCase00() throws Exception {
            Board board = getBoard(updateBoard);
            Post post = getPost(updateBoardPost08);

            MockHttpServletRequestBuilder requestBuilder = delete("/v1/boards/" + board.getId() + "/posts/" + post.getId());
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andDo(print());
        }

        @Test
        @Order(1)
        @DisplayName("발행한 게시물 삭제")
        public void testCase01() throws Exception {
            Board board = getBoard(updateBoard);
            Post post = getPost(updateBoardPost09);

            MockHttpServletRequestBuilder requestBuilder = delete("/v1/boards/" + board.getId() + "/posts/" + post.getId());
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andDo(print());
        }

        @Test
        @Order(2)
        @DisplayName("다른 계정의 게시물 삭제")
        public void testCase02() throws Exception {
            Board board = getBoard(updateBoard);
            Post post = getPost(updateBoardPost10);

            MockHttpServletRequestBuilder requestBuilder = delete("/v1/boards/" + board.getId() + "/posts/" + post.getId());
            setAuthToken(requestBuilder, common01);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(401))
                    .andExpect(jsonPath("$.code").value("04000002"))
                    .andDo(print());
        }

        @Test
        @Order(3)
        @DisplayName("비활성화 게시판의 게시물 삭제")
        public void testCase03() throws Exception {
            Board board = getBoard(disableBoard);
            Post post = getPost(disableBoardPost00);

            MockHttpServletRequestBuilder requestBuilder = delete("/v1/boards/" + board.getId() + "/posts/" + post.getId());
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("04000000"))
                    .andDo(print());
        }

        @Test
        @Order(4)
        @DisplayName("존재하지 않는 게시물 삭제")
        public void testCase04() throws Exception {
            Board board = getBoard(updateBoard);

            MockHttpServletRequestBuilder requestBuilder = delete("/v1/boards/" + board.getId() + "/posts/unknown");
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(404))
                    .andExpect(jsonPath("$.code").value("04000002"))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("게시물 좋아요/싫어요")
    class VoteTest {

        @Test
        @Order(0)
        @DisplayName("좋아요")
        public void testCase00() throws Exception {
            Board board = getBoard(voteBoard);
            Post post = getPost(votePost00);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "upvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/vote")
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
            Post post = getPost(votePost00);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "downvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/vote")
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
        @DisplayName("중복 좋아요")
        public void testCase02() throws Exception {
            Board board = getBoard(voteBoard);
            Post post = getPost(votePost00);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "upvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/vote")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common01);
            mockMvc.perform(requestBuilder);

            requestBuilder = patch("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/vote")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common01);
            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("04000006"))
                    .andDo(print());
        }

        @Test
        @Order(3)
        @DisplayName("중복 싫어요")
        public void testCase03() throws Exception {
            Board board = getBoard(voteBoard);
            Post post = getPost(votePost00);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "downvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/vote")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common01);
            mockMvc.perform(requestBuilder);

            requestBuilder = patch("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/vote")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common01);
            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("04000006"))
                    .andDo(print());
        }

        @Test
        @Order(4)
        @DisplayName("발행되지 않은 게시물")
        public void testCase04() throws Exception {
            Board board = getBoard(voteBoard);
            Post post = getPost(votePost01);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "upvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/vote")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(404))
                    .andExpect(jsonPath("$.code").value("04000005"))
                    .andDo(print());
        }

        @Test
        @Order(5)
        @DisplayName("다른 게시판의 게시물")
        public void testCase05() throws Exception {
            Board board = getBoard(enableBoard);
            Post post = getPost(votePost00);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "upvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/vote")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(404))
                    .andExpect(jsonPath("$.code").value("04000002"))
                    .andDo(print());
        }

        @Test
        @Order(6)
        @DisplayName("비활성화 게시판")
        public void testCase06() throws Exception {
            Board board = getBoard(disableBoard);
            Post post = getPost(disableBoardPost00);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "upvote");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/vote")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("04000005"))
                    .andDo(print());
        }

        @Test
        @Order(7)
        @DisplayName("올바르지 않은 요청")
        public void testCase07() throws Exception {
            Board board = getBoard(voteBoard);
            Post post = getPost(votePost00);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", "unknown");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/vote")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("04000007"))
                    .andDo(print());
        }

        @Test
        @Order(8)
        @DisplayName("타입을 포함하지 않은 요청")
        public void testCase08() throws Exception {
            Board board = getBoard(voteBoard);
            Post post = getPost(votePost00);

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/" + board.getId() + "/posts/" + post.getId() + "/vote");
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("00000001"))
                    .andDo(print());
        }
    }
}

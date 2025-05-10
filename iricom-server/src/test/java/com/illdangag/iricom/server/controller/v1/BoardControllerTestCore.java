package com.illdangag.iricom.server.controller.v1;

import com.illdangag.iricom.core.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.core.test.data.wrapper.TestBoardInfo;
import com.illdangag.iricom.server.IricomTestServiceSuite;
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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@DisplayName("controller: 게시판")
public class BoardControllerTestCore extends IricomTestServiceSuite {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    public BoardControllerTestCore(ApplicationContext context) {
        super(context);
    }

    @Nested
    @DisplayName("생성")
    class CreateTest {

        @Test
        @DisplayName("제목, 설명")
        void titleDescription() throws Exception {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("title", "new_board");
            requestBody.put("description", "new_board_description");

            MockHttpServletRequestBuilder requestBuilder = post("/v1/boards")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, systemAdmin);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.title").value("new_board"))
                    .andExpect(jsonPath("$.description").value("new_board_description"))
                    .andDo(print());
        }

        @Test
        @DisplayName("제목")
        void title() throws Exception {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("title", "only_title");

            MockHttpServletRequestBuilder requestBuilder = post("/v1/boards")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, systemAdmin);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.title").value("only_title"))
                    .andDo(print());
        }

        @Test
        @DisplayName("설명")
        void description() throws Exception {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("description", "only_description");

            MockHttpServletRequestBuilder requestBuilder = post("/v1/boards")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, systemAdmin);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("01020000"))
                    .andDo(print());
        }

        @Test
        @DisplayName("제목 길이 초과")
        void overflowTitle() throws Exception {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("title", TEXT_50 + "0");

            MockHttpServletRequestBuilder requestBuilder = post("/v1/boards")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, systemAdmin);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("01020000"))
                    .andDo(print());
        }

        @Test
        @DisplayName("제목 빈 문자열")
        void emptyStringTitle() throws Exception {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("title", "");

            MockHttpServletRequestBuilder requestBuilder = post("/v1/boards")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, systemAdmin);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("01020000"))
                    .andDo(print());
        }

        @Test
        @DisplayName("설명 빈 문자열")
        void emptyDescription() throws Exception {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("title", "empty_string");
            requestBody.put("description", "");

            MockHttpServletRequestBuilder requestBuilder = post("/v1/boards")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, systemAdmin);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.title").value("empty_string"))
                    .andExpect(jsonPath("$.description").value(""))
                    .andDo(print());
        }

        @Test
        @DisplayName("설명 길이 초과")
        void overflowDescription() throws Exception {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("title", "over");
            requestBody.put("description", TEXT_100 + "0");

            MockHttpServletRequestBuilder requestBuilder = post("/v1/boards")
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, systemAdmin);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("01020000"))
                    .andDo(print());
        }

        @Nested
        @DisplayName("권한")
        class AuthTest {

            @Test
            @DisplayName("게시판 관리자 권한으로 생성")
            void boardAdminAuth() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();
                // 게시판 생성
                setRandomBoard(Collections.singletonList(account));

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("title", "new board");
                requestBody.put("description", "new board description");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards")
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, account);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(401))
                        .andExpect(jsonPath("$.code").value("01000004"))
                        .andDo(print());
            }

            @Test
            @DisplayName("일반 계정으로 생성")
            void accountAuth() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("title", "new board");
                requestBody.put("description", "new board description");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards")
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, account);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(401))
                        .andExpect(jsonPath("$.code").value("01000004"))
                        .andDo(print());
            }
        }
    }

    @Nested
    @DisplayName("조회")
    class GetTest {

        @Nested
        @DisplayName("정보 조회")
        class GetInfoTest {

            @Test
            @DisplayName("존재하는 게시판")
            void existBoard() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();
                // 게시판 생성
                TestBoardInfo board = setRandomBoard();

                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/" + board.getId());
                setAuthToken(requestBuilder, account);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.id").exists())
                        .andExpect(jsonPath("$.title").exists())
                        .andExpect(jsonPath("$.description").exists())
                        .andExpect(jsonPath("$.enabled").value(true))
                        .andDo(print());
            }

            @Test
            @DisplayName("존재하지 않는 게시판")
            void notExistBoard() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();

                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/unknown");
                setAuthToken(requestBuilder, account);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(404))
                        .andExpect(jsonPath("$.code").value("03000000"))
                        .andDo(print());
            }
        }

        @Nested
        @DisplayName("목록 조회")
        class GetInfoListTest {

            @Test
            @DisplayName("기본 조건")
            void boardList() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();

                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards");
                setAuthToken(requestBuilder, account);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.total").exists())
                        .andExpect(jsonPath("$.skip").value(0))
                        .andExpect(jsonPath("$.limit").value(20))
                        .andExpect(jsonPath("$.boards").isArray())
                        .andExpect(jsonPath("$.boards").isNotEmpty())
                        .andDo(print());
            }

            @Test
            @DisplayName("skip")
            void skip() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();

                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards")
                        .param("skip", "1");
                setAuthToken(requestBuilder, account);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.total").exists())
                        .andExpect(jsonPath("$.skip").value(1))
                        .andExpect(jsonPath("$.limit").value(20))
                        .andExpect(jsonPath("$.boards").isArray())
                        .andExpect(jsonPath("$.boards").isNotEmpty())
                        .andDo(print());
            }

            @Test
            @DisplayName("limit")
            void limit() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();

                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards")
                        .param("limit", "1");
                setAuthToken(requestBuilder, account);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.total").exists())
                        .andExpect(jsonPath("$.skip").value(0))
                        .andExpect(jsonPath("$.limit").value(1))
                        .andExpect(jsonPath("$.boards").isArray())
                        .andExpect(jsonPath("$.boards", hasSize(1)))
                        .andDo(print());
            }

            @Test
            @DisplayName("keyword")
            void keyword() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();
                // 게시판 생성
                TestBoardInfo board = setRandomBoard();

                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards")
                        .param("keyword", board.getTitle());
                setAuthToken(requestBuilder, account);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.total").exists())
                        .andExpect(jsonPath("$.skip").value(0))
                        .andExpect(jsonPath("$.limit").value(20))
                        .andExpect(jsonPath("$.boards").isArray())
                        .andDo(print());
            }

            @Test
            @DisplayName("enabled")
            void enabled() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();

                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards")
                        .param("enabled", "true");
                setAuthToken(requestBuilder, account);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.total").exists())
                        .andExpect(jsonPath("$.skip").value(0))
                        .andExpect(jsonPath("$.limit").value(20))
                        .andDo(print());
            }
        }
    }

    @Nested
    @DisplayName("수정")
    class UpdateTest {

        @Test
        @DisplayName("제목, 설명, 활성화 여부")
        void titleDescriptionEnabled() throws Exception {
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("title", "update_title");
            requestBody.put("description", "update_description");
            requestBody.put("enabled", false);

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}", board.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, systemAdmin);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.title").value("update_title"))
                    .andExpect(jsonPath("$.description").value("update_description"))
                    .andExpect(jsonPath("$.enabled").value(false))
                    .andDo(print());
        }

        @Test
        @DisplayName("제목")
        void title() throws Exception {
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("title", "only_title");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}", board.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, systemAdmin);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.title").value("only_title"))
                    .andDo(print());
        }

        @Test
        @DisplayName("설명")
        void description() throws Exception {
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("description", "only_description");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}", board.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, systemAdmin);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.description").value("only_description"))
                    .andDo(print());
        }

        @Test
        @DisplayName("활성화 여부")
        void enabled() throws Exception {
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("enabled", true);

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}", board.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, systemAdmin);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.enabled").value(true))
                    .andDo(print());
        }

        @Test
        @DisplayName("제목 빈 문자열")
        void emptyStringTitle() throws Exception {
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("title", "");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}", board.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, systemAdmin);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("01020000"))
                    .andDo(print());
        }

        @Test
        @DisplayName("제목 길이 초과")
        void overflowTitle() throws Exception {
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("title", TEXT_10 + TEXT_10 + "0");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}", board.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, systemAdmin);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("01020000"))
                    .andDo(print());
        }

        @Test
        @DisplayName("설명 빈 문자열")
        void emptyStringDescription() throws Exception {
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("description", "");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}", board.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, systemAdmin);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.description").value(""))
                    .andDo(print());
        }

        @Test
        @DisplayName("설명 길이 초과")
        void overflowDescription() throws Exception {
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("description", TEXT_100 + ")");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}", board.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, systemAdmin);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("01020000"))
                    .andDo(print());
        }

        @Nested
        @DisplayName("권한")
        class UpdateAuthTest {

            @Test
            @DisplayName("게시판 관리자")
            void boardAdmin() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();
                TestBoardInfo board = setRandomBoard(Collections.singletonList(account));

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("title", "update_title");
                requestBody.put("description", "update_description");
                requestBody.put("enabled", false);

                MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}", board.getId())
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, account);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(401))
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
                requestBody.put("title", "update_title");
                requestBody.put("description", "update_description");
                requestBody.put("enabled", false);

                MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}", board.getId())
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, account);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(401))
                        .andDo(print());
            }
        }
    }
}

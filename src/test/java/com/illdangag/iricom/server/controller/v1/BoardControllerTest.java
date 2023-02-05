package com.illdangag.iricom.server.controller.v1;

import com.illdangag.iricom.server.data.entity.Board;
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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@DisplayName("게시판 테스트")
public class BoardControllerTest extends IricomTestSuite {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    public BoardControllerTest(ApplicationContext context) {
        super(context);
    }

    @Nested
    @DisplayName("생성")
    class CreateTest {

        @Test
        @Order(0)
        @DisplayName("제목, 설명")
        public void testCase00() throws Exception {
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
        @Order(1)
        @DisplayName("제목")
        public void testCase01() throws Exception {
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
        @Order(2)
        @DisplayName("설명")
        public void testCase02() throws Exception {
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
        @Order(3)
        @DisplayName("제목 길이 초과")
        public void testCase03() throws Exception {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("title", "012345678901234567890");

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
        @Order(4)
        @DisplayName("제목 빈 문자열")
        public void testCase04() throws Exception {
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
        @Order(5)
        @DisplayName("설명 빈 문자열")
        public void testCase05() throws Exception {
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
        @Order(6)
        @DisplayName("설명 길이 초과")
        public void testCase06() throws Exception {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("title", "over");
            requestBody.put("description", "01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");

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
            @Order(0)
            @DisplayName("게시판 관리자 권한으로 생성")
            public void testCase00() throws Exception {
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("title", "new board");
                requestBody.put("description", "new board description");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards")
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, enableBoardAdmin);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(401))
                        .andExpect(jsonPath("$.code").value("01000001"))
                        .andDo(print());
            }

            @Test
            @Order(1)
            @DisplayName("일반 계정으로 생성")
            public void testCase01() throws Exception {
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("title", "new board");
                requestBody.put("description", "new board description");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards")
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(401))
                        .andExpect(jsonPath("$.code").value("01000001"))
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
            @Order(0)
            @DisplayName("존재하는 게시판")
            public void testCase00() throws Exception {
                Board board = getBoard(enableBoard);

                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/" + board.getId());
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.id").exists())
                        .andExpect(jsonPath("$.title").exists())
                        .andExpect(jsonPath("$.description").exists())
                        .andExpect(jsonPath("$.enabled").value(true))
                        .andDo(print());
            }

            @Test
            @Order(1)
            @DisplayName("존재하지 않는 게시판")
            public void testCase01() throws Exception {
                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/unknown");
                setAuthToken(requestBuilder, common00);

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
            @Order(0)
            @DisplayName("기본 조건")
            public void testCase00() throws Exception {
                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards");
                setAuthToken(requestBuilder, common00);

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
            @Order(1)
            @DisplayName("skip")
            public void testCase01() throws Exception {
                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards")
                        .param("skip", "1");
                setAuthToken(requestBuilder, common00);

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
            @Order(2)
            @DisplayName("limit")
            public void testCase02() throws Exception {
                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards")
                        .param("limit", "1");
                setAuthToken(requestBuilder, common00);

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
            @Order(3)
            @DisplayName("keyword")
            public void testCase03() throws Exception {
                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards")
                        .param("keyword", enableBoard.getTitle());
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.total").exists())
                        .andExpect(jsonPath("$.skip").value(0))
                        .andExpect(jsonPath("$.limit").value(20))
                        .andExpect(jsonPath("$.boards").isArray())
                        .andExpect(jsonPath("$.boards", hasSize(1)))
                        .andDo(print());
            }

            @Test
            @Order(4)
            @DisplayName("enabled")
            public void testCase04() throws Exception {
                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards")
                        .param("enabled", "true");
                setAuthToken(requestBuilder, common00);

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
        @Order(0)
        @DisplayName("제목, 설명, 활성화 여부")
        public void testCase00() throws Exception {
            Board board = getBoard(updateBoard);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("title", "update_title");
            requestBody.put("description", "update_description");
            requestBody.put("enabled", false);

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/" + board.getId())
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
        @Order(1)
        @DisplayName("제목")
        public void testCase01() throws Exception {
            Board board = getBoard(updateBoard);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("title", "only_title");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/" + board.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, systemAdmin);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.title").value("only_title"))
                    .andDo(print());
        }

        @Test
        @Order(2)
        @DisplayName("설명")
        public void testCase02() throws Exception {
            Board board = getBoard(updateBoard);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("description", "only_description");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/" + board.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, systemAdmin);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.description").value("only_description"))
                    .andDo(print());
        }

        @Test
        @Order(3)
        @DisplayName("활성화 여부")
        public void testCase03() throws Exception {
            Board board = getBoard(updateBoard);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("enabled", true);

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/" + board.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, systemAdmin);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.enabled").value(true))
                    .andDo(print());
        }

        @Test
        @Order(4)
        @DisplayName("제목 빈 문자열")
        public void testCase04() throws Exception {
            Board board = getBoard(updateBoard);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("title", "");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/" + board.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, systemAdmin);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("01020000"))
                    .andDo(print());
        }

        @Test
        @Order(5)
        @DisplayName("제목 길이 초과")
        public void testCase05() throws Exception {
            Board board = getBoard(updateBoard);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("title", "012345678901234567890");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/" + board.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, systemAdmin);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.code").value("01020000"))
                    .andDo(print());
        }

        @Test
        @Order(6)
        @DisplayName("설명 빈 문자열")
        public void testCase06() throws Exception {
            Board board = getBoard(updateBoard);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("description", "");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/" + board.getId())
                    .content(getJsonString(requestBody))
                    .contentType(MediaType.APPLICATION_JSON);
            setAuthToken(requestBuilder, systemAdmin);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.description").value(""))
                    .andDo(print());
        }

        @Test
        @Order(7)
        @DisplayName("설명 길이 초과")
        public void testCase07() throws Exception {
            Board board = getBoard(updateBoard);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("description", "01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/" + board.getId())
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
            @Order(0)
            @DisplayName("게시판 관리자")
            public void testCase00() throws Exception {
                Board board = getBoard(updateBoard);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("title", "update_title");
                requestBody.put("description", "update_description");
                requestBody.put("enabled", false);

                MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/" + board.getId())
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, allBoardAdmin);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(401))
                        .andDo(print());
            }

            @Test
            @Order(1)
            @DisplayName("일반 계정")
            public void testCase01() throws Exception {
                Board board = getBoard(updateBoard);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("title", "update_title");
                requestBody.put("description", "update_description");
                requestBody.put("enabled", false);

                MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/" + board.getId())
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(401))
                        .andDo(print());
            }
        }
    }
}

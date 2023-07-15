package com.illdangag.iricom.server.controller.v1;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.data.entity.Board;
import com.illdangag.iricom.server.test.IricomTestSuite;
import lombok.extern.slf4j.Slf4j;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@DisplayName("게시판 관리자")
public class BoardAuthorizationControllerTest extends IricomTestSuite {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    public BoardAuthorizationControllerTest(ApplicationContext context) {
        super(context);
    }

    @Nested
    @DisplayName("게시판 관리자")
    class BoardAdminTest {

        @Nested
        @DisplayName("생성")
        class CreateTest {

            @Test
            @DisplayName("생성")
            public void testCase00() throws Exception {
                Account account = getAccount(toEnableBoardAdmin);
                Board board = getBoard(enableBoard);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("accountId", account.getId());
                requestBody.put("boardId", board.getId());

                MockHttpServletRequestBuilder requestBuilder = post("/v1/auth/boards")
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, systemAdmin);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andDo(print());
            }

            @Test
            @DisplayName("존재하지 않는 게시판")
            public void testCase01() throws Exception {
                Account account = getAccount(toEnableBoardAdmin);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("accountId", account.getId());
                requestBody.put("boardId", "unknown");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/auth/boards")
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, systemAdmin);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(404))
                        .andExpect(jsonPath("$.code").value("03000000"))
                        .andDo(print());
            }

            @Test
            @DisplayName("존재하지 않는 계정")
            public void testCase02() throws Exception {
                Board board = getBoard(enableBoard);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("accountId", "unknown");
                requestBody.put("boardId", board.getId());

                MockHttpServletRequestBuilder requestBuilder = post("/v1/auth/boards")
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, systemAdmin);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(404))
                        .andExpect(jsonPath("$.code").value("02000000"))
                        .andDo(print());
            }

            @Nested
            @DisplayName("권한")
            class AuthTest {

                @Test
                @DisplayName("게시판 관리자 계정으로 생성")
                public void testCase00() throws Exception {
                    Account account = getAccount(common00);
                    Board board = getBoard(enableBoard);

                    Map<String, Object> requestBody = new HashMap<>();
                    requestBody.put("accountId", account.getId());
                    requestBody.put("boardId", board.getId());

                    MockHttpServletRequestBuilder requestBuilder = post("/v1/auth/boards")
                            .content(getJsonString(requestBody))
                            .contentType(MediaType.APPLICATION_JSON);
                    setAuthToken(requestBuilder, enableBoardAdmin);

                    mockMvc.perform(requestBuilder)
                            .andExpect(status().is(401))
                            .andDo(print());
                }
            }
        }

        @Nested
        @DisplayName("삭제")
        class DeletedTest {

            @Test
            @DisplayName("삭제")
            public void testCase00() throws Exception {
                Account account = getAccount(toDisableBoardAdmin);
                Board board = getBoard(enableBoard);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("accountId", account.getId());
                requestBody.put("boardId", board.getId());

                MockHttpServletRequestBuilder requestBuilder = delete("/v1/auth/boards")
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, systemAdmin);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andDo(print());
            }

            @Test
            @DisplayName("존재하지 않는 게시판")
            public void testCase01() throws Exception {
                Account account = getAccount(toDisableBoardAdmin);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("accountId", account.getId());
                requestBody.put("boardId", "unknown");

                MockHttpServletRequestBuilder requestBuilder = delete("/v1/auth/boards")
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, systemAdmin);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(404))
                        .andExpect(jsonPath("$.code").value("03000000"))
                        .andDo(print());
            }

            @Test
            @DisplayName("존재하지 않는 계정")
            public void testCase02() throws Exception {
                Board board = getBoard(enableBoard);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("accountId", "unknown");
                requestBody.put("boardId", board.getId());

                MockHttpServletRequestBuilder requestBuilder = delete("/v1/auth/boards")
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, systemAdmin);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(404))
                        .andExpect(jsonPath("$.code").value("02000000"))
                        .andDo(print());
            }

            @Nested
            @DisplayName("권한")
            class AuthTest {

                @Test
                @DisplayName("게시판 관리자 계정으로 생성")
                public void testCase00() throws Exception {
                    Account account = getAccount(common00);
                    Board board = getBoard(enableBoard);

                    Map<String, Object> requestBody = new HashMap<>();
                    requestBody.put("accountId", account.getId());
                    requestBody.put("boardId", board.getId());

                    MockHttpServletRequestBuilder requestBuilder = delete("/v1/auth/boards")
                            .content(getJsonString(requestBody))
                            .contentType(MediaType.APPLICATION_JSON);
                    setAuthToken(requestBuilder, common00);

                    mockMvc.perform(requestBuilder)
                            .andExpect(status().is(401))
                            .andDo(print());
                }
            }
        }

        @Nested
        @DisplayName("목록 조회")
        class BoardAdminListGetTest {

            @Test
            @DisplayName("목록 조회")
            public void testCase00() throws Exception {
                MockHttpServletRequestBuilder requestBuilder = get("/v1/auth/boards");
                setAuthToken(requestBuilder, systemAdmin);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.total").exists())
                        .andExpect(jsonPath("$.skip").value(0))
                        .andExpect(jsonPath("$.limit").value(20))
                        .andExpect(jsonPath("$.boardAdmins").isArray())
                        .andDo(print());
            }

            @Test
            @DisplayName("게시판 제목으로 조회")
            public void testCase01() throws Exception {
                MockHttpServletRequestBuilder requestBuilder = get("/v1/auth/boards")
                        .param("keyword", enableBoard.getTitle());
                setAuthToken(requestBuilder, systemAdmin);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.total").exists())
                        .andExpect(jsonPath("$.skip").value(0))
                        .andExpect(jsonPath("$.limit").value(20))
                        .andExpect(jsonPath("$.boardAdmins").isArray())
                        .andDo(print());
            }

            @Test
            @DisplayName("게시판 관리자 계정으로 조회")
            public void testCase02() throws Exception {
                MockHttpServletRequestBuilder requestBuilder = get("/v1/auth/boards");
                setAuthToken(requestBuilder, enableBoardAdmin);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(401))
                        .andExpect(jsonPath("$.code").value("01000004"))
                        .andDo(print());
            }

            @Test
            @DisplayName("일반 계정으로 조회")
            public void testCase03() throws Exception {
                MockHttpServletRequestBuilder requestBuilder = get("/v1/auth/boards");
                setAuthToken(requestBuilder, common00);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(401))
                        .andExpect(jsonPath("$.code").value("01000004"))
                        .andDo(print());
            }
        }

        @Nested
        @DisplayName("정보 조회")
        class BoardAdminGetTest {

            @Test
            @DisplayName("조회")
            public void testCase00() throws Exception {
                Board board = getBoard(enableBoard);
                MockHttpServletRequestBuilder requestBuilder = get("/v1/auth/boards/" + board.getId());
                setAuthToken(requestBuilder, systemAdmin);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.id").exists())
                        .andExpect(jsonPath("$.title").exists())
                        .andExpect(jsonPath("$.description").exists())
                        .andExpect(jsonPath("$.isEnabled").exists())
                        .andExpect(jsonPath("$.accounts").isArray())
                        .andDo(print());
            }
        }
    }
}

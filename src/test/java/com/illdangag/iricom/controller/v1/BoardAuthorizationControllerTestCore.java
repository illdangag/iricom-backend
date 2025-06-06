package com.illdangag.iricom.controller.v1;

import com.illdangag.iricom.core.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.core.test.data.wrapper.TestBoardInfo;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@DisplayName("controller: 게시판 관리자")
public class BoardAuthorizationControllerTestCore extends IricomTestServiceSuite {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    public BoardAuthorizationControllerTestCore(ApplicationContext context) {
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
            void create() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();
                // 게시판 생성
                TestBoardInfo board = setRandomBoard();

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
            void notExistBoard() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();
                // 게시판 생성
                setRandomBoard();

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("accountId", account.getId());
                requestBody.put("boardId", "UNKNOWN_BOARD_ID");

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
            void notExistAccount() throws Exception {
                // 게시판 생성
                TestBoardInfo board = setRandomBoard();

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("accountId", "UNKNOWN_BOARD_ID");
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
                @DisplayName("게시판 관리자 계정으로 다른 게시판 관리자 추가")
                void boardAdminAuth() throws Exception {
                    // 계정 생성
                    TestAccountInfo account = setRandomAccount();
                    TestAccountInfo otherAccount = setRandomAccount();
                    // 게시판 생성
                    TestBoardInfo board = setRandomBoard(Collections.singletonList(account));

                    Map<String, Object> requestBody = new HashMap<>();
                    requestBody.put("accountId", otherAccount.getId());
                    requestBody.put("boardId", board.getId());

                    MockHttpServletRequestBuilder requestBuilder = post("/v1/auth/boards")
                            .content(getJsonString(requestBody))
                            .contentType(MediaType.APPLICATION_JSON);
                    setAuthToken(requestBuilder, account);

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
            void deleteBoardAdmin() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();
                // 게시판 생성
                TestBoardInfo board = setRandomBoard(Collections.singletonList(account));

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
            void notExistBoard() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();
                // 게시판 생성
                setRandomBoard(Collections.singletonList(account));

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("accountId", account.getId());
                requestBody.put("boardId", "UNKNOWN_BOARD_ID");

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
            void notExistAccount() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();
                // 게시판 생성
                TestBoardInfo board = setRandomBoard(Collections.singletonList(account));

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("accountId", "UNKNOWN_ACCOUNT_ID");
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
                @DisplayName("게시판 관리자 계정으로 다른 게시판 관리자 삭제")
                void boardAdminAuth() throws Exception {
                    // 계정 생성
                    TestAccountInfo account = setRandomAccount();
                    TestAccountInfo otherAccount = setRandomAccount();
                    // 게시판 생성
                    TestBoardInfo board = setRandomBoard(Arrays.asList(account, otherAccount));

                    Map<String, Object> requestBody = new HashMap<>();
                    requestBody.put("accountId", account.getId());
                    requestBody.put("boardId", board.getId());

                    MockHttpServletRequestBuilder requestBuilder = delete("/v1/auth/boards")
                            .content(getJsonString(requestBody))
                            .contentType(MediaType.APPLICATION_JSON);
                    setAuthToken(requestBuilder, otherAccount);

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
            void getList() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();
                // 게시판 생성
                setRandomBoard(Collections.singletonList(account));

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
            void getListByKeyword() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();
                // 게시판 생성
                TestBoardInfo board = setRandomBoard(Collections.singletonList(account));

                MockHttpServletRequestBuilder requestBuilder = get("/v1/auth/boards")
                        .param("keyword", board.getTitle());
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
            void boardAdminAuth() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();
                // 게시판 생성
                setRandomBoard(Collections.singletonList(account));

                MockHttpServletRequestBuilder requestBuilder = get("/v1/auth/boards");
                setAuthToken(requestBuilder, account);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(401))
                        .andExpect(jsonPath("$.code").value("01000004"))
                        .andDo(print());
            }

            @Test
            @DisplayName("일반 계정으로 조회")
            void accountAuth() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();
                TestAccountInfo otherAccount = setRandomAccount();
                // 게시판 생성
                setRandomBoard(Collections.singletonList(account));

                MockHttpServletRequestBuilder requestBuilder = get("/v1/auth/boards");
                setAuthToken(requestBuilder, otherAccount);

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
            void getInfo() throws Exception {
                // 계정 생성
                TestAccountInfo account = setRandomAccount();
                // 게시판 생성
                TestBoardInfo board = setRandomBoard(Collections.singletonList(account));

                MockHttpServletRequestBuilder requestBuilder = get("/v1/auth/boards/{boardId}", board.getId());
                setAuthToken(requestBuilder, systemAdmin);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.id").exists())
                        .andExpect(jsonPath("$.title").exists())
                        .andExpect(jsonPath("$.description").exists())
                        .andExpect(jsonPath("$.enabled").exists())
                        .andExpect(jsonPath("$.accounts").isArray())
                        .andDo(print());
            }
        }
    }
}

package com.illdangag.iricom.server.controller.v1;

import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@DisplayName("controller: 게시판 관리자")
public class BoardAuthorizationControllerTest extends IricomTestSuite {
    @Autowired
    MockMvc mockMvc;

    private final TestBoardInfo testBoardInfo00 = TestBoardInfo.builder()
            .title("restDoc").isEnabled(true).adminList(Collections.singletonList(allBoardAdmin)).build();

    @Autowired
    public BoardAuthorizationControllerTest(ApplicationContext context) {
        super(context);

        addTestBoardInfo(testBoardInfo00);

        init();
    }

    @Nested
    @DisplayName("게시판 관리자")
    class BoardAdminTest {

        @Nested
        @DisplayName("생성")
        class CreateTest {

            @Test
            @DisplayName("생성")
            public void create() throws Exception {
                String accountId = getAccountId(toEnableBoardAdmin);
                String boardId = getBoardId(testBoardInfo00);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("accountId", accountId);
                requestBody.put("boardId", boardId);

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
            public void notExistBoard() throws Exception {
                String accountId = getAccountId(toEnableBoardAdmin);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("accountId", accountId);
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
            public void notExistAccount() throws Exception {
                String boardId = getBoardId(testBoardInfo00);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("accountId", "unknown");
                requestBody.put("boardId", boardId);

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
                public void boardAdminAuth() throws Exception {
                    String accountId = getAccountId(common00);
                    String boardId = getBoardId(testBoardInfo00);

                    Map<String, Object> requestBody = new HashMap<>();
                    requestBody.put("accountId", accountId);
                    requestBody.put("boardId", boardId);

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
            public void deleteBoardAdmin() throws Exception {
                String accountId = getAccountId(toDisableBoardAdmin);
                String boardId = getBoardId(testBoardInfo00);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("accountId", accountId);
                requestBody.put("boardId", boardId);

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
            public void notExistBoard() throws Exception {
                String accountId = getAccountId(toDisableBoardAdmin);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("accountId", accountId);
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
            public void notExistAccount() throws Exception {
                String boardId = getBoardId(testBoardInfo00);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("accountId", "unknown");
                requestBody.put("boardId", boardId);

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
                public void boardAdminAuth() throws Exception {
                    String accountId = getAccountId(common00);
                    String boardId = getBoardId(testBoardInfo00);

                    Map<String, Object> requestBody = new HashMap<>();
                    requestBody.put("accountId", accountId);
                    requestBody.put("boardId", boardId);

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
            public void getList() throws Exception {
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
            public void getListByKeyword() throws Exception {
                MockHttpServletRequestBuilder requestBuilder = get("/v1/auth/boards")
                        .param("keyword", testBoardInfo00.getTitle());
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
            public void boardAdminAuth() throws Exception {
                MockHttpServletRequestBuilder requestBuilder = get("/v1/auth/boards");
                setAuthToken(requestBuilder, enableBoardAdmin);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(401))
                        .andExpect(jsonPath("$.code").value("01000004"))
                        .andDo(print());
            }

            @Test
            @DisplayName("일반 계정으로 조회")
            public void accountAuth() throws Exception {
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
            public void getInfo() throws Exception {
                String boardId = getBoardId(testBoardInfo00);
                MockHttpServletRequestBuilder requestBuilder = get("/v1/auth/boards/{boardId}", boardId);
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

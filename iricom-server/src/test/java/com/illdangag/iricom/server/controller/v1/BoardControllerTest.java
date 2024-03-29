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

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@DisplayName("controller: 게시판")
public class BoardControllerTest extends IricomTestSuite {
    @Autowired
    MockMvc mockMvc;

    private final TestBoardInfo testBoardInfo00 = TestBoardInfo.builder()
            .title("testBoardInfo00").isEnabled(true).build();
    private final TestBoardInfo testBoardInfo01 = TestBoardInfo.builder()
            .title("testBoardInfo01").isEnabled(true).build();

    @Autowired
    public BoardControllerTest(ApplicationContext context) {
        super(context);

        addTestBoardInfo(testBoardInfo00, testBoardInfo01);

        init();
    }

    @Nested
    @DisplayName("생성")
    class CreateTest {

        @Test
        @DisplayName("제목, 설명")
        public void titleDescription() throws Exception {
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
        public void title() throws Exception {
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
        public void description() throws Exception {
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
        public void overflowTitle() throws Exception {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("title", "012345678901234567890123456789012345678901234567890");

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
        public void emptyStringTitle() throws Exception {
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
        public void emptyDescription() throws Exception {
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
        public void overflowDescription() throws Exception {
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
            @DisplayName("게시판 관리자 권한으로 생성")
            public void boardAdminAuth() throws Exception {
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("title", "new board");
                requestBody.put("description", "new board description");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards")
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, enableBoardAdmin);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(401))
                        .andExpect(jsonPath("$.code").value("01000004"))
                        .andDo(print());
            }

            @Test
            @DisplayName("일반 계정으로 생성")
            public void accountAuth() throws Exception {
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("title", "new board");
                requestBody.put("description", "new board description");

                MockHttpServletRequestBuilder requestBuilder = post("/v1/boards")
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, common00);

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
            public void existBoard() throws Exception {
                String boardId = getBoardId(testBoardInfo00);

                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards/" + boardId);
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
            @DisplayName("존재하지 않는 게시판")
            public void notExistBoard() throws Exception {
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
            @DisplayName("기본 조건")
            public void boardList() throws Exception {
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
            @DisplayName("skip")
            public void skip() throws Exception {
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
            @DisplayName("limit")
            public void limit() throws Exception {
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
            @DisplayName("keyword")
            public void keyword() throws Exception {
                MockHttpServletRequestBuilder requestBuilder = get("/v1/boards")
                        .param("keyword", testBoardInfo00.getTitle());
                setAuthToken(requestBuilder, common00);

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
            public void enabled() throws Exception {
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
        @DisplayName("제목, 설명, 활성화 여부")
        public void titleDescriptionEnabled() throws Exception {
            String boardId = getBoardId(testBoardInfo01);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("title", "update_title");
            requestBody.put("description", "update_description");
            requestBody.put("enabled", false);

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}", boardId)
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
        public void title() throws Exception {
            String boardId = getBoardId(testBoardInfo01);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("title", "only_title");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}", boardId)
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
        public void description() throws Exception {
            String boardId = getBoardId(testBoardInfo01);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("description", "only_description");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}", boardId)
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
        public void enabled() throws Exception {
            String boardId = getBoardId(testBoardInfo01);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("enabled", true);

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}", boardId)
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
        public void emptyStringTitle() throws Exception {
            String boardId = getBoardId(testBoardInfo01);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("title", "");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}", boardId)
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
        public void overflowTitle() throws Exception {
            String boardId = getBoardId(testBoardInfo01);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("title", "012345678901234567890");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}", boardId)
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
        public void emptyStringDescription() throws Exception {
            String boardId = getBoardId(testBoardInfo01);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("description", "");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}", boardId)
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
        public void overflowDescription() throws Exception {
            String boardId = getBoardId(testBoardInfo01);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("description", "01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");

            MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}", boardId)
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
            public void boardAdmin() throws Exception {
                String boardId = getBoardId(testBoardInfo01);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("title", "update_title");
                requestBody.put("description", "update_description");
                requestBody.put("enabled", false);

                MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}", boardId)
                        .content(getJsonString(requestBody))
                        .contentType(MediaType.APPLICATION_JSON);
                setAuthToken(requestBuilder, allBoardAdmin);

                mockMvc.perform(requestBuilder)
                        .andExpect(status().is(401))
                        .andDo(print());
            }

            @Test
            @DisplayName("일반 계정")
            public void account() throws Exception {
                String boardId = getBoardId(testBoardInfo01);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("title", "update_title");
                requestBody.put("description", "update_description");
                requestBody.put("enabled", false);

                MockHttpServletRequestBuilder requestBuilder = patch("/v1/boards/{boardId}", boardId)
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

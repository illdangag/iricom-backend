package com.illdangag.iricom.server.controller.v1;

import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestAccountInfo;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("controller: 정보 조회")
public class InformationControllerTest extends IricomTestSuite {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    public InformationControllerTest(ApplicationContext context) {
        super(context);
    }

    @Nested
    @DisplayName("기본 정보")
    class DefaultInfoGet {
        @Test
        @DisplayName("관리자 계정")
        void testCase00() throws Exception {
            MockHttpServletRequestBuilder requestBuilder = get("/v1/infos");
            setAuthToken(requestBuilder, systemAdmin);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.email").value(systemAdmin.getEmail()))
                    .andExpect(jsonPath("$.auth").value("systemAdmin"))
                    .andDo(print());
        }

        @Test
        @DisplayName("게시판 관리자 계정")
        void testCase01() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            setRandomBoard(Collections.singletonList(account));

            MockHttpServletRequestBuilder requestBuilder = get("/v1/infos");
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.email").value(account.getEmail()))
                    .andExpect(jsonPath("$.auth").value("boardAdmin"))
                    .andDo(print());
        }

        @Test
        @DisplayName("일반 계정")
        void testCase02() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();

            MockHttpServletRequestBuilder requestBuilder = get("/v1/infos");
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.email").value(account.getEmail()))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("작성한 게시물 목록")
    class GetPostList {
        @Test
        void testCase00() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            TestBoardInfo board = setRandomBoard();
            // 게시물 생성
            setRandomPost(board, account, 13);

            MockHttpServletRequestBuilder requestBuilder = get("/v1/infos/posts");
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.total").value(13))
                    .andExpect(jsonPath("$.skip").value(0))
                    .andExpect(jsonPath("$.limit").value(20))
                    .andExpect(jsonPath("$.posts", hasSize(13)))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("관리자로 등록된 게시판 조회")
    class BoardAdminGetByBoardAdminTest {
        @Test
        @DisplayName("목록 조회")
        void getList() throws Exception {
            // 계정 생성
            TestAccountInfo account = setRandomAccount();
            // 게시판 생성
            setRandomBoard(Collections.singletonList(account), 5);

            MockHttpServletRequestBuilder requestBuilder = get("/v1/infos/admin/boards");
            setAuthToken(requestBuilder, account);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.total").value(5))
                    .andExpect(jsonPath("$.skip").value(0))
                    .andExpect(jsonPath("$.limit").value(20))
                    .andExpect(jsonPath("$.boards", hasSize(5)))
                    .andDo(print());
        }
    }
}

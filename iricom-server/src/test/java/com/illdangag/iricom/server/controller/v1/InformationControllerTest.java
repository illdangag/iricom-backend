package com.illdangag.iricom.server.controller.v1;

import com.illdangag.iricom.server.data.entity.Account;
import com.illdangag.iricom.server.test.IricomTestSuite;
import com.illdangag.iricom.server.test.data.wrapper.TestBoardInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Collections;

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
        public void testCase00() throws Exception {
            Account account = getAccount(systemAdmin);

            MockHttpServletRequestBuilder requestBuilder = get("/v1/infos");
            setAuthToken(requestBuilder, systemAdmin);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.email").value(account.getEmail()))
                    .andExpect(jsonPath("$.auth").value("systemAdmin"))
                    .andDo(print());
        }

        @Test
        @DisplayName("게시판 관리자 계정")
        public void testCase01() throws Exception {
            Account account = getAccount(allBoardAdmin);

            MockHttpServletRequestBuilder requestBuilder = get("/v1/infos");
            setAuthToken(requestBuilder, allBoardAdmin);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.email").value(account.getEmail()))
                    .andExpect(jsonPath("$.auth").value("boardAdmin"))
                    .andDo(print());
        }

        @Test
        @DisplayName("일반 계정")
        public void testCase02() throws Exception {
            Account account = getAccount(common00);

            MockHttpServletRequestBuilder requestBuilder = get("/v1/infos");
            setAuthToken(requestBuilder, common00);

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
        public void testCase00() throws Exception {
            MockHttpServletRequestBuilder requestBuilder = get("/v1/infos/posts");
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.total").exists())
                    .andExpect(jsonPath("$.skip").exists())
                    .andExpect(jsonPath("$.limit").exists())
                    .andExpect(jsonPath("$.posts").isArray())
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("관리자로 등록된 게시판 조회")
    class BoardAdminGetByBoardAdminTest {
        @Test
        @DisplayName("목록 조회")
        public void getList() throws Exception {
            // 게시판
            TestBoardInfo testBoardInfo00 = TestBoardInfo.builder()
                    .title("testBoardInfo00").isEnabled(true).undisclosed(false)
                    .adminList(Collections.singletonList(common00))
                    .build();
            TestBoardInfo testBoardInfo01 = TestBoardInfo.builder()
                    .title("testBoardInfo01").isEnabled(true).undisclosed(false)
                    .adminList(Collections.singletonList(common00))
                    .build();
            TestBoardInfo testBoardInfo02 = TestBoardInfo.builder()
                    .title("testBoardInfo02").isEnabled(true).undisclosed(false)
                    .adminList(Collections.singletonList(common00))
                    .build();
            addTestBoardInfo(testBoardInfo00, testBoardInfo01, testBoardInfo02);
            init();

            MockHttpServletRequestBuilder requestBuilder = get("/v1/infos/admin/boards");
            setAuthToken(requestBuilder, common00);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.total").exists())
                    .andExpect(jsonPath("$.skip").value(0))
                    .andExpect(jsonPath("$.limit").value(20))
                    .andExpect(jsonPath("$.boards").isArray())
                    .andDo(print());
        }

    }
}
